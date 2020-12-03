package com.example.iriscollectormobile.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.iriscollectormobile.Camera.CameraActivity;
import com.example.iriscollectormobile.MainActivity;
import com.example.iriscollectormobile.MainViewModel;
import com.example.iriscollectormobile.R;
import com.example.iriscollectormobile.data.ConstantVariable;
import com.example.iriscollectormobile.data.SessionVariable;
import com.example.iriscollectormobile.data.UserHistory;
import com.example.iriscollectormobile.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private MainViewModel mMainViewModel;
    private FragmentHomeBinding binding;

    // UserHistory 관련 멤버변수
    private String mSide;
    private Bitmap mIrisBitmap;
    private Uri mIrisFirebaseStorageUri;

    View.OnClickListener onClickListener;

    /** ((realtime database))를 사용하기위한 2개의 클래스의 인스턴스 변수 선언 **/
    private FirebaseDatabase mFirebaseDatabase;                 // 앱이 db 접근하기위한 진입점
    private DatabaseReference mUserHistoryDatabaseReference;    // db 참조개체(db의 특정 부분을 참조하는 클래스)
    private ChildEventListener mChildEventListener;             // (리스너)사용자의 데이터가 변경될때 트리거됨 realtimeDatabase

    /** ((Storage))을 사용하기위한 클래스의 인스턴스 변수 선언 **/
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mIrisPhogosStorageReference;
    StorageReference mPhotoRef;
    Button mBtnLeftEye;
    Button mBtnRightEye;
    Button mBtnSubmit;
    ImageView mImViewIris;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mMainViewModel = MainActivity.obtainViewModel(requireActivity());

        binding.setViewModel(mMainViewModel);
        binding.setLifecycleOwner(requireActivity());

        mFirebaseDatabase = FirebaseDatabase.getInstance();     // realtime db관련 클래스의 인스턴스
        mFirebaseStorage = FirebaseStorage.getInstance();       // Storage관련 클래스의 인스턴스

        mUserHistoryDatabaseReference = mFirebaseDatabase.getReference().child("UserHistoryData");
        mIrisPhogosStorageReference = mFirebaseStorage.getReference().child("iris_photos");

        final TextView textView = binding.textHome;
        mMainViewModel.getHomeText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        mBtnLeftEye = binding.cameraButtonLeft;
//        mBtnLeftEye.setOnClickListener(getOnClickListener());
        mBtnLeftEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSide = "left";
                Intent intent = new Intent(getActivity(),CameraActivity.class);
//                startActivity(intent);
                startActivityForResult(intent, ConstantVariable.REQUEST_CAMERA);
            }
        });

        mBtnRightEye = binding.cameraButtonRight;
        mBtnRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSide = "right";
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivityForResult(intent, ConstantVariable.REQUEST_CAMERA);
            }
        });

        mBtnSubmit = binding.submitButton;
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadFirebaseStorage();
            }
        });

        mImViewIris = binding.irisImg;
//        mImViewIris.setImageBitmap(SessionVariable.irisImage);

        attachDatabaseReadListener();
        return binding.getRoot();

    }

    /**
     * 사진 파일을 Firebase Storage에 파일을 업로드
     * @author 이재선
     * @date 2020-11-20 오후 6:52   **/
    public void uploadFirebaseStorage(){
        Log.d(TAG, "uploadFirebaseStorage");

        Uri irisUri = getImageUri(getActivity().getApplicationContext(), SessionVariable.irisImage);

        // chat_photos/<FILENAME>에 저장할 파일의 레퍼런스 가져옴
        mPhotoRef = mIrisPhogosStorageReference.child(irisUri.getLastPathSegment());

        // 파일을 Firebase Storage에 파일을 업로드 함
        mPhotoRef.putFile(irisUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Storage에 파일을 업로드 함");
                downloadFirebaseStorage();
            }
        });
        Log.d(TAG, "uploadFirebaseStorage  끝");


    }
    void downloadFirebaseStorage(){
        Log.d(TAG, "downloadFirebaseStorage");

        // Firebase Storage에 파일을 업로드 한 파일을 다운로드해서 friendlyMessage에 저장하기
        mPhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "uri 받아옴" + uri.toString());

                UserHistory userHistory = new UserHistory(SessionVariable.username, mSide, uri.toString(), null);
                mUserHistoryDatabaseReference.push().setValue(userHistory);
                detachDatabaseReadListener();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        Log.d(TAG, "downloadFirebaseStorage  끝");

    }



    @Override
    public void onResume() {
        if(SessionVariable.irisImage != null){
            mImViewIris.setImageBitmap(SessionVariable.irisImage);
        }
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    /**
     * 데이터베이스에 쓰기위해 Chlild 이벤트리스너 생성
     * @author 이재선
     * @date 2020-11-13 오전 11:03     **/
    private void attachDatabaseReadListener(){
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // 새 메시지가 삽입될때마다 호출. 리스너를 처음 연결할때 모든 Child 메시지에대해 이 메서드가 호출됨
//                    UserHistory userHistory = snapshot.getValue(UserHistory.class);
//                    FriendlyMessage friendlyMessage = snapshot.getValue(FriendlyMessage.class);
//                    mMessageAdapter.add(friendlyMessage);
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
     * Chlild 이벤트리스너 해지
     * @author 이재선
     * @date 2020-11-13 오전 11:04   **/
    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mUserHistoryDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    /**
     * bitmap image를 uri 이미지로 변환하는 함수
     * @author 이재선
     * @date 2020-11-19 오후 3:04   **/
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }



}