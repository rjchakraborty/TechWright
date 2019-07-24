package com.techwright.demo.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HTTPClient {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    public static Request getRequest(String url, String parameters, boolean isPOST){

        Request.Builder builder =   new Request.Builder();
        builder.url(url);
        if(isPOST && parameters != null) {
            RequestBody body = RequestBody.create(MEDIA_TYPE, parameters);
            builder.post(body);
        }else{
            builder.get();
        }


        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("cache-control", "no-cache");

        return  builder.build();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
