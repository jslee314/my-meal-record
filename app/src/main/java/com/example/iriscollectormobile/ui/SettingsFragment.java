package com.example.iriscollectormobile.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.iriscollectormobile.MainActivity;
import com.example.iriscollectormobile.MainViewModel;
import com.example.iriscollectormobile.R;
import com.example.iriscollectormobile.databinding.FragmentSettingsBinding;
import com.firebase.ui.auth.AuthUI;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private MainViewModel mViewModel;

    private TextView textView;
    private Button btnLogout;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        mViewModel =  MainActivity.obtainViewModel(requireActivity());

        textView = binding.textSettings;
        mViewModel.getSettingsText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Log.d(TAG, "onCreate");

        mViewModel.initSettingFragment();

        btnLogout=binding.btnLogout;
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity().getApplicationContext()); // 로그아웃 처리 함수(AuthUI를 사용하는 provider로 로그인한 경우)
            }
        });
        return binding.getRoot();
    }
}