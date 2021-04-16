package com.example.MyMealRecord.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.MyMealRecord.camera.CameraActivity;
import com.example.MyMealRecord.MainActivity;
import com.example.MyMealRecord.MainViewModel;
import com.example.MyMealRecord.R;
import com.example.MyMealRecord.data.ConstantVariable;
import com.example.MyMealRecord.data.SessionVariable;
import com.example.MyMealRecord.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HomeFragment extends Fragment {
    private MainViewModel mViewModel;
    private FragmentHomeBinding binding;

    private TextView mTextView;
    private ImageView mImViewIris;

    private Button mBtnLeftEye;
    private Button mBtnRightEye;
    private Button mBtnSubmit;
    private EditText mEditTextUserHistoryName;
    private EditText mEditTextUserHistoryEmail;

    View.OnClickListener onClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mViewModel = MainActivity.obtainViewModel(requireActivity());
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(requireActivity());

        mViewModel.initFirebaseDatabase();
        mViewModel.initFirebaseStorage();

        mTextView = binding.textHome;
        mViewModel.getHomeText().setValue("홍채 촬영 후 'SUBMIT'클릭"); // commit test
        mViewModel.getHomeText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTextView.setText(s);
            }
        });

        mBtnLeftEye = binding.cameraButtonLeft;
        mBtnLeftEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionVariable.side = "left";
                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivityForResult(intent, ConstantVariable.REQUEST_CAMERA);
            }
        });

        mBtnRightEye = binding.cameraButtonRight;
        mBtnRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionVariable.side = "right";

                Intent intent = new Intent(getActivity(),CameraActivity.class);
                startActivityForResult(intent, ConstantVariable.REQUEST_CAMERA);
            }
        });

        mBtnSubmit = binding.submitButton;
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SessionVariable.irisImage==null){
                    Toast.makeText(getContext(), "사진을 먼저 촬영하세요", Toast.LENGTH_LONG).show();
                    return;
                }
                Uri irisUri = getImageUri(getActivity().getApplicationContext(), SessionVariable.irisImage);
                uploadFirebaseStorage(irisUri);
            }
        });
        mImViewIris = binding.irisImg;

        mEditTextUserHistoryName = binding.userHistoryName;
        mViewModel.getUserHistoryName().setValue(mEditTextUserHistoryName.getText().toString());
        mViewModel.getUserHistoryName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mEditTextUserHistoryName.setText(s);
            }
        });

        mEditTextUserHistoryEmail = binding.userHistoryEmail;
        mViewModel.getUserHistoryEmail().setValue(mEditTextUserHistoryName.getText().toString());
        mViewModel.getUserHistoryEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mEditTextUserHistoryEmail.setText(s);
            }
        });
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
        // Uri irisUri = getImageUri(getActivity().getApplicationContext(), SessionVariable.irisImage);
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