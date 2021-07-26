package com.example.mymealrecord.data;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.mymealrecord.R;
import java.util.List;

public class UserHistoryAdapter extends ArrayAdapter<Meals> {

    public UserHistoryAdapter(@NonNull Context context, int resource, List<Meals> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_userhistory, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView sideTextView = (TextView) convertView.findViewById(R.id.sideTextView);

        Meals userHistory = getItem(position);

        boolean isPhoto = userHistory.getMealImageUrl() != null;
        if (isPhoto) {
            dateTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(userHistory.getMealImageUrl())
                    .into(photoImageView);
        } else {
            dateTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            dateTextView.setText(userHistory.getAcquisitionDate());
        }
        nameTextView.setText(userHistory.getRating().toString());
        sideTextView.setText(userHistory.getMealTime());

        return convertView;
    }


}
