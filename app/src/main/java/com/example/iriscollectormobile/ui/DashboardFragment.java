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
import com.example.iriscollectormobile.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private MainViewModel mViewModel;
    private FragmentDashboardBinding binding;

    private TextView mTextView;
    private ListView mUserHistoryListView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        mViewModel =  MainActivity.obtainViewModel(requireActivity());

        /** UI references 초기화 **/
        mTextView = binding.textDashboard;
        mViewModel.getDashboardText().setValue("This is dashboard fragment");

        mUserHistoryListView = binding.listViewUserHistory;
        mUserHistoryListView.setAdapter(mViewModel.getUserHistoryAdapter());

        mViewModel.getDashboardText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTextView.setText(s);
            }
        });

        return binding.getRoot();
    }



}