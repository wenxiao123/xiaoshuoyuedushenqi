package com.novel.collection.presenter;

import com.novel.collection.base.BasePresenter;
import com.novel.collection.constract.ILoginContract;
import com.novel.collection.constract.IRankContract;
import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Noval_details;
import com.novel.collection.model.LoginModel;
import com.novel.collection.model.RankModel;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public class LoginPresenter extends BasePresenter<ILoginContract.View>
        implements ILoginContract.Presenter {

    private ILoginContract.Model mModel;

    public LoginPresenter() {
        mModel = new LoginModel(this);
    }

    @Override
    public void getVerticalSuccess(String code) {
        if (isAttachView()) {
            getMvpView().getVerticalSuccess(code);
        }
    }

    @Override
    public void getVerticalError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getVerticalError(errorMsg);
        }
    }

    @Override
    public void getLoginSuccess(Login_admin loginAdminl) {
        if (isAttachView()) {
            getMvpView().getLoginSuccess(loginAdminl);
        }
    }

    @Override
    public void getLoginError(String errorMsg) {
        if (isAttachView()) {
            getMvpView().getLoginError(errorMsg);
        }
    }

    @Override
    public void getVertical(String moildle) {
        mModel.getVertical(moildle);
    }

    @Override
    public void getLogin(String diviceid,String mobile, String code) {
      mModel.getLogin(diviceid,mobile,code);
    }
}
