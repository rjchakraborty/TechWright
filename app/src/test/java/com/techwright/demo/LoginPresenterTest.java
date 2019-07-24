package com.techwright.demo;

import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.test.presenter.LoginPresenter;
import com.techwright.demo.test.listeners.LoginView;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoginPresenterTest {

    @Test
    public void checkIfLoginAttemptIsExceeded() {
        LoginView loginView = mock(LoginView.class);
        LoginPresenter loginPresenter = new LoginPresenter(loginView);
        Assert.assertEquals(1, loginPresenter.incrementLoginAttempt());
        Assert.assertEquals(2, loginPresenter.incrementLoginAttempt());
        Assert.assertEquals(3, loginPresenter.incrementLoginAttempt());
        Assert.assertTrue(loginPresenter.isLoginAttemptExceeded());

    }

    @Test
    public void checkIfLoginAttemptIsNotExceeded() {
        LoginView loginView = mock(LoginView.class);
        LoginPresenter loginPresenter = new LoginPresenter(loginView);
        Assert.assertFalse(loginPresenter.isLoginAttemptExceeded());
    }

    @Test
    public void checkUsernameAndPasswordIsCorrect() {
        LoginView loginView = mock(LoginView.class);
        LoginPresenter loginPresenter = new LoginPresenter(loginView);
        loginPresenter.doLogin(AppConstants.DEMO_USER, AppConstants.DEMO_PASS);
        verify(loginView).showLoginSuccessMessage();
    }

    @Test
    public void checkUsernameAndPasswordIsInCorrect() {
        LoginView loginView = mock(LoginView.class);
        LoginPresenter loginPresenter = new LoginPresenter(loginView);
        loginPresenter.doLogin("xyz", "123456@R");
        loginPresenter.doLogin("xyz", "123456@R");
        loginPresenter.doLogin("xyz@email.com", "1234");
        loginPresenter.doLogin("xyz", "123456@R");
        verify(loginView).showErrorMessageForMaxLoginAttempt();

    }
}
