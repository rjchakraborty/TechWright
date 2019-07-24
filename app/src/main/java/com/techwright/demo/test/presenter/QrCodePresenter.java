package com.techwright.demo.test.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.test.listeners.QrCodeView;
import com.techwright.demo.util.StringUtil;

public class QrCodePresenter {

    private static final int MAX_SCAN_ATTEMPT = 3;
    private final QrCodeView qrCodeView;
    private int scanAttempt;

    public QrCodePresenter(QrCodeView qrCodeView){
        this.qrCodeView = qrCodeView;
    }

    public int incrementScanAttempt(){
        scanAttempt += 1;
        return scanAttempt;
    }

    public boolean isScanAttemptExceeded(){
        return scanAttempt >= MAX_SCAN_ATTEMPT;
    }

    public void doScan(Intent intent){
        if(isScanAttemptExceeded()){
            qrCodeView.showErrorMessageForMaxScanAttempt();
            return;
        }

        if(intent != null && intent.getStringExtra(AppConstants.GOT_RESULT) != null && StringUtil.checkIfNull(intent.getStringExtra(AppConstants.GOT_RESULT))){
            qrCodeView.showQrCodeSuccessMessage();
            return;
        }

        // increment login attempt if it's fail
        incrementScanAttempt();
        qrCodeView.showErrorMessageForInvalidQrCode();
    }
}
