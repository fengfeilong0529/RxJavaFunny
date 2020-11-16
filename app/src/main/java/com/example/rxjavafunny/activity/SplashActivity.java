package com.example.rxjavafunny.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rxjavafunny.R;
import com.example.rxjavafunny.base.BaseActivity;
import com.example.rxjavafunny.util.AppUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.ivLogo)
    ImageView mIvLogo;
    @BindView(R.id.tvVersion)
    TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        mTvVersion.setText(String.format("Version: %s", AppUtil.getVersionName(this)));

        Disposable disposable = Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        toLoginActivity();
                    }
                });
        addDisposable(disposable);
    }

    private void toLoginActivity() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
