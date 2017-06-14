package com.decitone.mvp.app;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.decitone.mvp.BuildConfig;
import com.decitone.mvp.mvp.model.api.Api;
import com.decitone.mvp.mvp.model.api.cache.CommonCache;
import com.decitone.mvp.mvp.model.api.service.CommonService;
import com.google.gson.GsonBuilder;
import com.jess.arms.base.delegate.AppDelegate;
import com.jess.arms.di.module.AppModule;
import com.jess.arms.di.module.ClientModule;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.GlobalHttpHandler;
import com.jess.arms.http.RequestInterceptor;
import com.jess.arms.integration.ConfigModule;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.utils.UiUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache2.internal.RxCache;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErroListener;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class GlobalConfiguration implements ConfigModule {
    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        //使用builder可以为框架配置一些配置信息
        builder.baseurl(Api.APP_DOMAIN)
                .gsonConfiguration(new AppModule.GsonConfiguration() {//这里可以自己自定义配置Gson的参数
                    @Override
                    public void configGson(Context context, GsonBuilder builder) {
                        builder.serializeNulls()//支持序列化null的参数
                                .enableComplexMapKeySerialization();//支持将序列化key为object的map,默认只能序列化key为string的map
                    }
                })
                .retrofitConfiguration(new ClientModule.RetrofitConfiguration() {//这里可以自己自定义配置Retrofit的参数,甚至你可以替换系统配置好的okhttp对象
                    @Override
                    public void configRetrofit(Context context, Retrofit.Builder builder) {
                        //retrofitBuilder.addConverterFactory(FastJsonConverterFactory.create());//比如使用fastjson替代gson
                    }
                })
                .okhttpConfiguration(new ClientModule.OkhttpConfiguration() {//这里可以自己自定义配置Okhttp的参数
                    @Override
                    public void configOkhttp(Context context, OkHttpClient.Builder builder) {
                        builder.writeTimeout(10, TimeUnit.SECONDS);
                    }
                })
                .rxCacheConfiguration(new ClientModule.RxCacheConfiguration() {//这里可以自己自定义配置RxCache的参数
                    @Override
                    public void configRxCache(Context context, RxCache.Builder builder) {
                        builder.useExpiredDataIfLoaderNotAvailable(true);
                    }
                })
                .globalHttpHandler(new GlobalHttpHandler() {// 这里可以提供一个全局处理Http请求和响应结果的处理类,
                    // 这里可以比客户端提前一步拿到服务器返回的结果,可以做一些操作,比如token超时,重新获取
                    @Override
                    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
                         /* 这里可以先客户端一步拿到每一次http请求的结果,可以解析成json,做一些操作,如检测到token过期后
                           重新请求token,并重新执行请求 */
                        try {
                            if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body())) {
                                JSONArray array = new JSONArray(httpResult);
                                JSONObject object = (JSONObject) array.get(0);
                                String login = object.getString("login");
                                String avatar_url = object.getString("avatar_url");
                                Timber.w("Result ------> " + login + "    ||   Avatar_url------> " + avatar_url);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return response;
                        }

                     /* 这里如果发现token过期,可以先请求最新的token,然后在拿新的token放入request里去重新请求
                        注意在这个回调之前已经调用过proceed,所以这里必须自己去建立网络请求,如使用okhttp使用新的request去请求
                        create a new request and modify it accordingly using the new token
                        Request newRequest = chain.request().newBuilder().header("token", newToken)
                                             .build();

                        retry the request

                        response.body().close();
                        如果使用okhttp将新的请求,请求成功后,将返回的response  return出去即可
                        如果不需要返回新的结果,则直接把response参数返回出去 */

                        return response;
                    }

                    // 这里可以在请求服务器之前可以拿到request,做一些操作比如给request统一添加token或者header以及参数加密等操作
                    @Override
                    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
                        /* 如果需要再请求服务器之前做一些操作,则重新返回一个做过操作的的requeat如增加header,不做操作则直接返回request参数
                           return chain.request().newBuilder().header("token", tokenId)
                                  .build(); */
                        return request;
                    }
                })
                .responseErroListener(new ResponseErroListener() {
                    @Override
                    public void handleResponseError(Context context, Exception e) {
                         /* 用来提供处理所有错误的监听
                        rxjava必要要使用ErrorHandleSubscriber(默认实现Subscriber的onError方法),此监听才生效 */
                        Timber.w("------------>" + e.getMessage());
                        UiUtils.SnackbarText("net error");
                    }
                });
    }

    @Override
    public void registerComponents(Context context, IRepositoryManager repositoryManager) {
     //使用repositoryManager可以注入一些服务
     repositoryManager.injectRetrofitService(CommonService.class ,CommonService.class);//Retrofit需要的Service
     repositoryManager.injectCacheService(CommonCache.class);//RxCache需要的Service
    }

    @Override
    public void injectAppLifecycle(Context context, List<AppDelegate.Lifecycle> lifecycles) {
        // AppDelegate.Lifecycle 的所有方法都会在基类Application对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        lifecycles.add(new AppDelegate.Lifecycle() {
            private RefWatcher mRefWatcher;//leakCanary观察器

            @Override
            public void onCreate(Application application) {
                if (BuildConfig.LOG_DEBUG) {//Timber日志打印
                    Timber.plant(new Timber.DebugTree());
                }
                //leakCanary内存泄露检查
                this.mRefWatcher = BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED;
            }

            @Override
            public void onTerminate(Application application) {
                this.mRefWatcher = null;
            }
        });
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
    //向Activity的生命周期中注入一些自定义逻辑
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
    //向Fragment的生命周期中注入一些自定义逻辑
    }
}