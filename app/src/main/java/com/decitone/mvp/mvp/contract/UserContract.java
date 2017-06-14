package com.decitone.mvp.mvp.contract;

import com.decitone.mvp.mvp.model.entity.User;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/6/14.
 */

public interface UserContract {

    interface View extends IView{
        void setUserInfo(User userInfo);
    }

    interface Model extends IModel {
        int USERS_PER_PAGE = 10;
        Observable<List<User>> getUsers(int lastIdQueried, boolean update);
    }

}
