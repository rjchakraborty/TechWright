package com.techwright.demo;

import android.app.Activity;
import android.content.Intent;

import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.test.listeners.QrCodeView;
import com.techwright.demo.test.presenter.QrCodePresenter;
import com.techwright.demo.test.presenter.QrCodePresenter;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class QrCodePresenterTest {

    @Test
    public void checkIfScanAttemptIsExceeded() {
        QrCodeView qrCodeView = mock(QrCodeView.class);
        QrCodePresenter qrCodePresenter = new QrCodePresenter(qrCodeView);
        Assert.assertEquals(1, qrCodePresenter.incrementScanAttempt());
        Assert.assertEquals(2, qrCodePresenter.incrementScanAttempt());
        Assert.assertEquals(3, qrCodePresenter.incrementScanAttempt());
        Assert.assertTrue(qrCodePresenter.isScanAttemptExceeded());

    }

    @Test
    public void checkIfScanAttemptIsNotExceeded() {
        QrCodeView qrCodeView = mock(QrCodeView.class);
        QrCodePresenter qrCodePresenter = new QrCodePresenter(qrCodeView);
        Assert.assertFalse(qrCodePresenter.isScanAttemptExceeded());
    }

    @Test
    public void checkQrCodeScanResultIsCorrect() {
        QrCodeView qrCodeView = mock(QrCodeView.class);
        QrCodePresenter qrCodePresenter = new QrCodePresenter(qrCodeView);
        qrCodePresenter.doScan(getQrIntent("30d7d828-a737-11e9-bb9c-42010aa00005"));
        verify(qrCodeView).showQrCodeSuccessMessage();
    }

    private Intent getQrIntent(String resultString){
        Intent data = new Intent();
        data.putExtra(AppConstants.GOT_RESULT, resultString);
        return data;
    }

    @Test
    public void checkQrCodeScanResultIsInCorrect() {
        QrCodeView QrCodeView = mock(QrCodeView.class);
        QrCodePresenter qrCodePresenter = new QrCodePresenter(QrCodeView);
        qrCodePresenter.doScan(getQrIntent("30d"));
        qrCodePresenter.doScan(getQrIntent("30d7d828"));
        qrCodePresenter.doScan(getQrIntent(""));
        qrCodePresenter.doScan(getQrIntent("30d7d828"));
        verify(QrCodeView).showErrorMessageForInvalidQrCode();

    }
}
