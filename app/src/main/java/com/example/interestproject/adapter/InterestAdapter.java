package com.example.interestproject.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.interestproject.R;

import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private Context mContext;
    private List<String> interestsList;
    String interest;

    public InterestAdapter(Context mContext, List<String> interestsList){
        this.mContext = mContext;
        this.interestsList = interestsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.interest_item, parent, false);
        return new InterestAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        interest = interestsList.get(position);
        holder.valueInterest.setText(interest);
    }

    public String getItem(int position) {
        return interestsList.get(position);
    }

    @Override
    public int getItemCount() {
        return interestsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView valueInterest;
        public ViewHolder(View itemView){
            super(itemView);
            valueInterest = itemView.findViewById(R.id.user_item_message_username);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //clickListener.onItemClick(getAdapterPosition(), v);

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

}
