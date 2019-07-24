package com.techwright.demo.test.presenter;

import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.test.listeners.LoginView;

public class LoginPresenter {

    private static final int MAX_LOGIN_ATTEMPT = 3;
    private final LoginView loginView;
    private int loginAttempt;

    public LoginPresenter (LoginView loginView){
        this.loginView = loginView;
    }

    public int incrementLoginAttempt(){
        loginAttempt += 1;
        return loginAttempt;
    }

    public boolean isLoginAttemptExceeded(){
        return loginAttempt >= MAX_LOGIN_ATTEMPT;
    }

    public void doLogin(String userName, String password){
        if(isLoginAttemptExceeded()){
            loginView.showErrorMessageForMaxLoginAttempt();
            return;
        }

        if(userName.equals(AppConstants.DEMO_USER) && password.equals(AppConstants.DEMO_PASS)){
            loginView.showLoginSuccessMessage();
            return;
        }

        // increment login attempt if it's fail
        incrementLoginAttempt();
        loginView.showErrorMessageForUsernamePassword();
    }
}
