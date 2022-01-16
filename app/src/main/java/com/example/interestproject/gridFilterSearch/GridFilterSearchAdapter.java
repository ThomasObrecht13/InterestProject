package com.example.interestproject.gridFilterSearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.interestproject.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridFilterSearchAdapter extends ArrayAdapter {

    ArrayList<GridFilterSearchItem> userList = new ArrayList<>();

    public GridFilterSearchAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        userList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_view_items_filter_search, null);

        TextView textView = (TextView) v.findViewById(R.id.textView);
        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.imageView);

        textView.setText(userList.get(position).getUserName());

        Glide.with(getContext())
                .load(userList.get(position).getUserImage())
                .into(circleImageView);

        return v;

    }

}