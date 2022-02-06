package com.example.interestproject.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.R;

import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mInterest;
    private boolean isClickable;

    public InterestAdapter(Context mContext, List<String> mInterest, boolean isClickable){
        this.mContext = mContext;
        this.mInterest = mInterest;
        this.isClickable = isClickable;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.interest_item, parent, false);
        return new InterestAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String interest = mInterest.get(position);
        holder.valueInterest.setText(interest);
    }

    public String getItem(int position) {
        return mInterest.get(position);
    }

    @Override
    public int getItemCount() {
        return mInterest.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView valueInterest;
        private boolean isSelected;
        private LinearLayout linearLayout;

        public ViewHolder(View itemView){
            super(itemView);
            valueInterest = itemView.findViewById(R.id.interest_value);
            linearLayout = itemView.findViewById(R.id.linear_layout_interest);
            isSelected = false;
/*
            if(isClickable) {
                valueInterest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isSelected = !isSelected;
                        if(isSelected) {
                            linearLayout.setBackgroundResource(R.drawable.background_interest_selected);
                        }
                        else {
                            linearLayout.setBackgroundResource(R.drawable.background_interest_unselected);
                        }
                    }
                });
            }*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }



}
