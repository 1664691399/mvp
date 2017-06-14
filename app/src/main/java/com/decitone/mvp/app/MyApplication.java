package com.decitone.mvp.app;

import android.app.Application;
import com.decitone.mvp.BuildConfig;
import com.google.gson.Gson;
import com.jess.arms.base.BaseApplication;
import com.jess.arms.base.delegate.AppDelegate;
import com.jess.arms.di.module.AppModule;
import com.jess.arms.di.module.ClientModule;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.integration.AppManager;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.widget.imageloader.ImageLoader;
import com.squareup.leakcanary.LeakCanary;
import java.io.File;
import javax.inject.Singleton;
import dagger.Component;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.LOG_DEBUG) {//Timber日志打印
            Timber.plant(new Timber.DebugTree());
        }
        if (BuildConfig.USE_CANARY) {//leakCanary内存泄露检查
            LeakCanary.install(this);
        }
    }

    @Singleton
    @Component(modules = {AppModule.class, ClientModule.class, GlobalConfigModule.class})
    public interface AppComponent {
        Application Application();

        //用于管理网络请求层,以及数据缓存层
        IRepositoryManager repositoryManager();

        //Rxjava错误处理管理类
        RxErrorHandler rxErrorHandler();


        OkHttpClient okHttpClient();

        //图片管理器,用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
        ImageLoader imageLoader();

        //gson
        Gson gson();

        //缓存文件根目录(RxCache和Glide的的缓存都已经作为子文件夹在这个目录里),应该将所有缓存放到
        // 这个根目录里,便于管理和清理,可在GlobeConfigModule里配置
        File cacheFile();

        //用于管理所有activity
        AppManager appManager();

        void inject(AppDelegate delegate);
    }
}
