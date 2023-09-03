package com.example.funsta.Fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsta.Adapter.followerAdapter;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.followersModel;
import com.example.funsta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private Bitmap selectedImg;
    TextView userName;
    TextView intrest, followers, friends, photos;
    ImageView edit, verifyAccount, profile;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public ActivityResultLauncher<Intent> ActivityResultSelectImg, ActivityResultProfile;

    RecyclerView friendRv;
    ArrayList<followersModel> friendList;
    ImageView changeCoverPic, idProfile;

    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    public ProfileFragment() {

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

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        friendRv = view.findViewById(R.id.friendRv);
        friendList = new ArrayList<>();
        changeCoverPic = view.findViewById(R.id.changeCoverPhoto);
        idProfile = view.findViewById(R.id.idProfile);
        userName = view.findViewById(R.id.profileName);
        intrest = view.findViewById(R.id.intrest);
        edit = view.findViewById(R.id.edit);
        intrest.setEnabled(false);
        verifyAccount = view.findViewById(R.id.verifyAccount);
        profile = view.findViewById(R.id.profile_image);
        friends = view.findViewById(R.id.idFriends);
        followers = view.findViewById(R.id.idFollowers);
        photos = view.findViewById(R.id.idPhotos);

        registerActivityForSelectImg();

        fetchingCoverPhoto();


        followerAdapter adapter = new followerAdapter(getContext(), friendList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        friendRv.setLayoutManager(layoutManager);
        friendRv.setAdapter(adapter);

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("followers")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            followersModel follow = dataSnapshot.getValue(followersModel.class);
                            friendList.add(follow);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        changeCoverPic.setOnClickListener(v -> {

            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            } else {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra("code", 1);

                //startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                ActivityResultSelectImg.launch(galleryIntent);
                Log.d("incoverpic", "correct");
            }
        });

        verifyAccount.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            } else {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra("code", 2);

                //startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                ActivityResultProfile.launch(galleryIntent);
                Log.d("incoverpic", "correct inside the coverPhoto");
            }

        });


        return view;
    }

    private void fetchingCoverPhoto() {

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    UserModel user = snapshot.getValue(UserModel.class);
                    Log.d("userDetails",user.getFollowingCounts()+" "+user);
                    Picasso.get().load(user.getCover_photo())
                            .placeholder(R.drawable.picture)
                            .into(idProfile);
                    //idProfile.setImageDrawable(null);
                    userName.setText(user.getName());
                    Picasso.get().load(user.getProfile())
                            .placeholder(R.drawable.picture)
                            .into(profile);
                    followers.setText(user.getFollowingCounts()+"");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void registerActivityForSelectImg() {

        ActivityResultSelectImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

                result -> {

                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    Log.d("incoverpic", "result " + resultCode + " " + data + " " + Activity.RESULT_OK);

                    if (resultCode == Activity.RESULT_OK && data != null) {

                        Uri uri = data.getData();
                        try {
                            selectedImg = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), data.getData());
                            idProfile.setImageURI(uri);
                            final StorageReference reference = storage.getReference().child("cover_photo")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getContext(), "Cover_photo saved", Toast.LENGTH_SHORT).show();
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users").child(auth.getUid()).child("cover_photo")
                                                    .setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });


        ActivityResultProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    Intent data = result.getData();
                    int resultCode = result.getResultCode();

                    if (resultCode == Activity.RESULT_OK && data != null) {

                        Uri uri = data.getData();
                        try {
                            selectedImg = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), data.getData());
                            profile.setImageURI(uri);
                            final StorageReference reference = storage.getReference().child("profile_img")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getContext(), "profile_img saved", Toast.LENGTH_SHORT).show();
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users").child(auth.getUid()).child("profile")
                                                    .setValue(uri.toString());
                                        }
                                    });
                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });
    }


}