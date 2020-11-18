package com.example.iriscollectormobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.example.iriscollectormobile.data.SessionVariable;
import com.example.iriscollectormobile.data.UserHistory;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;

    /** ((authentication))을 사용하기위한 클래스의 인스턴스 변수 선언 **/
    private FirebaseAuth mFirebaseAuth;    // 인증상태
    private FirebaseAuth.AuthStateListener mAuthStateListener;  // (리스너)사용자 인증상태가 변경될 때(로그인/아웃) 트리거됨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();         // authentication관련 클래스의 인스턴스

        /** 네비게이션 관련 */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        /** 인증을 하기위한  **/
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // 인증을 하기위한 이벤트 리스너 생성
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();  // 사용자가 로그인했는지 안했는지 체크
                if(user != null){
                    // user가 로그인인 한 경우 -> 로그인 완료 토스트 띄움(완료 Activity 를 따로 만들어도 됨)
                    //Toast.makeText(MainActivity.this, "로그인 완료, 환영합니다!!", Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getDisplayName());

                }else {
                    // user가 로그아웃한 경우 -> 로그인 로직 시작
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
//        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** AuthUI 로직 실행 후 결과 반환에 따른 실행 */
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "로그인 성공!!", Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "로그인 실패 ㅜㅜ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** (CASE 1) :사용자가 로그인 한 경우 **/
    private void onSignedInInitialize(String username){
        SessionVariable.username = username;
    }

    /** (CASE 2) : 사용자가 로그아웃 한 경우 **/
    private void onSignedOutCleanup(){
//        // user를 익명으로 바꾸고, 메시지(어뎁터)를 지우고, db데이터베이스와 연결된 이벤트 리스너 해지
//        mUsername = ANONYMOUS;
//        mMessageAdapter.clear();    // mMessageAdapter를 지우지 않으면 로그인,아웃할때 중복메시지가 나올수 있는 버그가 발생함
    }



}