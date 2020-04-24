package com.example.administrator.xiaoshuoyuedushenqi.presenter;

import com.example.administrator.xiaoshuoyuedushenqi.base.BasePresenter;
import com.example.administrator.xiaoshuoyuedushenqi.constract.ILoginContract;
import com.example.administrator.xiaoshuoyuedushenqi.constract.IRankContract;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;
import com.example.administrator.xiaoshuoyuedushenqi.model.LoginModel;
import com.example.administrator.xiaoshuoyuedushenqi.model.RankModel;

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
    public void getLogin(String mobile, String code) {
      mModel.getLogin(mobile,code);
    }
}
