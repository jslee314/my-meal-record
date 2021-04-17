package com.example.mymealrecord.util;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymealrecord.camera.CameraViewModel;
import com.example.mymealrecord.MainViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;
    private Application mApplication;

    public ViewModelFactory(Application application) {
        mApplication = application;
    }
    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }

    /**
    * @내용  : 요청들어온 modelClass 에 맞는 ViewModel 객체를 생성해서 리턴시켜주는 함수 (싱글톤으로 처리)
    * @작성일 : 2021/04/17
    * @작성자 : 이재선
    **/
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(CameraViewModel.class)) {
            return (T) new CameraViewModel(mApplication);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }


}
