package com.example.funsta.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.funsta.Activity.ProfileEditActivity;
import com.example.funsta.Adapter.ViewPagerAdapter;
import com.example.funsta.MainActivity;
import com.example.funsta.Model.DatatoProfileSingleton;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.followersModel;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class newProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    TextView profileName, profession, followersCount, followingCounts, postCount ,followers, following;
    ImageView profile;
    Button btnEdit;

    ViewPager viewPager;
    TabLayout tabLayout;


    public newProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile, container, false);
        profession = view.findViewById(R.id.idProfession);

        profileName = view.findViewById(R.id.profileName);
        btnEdit = view.findViewById(R.id.btnEdit);
        profile = view.findViewById(R.id.profile_image);
        tabLayout = view.findViewById(R.id.tabLayout);
        followersCount = view.findViewById(R.id.tvFollowersCount);
        followingCounts = view.findViewById(R.id.tvFollowingCount);
        postCount = view.findViewById(R.id.tvPostCount);
        followers = view.findViewById(R.id.tvFollowers);
        following =view.findViewById(R.id.tvFollowing);

        // Communicate with MainActivity to enable Toolbar and NavigationDrawer
        ((MainActivity) requireActivity()).setToolbarAndDrawerState(true);

        String userId = auth.getUid();

        UserModel otherUser = DatatoProfileSingleton.getInstance().getSharedData();

        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());


        if (otherUser != null) {
            fetchUserDetails(otherUser.getUserId());
            pagerAdapter.addFragment(new MyPostFragment(otherUser.getUserId()), "Posts");
            Log.d("otherUser", otherUser.getUserId());

        } else {
            fetchUserDetails(userId);
            pagerAdapter.addFragment(new MyPostFragment(userId), "Posts");
            pagerAdapter.addFragment(new SavedPostFragment(), "Saved Post");
        }

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (otherUser == null) {
            btnEdit.setText("Edit Profile");
        } else {
            setBtnBg(otherUser);
        }



        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otherUser == null) {
                    startActivity(new Intent(getActivity(), ProfileEditActivity.class));
                } else {

                    String text = btnEdit.getText().toString().toLowerCase().trim();
                    if (text.equals("follow")) {
                        // follow karna hai isme and at the end button ko following set karna hai
                        Log.d("followersCount", "before " + otherUser.getFollowersCount());
                        callFollow(otherUser);

                    } else if (text.equals("following")) {
                        // isme unfollow karna hi and button ko follow set karna hai
                        Log.d("followersCount", "before following " + otherUser.getFollowersCount());
                        removeFollow(otherUser);

                    } else {
                        btnEdit.setText("Edit Profile");
                        startActivity(new Intent(getActivity(), ProfileEditActivity.class));
                    }
                }
            }
        });

        return view;
    }

    public void removeFollow(UserModel user) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            btnEdit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_btn));
                            btnEdit.setText("Following");
                            btnEdit.setTextColor(getContext().getColor(R.color.material_dynamic_neutral70));


                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                int count = snapshot.getValue(Integer.class);
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("Users")
                                                                        .child(user.getUserId())
                                                                        .child("followersCount")
                                                                        .setValue(count == 0 ? 0 : count - 1).addOnSuccessListener(unused1 -> {
                                                                            Log.d("followersCount", user.getFollowersCount() + "");

                                                                            btnEdit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.buttom));
                                                                            btnEdit.setText("Follow");
                                                                            btnEdit.setTextColor(getContext().getColor(R.color.white));
                                                                            followersCount.setText((count==0?0 :count-1) + "");
                                                                            Toast.makeText(getContext(), "you Unfollow " + user.getName(), Toast.LENGTH_SHORT).show();
                                                                            // here we are deleting the following user and decrementing the count
                                                                            deleteFollowing(user);
                                                                        });

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            });
                                }
                            });
                            dialog.show();
                            dialog.create();
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
                                                    .setValue(currUser.getFollowingCounts() - 1 < 0 ? 0 : currUser.getFollowingCounts() - 1)
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

    private void callFollow(UserModel user) {


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
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int count = snapshot.getValue(Integer.class);
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserId())
                                            .child("followersCount")
                                            .setValue(count + 1)
                                            .addOnSuccessListener(unused1 -> {
                                                btnEdit.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_btn));
                                                btnEdit.setText("Following");
                                                btnEdit.setTextColor(getContext().getColor(R.color.material_dynamic_neutral70));
                                                followersCount.setText((count + 1) + "");
//                                              btnEdit.setEnabled(false);
                                                Toast.makeText(getContext(), "you followed " + user.getName(), Toast.LENGTH_SHORT).show();
                                                notificationModel notification = new notificationModel();
                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                notification.setNotificatonAt(new Date().getTime());
                                                notification.setType("follow");
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("notifications")
                                                        .child(user.getUserId())
                                                        .push()
                                                        .setValue(notification);
                                                Log.d("follow", "followers added ");
                                                // here we are adding the following details to the currentUser
                                                setFollowing(user);
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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
                                                            Log.d("follow", "following added");
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

    private void setBtnBg(UserModel user) {

        database.getReference().child("Users")
                .child(user.getUserId())
                .child("followers")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            btnEdit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.following_btn));
                            btnEdit.setText("Following");
                            btnEdit.setTextColor(getActivity().getColor(R.color.material_dynamic_neutral70));
                        } else {
                            btnEdit.setText("follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    private void fetchUserDetails(String user) {

        database.getReference().child("Users")
                .child(user)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            UserModel user = snapshot.getValue(UserModel.class);

                            Log.d("toUser", user.getFollowingCounts() + " " + user);

                            //idProfile.setImageDrawable(null);
                            profileName.setText(user.getName());
                            profession.setText(user.getProfession());
                            followersCount.setText(user.getFollowersCount() + "");
                            followingCounts.setText(user.getFollowingCounts() + "");
                            postCount.setText(user.getPostCounts() + "");
                            Picasso.get().load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(profile);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Communicate with MainActivity to disable Toolbar and NavigationDrawer
        ((MainActivity) requireActivity()).setToolbarAndDrawerState(false);
        DatatoProfileSingleton.getInstance().setSharedData(null);
    }
}