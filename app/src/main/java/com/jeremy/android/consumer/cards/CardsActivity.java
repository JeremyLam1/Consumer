package com.jeremy.android.consumer.cards;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cantrowitz.rxbroadcast.RxBroadcast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.about.AboutActivity;
import com.jeremy.android.consumer.adapter.CardsPullToRefreshAdapter;
import com.jeremy.android.consumer.addCard.AddCardActivity;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.cardDetail.CardDetailActivity;
import com.jeremy.android.consumer.data.bean.CardItem;
import com.jeremy.android.consumer.setting.SettingActivity;
import com.jeremy.android.consumer.data.bean.VersionConfig;
import com.jeremy.android.consumer.utils.BroadcastUtils;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.consumer.utils.RequestCode;
import com.jeremy.android.consumer.view.DividerItemDecoration;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class CardsActivity extends BaseActivity implements CardsContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.layout_refresh)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.rcV_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.layout_ad)
    LinearLayout layoutAd;

    private String search = "";

    private SearchView mSearchView;

    private Disposable localDisposable;

    @Inject
    CardsPullToRefreshAdapter mAdapter;

    @Inject
    CardsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DaggerCardsComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .cardItemListModule(new CardItemListModule())
                .cardsPresenterModule(new CardsPresenterModule(this))
                .build()
                .inject(this);

        mToolbar.setTitle(getString(R.string.card_list));
        setSupportActionBar(mToolbar);

        initRefreshLayout();
        initRecyclerView();
        initBroadcast();

        checkSimCard();

        mPresenter.subscribe();
        mPresenter.checkVersionUpdate();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.themeColor));
        mRefreshLayout.setOnRefreshListener(() -> {
            mAdapter.setEnableLoadMore(false);
            mPresenter.loadCards(true, search);
        });
    }

    private void initRecyclerView() {

        mAdapter.setOnLoadMoreListener(() -> {
            mRefreshLayout.setEnabled(false);
            mPresenter.loadCards(false, search);
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDividerWitdh(10);
        dividerItemDecoration.setDividerColor(ContextCompat.getColor(this, R.color.colorPrimary));

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (adapter.getItemViewType(position) == CardItem.ADD) {
                    mPresenter.addNewCard();
                } else {
                    if (mAdapter.getItem(position).isShowDeletes()) {
                        mAdapter.getItem(position).setShowDeletes(false);
                        mAdapter.notifyItemChanged(position);
                    } else {
                        long cardId = mAdapter.getItem(position).getCardId();
                        mPresenter.openCardDetail(cardId);
                    }
                }
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.getItem(position).setShowDeletes(!mAdapter.getItem(position).isShowDeletes());
                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.img_delete:
                        showDeleteDialog(position, mAdapter.getItem(position).getCardId());
                        break;
                }
            }
        });

    }

    private void initBroadcast() {
        IntentFilter intentFilter = new IntentFilter(BroadcastUtils.ACTION_SHOW_VERSION_DIALOG);
        localDisposable = RxBroadcast.fromBroadcast(this, intentFilter)
                .subscribe(intent -> {
                    VersionConfig config = intent.getParcelableExtra(BundleKeys.VERSION_CONFIG);
                    showVersionUpdateDialog(config);
                });
    }

    private void checkSimCard() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        if (simState != TelephonyManager.SIM_STATE_READY) {
            showSimDialog();
        }
    }

    private void goActivity(Class cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
        if (localDisposable != null) {
            localDisposable.dispose();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    private long mExitTime = 0; // 退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSearchView.isShown()) {
                return false;
            }
            if (System.currentTimeMillis() - mExitTime > 2000) {
                showMsg(R.string.hint_back_to_desk);
                mExitTime = System.currentTimeMillis();
                return false;
            } else {
                moveTaskToBack(false);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showHasNewVersion(VersionConfig config) {
        showVersionUpdateDialog(config);
    }

    @Override
    public void showRefreshLoadingIndicator() {
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setRefreshing(true);
        mAdapter.setEnableLoadMore(false);
    }

    @Override
    public void hideRefreshLoadingIndicator(boolean isLoadMoreEnd) {
        mRefreshLayout.setRefreshing(false);
        mAdapter.setEnableLoadMore(!isLoadMoreEnd);
    }

    @Override
    public void hideMoreLoadingIndicator(boolean loadMoreEnd) {
        if (loadMoreEnd) {
            mAdapter.loadMoreEnd();
        } else {
            mAdapter.loadMoreComplete();
        }
        mRefreshLayout.setEnabled(true);
    }

    @Override
    public void showRefreshToGetCards() {
        mRefreshLayout.setRefreshing(true);
        mAdapter.setEnableLoadMore(false);
        mPresenter.loadCards(true, search);
    }

    @Override
    public void showAddCard() {
        goActivity(AddCardActivity.class, RequestCode.CODE_ADD_CARD);
    }

    @Override
    public void showCardDetail(long cardId) {
        Intent intent = new Intent(CardsActivity.this, CardDetailActivity.class);
        intent.putExtra(BundleKeys.CARD_ID, cardId);
        startActivityForResult(intent, RequestCode.CODE_CARD_DETAIL);
    }

    @Override
    public void showLoadingCardsError(String errorMsg) {
        showMsg(errorMsg);
    }

    public void showDeleteDialog(int pos, long cardId) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getString(R.string.sure_to_delete));
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {

        });
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            mPresenter.deleteCard(pos, cardId);
        });
        AlertDialog dialog = alert.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void showCardDeleted(int pos) {
        mAdapter.getData().remove(pos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCardsUpdated() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoDataMsg() {
        showMsg(R.string.no_data);
    }

    @Override
    public void showIsAllDataMsg() {
        showMsg(R.string.is_all_data);
    }

    @Override
    public void showSettingSavedMsg() {
        showMsg(R.string.save_success);
    }

    @Override
    public void showUpdateCardSuccessMsg() {
        showMsg(R.string.save_success);
    }

    @Override
    public void showAddCardSuccessMsg() {
        showMsg(R.string.add_success);
    }

    @Override
    public List<CardItem> getCardItems() {
        return mAdapter.getData();
    }

    @Override
    public void setPresenter(CardsContract.Presenter presenter) {
        mPresenter = (CardsPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return this.bindUntilEvent(ActivityEvent.DESTROY);
    }

    private void showSimDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getString(R.string.sim_no_ready));
        alert.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
        });
        AlertDialog dialog = alert.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    private void showVersionUpdateDialog(final VersionConfig config) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyApplication.get(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        mBuilder.setTitle(getString(R.string.has_new_version) + config.getVerName());
        mBuilder.setMessage(config.getChangesString());
        mBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> {

        });
        mBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(config.getFileName());
                    intent.setData(content_url);
                    startActivity(intent);
                }
        );
        AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.menu_search);

        mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.hint_search));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        SearchView.SearchAutoComplete textView = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        ImageView goButton = (ImageView) mSearchView.findViewById(R.id.search_go_btn);
        goButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search_white));

        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(textView, R.drawable.cursor_white);
        } catch (Exception ignored) {
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshCards(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            MenuItemCompat.setOnActionExpandListener(menuItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem menuItem) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                            refreshCards("");
                            return true;
                        }
                    });
        } else {
            mSearchView.setOnCloseListener(() -> {
                refreshCards("");
                return true;
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void refreshCards(String key) {
        search = key;
        mRefreshLayout.setRefreshing(true);
        mAdapter.setEnableLoadMore(false);
        mPresenter.loadCards(true, search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                goActivity(SettingActivity.class, RequestCode.CODE_SETTING);
                break;
            case R.id.menu_about:
                goActivity(AboutActivity.class, RequestCode.CODE_ABOUT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
