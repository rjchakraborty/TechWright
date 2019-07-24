package com.techwright.demo.adapter;

/*
 * Created by user on 16-04-2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.techwright.demo.R;
import com.techwright.demo.constants.AppConstants;
import com.techwright.demo.model.OrderedProduct;
import com.techwright.demo.modules.glide.GlideApp;
import com.techwright.demo.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by RJ Chakraborty on 23-07-2019.
 */

/**
 * Handle the OrderProduct Model to bind it with Recycler Viewholder UI
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<OrderedProduct> proList = new ArrayList<>();
    private Context context;
    private class ProductHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView thumbnail;
        private AppCompatTextView tv_product_title, tv_price, tv_product_brand, tv_desc;

        private ProductHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            tv_product_title = view.findViewById(R.id.tv_product_title);
            tv_price = view.findViewById(R.id.tv_price);
            tv_product_brand = view.findViewById(R.id.tv_product_brand);
            tv_desc = view.findViewById(R.id.tv_desc);
        }
    }


    public OrderAdapter(List<OrderedProduct> proList, Context context) {
        this.proList = proList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent, false);
        return new ProductHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final OrderedProduct product = proList.get(position);
        if (product != null) {
            ((ProductHolder) holder).tv_product_title.setText(product.getProduct_name());
            ((ProductHolder) holder).tv_product_brand.setText(product.getBrand_name());
            ((ProductHolder) holder).tv_price.setText(String.format("%s %s", product.getMrp(), product.getCurrency()));


            StringBuilder productDesc = new StringBuilder();
            if(StringUtil.checkIfNull(product.getColour_name())) {
                productDesc.append(product.getColour_name());
                productDesc.append(", ");
            }

            if(StringUtil.checkIfNull(product.getSize())) {
                productDesc.append(product.getSize());
                productDesc.append(", ");
            }

            if(StringUtil.checkIfNull(product.getCollection())) {
                productDesc.append(product.getCollection());
            }

            ((ProductHolder) holder).tv_desc.setText(productDesc);

            String productImage = product.getDisplay_image();


            GlideApp.with(context)
                    .load(productImage)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .thumbnail(0.5f)
                    .centerInside()
                    .into(((ProductHolder) holder).thumbnail);


        }

    }



    @Override
    public int getItemCount() {
        return proList == null ? 0 : proList.size();
    }


}