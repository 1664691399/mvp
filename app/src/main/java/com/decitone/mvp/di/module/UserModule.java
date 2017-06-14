package com.decitone.mvp.di.module;

import com.decitone.mvp.mvp.contract.UserContract;
import com.decitone.mvp.mvp.model.UserModel;
import com.jess.arms.di.scope.ActivityScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2017/6/14.
 */

@Module
public class UserModule {
    private UserContract.View view;

    //构建UserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
    public UserModule(UserContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    UserContract.View provideUserView(){
        return this.view;
    }

    @ActivityScope
    @Provides
    UserContract.Model provideUserModel(UserModel model){
        return model;
    }
}
