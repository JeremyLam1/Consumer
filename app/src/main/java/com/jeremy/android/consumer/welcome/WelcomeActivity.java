package com.jeremy.android.consumer.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jeremy.android.consumer.R;
import com.jeremy.android.consumer.base.BaseActivity;
import com.jeremy.android.consumer.cards.CardsActivity;
import com.jeremy.android.consumer.utils.VersionUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;

public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.tv_app_name)
    TextView tvAppName;

    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        String appName = VersionUtils.getAppName(this);
        String version = VersionUtils.getVersionName(this);

        tvAppName.setText(appName);
        tvVersion.setText(version);

        Flowable.timer(3000, TimeUnit.MILLISECONDS)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(o -> {
                    goMainActivity();
                });

    }

    private void goMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, CardsActivity.class);
        startActivity(intent);
        finish();
    }
}
