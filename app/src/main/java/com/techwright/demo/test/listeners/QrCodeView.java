package com.techwright.demo.test.listeners;

public interface QrCodeView {
    void showErrorMessageForInvalidQrCode();
    void showErrorMessageForMaxScanAttempt();
    void showQrCodeSuccessMessage();
}
