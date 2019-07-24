package com.techwright.demo.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Created by RJ Chakraborty on 23-07-2019.
 */

public class InputValidation {

    private Context context;
    public InputValidation(Context context){
        this.context = context;
    }

    public boolean isFilled(AppCompatTextView textView, String value, String message){
        if(TextUtils.isEmpty(value)){
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
            return true;
        }else{
           textView.setVisibility(View.GONE);
            return false;
        }
    }

    public boolean isFilled(TextInputLayout textInputLayout, String value, String message){
        if(TextUtils.isEmpty(value)){
            textInputLayout.setError(message);
            return true;
        }else{
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            return false;
        }
    }

    public boolean isEmailValid(AppCompatTextView textView, String value, String message){
        if(!Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()){
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
            return true;
        }else{
            textView.setVisibility(View.GONE);
            return false;
        }
    }
    public boolean isEmailValid(TextInputLayout textInputEmailLayout, String value, String message){
        if(!Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()){
            textInputEmailLayout.setError(message);
            return true;
        }else{
            textInputEmailLayout.setError(null);
            textInputEmailLayout.setErrorEnabled(false);
            return false;
        }
    }

    public boolean isMobileValid(AppCompatTextView textView, String value, String message){
        String PHONE_PATTERN = "[1-9][0-9]{9}";
        if(!Pattern.compile(PHONE_PATTERN).matcher(value.trim()).matches()){
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
            return true;
        }else{
            textView.setVisibility(View.GONE);
            return false;
        }
    }

    public boolean isPasswordValid(AppCompatTextView textView, String password, String message) {

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[`~!@#$%^&*()-_+=])(?=\\S+$).{8,}$";
        if(!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()){
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
            return true;
        }else{
            textView.setVisibility(View.GONE);
            return false;
        }
    }

    public boolean isPasswordValid(TextInputLayout textInputLayout, String password, String message) {

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[`~!@#$%^&*()-_+=])(?=\\S+$).{8,}$";
        if(!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()){
            textInputLayout.setError(message);
            return true;
        }else{
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            return false;
        }
    }

    public boolean isValueSame(AppCompatTextView textView, String value1, String value2, String message) {
        if(!value1.equalsIgnoreCase(value2)){
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
            return true;
        }else{
            if(TextUtils.isEmpty(value1)){
                textView.setVisibility(View.VISIBLE);
                textView.setText(message);
                return true;
            }else {
                textView.setVisibility(View.GONE);
                return false;
            }
        }
    }

    public boolean isValueSame(TextInputLayout textInputLayout, String value1, String value2, String message) {
        if(!value1.equalsIgnoreCase(value2)){
            textInputLayout.setError(message);
            return true;
        }else{
            if(TextUtils.isEmpty(value1)){
                textInputLayout.setError(message);
                return true;
            }else {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                return false;
            }
        }
    }
}
