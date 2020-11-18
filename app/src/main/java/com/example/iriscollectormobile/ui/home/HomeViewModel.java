package com.example.iriscollectormobile.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.iriscollectormobile.data.SessionVariable;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hi " + SessionVariable.username+", This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}