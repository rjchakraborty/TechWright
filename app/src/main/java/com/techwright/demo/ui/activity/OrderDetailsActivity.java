package com.techwright.demo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.techwright.demo.R;
import com.techwright.demo.adapter.OrderAdapter;
import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.model.Order;
import com.techwright.demo.model.OrderedProduct;
import com.techwright.demo.util.HTTPClient;
import com.techwright.demo.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity activity = OrderDetailsActivity.this;
    private AppCompatImageView btn_back;
    private RecyclerView proRecycler;
    private List<OrderedProduct> proList;
    private OrderAdapter adapter;
    private AppCompatTextView tv_order_id;
    private CardView cv_price_details;
    private AppCompatTextView tv_price_title, tv_total_price, tv_tax, tv_amount_payable, tv_order_date, tv_store_name, tv_order_status;
    private AppCompatImageView img_order_status;
    private AppCompatButton btn_verify;
    private AlertDialog sDialog;
    private Order order;
    private String orderStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        initViews();
        initListeners();
        proList = new ArrayList<>();
        orderStr = getIntent().getStringExtra(AppConstants.RESPONSE);

        if (orderStr != null) {

            sDialog = new SpotsDialog.Builder()
                    .setContext(activity)
                    .setMessage(R.string.please_wait)
                    .setCancelable(false)
                    .build();

            //set recyclerView adapter first
            adapter = new OrderAdapter(proList, activity);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
            proRecycler.setLayoutManager(mLayoutManager);
            proRecycler.setItemAnimator(new DefaultItemAnimator());
            proRecycler.setAdapter(adapter);

            loadData();

        } else {
            Toast.makeText(activity, getString(R.string.error_retrieve_order), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Load the Order data and populate it in current layout UI
     */

    private void loadData() {
        try {
            JSONObject rootObject = new JSONObject(orderStr);

            JSONObject orderObject = rootObject.optJSONObject("order");
            if (orderObject != null) {
                order = new Order(orderObject);

                if (order != null) {
                    tv_order_id.setText(order.getOrder_no());
                    cv_price_details.setVisibility(View.VISIBLE);
                    tv_price_title.setText(String.format("Price(%s item)", order.getQuantity()));
                    tv_tax.setText(String.format("%s %s", order.getTotal_tax(), order.getCurrency()));
                    tv_total_price.setText(String.format("%s %s", order.getGross_amount(), order.getCurrency()));
                    tv_store_name.setText(order.getStore());
                    setOrderStatus(order.getStatus());
                    tv_amount_payable.setText(String.format("%s %s", order.getNet_amount(), order.getCurrency()));
                    tv_order_date.setText(order.getCreated_at());

                }
                JSONArray itemArray = orderObject.optJSONArray("items");

                if (itemArray != null && itemArray.length() > 0) {
                    clearList();
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject itemObject = itemArray.optJSONObject(i);

                        if (itemObject != null) {
                            JSONObject item = itemObject.optJSONObject("item");
                            if (item != null) {
                                OrderedProduct orderedProduct = new OrderedProduct(item, orderObject.optString("currency"));
                                proList.add(orderedProduct);
                            }
                        }
                    }
                    updateView();
                }
            }


        } catch (Exception e) {
            Toast.makeText(activity, getString(R.string.error_retrieve_order), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void setOrderStatus(String status) {
        switch (status) {
            case AppConstants.ORDER_COMPLETED:
                img_order_status.setImageResource(R.drawable.ic_check_circle);
                tv_order_status.setText(getString(R.string.completed));
                break;
            case AppConstants.ORDER_FAILED:
                img_order_status.setImageResource(R.drawable.ic_remove_circle);
                tv_order_status.setText(getString(R.string.failed));
                break;
        }
    }

    private void updateView() {
        if (!proRecycler.isComputingLayout()) {
            adapter.notifyDataSetChanged();
        }
    }


    private void clearList() {
        if (proList != null && !proList.isEmpty()) {
            proList.clear();
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * Bind UI Views
     */
    private void initViews() {
        proRecycler = findViewById(R.id.cartView);
        btn_back = findViewById(R.id.btn_back);
        btn_verify = findViewById(R.id.btn_verify);
        tv_order_id = findViewById(R.id.tv_order_id);
        tv_store_name = findViewById(R.id.tv_store_name);
        tv_order_date = findViewById(R.id.tv_order_date);
        tv_price_title = findViewById(R.id.tv_price_title);
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_tax = findViewById(R.id.tv_tax);
        img_order_status = findViewById(R.id.img_order_status);
        tv_order_status = findViewById(R.id.tv_order_status);
        tv_amount_payable = findViewById(R.id.tv_amount_payable);
        cv_price_details = findViewById(R.id.cv_price_details);
    }


    /**
     * Init onClick Listeners
     */
    private void initListeners() {
        btn_back.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_verify:
                if(StringUtil.checkIfNull(order.getTracking_id())) {
                    String url = AppConstants.BASE_URL + order.getTracking_id().trim() +"/verify";
                    if (HTTPClient.isNetworkAvailable(activity)) {
                        callVerifyApi(url);
                    } else {
                        Toast.makeText(activity, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }


    /**
     * REST API CALLS
     **/

    private void callVerifyApi(String url) {
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
        if(StringUtil.checkIfNull(responseStr)){
            btn_verify.setText(getString(R.string.verified));
        }
    }


    /** REST API CALLS **/

}
