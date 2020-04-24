package com.example.administrator.xiaoshuoyuedushenqi.constract;

import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Login_admin;
import com.example.administrator.xiaoshuoyuedushenqi.entity.bean.Noval_details;

import java.util.List;

/**
 * @author
 * Created on 2019/11/6
 */
public interface ILoginContract {
    interface View {
        void getVerticalSuccess(String code);
        void getVerticalError(String errorMsg);

        void getLoginSuccess(Login_admin loginAdminl);
        void getLoginError(String errorMsg);
    }
    interface Presenter {
        void getVerticalSuccess(String code);
        void getVerticalError(String errorMsg);

        void getLoginSuccess(Login_admin loginAdminl);
        void getLoginError(String errorMsg);

        void getVertical(String moildle);
        void getLogin(String mobile,String code);
    }
    interface Model {
        void getVertical(String moildle);
        void getLogin(String mobile,String code);
    }
}
