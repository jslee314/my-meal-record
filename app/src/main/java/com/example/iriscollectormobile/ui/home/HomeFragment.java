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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Button btnLeftEye;
    Button btnRightEye;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
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

        return root;
    }


}