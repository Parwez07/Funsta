package com.example.funsta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Activity.CommentActivity;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.NotificationViewHolder> {

    Context context;
    ArrayList<notificationModel> list;

    public notificationAdapter(Context context, ArrayList<notificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.notification2_smaple, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        Log.d("noti", "notification adapter bindview");
        notificationModel notification = list.get(position);
        String type = notification.getType();

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Log.d("userNoti", user + " ");
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.picture)
                                .into(holder.profile);
                        String time = TimeAgo.using(notification.getNotificatonAt());
                        holder.time.setText(time);
                        if (type.equals("like")) {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " liked your post"));
                        } else if (type.equals("comment")) {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + " commented on your post"));
                        } else {
                            holder.notification.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + "start following you"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!type.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("notifications")
                            .child(notification.getPosdtedBy())
                            .child(notification.getNotificationId())
                            .child("checkOpen").setValue(true);
                    holder.openNotification.setBackgroundColor(context.getColor(R.color.white));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", notification.getPostId());
                    intent.putExtra("postedBy", notification.getPosdtedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });
        boolean checkOpen = notification.getCheckOpen();
        if (checkOpen) {
            holder.openNotification.setBackgroundColor(context.getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        ImageView profile;
        TextView notification;
        ConstraintLayout openNotification;
        TextView time;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile_image);
            notification = itemView.findViewById(R.id.noti_notification);
            time = itemView.findViewById(R.id.time);
            openNotification = itemView.findViewById(R.id.openNotification);
        }
    }
}
