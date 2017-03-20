package com.jeremy.android.consumer.cardDetail;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jeremy.android.consumer.MyApplication;
import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.cardDetail.tabs.comsumption.ConsumptionFragment;
import com.jeremy.android.consumer.cardDetail.tabs.consumptionRecords.ConsumptionRecordFragment;
import com.jeremy.android.consumer.cardDetail.tabs.detailInfo.DetailInfoFragment;
import com.jeremy.android.consumer.cardDetail.tabs.recharge.RechargeFragment;
import com.jeremy.android.consumer.cardDetail.tabs.rechargeRecords.RechargeRecordFragment;
import com.jeremy.android.consumer.utils.BundleKeys;
import com.jeremy.android.database.model.Card;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardDetailActivity extends BaseActivity implements CardDetailContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewpager;

    private ConsumptionFragment consumption;
    private RechargeFragment recharge;
    private RechargeRecordFragment rechargeRecord;
    private ConsumptionRecordFragment consumptionRecord;
    private DetailInfoFragment detailInfo;

    private boolean showSaveBtn = false;

    @Inject
    CardDetailPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        ButterKnife.bind(this);

        long mCardId = getIntent().getLongExtra(BundleKeys.CARD_ID, -1);

        DaggerCardDetailComponent.builder()
                .appComponent(MyApplication.get().getAppComponent())
                .cardDetailPresenterModule(new CardDetailPresenterModule(this, mCardId))
                .build()
                .inject(this);

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPresenter.subscribe();
    }

    @Override
    public void setPresenter(CardDetailContract.Presenter presenter) {
        mPresenter = (CardDetailPresenter) presenter;
    }

    @Override
    public <T> LifecycleTransformer<T> getBindToLifecycle() {
        return this.bindToLifecycle();
    }

    @Override
    public void initViewPage(Card card) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.CARD, card);

        consumption = (ConsumptionFragment) ConsumptionFragment.newInstance();
        consumptionRecord = (ConsumptionRecordFragment) ConsumptionRecordFragment.newInstance();
        recharge = (RechargeFragment) RechargeFragment.newInstance();
        rechargeRecord = (RechargeRecordFragment) RechargeRecordFragment.newInstance();
        detailInfo = (DetailInfoFragment) DetailInfoFragment.newInstance();

        consumption.setArguments(bundle);
        consumptionRecord.setArguments(bundle);
        recharge.setArguments(bundle);
        rechargeRecord.setArguments(bundle);
        detailInfo.setArguments(bundle);

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(consumption, getString(R.string.consumption));
        adapter.addFragment(recharge, getString(R.string.recharge));
        adapter.addFragment(rechargeRecord, getString(R.string.recharge_record));
        adapter.addFragment(consumptionRecord, getString(R.string.consumption_record));
        adapter.addFragment(detailInfo, getString(R.string.detail_info));

        mViewpager.setOffscreenPageLimit(5);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showSaveBtn = adapter.getItem(position) instanceof DetailInfoFragment;
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewpager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void showPageMsg(String msg) {
        showMsg(msg);
    }

    @Override
    public void showCardListPage() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        MenuItem actionSave = menu.findItem(R.id.action_save);
        if (showSaveBtn) {
            actionSave.setVisible(true);
        } else {
            actionSave.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
//                mPresenter.saveDetailInfo();
                detailInfo.updateCard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }
}
