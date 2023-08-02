package com.example.funsta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.DashboardModel;
import com.example.funsta.R;

import java.util.ArrayList;

public class DashboardAdapter  extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    Context context;
    ArrayList<DashboardModel> list;

    public DashboardAdapter(Context context, ArrayList<DashboardModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_rv_sample,parent,false);

        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {

        DashboardModel model = list.get(position);
        holder.about.setText(model.getAbout());
        holder.profileImg.setImageResource(model.getProfile());
        holder.postImg.setImageResource(model.getPostImg());
        holder.share.setText(model.getShare());
        holder.save.setImageResource(model.getSave());
        holder.comment.setText(model.getComment());
        holder.like.setText(model.getLike());
        holder.name.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public  class DashboardViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImg,postImg,save;
        TextView like,share,comment,about,name;
        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profile_image);
            postImg = itemView.findViewById(R.id.postImg);
            save = itemView.findViewById(R.id.save);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
            comment =itemView.findViewById(R.id.comment);
            about =itemView.findViewById(R.id.userAbout);
            name = itemView.findViewById(R.id.userName);
        }
    }
}
