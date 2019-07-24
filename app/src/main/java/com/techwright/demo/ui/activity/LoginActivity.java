package com.techwright.demo.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.techwright.demo.R;
import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.helper.InputValidation;
import com.techwright.demo.test.listeners.QrCodeView;
import com.techwright.demo.test.presenter.LoginPresenter;
import com.techwright.demo.test.listeners.LoginView;
import com.techwright.demo.modules.qrcodescanner.QrCodeActivity;
import com.techwright.demo.test.presenter.QrCodePresenter;
import com.techwright.demo.util.HTTPClient;
import com.techwright.demo.util.KeyboardUtil;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;



/**
 * Created by RJ Chakraborty on 23-07-2019.
 */
public class LoginActivity extends AppCompatActivity implements LoginView, QrCodeView, View.OnClickListener {

    private AppCompatActivity activity = LoginActivity.this;
    private TextInputLayout textInputEmailLayout;
    private TextInputEditText textInputEmailEditText;
    private TextInputLayout textInputPasswordLayout;
    private TextInputEditText textInputPasswordEditText;
    private AppCompatButton bt_login;
    private InputValidation inputValidation;
    private AlertDialog sDialog;
    private LoginPresenter loginPresenter;
    private QrCodePresenter qrCodePresenter;
    private RelativeLayout ll_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initListeners();
        initializePresenter();

        sDialog = new SpotsDialog.Builder()
                .setContext(activity)
                .setMessage(R.string.please_wait)
                .setCancelable(false)
                .build();
    }



    private void initializePresenter(){
        loginPresenter = new LoginPresenter(this);
        qrCodePresenter = new QrCodePresenter(this);
    }

    private void initViews() {
        textInputEmailLayout = findViewById(R.id.textInputEmailLayout);
        textInputEmailEditText = findViewById(R.id.textInputEmailEditText);
        textInputPasswordLayout = findViewById(R.id.textInputPasswordLayout);
        textInputPasswordEditText = findViewById(R.id.textInputPasswordEditText);
        bt_login = findViewById(R.id.btn_login);
        ll_login = findViewById(R.id.ll_login);
        inputValidation = new InputValidation(activity);
    }

    private void initListeners() {
        bt_login.setOnClickListener(this);
    }

    private void verifyDataLogin() {
        if(textInputEmailEditText.getText() == null || textInputPasswordEditText.getText() == null)
            return;

        String enteredEmailStr = textInputEmailEditText.getText().toString().toLowerCase().trim();
        String enteredPasswordStr = textInputPasswordEditText.getText().toString().trim();

        loginPresenter.doLogin(enteredEmailStr, enteredPasswordStr); // Login Username Password Test

        boolean isValid = true;

        if (inputValidation.isFilled(textInputEmailLayout, enteredEmailStr, getString(R.string.please_enter_email_address))) {
            isValid = false;
        }
        if (inputValidation.isEmailValid(textInputEmailLayout, enteredEmailStr, getString(R.string.please_enter_valid_email_address))) {
            isValid = false;
        }
        if (inputValidation.isFilled(textInputPasswordLayout, enteredPasswordStr, getString(R.string.please_enter_password))) {
            isValid = false;
        }

        if (inputValidation.isPasswordValid(textInputPasswordLayout, enteredPasswordStr, getString(R.string.password_must_be_at_least___))) {
            isValid = false;
        }

        KeyboardUtil.hideSoftKeyboard(activity);

        if (isValid) {
            callApi(enteredEmailStr, enteredPasswordStr);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scords[] = new int[2];
            v.getLocationOnScreen(scords);
            float x = ev.getRawX() + v.getLeft() - scords[0];
            float y = ev.getRawY() + v.getTop() - scords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                KeyboardUtil.hideSoftKeyboard(LoginActivity.this);
        }
        return super.dispatchTouchEvent(ev);
    }


    private void callApi(final String userName, String password) {
        if(userName.equals(AppConstants.DEMO_USER) && password.equals(AppConstants.DEMO_PASS)){
            startScanner();
        }else{
            Toast.makeText(activity, "Invalid username password!", Toast.LENGTH_SHORT).show();
        }
    }



    private void startScanner() {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Intent qInt = new Intent(activity, QrCodeActivity.class);
                            startActivityForResult(qInt, AppConstants.REQUEST_CODE_QR_SCAN);
                        } else {
                            android.widget.Toast.makeText(activity, getString(R.string.permission_denied), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }

                });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_QR_SCAN) {

            qrCodePresenter.doScan(data); // Qr Code Test

            if (resultCode == Activity.RESULT_OK && data != null) {
                //Getting the passed result
                String result = data.getStringExtra(AppConstants.GOT_RESULT);
                if (result != null && !TextUtils.isEmpty(result)) {
                    String url = AppConstants.BASE_URL + result.trim();
                    if(HTTPClient.isNetworkAvailable(activity)) {
                        callApi(url);
                    }else{
                        Toast.makeText(activity, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            } 
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                verifyDataLogin();
                break;
        }
    }

    @Override
    public void showErrorMessageForUsernamePassword() {
        Snackbar.make(ll_login, "Please check Username or Password", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessageForMaxLoginAttempt() {
        Snackbar.make(ll_login, "You have exceeded MAX attempt", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginSuccessMessage() {
        Snackbar.make(ll_login, "Login Successful.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessageForInvalidQrCode() {
        Snackbar.make(ll_login, "Please check QR Code", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessageForMaxScanAttempt() {
        Snackbar.make(ll_login, "You have exceeded MAX attempt", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showQrCodeSuccessMessage() {
        Snackbar.make(ll_login, "QR Code Scanned Successfully", Snackbar.LENGTH_SHORT).show();
    }

    /** REST API CALLS **/

    private void callApi(String url) {
        try {
            if (sDialog != null && !sDialog.isShowing()) {
                sDialog.show();
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            client.newCall(HTTPClient.getRequest(url, null, false)).enqueue(new Callback() {
                Handler mainHandler = new Handler(activity.getMainLooper());

                @Override
                public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sDialog != null && sDialog.isShowing()) {
                                sDialog.dismiss();
                            }
                            Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response)
                        throws IOException {
                    final boolean success = response.isSuccessful();
                    final String responseStr = response.body().string();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sDialog != null && sDialog.isShowing()) {
                                sDialog.dismiss();
                            }
                            if (response.code() == 401) {
                                Toast.makeText(activity, "un-authorized access", Toast.LENGTH_SHORT).show();
                            } else if (success) {
                                try {
                                    handleResponse(responseStr);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }
            });

        } catch (Exception e) {
            if (sDialog != null && sDialog.isShowing()) {
                sDialog.dismiss();
            }
            e.printStackTrace();
        }

    }

    private void handleResponse(String responseStr) {
        Intent orderInt = new Intent(activity, OrderDetailsActivity.class);
        orderInt.putExtra(AppConstants.RESPONSE, responseStr);
        startActivity(orderInt);
    }


    /** REST API CALLS **/

}
