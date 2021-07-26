package com.example.mymealrecord.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.bumptech.glide.RequestManager;

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

    @BindingAdapter(value = {"rating"})
    public static void setRating(RatingBar view, float value) {
        if (view == null) {
            return;
        }
        float rating = view.getRating();
        if (rating == value) {
            return;
        }
        //model->view
        view.setRating(value);
    }

//    @BindingAdapter(value = {"onRatingChanged"}, requireAll = false)
//    public static void setListeners(RatingBar view, final RatingBar.OnRatingBarChangeListener listener,
//                                    final InverseBindingListener ratingChange) {
//        if (ratingChange == null) {
//            view.setOnRatingBarChangeListener(listener);
//        } else {
//            view.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                @Override
//                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                    if (listener != null) {
//                        listener.onRatingChanged(ratingBar, rating, fromUser);
//                    }
//                    ratingChange.onChange();
//                }
//            });
//        }
//    }
}
