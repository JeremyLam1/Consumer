package com.jeremy.android.consumer.setting;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.data.source.remote.webApi.ApiService;
import com.jeremy.android.consumer.utils.ImeiUtils;
import com.jeremy.android.consumer.utils.NetworkUtils;
import com.jeremy.android.consumer.utils.PreferencesUtils;
import com.jeremy.android.consumer.utils.SmsUtils;
import com.jeremy.android.database.AppDatabase;
import com.jeremy.android.database.model.Bom;
import com.jeremy.android.database.model.Card;
import com.jeremy.android.database.model.Consumption;
import com.jeremy.android.database.model.Recharge;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Jeremy on 2017/2/17.
 */

public class SettingPresenter implements SettingContract.Presenter {

    private SettingContract.View view;

    private PreferencesUtils preferences;

    private ApiService apiService;

    private ArrayMap<Long, Long> cardIdMaps;//<原始id，插入数据库后的id>

    /**
     * 数据库下载位置
     */
    private String DB_DOWNLOAD_DIR_PATH;

    /**
     * 数据库名称
     */
    private String DB_ZIP_NAME;

    /**
     * 该设备imei
     */
    private String IMEI = "";

    @Inject
    SettingPresenter(ApiService apiService, SettingContract.View view) {
        this.apiService = apiService;
        this.view = view;
        view.setPresenter(this);

        preferences = PreferencesUtils.getInstance((Context) view);
    }

    @Override
    public void subscribe() {
        Context ctx = (Context) view;

        IMEI = ImeiUtils.getImei(ctx);
        DB_ZIP_NAME = preferences.getStoreName(ctx) + "_" + IMEI + ".zip";
        DB_DOWNLOAD_DIR_PATH = Environment.getExternalStorageDirectory() + "/Consumer";

        String storeName = preferences.getStoreName((Context) view);
        boolean isOpen = preferences.getSMSEnable();
        String rechargeSmsTemplate = SmsUtils.getRechargeSmsTemplate(ctx);
        String consumeSmsTemplate = SmsUtils.getConsumeSmsTemplate(ctx);

        view.showStoreName(storeName);
        view.showSmsEnableStatus(isOpen);
        view.showRechargeTemplate(rechargeSmsTemplate);
        view.showConsumerTemplate(consumeSmsTemplate);
    }

    @Override
    public void openBomSetting() {
        view.showBomSettingsPage();
    }

