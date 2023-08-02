package com.example.funsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.funsta.Model.StoryModel;
import com.example.funsta.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    Context context;
    ArrayList<StoryModel> list;

    public StoryAdapter(Context context, ArrayList<StoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_rv_design,parent,false);
        StoryViewHolder viewHolder = new StoryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {

        holder.storyImg.setImageResource(list.get(position).getStory());
        holder.profileName.setText(list.get(position).getProfile_name());
        holder.profileImg.setImageResource(list.get(position).getProfile_img());
        holder.storyType.setImageResource(list.get(position).getStoryType());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class  StoryViewHolder extends  RecyclerView.ViewHolder{

        TextView profileName;
        ImageView storyType,storyImg,profileImg;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            profileName = itemView.findViewById(R.id.profileName);
            storyImg = itemView.findViewById(R.id.story);
            profileImg = itemView.findViewById(R.id.Profile_image);
            storyType = itemView.findViewById(R.id.storyType);
        }
    }
}
