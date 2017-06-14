package com.decitone.mvp.mvp.model.api.service;

import com.decitone.mvp.mvp.model.entity.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/6/14.
 */

public interface UserService {

    String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";
    @Headers({HEADER_API_VERSION})
    @GET("/users")
    Observable<List<User>> getUsers(@Query("since") int lastIdQueried, @Query("per_page") int perPage);

}
