package com.novel.collection.constract;

import com.novel.collection.entity.bean.Login_admin;
import com.novel.collection.entity.bean.Noval_details;

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
        void getLogin(String diveceid,String mobile,String code);
        void getLogin(String diveceid,String mobile,String code,String chanel);
    }
    interface Model {
        void getVertical(String moildle);
        void getLogin(String diveceid,String mobile,String code);
        void getLogin(String diveceid,String mobile,String code,String chanel);
    }
}
