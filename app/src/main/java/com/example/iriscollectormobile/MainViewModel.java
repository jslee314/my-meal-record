package com.example.iriscollectormobile;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.iriscollectormobile.data.SessionVariable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> homeText;
    private MutableLiveData<String> dashboardText;
    private MutableLiveData<String> notificationsText;

    private MutableLiveData<Bitmap> irisImageBitmap = new MutableLiveData<>();


    public MainViewModel(@NonNull Application application) {
        super(application);
        homeText = new MutableLiveData<>();
        homeText.setValue("Hi " + SessionVariable.username+", This is home fragment");

        dashboardText = new MutableLiveData<>();
        dashboardText.setValue("This is dashboard fragment");

        notificationsText = new MutableLiveData<>();
        notificationsText.setValue("This is notifications fragment");

    }


}
