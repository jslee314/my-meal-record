package com.example.iriscollectormobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.iriscollectormobile.Camera.CameraActivity;
import com.example.iriscollectormobile.R;
import com.example.iriscollectormobile.data.SessionVariable;
import com.example.iriscollectormobile.data.UserHistory;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment {

    /** ((realtime database))를 사용하기위한 2개의 클래스의 인스턴스 변수 선언 **/
    private FirebaseDatabase mFirebaseDatabase;                 // 앱이 db 접근하기위한 진입점
    private DatabaseReference mUserHistoryDatabaseReference;    // db 참조개체(db의 특정 부분을 참조하는 클래스)
    private ChildEventListener mChildEventListener;             // (리스너)사용자의 데이터가 변경될때 트리거됨 realtimeDatabase

    private HomeViewModel homeViewModel;
    Button btnLeftEye;
    Button btnRightEye;
    Button btnSubmit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mFirebaseDatabase = FirebaseDatabase.getInstance(); // realtime db관련 클래스의 인스턴스
        mUserHistoryDatabaseReference = mFirebaseDatabase.getReference().child("UserHistoryData");
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        btnLeftEye=root.findViewById(R.id.camera_button_left);
        btnLeftEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionVariable.irisSide = "left";
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivity(intent);
            }
        });

        btnRightEye=root.findViewById(R.id.camera_button_right);
        btnRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionVariable.irisSide = "right";
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivity(intent);
            }
        });

        btnSubmit=root.findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UserHistory userHistory = new UserHistory(SessionVariable.username, null,null, null);
                mUserHistoryDatabaseReference.push().setValue(userHistory);
                detachDatabaseReadListener();
            }
        });
        attachDatabaseReadListener();

        return root;
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



}