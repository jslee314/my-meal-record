package com.example.MyMealRecord.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.MyMealRecord.MainActivity;
import com.example.MyMealRecord.R;
import com.example.MyMealRecord.data.SessionVariable;
import com.example.MyMealRecord.databinding.ActivityCameraBinding;
import com.example.MyMealRecord.util.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private CameraViewModel mViewModel;
    private ActivityCameraBinding binding;

    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    PreviewView mPreviewView;
    FloatingActionButton mCaptureButton;
    TextView mZoomRatioLabel;
    ImageButton mPlusEV;
    ImageButton mDecEV;
    Camera camera;
    CameraInfo cameraInfo;
    CameraControl cameraControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        mViewModel = obtainViewModel(this);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mPreviewView = binding.previewView;
        mCaptureButton = binding.captureBtnImg;

        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        /** 카메라 배율 조정 관련 */
        mZoomRatioLabel = binding.ratioTV;
        mPlusEV = binding.plusRatio;
        mDecEV = binding.minusRatio;

        // 액션바 숨기기
        getSupportActionBar().hide();

    }


    /** 카메라 접근 권한을 획득한 후 카메라 실행 */
    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future. This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /** 카메라 실행 */
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        ImageCapture.Builder builder = new ImageCapture.Builder();  // 사진 촬영 관련
        final ImageCapture imageCapture = builder.setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()).build();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        // Attach use cases to the camera with the same lifecycle owner
        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        cameraInfo = camera.getCameraInfo();
        cameraControl = camera.getCameraControl();


        /** 카메라 가운데 초점 */
        MeteringPointFactory factory = mPreviewView.getMeteringPointFactory();
        int centerWidth = mPreviewView.getWidth()/2;
        int centerHeight = mPreviewView.getHeight()/2;

        MeteringPoint point = factory.createPoint(centerWidth,centerHeight);

        FocusMeteringAction action =  new FocusMeteringAction.Builder(point).build();
        cameraControl.startFocusAndMetering(action);



        /** 카메라 Zoom 조정 : (+) 버튼 클릭시 화면 Zoom 높임 */
        mPlusEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현재 비율을 받아옴
                float zoomRatio = cameraInfo.getZoomState().getValue().getZoomRatio();

                // 비율 높임
                float newZoom = zoomRatio + 0.05f;

                // 텍스트뷰에 업데이트
                mZoomRatioLabel.setText(String.format("%.2f", newZoom));

                float clampedNewZoom = MathUtils.clamp(newZoom,
                        cameraInfo.getZoomState().getValue().getMinZoomRatio(),
                        cameraInfo.getZoomState().getValue().getMaxZoomRatio());

                ListenableFuture<Void> listenableFuture = cameraControl.setZoomRatio(clampedNewZoom);
                Futures.addCallback(listenableFuture, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void result) {
                        Log.d(TAG, "setZoomRatio onSuccess: " + clampedNewZoom);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(TAG, "setZoomRatio failed, " + t);
                    }
                }, ContextCompat.getMainExecutor(CameraActivity.this));
            }
        });

        /** 카메라 Zoom 조정 : (-) 버튼 클릭시 화면 Zoom 높임 */
        mDecEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 비율을 받아옴
                float zoomRatio = cameraInfo.getZoomState().getValue().getZoomRatio();
                // 비율 높임
                float newZoom = zoomRatio - 0.05f;

                // 텍스트뷰에 업데이트
                mZoomRatioLabel.setText(String.format("%.2f", newZoom));

                float clampedNewZoom = MathUtils.clamp(newZoom,
                        cameraInfo.getZoomState().getValue().getMinZoomRatio(),
                        cameraInfo.getZoomState().getValue().getMaxZoomRatio());

                ListenableFuture<Void> listenableFuture = cameraControl.setZoomRatio(clampedNewZoom);
                Futures.addCallback(listenableFuture, new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void result) {
                        Log.d(TAG, "setZoomRatio onSuccess: " + clampedNewZoom);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(TAG, "setZoomRatio failed, " + t);
                    }
                }, ContextCompat.getMainExecutor(CameraActivity.this));
            }
        });

        /** 캡쳐 이미지 버튼이 눌렀을때 실행되는 리스너 */
        mCaptureButton.setOnClickListener(v -> {

            // 1.캡처된 이미지의 메모리 내 버퍼를 제공 : takePicture(Executor, OnImageCapturedCallback)
            imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    Bitmap bitmapImage = imageProxyToBitmap(image);
                    SessionVariable.irisImage = bitmapImage;

                    Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    super.onCaptureSuccess(image);
                }
                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                }
            });

            // 2. 캡처된 이미지를 제공된 파일 위치에 저장 : takePicture(OutputFileOptions, Executor, OnImageSavedCallback)
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    error.printStackTrace();
                }
            });

        });
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String getBatchDirectoryName() {
        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }
        return app_folder_path;
    }

    /**
     * 카메라 권한 추가
     * @author 이재선
     * @date 2020-11-17 오후 1:58   **/
    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
    /**
     * ViewModel 팩토리 객체를 생성 함수.
     * @author 이재선
     * @date 2020-11-19 오후 5:25   **/
    @NonNull
    public static CameraViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, (ViewModelProvider.Factory) factory).get(CameraViewModel.class);
    }

}
