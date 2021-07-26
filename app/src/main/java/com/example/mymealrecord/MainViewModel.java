package com.example.mymealrecord;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.mymealrecord.data.SessionVariable;
import com.example.mymealrecord.data.Meals;
import com.example.mymealrecord.data.UserHistoryAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> homeText;
    private MutableLiveData<String> dashboardText;
    private MutableLiveData<String> settingsText;

    // User 관련 멤벼변수
    private String userName;

    // UserHistory 관련 멤버변수
    Meals mMeals;
    private MutableLiveData<Bitmap> homeIrisImageBitmap;

    private Uri irisFirebaseStorageUri;
    public UserHistoryAdapter userHistoryAdapter;

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

    /** (CASE 1) :사용자가 로그인 한 경우 **/
    public void onSignedInInitialize(String userName){
        setUserName(userName);
        DataSet();
        attachDatabaseReadListener();
    }

    /** (CASE 2) : 사용자가 로그아웃 한 경우 **/
    public void onSignedOutCleanup(){
        // user를 익명으+로 바꾸고, 메시지(어뎁터)를 지우고, db데이터베이스와 연결된 이벤트 리스너 해지
        userName = "anonymous";
        getUserHistoryAdapter().clear();    // mMessageAdapter를 지우지 않으면 로그인,아웃할때 중복메시지가 나올수 있는 버그가 발생함
        detachDatabaseReadListener();
    }


    public void initFirebaseDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();     // realtime db관련 클래스의 인스턴스
//        mUserHistoryDatabaseReference = mFirebaseDatabase.getReference().child("UserHistoryData");
    }

    public void initFirebaseStorage(){
        mFirebaseStorage = FirebaseStorage.getInstance();       // Storage관련 클래스의 인스턴스
        mIrisPhogosStorageReference = mFirebaseStorage.getReference().child("meal_photos"); // 콘솔에 만든 파일(컬렉션) 이름: "meal_photos"
    }

    public void DataSet(){
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        mUserHistoryDatabaseReference = mFirebaseDatabase.getReference().child("user").child(uid).child("UserHistoryData");

        mFirebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
            // 위의 함수는 전부 비동기 처리가 이루어지기 때문에 전부 이벤트가 발생하는 시점에서 이 함수를 써주셔야 합니다 앞으로 지겹게 볼 예정입니다
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //데이터 변경이 감지가 되면 이 함수가 자동으로 콜백됨, 이때 dataSnapashot 는 값을 내려 받을떄 사용함으로 지금은 쓰지 않습니다
                Log.d("addValueEventListener", "onDataChange");
                mUserHistoryDatabaseReference = mFirebaseDatabase.getReference().child("user").child(uid).child("UserHistoryData");

                //RealTimeDB는 기본적으로 parent , child , value 값으로 이루어져 있습니다 지금은 최초로 로그인한 사람의
                //색인을 만들고자 지금과 같은 작업을 하는 중입니다 즉 처음 들어오는 사람에게 DB자리를 내준다고 생각하시면됩니다
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // RealTimeDB와 통신 에러 등등 데이터를 정상적으로 받지 못할때 콜백함수로서 이곳으로 들어옵니다
            }
        });
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
                    Meals userHistory = snapshot.getValue(Meals.class);
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
    /**
     * 데이터베이스에서 읽어온 후 실시간 DB에 넣기 위한 Chlild 이벤트리스너 생성
     * @author 이재선
     * @date 2020-11-13 오전 11:03     **/
    public void downloadFirebaseStorage(){
        // Firebase Storage에 파일을 업로드 한 파일을 다운로드해서 friendlyMessage에 저장하기
        mPhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy.MM.dd(HH:mm:ss)", Locale.KOREA);
                String date =  mDateFormat.format(new Date());

                /**
                 *     String mealImageUrl;
                 *     String mealTime;
                 *     String foodMemo;
                 *     Integer Calorie;
                 *     Integer Rating;
                 *     String acquisitionDate;
                 */
                Log.d("jjslee", "getFoodMemo" + mMeals.getFoodMemo());
                Log.d("jjslee", "getCalorie" + mMeals.getCalorie());
                Log.d("jjslee", "getRating" + mMeals.getRating());

                Meals userHistory = new Meals(
                        uri.toString(),
                        SessionVariable.mealTime,
                        mMeals.getFoodMemo(),
                        mMeals.getCalorie(),
                        mMeals.getRating(),
                        date);
                mUserHistoryDatabaseReference.push().setValue(userHistory);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("jjslee", "exception: "+ exception.toString());
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
