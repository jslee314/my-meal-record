package com.example.iriscollectormobile.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.iriscollectormobile.MainViewModel;
import com.example.iriscollectormobile.R;
import com.example.iriscollectormobile.data.UserHistory;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private MainViewModel mMainViewModel;

    private TextView mTextView;

    private ListView mUserHistoryListView;
    private UserHistoryAdapter mUserHistoryAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        /** UI references 초기화 **/
        mTextView = (TextView) root.findViewById(R.id.text_dashboard);
        mUserHistoryListView = (ListView) root.findViewById(R.id.listView_userHistory);

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


        return root;
    }



}