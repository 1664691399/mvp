package com.decitone.mvp.mvp.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.decitone.mvp.R;
import com.decitone.mvp.di.component.DaggerUserComponent;
import com.decitone.mvp.mvp.presenter.UserPresenter;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;

public class UserActivity extends BaseActivity<UserPresenter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_user;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }
}
