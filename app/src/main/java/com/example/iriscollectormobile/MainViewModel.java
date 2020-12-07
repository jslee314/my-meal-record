package com.example.iriscollectormobile;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.iriscollectormobile.data.SessionVariable;
import com.example.iriscollectormobile.data.UserHistory;
import com.example.iriscollectormobile.data.UserHistoryAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> homeText;
    private MutableLiveData<String> dashboardText;
    private MutableLiveData<String> settingsText;

    private MutableLiveData<Bitmap> homeIrisImageBitmap;

    // User 관련 멤벼변수
    private String userName;
    public UserHistoryAdapter userHistoryAdapter;

    // UserHistory 관련 멤버변수
    private String side;
    private Bitmap irisBitmap;
    private Uri irisFirebaseStorageUri;


    /** ((authentication))을 사용하기위한 클래스의 인스턴스 변수 선언 **/
    public FirebaseAuth mFirebaseAuth;    // 인증상태
    public FirebaseAuth.AuthStateListener mAuthStateListener;  // (리스너)사용자 인증상태가 변경될 때(로그인/아웃) 트리거됨

    /** ((realtime database))를 사용하기위한 2개의 클래스의 인스턴스 변수 선언 **/
    public FirebaseDatabase mFirebaseDatabase;                 // 앱이 db 접근하기위한 진입점
    public DatabaseReference mUserHistoryDatabaseReference;    // db 참조개체(db의 특정 부분을 참조하는 클래스)
    public ChildEventListener mChildEventListener;             // (리스너)사용자의 데이터가 변경될때 트리거됨 realtimeDatabase

    /** ((Storage))을 사용하기위한 클래스의 인스턴스 변수 선언 **/
    public FirebaseStorage mFirebaseStorage;
    public StorageReference mIrisPhogosStorageReference;
    public StorageReference mPhotoRef;

    public MainViewModel(@NonNull Application application) {
        super(application);
        homeText = new MutableLiveData<>();
        dashboardText = new MutableLiveData<>();
        settingsText = new MutableLiveData<>();

        homeIrisImageBitmap = new MutableLiveData<>();


    }
    public void initHomeFragment(){
        homeText.setValue("홍채 촬영 후 'SUBMIT'클릭");
    }
    public void initDashboardFragment(){
        dashboardText.setValue("This is dashboard fragment");
    }
    public void initSettingFragment(){
        settingsText.setValue(userName + "님 개인정보를 추가 입력하세요");
    }
    public void initFirebaseDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();     // realtime db관련 클래스의 인스턴스
        mUserHistoryDatabaseReference = mFirebaseDatabase.getReference().child("UserHistoryData");

    }
    public void initFirebaseStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();       // Storage관련 클래스의 인스턴스
        mIrisPhogosStorageReference = mFirebaseStorage.getReference().child("iris_photos");
    }


    /** (CASE 1) :사용자가 로그인 한 경우 **/
    public void onSignedInInitialize(String userName){
        setUserName(userName);

        attachDatabaseReadListener();

    }
    /** (CASE 2) : 사용자가 로그아웃 한 경우 **/
    public void onSignedOutCleanup(){
        // user를 익명으로 바꾸고, 메시지(어뎁터)를 지우고, db데이터베이스와 연결된 이벤트 리스너 해지
        userName = "anonymous";
        getUserHistoryAdapter().clear();    // mMessageAdapter를 지우지 않으면 로그인,아웃할때 중복메시지가 나올수 있는 버그가 발생함
        detachDatabaseReadListener();
    }

    /**
     * 데이터베이스에 쓰기위해 Chlild 이벤트리스너 생성
     * @author 이재선
     * @date 2020-11-13 오전 11:03     **/
    public void attachDatabaseReadListener(){
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // 새 메시지가 삽입될때마다 호출. 리스너를 처음 연결할때 모든 Child 메시지에대해 이 메서드가 호출됨
                    UserHistory userHistory = snapshot.getValue(UserHistory.class);
                    getUserHistoryAdapter().add(userHistory);
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // 기존 메시지의 내용이 변경될때 호출
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    // 기존 메시지가 삭제될때 호출
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // 기존 메시지의 목록에서의 위치가 변경될때 호출
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 오류가 발행했을때 호출, 주로 권한이 없을때
                }
            };
            mUserHistoryDatabaseReference.addChildEventListener(mChildEventListener); // 정확히 내가 뭘 듣고있는지 정의
        }
    }
    public void downloadFirebaseStorage(){
        // Firebase Storage에 파일을 업로드 한 파일을 다운로드해서 friendlyMessage에 저장하기
        mPhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String side = getSide();
                UserHistory userHistory = new UserHistory("test1", "side", uri.toString(), "2020.05.02");
                mUserHistoryDatabaseReference.push().setValue(userHistory);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    /**
     * Chlild 이벤트리스너 해지
     * @author 이재선
     * @date 2020-11-13 오전 11:04   **/
    public void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mUserHistoryDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


}
