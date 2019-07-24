package com.techwright.demo.test.listeners;

public interface LoginView {
    void showErrorMessageForUsernamePassword();
    void showErrorMessageForMaxLoginAttempt();
    void showLoginSuccessMessage();
}
