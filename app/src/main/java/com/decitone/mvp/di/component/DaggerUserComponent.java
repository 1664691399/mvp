package com.decitone.mvp.di.component;

import com.decitone.mvp.di.module.UserModule;
import com.decitone.mvp.mvp.ui.activity.UserActivity;
import com.jess.arms.di.component.*;
import com.jess.arms.di.scope.ActivityScope;
import dagger.Component;

/**
 * Created by Administrator on 2017/6/14.
 */

@ActivityScope
@Component(modules = UserModule.class ,dependencies = AppComponent.class)
public interface DaggerUserComponent {
    void inject(UserActivity activity);
}
