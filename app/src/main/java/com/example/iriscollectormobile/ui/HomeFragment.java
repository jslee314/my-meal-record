package com.example.iriscollectormobile.ui;

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
    private MainViewModel mViewModel;
    private FragmentHomeBinding binding;

    Button mBtnLeftEye;
    Button mBtnRightEye;
    Button mBtnSubmit;
    ImageView mImViewIris;
    TextView textView;
    View.OnClickListener onClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mViewModel = MainActivity.obtainViewModel(requireActivity());
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(requireActivity());

        mViewModel.initHomeFragment();

        mViewModel.initFirebaseDatabase();
        mViewModel.initFirebaseStorage();

        textView = binding.textHome;
        mViewModel.getHomeText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        mBtnLeftEye = binding.cameraButtonLeft;
        mBtnLeftEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setSide("left");
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivityForResult(intent, ConstantVariable.REQUEST_CAMERA);
            }
        });

        mBtnRightEye = binding.cameraButtonRight;
        mBtnRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setSide("right");
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivityForResult(intent, ConstantVariable.REQUEST_CAMERA);
            }
        });

        mBtnSubmit = binding.submitButton;
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri irisUri = getImageUri(getActivity().getApplicationContext(), SessionVariable.irisImage);
                uploadFirebaseStorage(irisUri);
            }
        });

        mImViewIris = binding.irisImg;

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        if(SessionVariable.irisImage != null){
            mImViewIris.setImageBitmap(SessionVariable.irisImage);
        }
        super.onResume();
    }


    /**
     * 사진 파일을 Firebase Storage에 파일을 업로드
     * @author 이재선
     * @date 2020-11-20 오후 6:52   **/
    public void uploadFirebaseStorage(Uri irisUri){
//        Uri irisUri = getImageUri(getActivity().getApplicationContext(), SessionVariable.irisImage);
        // chat_photos/<FILENAME>에 저장할 파일의 레퍼런스 가져옴
        mViewModel.mPhotoRef = mViewModel.mIrisPhogosStorageReference.child(irisUri.getLastPathSegment());
        // 파일을 Firebase Storage에 파일을 업로드 함
        mViewModel.mPhotoRef.putFile(irisUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mViewModel.downloadFirebaseStorage();
            }
        });
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