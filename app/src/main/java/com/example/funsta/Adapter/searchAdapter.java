package com.example.funsta.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
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

import com.example.funsta.MainActivity;
import com.example.funsta.Model.DatatoProfileSingleton;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.followersModel;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.seachViewHolder> {

    Context context;
    ArrayList<UserModel> list;

    UserModel user;
    ArrayList<UserModel> duplicateList;

    FirebaseDatabase database;

    FirebaseAuth auth;

    public searchAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
        this.duplicateList = new ArrayList<>(list);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public seachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_search, parent, false);

        return new seachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull seachViewHolder holder, int position) {

        // when the user click on the itemview we will show that user details at the profile fragment

        if (duplicateList.size() > 0) {

            user = duplicateList.get(position);
            userDetailsFollowUnfollow(user, holder);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatatoProfileSingleton.getInstance().setSharedData(duplicateList.get(holder.getAdapterPosition()));
                    ReadableBottomBar readableBottomBar = ((MainActivity) context).findViewById(R.id.readableButtomBar);
                    readableBottomBar.selectItem(4);


                }
            });
        } else {
            user = list.get(position);
            userDetailsFollowUnfollow(user, holder);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatatoProfileSingleton.getInstance().setSharedData(list.get(holder.getAdapterPosition()));
                    ReadableBottomBar readableBottomBar = ((MainActivity) context).findViewById(R.id.readableButtomBar);
                    readableBottomBar.selectItem(4);

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return duplicateList.size() == 0 ? list.size() : duplicateList.size();
    }

    public class seachViewHolder extends RecyclerView.ViewHolder {

        Button btnFollow;
        TextView idName, profession;
        ImageView idProfile;

        public seachViewHolder(@NonNull View itemView) {
            super(itemView);

            btnFollow = itemView.findViewById(R.id.btnEdit);
            idName = itemView.findViewById(R.id.idName);
            profession = itemView.findViewById(R.id.idProfession);
            idProfile = itemView.findViewById(R.id.profile_image);
        }
    }

    public void filterSearch(String name) {

        Log.d("filterS", "idhar aa rahe hai");
        duplicateList.clear();
        if (name.isEmpty()) {
            Log.d("filterS", "name empty hai");
            duplicateList.addAll(list);

        } else {

            for (UserModel user : list) {
                if (user.getName().toLowerCase().contains(name.toLowerCase())) {
                    Log.d("filterS", user.getName() + " idhar aa hai");
                    duplicateList.add(user);
                }

            }
            Collections.sort(duplicateList, new NameComparator(name.toLowerCase()));

        }


        notifyDataSetChanged();

    }


    public void userDetailsFollowUnfollow(UserModel user, seachViewHolder holder) {

        holder.profession.setText(user.getProfession());
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

                                                                    // here we are deleting the following user and decrementing the count
                                                                    deleteFollowing(user);
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
                                followersModel follow = new followersModel();
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
                                                        // here we are adding the following details to the currentUser
                                                        setFollowing(user);
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


    public void deleteFollowing(UserModel user) {

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(auth.getUid())
                .child("followings")
                .child(user.getUserId())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        database.getReference().child("Users")
                                .child(auth.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            UserModel currUser = snapshot.getValue(UserModel.class);
                                            database.getReference().child("Users")
                                                    .child(auth.getUid())
                                                    .child("followingCounts")
                                                    .setValue(currUser.getFollowingCounts() - 1 <0 ?0:currUser.getFollowingCounts()-1)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                        Log.d("curruser", "follwoingcount added");
                    }
                });

    }

    public void setFollowing(UserModel user) {

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(auth.getUid())
                .child("followings")
                .child(user.getUserId())
                .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        database.getReference().child("Users")
                                .child(auth.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            UserModel currUser = snapshot.getValue(UserModel.class);
                                            database.getReference().child("Users")
                                                    .child(auth.getUid())
                                                    .child("followingCounts")
                                                    .setValue(currUser.getFollowingCounts() + 1)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                        Log.d("curruser", "follwoingcount added");
                    }
                });
    }


    class NameComparator implements Comparator<UserModel> {
        private String name;

        public NameComparator(String searchedName) {
            this.name = searchedName;
        }

        @Override
        public int compare(UserModel user1, UserModel user2) {

            if (user1.getProfession() == null || user1.getProfession().isEmpty())
                return 1;
            if (user2.getProfession() == null || user2.getProfession().isEmpty())
                return -1;



            if (user1.getName().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                return -1;
            } else if (user2.getName().trim().toLowerCase().equals(name.toLowerCase().trim())) {
                return 1;
            } else {
                return user1.getProfession().toLowerCase().compareTo(user2.getProfession().toLowerCase());
            }
        }
    }
}
