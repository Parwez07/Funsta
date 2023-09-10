package com.example.funsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userPostAdapter extends RecyclerView.Adapter<userPostAdapter.userPostViewHolder> {

    Context context;
    ArrayList<String> list;

    public userPostAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public userPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_post_rv,parent,false);

        return new userPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userPostViewHolder holder, int position) {

        Picasso.get()
                .load(list.get(position))
                .into(holder.userPostImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class userPostViewHolder extends RecyclerView.ViewHolder{

        ImageView userPostImg;
        public userPostViewHolder(@NonNull View itemView) {
            super(itemView);

            userPostImg = itemView.findViewById(R.id.userPosts);

        }
    }
}
