package com.example.funsta.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter <notificationAdapter.NotificationViewHolder>{

    Context context;
    ArrayList<notificationModel> list;

    public notificationAdapter(Context context, ArrayList<notificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification2_smaple,parent,false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        holder.profile.setImageResource(list.get(position).getProfile());
        String noti = list.get(position).getNotification();
        holder.notification.setText(Html.fromHtml(noti,1));
        holder.time.setText(list.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{

        ImageView profile;
        TextView notification;
        TextView time;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            notification = itemView.findViewById(R.id.noti_notification);
            time = itemView.findViewById(R.id.time);
        }
    }
}
