package com.example.funsta.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.funsta.Activity.ProfileEditActivity;
import com.example.funsta.Adapter.ViewPagerAdapter;
import com.example.funsta.MainActivity;
import com.example.funsta.Model.UserModel;
import com.example.funsta.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;


public class newProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    TextView profileName, profession, followersCount, followingCounts,postCount;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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


        // Communicate with MainActivity to enable Toolbar and NavigationDrawer
        ((MainActivity) requireActivity()).setToolbarAndDrawerState(true);


        fetchingCoverPhoto();

        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new MyPostFragment(), "My Post");
        pagerAdapter.addFragment(new SearchFragment(), "Saved Post");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);

            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void fetchingCoverPhoto() {

        database.getReference().child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            UserModel user = snapshot.getValue(UserModel.class);
                            Log.d("userDetails", user.getFollowingCounts() + " " + user);

                            //idProfile.setImageDrawable(null);
                            profileName.setText(user.getName());
                            profession.setText(user.getProfession());
                            followersCount.setText(user.getFollowingCounts() + "");
                            followingCounts.setText(user.getFollowingCounts()+"");
                            postCount.setText(user.getPostCounts()+"");
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
    }
}