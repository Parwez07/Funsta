package com.example.funsta.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.funsta.Model.PostModel;
import com.example.funsta.Model.UserModel;
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
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;


public class PostFragment extends Fragment {

    Button btnPost;
    EditText postDescription;
    ImageView addImg, postImg, profileImg;
    TextView userName, profession;
    Uri uri;
    String description;



    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    ProgressDialog dialog;

    public ActivityResultLauncher<Intent> ActivityResultSelectImg;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        btnPost = view.findViewById(R.id.btnPost);
        postDescription = view.findViewById(R.id.idPostDescription);
        addImg = view.findViewById(R.id.btnImg);
        postImg = view.findViewById(R.id.imgPost);
        profileImg = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.idName);
        profession = view.findViewById(R.id.idProfession);

        registerActivityForSelectImg();
        fetchingUserDetails();


        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Post Uploading");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                description = postDescription.getText().toString();
                if (!description.isEmpty()) {
                    btnPost.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.gradient_2));
                    btnPost.setTextColor(getContext().getColor(R.color.white));
                    btnPost.setEnabled(true);
                } else {
                    btnPost.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.following_btn));
                    btnPost.setTextColor(getContext().getColor(R.color.material_dynamic_neutral70));
                    btnPost.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    new ActivityResultContracts.RequestMultiplePermissions();
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

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                final StorageReference reference = storage.getReference().child("posts")
                        .child(auth.getUid())
                        .child(new Date().getTime() + "");

                if(uri!=null) {
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    PostModel post = new PostModel();
                                    post.setPostImg(uri.toString());
                                    post.setPostedBy(auth.getUid());
                                    post.setPostDescription(postDescription.getText().toString());
                                    post.setPostedAt(new Date().getTime());

                                    database.getReference().child("posts")
                                            .push()
                                            .setValue(post)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    postCount();
                                                    Toast.makeText(getContext(), "Posted Succesful", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();

                                                }
                                            });
                                }
                            });
                        }
                    });
                }
                else{
                    PostModel post = new PostModel();
                    post.setPostedBy(auth.getUid());
                    post.setPostDescription(postDescription.getText().toString());
                    post.setPostedAt(new Date().getTime());

                    database.getReference().child("posts")
                            .push()
                            .setValue(post)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    postDescription.setText("");
                                    postCount();
                                    Toast.makeText(getContext(), "Posted Succesful", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

                                    ReadableBottomBar readableBottomBar = getActivity().findViewById(R.id.readableButtomBar);
                                    readableBottomBar.selectItem(0);

                                }
                            });
                }
            }
        });

        return view;
    }

    private  void postCount(){
        database.getReference().child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            UserModel user = snapshot.getValue(UserModel.class);
                            database.getReference().child("Users")
                                    .child(auth.getUid())
                                    .child("postCounts")
                                    .setValue(user.getPostCounts()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("postCount","postCount incremented"+user.getPostCounts()+1);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public void registerActivityForSelectImg() {

        ActivityResultSelectImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                int resultCode = result.getResultCode();
                Intent data = result.getData();
                Log.d("incoverpic", "result " + resultCode + " " + data + " " + Activity.RESULT_OK);

                if (resultCode == Activity.RESULT_OK && data != null) {
                    uri = data.getData();
                    postImg.setImageURI(uri);
                    postImg.setVisibility(View.VISIBLE);
                    btnPost.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.gradient_2));
                    btnPost.setTextColor(getContext().getColor(R.color.white));
                    btnPost.setEnabled(true);

                }
            }
        });

    }

    private void fetchingUserDetails() {

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    UserModel user = snapshot.getValue(UserModel.class);
                    Log.d("userDetails", user.getFollowingCounts() + " " + user);

                    //idProfile.setImageDrawable(null);
                    userName.setText(user.getName());
                    profession.setText(user.getProfession());
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.picture).into(profileImg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}