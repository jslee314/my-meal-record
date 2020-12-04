package com.example.iriscollectormobile;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.iriscollectormobile.Camera.CameraActivity;
import com.example.iriscollectormobile.Camera.CameraViewModel;

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
     * @작성자 : 길용현
     * @최초작성일 : 2019-09-03 오후 5:57
     * @내용 : 요청들어온 modelClass 에 맞는 ViewModel 객체를 생성해서 리턴시켜주는 함수 (싱글톤으로 처리)
     * @수정 :
     * @버젼 : 1.0.0
     **/
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(CameraActivity.class)) {
            return (T) new CameraViewModel(mApplication);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }


}
