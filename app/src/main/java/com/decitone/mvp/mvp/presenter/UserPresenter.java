package com.decitone.mvp.mvp.presenter;

import com.decitone.mvp.mvp.contract.UserContract;
import com.decitone.mvp.mvp.model.entity.User;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.mvp.IPresenter;

import javax.inject.Inject;

/**
 * Created by Administrator on 2017/6/14.
 */

public class UserPresenter extends BasePresenter<UserContract.Model ,UserContract.View> implements IPresenter{

    @Inject
    public UserPresenter(UserContract.Model model, UserContract.View rootView) {
        super(model ,rootView);
    }

    public void requestUser(int id) {
        mModel.getUser(id);
    }
}
