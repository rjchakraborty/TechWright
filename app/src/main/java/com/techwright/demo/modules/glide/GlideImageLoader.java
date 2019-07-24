package com.techwright.demo.modules.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.techwright.demo.helper.CircularProgressBar;


public class GlideImageLoader {

    private AppCompatImageView mImageView;
    private CircularProgressBar mProgressBar;
    private Context context;

    private GlideImageLoader(AppCompatImageView imageView, CircularProgressBar progressBar, Context context) {
        this.mImageView = imageView;
        this.mProgressBar = progressBar;
    }


    public static GlideImageLoader with(Context context) {
        return new Builder(context).build();
    }

    public static class Builder {
        private AppCompatImageView mImageView;
        private CircularProgressBar mProgressBar;
        private Context mContext;

        public Builder(@NonNull Context context) {
            this.mContext = context;
        }

        public Builder imageview(AppCompatImageView imageView) {
            this.mImageView = imageView;
            return this;
        }

        public Builder progress(CircularProgressBar progressBar) {
            this.mProgressBar = progressBar;
            this.mProgressBar.setIndeterminate(false);
            return this;
        }


        public GlideImageLoader build() {
            return new GlideImageLoader(mImageView, mProgressBar, mContext);
        }

    }


    public void load(final String url, final RequestOptions options) {
        if (url == null || options == null) return;

        onConnecting();

        //set Listener & start
        CustomGlideModule.expect(url, new CustomGlideModule.UIonProgressListener() {
            @Override
            public void onProgress(long bytesRead, long expectedLength) {
                if (mProgressBar != null && expectedLength > 0) {
                    mProgressBar.setProgress((int) (100 * bytesRead / expectedLength));
                }
            }

            @Override
            public float getGranualityPercentage() {
                return 1.0f;
            }
        });


        //Get Image
        GlideApp.with(mImageView.getContext())
                .load(url)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        CustomGlideModule.forget(url);
                        onFinished();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        CustomGlideModule.forget(url);
                        onFinished();
                        return false;
                    }
                }).into(mImageView);
    }


    private void onConnecting() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    private void onFinished() {
        if (mProgressBar != null && mImageView != null) {
            mProgressBar.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        }
    }
}