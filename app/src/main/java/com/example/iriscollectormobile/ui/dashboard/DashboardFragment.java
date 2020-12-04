package com.example.iriscollectormobile.ui.dashboard;

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
import androidx.lifecycle.ViewModelProviders;

import com.example.iriscollectormobile.MainActivity;
import com.example.iriscollectormobile.MainViewModel;
import com.example.iriscollectormobile.R;
import com.example.iriscollectormobile.data.UserHistory;
import com.example.iriscollectormobile.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    private MainViewModel mMainViewModel;
    private FragmentDashboardBinding binding;

    private TextView mTextView;
    private ListView mUserHistoryListView;
    private UserHistoryAdapter mUserHistoryAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        mMainViewModel =  MainActivity.obtainViewModel(requireActivity());
        
        /** UI references 초기화 **/
        mTextView = binding.textDashboard;
        mUserHistoryListView = binding.listViewUserHistory;


        // Initialize message ListView and its adapter
        List<UserHistory> userHistories = new ArrayList<>();
        mUserHistoryAdapter = new UserHistoryAdapter(getContext(), R.layout.item_userhistory, userHistories);
//        mUserHistoryListView.setAdapter(mUserHistoryAdapter);
        mMainViewModel.getUserHistoryAdapter().setValue(mUserHistoryAdapter);

        mMainViewModel.getDashboardText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTextView.setText(s);
            }
        });

        return binding.getRoot();
    }



}