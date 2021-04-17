package com.example.mymealrecord.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import javax.annotation.Nullable;

public abstract class CustomBindingAdapter {


    /**
    * @내용  :  bitmap 가 있으면 imageView 에 설정 하고
     *      *          없으면 defaultResourceId 를 imageView 에 설정 한다.
    * @작성일 : 2020/12/17
    * @작성자 : 이재선
    **/
    @BindingAdapter(value = {"bind:setBitmap", "bind:setDefaultResourceId"}, requireAll = false)
    public static void setBitmap(ImageView imageView, @Nullable Bitmap bitmap, int defaultResourceId) {
        if (bitmap == null) {
            if (defaultResourceId <= 0) return;
            imageView.setImageResource(defaultResourceId);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

}
