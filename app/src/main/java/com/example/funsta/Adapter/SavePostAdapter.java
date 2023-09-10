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

public class SavePostAdapter extends RecyclerView.Adapter<SavePostAdapter.savePostViewHolder> {

    Context context;
    ArrayList<String> list;

    public SavePostAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public savePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_post_rv,parent,false);

        return new savePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull savePostViewHolder holder, int position) {

        Picasso.get()
                .load(list.get(position))
                .into(holder.saveImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class savePostViewHolder extends RecyclerView.ViewHolder{

        ImageView saveImg;
        public savePostViewHolder(@NonNull View itemView) {
            super(itemView);

            saveImg = itemView.findViewById(R.id.userPosts);
        }
    }
}
