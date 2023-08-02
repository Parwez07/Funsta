package com.example.funsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.friendModel;
import com.example.funsta.R;

import java.util.ArrayList;

public class friendAdapter extends RecyclerView.Adapter<friendAdapter.friendViewHolder> {

    Context context;
    ArrayList<friendModel> friendList;

    public friendAdapter(Context context, ArrayList<friendModel> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public friendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_rv,parent,false);

        return new friendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull friendViewHolder holder, int position) {

        holder.profileImg.setImageResource(friendList.get(position).getProfile());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class friendViewHolder extends  RecyclerView.ViewHolder{

        ImageView profileImg;
        public friendViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profile_image);
        }
    }

}
