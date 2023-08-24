package com.example.funsta.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.funsta.Adapter.postAdapter;
import com.example.funsta.Adapter.StoryAdapter;
import com.example.funsta.Model.PostModel;
import com.example.funsta.Model.StoryModel;
import com.example.funsta.Model.userStories;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class HomeFragment extends Fragment {


    TextView textView;


    ShimmerRecyclerView dashboardRv,storyRv;
    ArrayList<StoryModel> list;
    ImageView addStory;

    ProgressDialog dialog;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ArrayList<PostModel> dashboardList;
    public ActivityResultLauncher<Intent> ActivityResultSelectImg;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // setting the gradient color to the text "funsta"
        textView = view.findViewById(R.id.textView);
        addStory = view.findViewById(R.id.addStory);
        registerActivityForSelectImg();
        textView.setText(getResources().getString(R.string.funsta));
        TextPaint paint = textView.getPaint();
        float width = paint.measureText(getResources().getString(R.string.funsta));

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#DD129D"),
                        Color.parseColor("#FBC02D"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);

        dashboardRv = view.findViewById(R.id.dashboardRv);
        storyRv = view.findViewById(R.id.storyRv);
        storyRv.showShimmerAdapter();
        dashboardRv.showShimmerAdapter();

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        // recyclerView story


        list = new ArrayList<>();

        StoryAdapter adapter = new StoryAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(layoutManager);
        storyRv.setNestedScrollingEnabled(true);


        database.getReference().child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            list.clear();
                            for(DataSnapshot storySnapshot:snapshot.getChildren()){
                                StoryModel story = new StoryModel();
                                story.setStoryBy(storySnapshot.getKey());
                                story.setStoryAt(storySnapshot.child("postedAt").getValue(Long.class));

                                ArrayList<userStories> stories = new ArrayList<>();

                                for(DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){

                                    userStories user = snapshot1.getValue(userStories.class);
                                    if(!user.getStoryImg().isEmpty()||(user.getStoryImg()!=null))
                                        stories.add(user);
                                }
                                story.setStories(stories);

                                list.add(story);
                            }
                            storyRv.setAdapter(adapter);
                            storyRv.hideShimmerAdapter();
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        // dashboard recyclerview

        dashboardList = new ArrayList<>();


        postAdapter postAdapter = new postAdapter(getContext(), dashboardList);
        LinearLayoutManager dashLinearLayoutManager = new LinearLayoutManager(getContext());
        dashboardRv.setLayoutManager(dashLinearLayoutManager);
        dashboardRv.setNestedScrollingEnabled(false);


        database.getReference().child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dashboardList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            PostModel post = dataSnapshot.getValue(PostModel.class);
                            post.setPostId(dataSnapshot.getKey());
                            dashboardList.add(post);
                        }
                        dashboardRv.setAdapter(postAdapter);
                        dashboardRv.hideShimmerAdapter();
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                } else {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");

                    //startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                    ActivityResultSelectImg.launch(galleryIntent);
                    Log.d("incoverpic", "correct");
                }
            }
        });


        return view;
    }

    public void registerActivityForSelectImg() {

        ActivityResultSelectImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {

                int resultCode = result.getResultCode();
                Intent data = result.getData();

                Log.d("incoverpic", "result " + resultCode + " " + data + " " + Activity.RESULT_OK);
                dialog.show();
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.d("inside", "inside");

                    Uri uri = data.getData();
                    final StorageReference reference = storage.getReference()
                            .child("stories")
                            .child(auth.getUid())
                            .child(new Date().getTime() + "");
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    StoryModel story = new StoryModel();
                                    story.setStoryAt(new Date().getTime());

                                    database.getReference().child("stories")
                                            .child(auth.getUid())
                                            .child("postedAt")
                                            .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    userStories stories = new userStories(uri.toString(), story.getStoryAt());

                                                    database.getReference().child("stories")
                                                            .child(auth.getUid())
                                                            .child("userStories")
                                                            .push()
                                                            .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    });

                }
            }
        });

    }




}

