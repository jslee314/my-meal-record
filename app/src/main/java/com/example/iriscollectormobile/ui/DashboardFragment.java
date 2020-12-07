package com.example.iriscollectormobile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.iriscollectormobile.MainActivity;
import com.example.iriscollectormobile.MainViewModel;
import com.example.iriscollectormobile.R;
import com.example.iriscollectormobile.data.UserHistory;
import com.example.iriscollectormobile.databinding.FragmentDashboardBinding;
import com.example.iriscollectormobile.data.UserHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    private MainViewModel mMainViewModel;
    private FragmentDashboardBinding binding;

    private TextView mTextView;
    private ListView mUserHistoryListView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        mMainViewModel =  MainActivity.obtainViewModel(requireActivity());

        /** UI references 초기화 **/
        mTextView = binding.textDashboard;
        mUserHistoryListView = binding.listViewUserHistory;
        mUserHistoryListView.setAdapter(mMainViewModel.getUserHistoryAdapter());

        mMainViewModel.initDashboardFragment();



        mMainViewModel.getDashboardText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTextView.setText(s);
            }
        });

        return binding.getRoot();
    }



}