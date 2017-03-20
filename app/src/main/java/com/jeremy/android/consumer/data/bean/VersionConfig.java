package com.jeremy.android.consumer.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremy on 2017/3/1.
 * <p>
 * 格式：
 * {
 * "appName": "app名称",
 * "fileName": "http://...../.....apk",
 * "verName": "1.2",
 * "verCode": 3,
 * "changes": [
 * "1. 修正......",
 * "2. 修正......",
 * "3. 修正......",
 * "4. 修正......"
 * ],
 * }
 */

public class VersionConfig implements Parcelable {

    private String appName = "";
    private String fileName = "";
    private String verName = "";
    private int verCode = 0;
    private List<String> changes;

    public VersionConfig() {
        changes = new ArrayList<>();
    }

    protected VersionConfig(Parcel in) {
        appName = in.readString();
        fileName = in.readString();
        verName = in.readString();
        verCode = in.readInt();
        changes = in.createStringArrayList();
    }

    public static final Creator<VersionConfig> CREATOR = new Creator<VersionConfig>() {
        @Override
        public VersionConfig createFromParcel(Parcel in) {
            return new VersionConfig(in);
        }

        @Override
        public VersionConfig[] newArray(int size) {
            return new VersionConfig[size];
        }
    };

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public List<String> getChanges() {
        return changes;
    }

    public void setChanges(List<String> changes) {
        this.changes = changes;
    }

    public String getChangesString() {
        StringBuffer sb = new StringBuffer();
        for (String s : changes) {
            sb.append(s).append("\n");
        }
        sb.deleteCharAt(sb.length() - "\n".length());
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(appName);
        parcel.writeString(fileName);
        parcel.writeString(verName);
        parcel.writeInt(verCode);
        parcel.writeStringList(changes);
    }
}
