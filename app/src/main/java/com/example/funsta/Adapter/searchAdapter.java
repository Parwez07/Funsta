package com.example.funsta.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.followModel;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.seachViewHolder> {

    Context context;
    ArrayList<UserModel> list;

    public searchAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public seachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_search, parent, false);

        return new seachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull seachViewHolder holder, int position) {

        UserModel user = list.get(position);
        //holder.intrest.setText(user.getIntrest());
        holder.idName.setText(user.getName());
        Picasso.get().load(user.getProfile())
                .placeholder(R.drawable.picture)
                .into(holder.idProfile);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            holder.btnFollow.setBackground(ContextCompat.getDrawable(context, R.drawable.following_btn));
                            holder.btnFollow.setText("Following");
                            holder.btnFollow.setTextColor(context.getColor(R.color.material_dynamic_neutral70));
                            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setTitle("Unfollow");
                                    dialog.setMessage("Do you really want to Unfollow?");
                                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Users").child(user.getUserId())
                                                    .child("followers")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .removeValue()
                                                    .addOnSuccessListener(unused -> {
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Users")
                                                                .child(user.getUserId())
                                                                .child("followersCount")
                                                                .setValue(user.getFollowersCount() - 1).addOnSuccessListener(unused1 -> {
                                                                    holder.btnFollow.setBackground(ContextCompat.getDrawable(context, R.drawable.buttom));
                                                                    holder.btnFollow.setText("Follow");
                                                                    holder.btnFollow.setTextColor(context.getColor(R.color.white));
                                                                    Toast.makeText(context, "you Unfollow " + user.getName(), Toast.LENGTH_SHORT).show();
                                                                });
                                                    });
                                        }
                                    });
                                    dialog.show();
                                    dialog.create();
                                }
                            });
                        } else {
                            holder.btnFollow.setOnClickListener(v -> {
                                followModel follow = new followModel();
                                follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                follow.setFollowedAt(new Date().getTime());
                                FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(user.getUserId())
                                        .child("followers")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .setValue(follow)
                                        .addOnSuccessListener(unused -> {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Users")
                                                    .child(user.getUserId())
                                                    .child("followersCount")
                                                    .setValue(user.getFollowersCount() + 1).addOnSuccessListener(unused1 -> {
                                                        holder.btnFollow.setBackground(ContextCompat.getDrawable(context, R.drawable.following_btn));
                                                        holder.btnFollow.setText("Following");
                                                        holder.btnFollow.setTextColor(context.getColor(R.color.material_dynamic_neutral70));
                                                        holder.btnFollow.setEnabled(false);
                                                        Toast.makeText(context, "you followed " + user.getName(), Toast.LENGTH_SHORT).show();

                                                        notificationModel notification = new notificationModel();
                                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                        notification.setNotificatonAt(new Date().getTime());
                                                        notification.setType("follow");
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("notifications")
                                                                .child(user.getUserId())
                                                                .push()
                                                                .setValue(notification);
                                                    });
                                        });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class seachViewHolder extends RecyclerView.ViewHolder {

        Button btnFollow;
        TextView idName, intrest;
        ImageView idProfile;

        public seachViewHolder(@NonNull View itemView) {
            super(itemView);

            btnFollow = itemView.findViewById(R.id.btnEdit);
            idName = itemView.findViewById(R.id.idName);
            intrest = itemView.findViewById(R.id.intrest);
            idProfile = itemView.findViewById(R.id.profile_image);
        }
    }
}