    @Override
    public void uploadLocalDatas() {
        Context ctx = (Context) view;
        if (!NetworkUtils.isNetworkAvailable(ctx)) {
            String pageMsg = ctx.getString(R.string.please_check_network);
            view.showPageMsg(pageMsg);
            return;
        }

        String srcDbPath = ctx.getDatabasePath(AppDatabase.NAME + ".db").getPath();
        Observable.just(srcDbPath)
                .compose(view.getBindToLifecycle())
                .flatMap(new Function<String, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(String s) throws Exception {
                        //复制数据库文件到备份文件
                        String copyDirPath = ctx.getFilesDir().getAbsolutePath() + "/" + IMEI;
                        File dirFile = copyFile(srcDbPath, copyDirPath, AppDatabase.NAME + ".db");
                        return Observable.just(dirFile);
                    }
                })
                .map(file -> {
                    //压缩数据库备份文件
                    File zipFile = new File(ctx.getFilesDir().getAbsolutePath() + "/" + DB_ZIP_NAME);
                    ZipUtil.pack(file, zipFile);
                    return zipFile;
                })
                .subscribeOn(Schedulers.computation())
                .flatMap(new Function<File, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(File file) throws Exception {
                        //上传备份文件
                        // 创建 RequestBody，用于封装构建RequestBody
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        // MultipartBody.Part  和后端约定好Key，这里的partName是用image
                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        // 添加描述
                        String descriptionString = "数据库备份";
                        RequestBody description;
                        description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
                        return apiService.uploadDbBackups(description, body);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                            String loadingHint = ctx.getString(R.string.backup_in_progress);
                            view.showLoading(loadingHint);
                        }
                )
                .subscribe(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String pageMsg = ctx.getString(R.string.backup_success);
                        view.showPageMsg(pageMsg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        String pageMsg = ctx.getString(R.string.backup_error) + "(" + e.getMessage() + ")";
                        view.showPageMsg(pageMsg);
                        view.hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }

    @Override
    public void downloadRemoteDatas() {
        Context ctx = (Context) view;
        if (!NetworkUtils.isNetworkAvailable(ctx)) {
            String pageMsg = ctx.getString(R.string.please_check_network);
            view.showPageMsg(pageMsg);
            return;
        }

        //服务器数据库备份文件地址（这里以从我的网盘下载举例）
        String remoteDbBackupUrl = "https://shcm09.baidupcs.com/file/49f5836e3c137a8b389dddd59de3a799?bkt=p3-140049f5836e3c137a8b389dddd59de3a799f63e0074000000000579&fid=474228867-250528-1042733811850747&time=1489975311&sign=FDTAXGERLBHS-DCb740ccc5511e5e8fedcff06b081203-ViQ8EaEDs2bkIwRV4U8xQTPkUFI%3D&to=71&size=1401&sta_dx=1401&sta_cs=0&sta_ft=zip&sta_ct=3&sta_mt=3&fm2=MH,Yangquan,Netizen-anywhere,,beijingcmnet&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=140049f5836e3c137a8b389dddd59de3a799f63e0074000000000579&sl=72286287&expires=8h&rt=pr&r=607951041&mlogid=1818733807103372890&vuk=474228867&vbdid=1173876074&fin=%E6%B6%88%E8%B4%B9%E6%9C%AC_354225060181851.zip&fn=%E6%B6%88%E8%B4%B9%E6%9C%AC_354225060181851.zip&rtype=1&iv=0&dp-logid=1818733807103372890&dp-callid=0.1.1&hps=1&csl=400&csign=g2Ef98akFKh5yw%2F22xmzbV722V0%3D&by=themis";
        apiService.downloadDbBackups(remoteDbBackupUrl)
                .compose(view.getBindToLifecycle())
                .flatMap(new Function<ResponseBody, ObservableSource<InputStream>>() {
                    @Override
                    public ObservableSource<InputStream> apply(ResponseBody responseBody) throws Exception {
                        //下载数据库备份文件返回输入流
                        return Observable.just(responseBody.byteStream());
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<InputStream, ObservableSource<File>>() {
                    @Override
                    public ObservableSource<File> apply(InputStream inputStream) throws Exception {
                        //输入流转为文件
                        File remoteDbBackZipFile = saveZipFile(inputStream);
                        return Observable.just(remoteDbBackZipFile);
                    }
                })
                .map(zipFile -> {
                    //解压文件
                    String restoreDir = DB_DOWNLOAD_DIR_PATH + "/" + IMEI;
                    File file = new File(restoreDir);
                    if (file.exists() && file.isDirectory()) {
                        file.delete();
                    }
                    ZipUtil.unpack(zipFile, file);
                    return file;
                })
                .doOnNext(file -> {
                    //还原数据库
                    //mark：因为存在主外键关系，所以需先恢复Card表数据，再恢复其他表数据
                    cardIdMaps = new ArrayMap<>();
                    SQLiteDatabase restoreDB = SQLiteDatabase.openOrCreateDatabase(file.getPath() + "/" + AppDatabase.NAME + ".db", null);
                    restoreDB.beginTransaction();
                    Cursor cursor = null;
                    try {
                        //单独先恢复Card表数据
                        cursor = restoreDB.rawQuery("select * from Card", null);
                        restoreCards(cursor);

                        //遍历表
                        cursor = restoreDB.rawQuery("select name from sqlite_master where type='table'", null);
                        while (cursor.moveToNext()) {
                            //获取表名
                            String tbName = cursor.getString(0);
                            if (tbName.equals("Card")) {
                                continue;
                            }
                            Cursor c = restoreDB.rawQuery("select * from " + tbName, null);
                            switch (tbName) {
                                case "Bom":
                                    restoreBoms(c);
                                    break;
                                case "Card":
                                    //continue
                                    break;
                                case "Consumption":
                                    restoreConsumptions(c);
                                    break;
                                case "Recharge":
                                    restoreRecharges(c);
                                    break;
                                default:
                                    break;
                            }
                            c.close();
                        }
                        restoreDB.setTransactionSuccessful();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        restoreDB.endTransaction();
                        restoreDB.close();
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    String pageMsg = ctx.getString(R.string.restore_in_progress);
                    view.showLoading(pageMsg);
                })
                .subscribe(new DefaultObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        String pageMsg = ctx.getString(R.string.restore_success);
                        view.showPageMsg(pageMsg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        String pageMsg = ctx.getString(R.string.restore_error) + "(" + e.getMessage() + ")";
                        view.showPageMsg(pageMsg);
                        view.hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }


    /**
     * 拷贝数据库到指定目录下
     *
     * @param oldPath
     * @param dirPath
     * @param fileName
     */
    private File copyFile(String oldPath, String dirPath, String fileName) throws Exception {
        int bytesum = 0;
        int byteread;
        File dirFile = new File(dirPath);
        if (dirFile.exists()) {
            dirFile.delete();
        }
        dirFile.mkdirs();
        File oldfile = new File(oldPath);
        if (oldfile.exists()) { //文件存在时
            InputStream inStream = new FileInputStream(oldPath); //读入原文件
            FileOutputStream fs = new FileOutputStream(dirPath + "/" + fileName);
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; //字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
        }
        return dirFile;
    }

    /**
     * 保存下载数据库文件
     */
    private File saveZipFile(InputStream is) throws IOException {
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;
        try {
            File dir = new File(DB_DOWNLOAD_DIR_PATH);
            if (dir.exists()) {
                dir.delete();
            }
            dir.mkdirs();
            File dbFile = new File(dir, DB_ZIP_NAME);
            fos = new FileOutputStream(dbFile);
            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return dbFile;
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    private void restoreBoms(Cursor c) {
        while (c.moveToNext()) {
            Bom bom = new Bom();
            try {
                bom._id = c.getLong(c.getColumnIndex("_id"));
            } catch (Exception ignored) {
            }
            try {
                bom.name = c.getString(c.getColumnIndex("name"));
            } catch (Exception ignored) {
            }
            try {
                bom.price = c.getFloat(c.getColumnIndex("price"));
            } catch (Exception ignored) {
            }
            try {
                bom.unit = c.getString(c.getColumnIndex("unit"));
            } catch (Exception ignored) {
            }
            try {
                bom.memo = c.getString(c.getColumnIndex("memo"));
            } catch (Exception ignored) {
            }
            try {
                bom.selected = c.getInt(c.getColumnIndex("selected"));
            } catch (Exception ignored) {
            }
            bom.save();
        }
    }

    private void restoreCards(Cursor c) {
        while (c.moveToNext()) {
            Card card = new Card();
            Long cardId = c.getLong(c.getColumnIndex("_id"));
            try {
                card._id = cardId;
            } catch (Exception ignored) {
            }
            try {
                card.cardNo = c.getString(c.getColumnIndex("cardNo"));
            } catch (Exception ignored) {
            }
            try {
                card.userName = c.getString(c.getColumnIndex("userName"));
            } catch (Exception ignored) {
            }
            try {
                card.userPhone = c.getString(c.getColumnIndex("userPhone"));
            } catch (Exception ignored) {
            }
            try {
                card.userAddr = c.getString(c.getColumnIndex("userAddr"));
            } catch (Exception ignored) {
            }
            try {
                card.memo = c.getString(c.getColumnIndex("memo"));
            } catch (Exception ignored) {
            }
            try {
                card.createDate = c.getLong(c.getColumnIndex("createDate"));
            } catch (Exception ignored) {
            }
            try {
                card.cardExpired = c.getLong(c.getColumnIndex("cardExpired"));
            } catch (Exception ignored) {
            }
            try {
                card.cardBalance = c.getFloat(c.getColumnIndex("cardBalance"));
            } catch (Exception ignored) {
            }
            try {
                card.userPoints = c.getFloat(c.getColumnIndex("userPoints"));
            } catch (Exception ignored) {
            }
            try {
                card.userDelete = c.getInt(c.getColumnIndex("userDelete"));
            } catch (Exception ignored) {
            }
            card.save();
            Long newCardId = card._id;
            cardIdMaps.put(cardId, newCardId);
        }
    }

    private void restoreConsumptions(Cursor c) {
        while (c.moveToNext()) {
            Consumption consumption = new Consumption();
            try {
                consumption._id = c.getLong(c.getColumnIndex("_id"));
            } catch (Exception ignored) {
            }
            try {
                consumption.cardId = c.getLong(c.getColumnIndex("cardId"));
            } catch (Exception ignored) {
            }
            try {
                consumption.bomName = c.getString(c.getColumnIndex("bomName"));
            } catch (Exception ignored) {
            }
            try {
                consumption.bomUnitPrice = c.getFloat(c.getColumnIndex("bomUnitPrice"));
            } catch (Exception ignored) {
            }
            try {
                consumption.unit = c.getString(c.getColumnIndex("unit"));
            } catch (Exception ignored) {
            }
            try {
                consumption.payTime = c.getLong(c.getColumnIndex("payTime"));
            } catch (Exception ignored) {
            }
            try {
                consumption.payMoney = c.getFloat(c.getColumnIndex("payMoney"));
            } catch (Exception ignored) {
            }
            try {
                consumption.payCount = c.getInt(c.getColumnIndex("payCount"));
            } catch (Exception ignored) {
            }
            try {
                consumption.payTimeLength = c.getFloat(c.getColumnIndex("payTimeLength"));
            } catch (Exception ignored) {
            }
            try {
                consumption.memo = c.getString(c.getColumnIndex("memo"));
            } catch (Exception ignored) {
            }
            consumption.cardId = cardIdMaps.get(consumption.cardId);
            consumption.save();
        }
    }

    private void restoreRecharges(Cursor c) {
        while (c.moveToNext()) {
            Recharge recharge = new Recharge();
            try {
                recharge._id = c.getLong(c.getColumnIndex("_id"));
            } catch (Exception ignored) {
            }
            try {
                recharge.cardId = c.getLong(c.getColumnIndex("cardId"));
            } catch (Exception ignored) {
            }
            try {
                recharge.memo = c.getString(c.getColumnIndex("memo"));
            } catch (Exception ignored) {
            }
            try {
                recharge.chargeTime = c.getLong(c.getColumnIndex("chargeTime"));
            } catch (Exception ignored) {
            }
            try {
                recharge.chargeMoney = c.getFloat(c.getColumnIndex("chargeMoney"));
            } catch (Exception ignored) {
            }
            recharge.cardId = cardIdMaps.get(recharge.cardId);
            recharge.save();
        }
    }

    @Override
    public void saveSetting(String storeName, boolean smsEnable) {
        if (TextUtils.isEmpty(storeName)) {
            Context ctx = (Context) view;
            String pageMsg = ctx.getString(R.string.store_name) + ctx.getString(R.string.field_no_be_null);
            view.showPageMsg(pageMsg);
            return;
        }

        preferences.setStoreName(storeName);
        preferences.setSMSEnable(smsEnable);

        view.showCardListPage();
    }

    @Override
    public void unsubscribe() {
        if (view != null) {
            view = null;
        }
    }
}
