package com.example.iriscollectormobile;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.iriscollectormobile.data.SessionVariable;
import com.example.iriscollectormobile.ui.dashboard.UserHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> homeText;
    private MutableLiveData<String> dashboardText;
    private MutableLiveData<String> settingsText;

    private MutableLiveData<Bitmap> homeIrisImageBitmap;
    private MutableLiveData<UserHistoryAdapter> userHistoryAdapter;

    private String userName;



//    /** ((realtime database))를 사용하기위한 2개의 클래스의 인스턴스 변수 선언 **/
//    public FirebaseDatabase mFirebaseDatabase;                 // 앱이 db 접근하기위한 진입점
//    public DatabaseReference mUserHistoryDatabaseReference;    // db 참조개체(db의 특정 부분을 참조하는 클래스)
//    public ChildEventListener mChildEventListener;             // (리스너)사용자의 데이터가 변경될때 트리거됨 realtimeDatabase
//
//    /** ((Storage))을 사용하기위한 클래스의 인스턴스 변수 선언 **/
//    public FirebaseStorage mFirebaseStorage;
//    public StorageReference mIrisPhogosStorageReference;

    /** ((authentication))을 사용하기위한 클래스의 인스턴스 변수 선언 **/
    public FirebaseAuth mFirebaseAuth;    // 인증상태
    public FirebaseAuth.AuthStateListener mAuthStateListener;  // (리스너)사용자 인증상태가 변경될 때(로그인/아웃) 트리거됨



    public MainViewModel(@NonNull Application application) {
        super(application);
        homeText = new MutableLiveData<>();
        dashboardText = new MutableLiveData<>();
        settingsText = new MutableLiveData<>();

        homeIrisImageBitmap = new MutableLiveData<>();
        userHistoryAdapter = new MutableLiveData<>();


        homeText.setValue("홍채 촬영 후 'SUBMIT'클릭");
        dashboardText.setValue("This is dashboard fragment");
        settingsText.setValue(userName + "님 개인정보를 추가 입력하세요");

    }

    /** (CASE 1) :사용자가 로그인 한 경우 **/
    public void onSignedInInitialize(){


    }

    /** (CASE 2) : 사용자가 로그아웃 한 경우 **/
    public void onSignedOutCleanup(){
        // user를 익명으로 바꾸고, 메시지(어뎁터)를 지우고, db데이터베이스와 연결된 이벤트 리스너 해지
        userName = "anonymous";
        getUserHistoryAdapter().getValue().clear();    // mMessageAdapter를 지우지 않으면 로그인,아웃할때 중복메시지가 나올수 있는 버그가 발생함
    }


}
