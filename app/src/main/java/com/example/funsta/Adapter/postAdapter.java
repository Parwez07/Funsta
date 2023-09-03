package com.example.funsta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Activity.CommentActivity;
import com.example.funsta.Model.PostModel;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class postAdapter extends RecyclerView.Adapter<postAdapter.DashboardViewHolder> {

    Context context;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<PostModel> list;

    public postAdapter(Context context, ArrayList<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_rv_sample, parent, false);

        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {

        PostModel model = list.get(position);

        if(model.getPostImg()!=null) {
            holder.postImg.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(model.getPostImg())
                    .placeholder(R.drawable.picture)
                    .into(holder.postImg);
        }
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.picture)
                                .into(holder.profileImg);
                        holder.name.setText(user.getName());
                        holder.about.setText(user.getProfession());
                        holder.like.setText(model.getPostLikes() + "");
                        holder.comment.setText(model.getCommentCounts()+"");


                        if (model.getPostDescription() == null || model.getPostDescription().isEmpty()) {
                            holder.postDescription.setVisibility(View.GONE);
                        }
                        holder.postDescription.setText(model.getPostDescription());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(Objects.requireNonNull(auth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                            holder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference().child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .removeValue()
                                            .addOnSuccessListener(unused -> {
                                                FirebaseDatabase.getInstance().getReference().child("posts")
                                                        .child(model.getPostId())
                                                        .child("postLikes")
                                                        .setValue(model.getPostLikes() - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);

                                                            }
                                                        });

                                            });
                                }
                            });
                        }else{
                            holder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference().child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(unused -> {
                                                FirebaseDatabase.getInstance().getReference().child("posts")
                                                        .child(model.getPostId())
                                                        .child("postLikes")
                                                        .setValue(model.getPostLikes() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);

                                                                notificationModel notification = new notificationModel();
                                                                notification.setNotificationBy(auth.getUid());
                                                                notification.setNotificatonAt(new Date().getTime());
                                                                notification.setPostId(model.getPostId());
                                                                notification.setPosdtedBy(model.getPostedBy());
                                                                notification.setType("like");

                                                                database.getReference()
                                                                        .child("notifications")
                                                                        .child(model.getPostedBy())
                                                                        .push()
                                                                        .setValue(notification);
                                                            }
                                                        });

                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Intent.ACTION_SEND);
                intent.setType ("image/*");
                intent.putExtra(Intent.EXTRA_TEXT,"hey check out this post"+model.getPostImg());
                Intent chooser = Intent.createChooser(intent,"share this meme using...");
                context.startActivity(chooser);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class DashboardViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImg, postImg, save;
        TextView like, share, comment, about, name, postDescription;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profile_image);
            postImg = itemView.findViewById(R.id.postImg);
            save = itemView.findViewById(R.id.save);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
            comment = itemView.findViewById(R.id.profileName);
            about = itemView.findViewById(R.id.profession);
            name = itemView.findViewById(R.id.userName);
            postDescription = itemView.findViewById(R.id.postDescription);
        }
    }


}
