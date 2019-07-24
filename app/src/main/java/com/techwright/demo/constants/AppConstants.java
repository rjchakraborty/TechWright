package com.techwright.demo.constants;


public interface AppConstants {

    String DEMO_USER = "admin@techwright.com";
    String DEMO_PASS = "admin@123456";

    String BASE_URL = "http://poneglyph.techwright.io/api/v1/orders/";

    int REQUEST_CODE_QR_SCAN = 112;

    String RESPONSE = "response";
    String GOT_RESULT = "com.techwright.got_qr_scan_relult";
    String ERROR_DECODING_IMAGE = "com.at.teaerp.error_decoding_image";
    String LOGTAG = "QRScannerQRCodeActivity";

    String ORDER_COMPLETED = "order_completed";
    String ORDER_FAILED = "order_failed";
}
