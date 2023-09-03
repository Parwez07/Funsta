package com.example.funsta.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.followersModel;
import com.example.funsta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class followerAdapter extends RecyclerView.Adapter<followerAdapter.friendViewHolder> {

    Context context;
    ArrayList<followersModel> friendList;

    public followerAdapter(Context context, ArrayList<followersModel> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public friendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_rv, parent, false);

        return new friendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull friendViewHolder holder, int position) {

        followersModel follow = friendList.get(position);
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(follow.getFollowedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Log.d("userdetails"," "+user);

                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(holder.profileImg);
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class friendViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImg;

        public friendViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.profile_image);
        }
    }

}
