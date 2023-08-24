package com.example.funsta.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsta.Adapter.commentAdapter;
import com.example.funsta.Model.PostModel;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.commentModel;
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

public class CommentActivity extends AppCompatActivity {

    Intent intent;
    String postId, postedBy;
    ImageView postImg, profileImg, imgSendbtn;
    TextView profileName, postDescription, like, comments, share;
    EditText etComment;
    RecyclerView commentRv;
    Toolbar toolbar;
    ArrayList<commentModel> list= new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        toolbar = findViewById(R.id.toolbar2);

        setSupportActionBar(toolbar);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        Log.d("commentId", postedBy + " " + postId);

        postImg = findViewById(R.id.postImg);
        profileImg = findViewById(R.id.profile_image);
        imgSendbtn = findViewById(R.id.imgSend);
        profileName = findViewById(R.id.idProfileName);
        postDescription = findViewById(R.id.postDescription);
        like = findViewById(R.id.like);
        comments = findViewById(R.id.profileName);
        share = findViewById(R.id.share);
        etComment = findViewById(R.id.etComment);
        commentRv = findViewById(R.id.commentRv);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        database.getReference().child("posts")
                .child(postId)
                .child("likes")
                .child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference().child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        PostModel post = snapshot.getValue(PostModel.class);
                        Picasso.get()
                                .load(post.getPostImg())
                                .placeholder(R.drawable.picture)
                                .into(postImg);
                        postDescription.setText(post.getPostDescription());
                        like.setText(post.getPostLikes()+"");
                        comments.setText(post.getCommentCounts()+"");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        database.getReference().child("Users")
                .child(postedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get()
                                .load(user.getProfile())
                                .placeholder(R.drawable.picture)
                                .into(profileImg);
                        profileName.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String comment = etComment.getText().toString();
                if(!comment.isEmpty()){
                    imgSendbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            commentModel commment = new commentModel();
                            commment.setCommentBody(etComment.getText().toString());
                            commment.setCommentedAt(new Date().getTime());
                            commment.setCommentedBy(auth.getUid());

                            database.getReference().child("posts")
                                    .child(postId)
                                    .child("comments")
                                    .push()
                                    .setValue(commment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            database.getReference().child("posts")
                                                    .child(postId)
                                                    .child("commentCounts")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            int commentsCount =0;
                                                            if(snapshot.exists()){
                                                                commentsCount = snapshot.getValue(Integer.class);
                                                            }
                                                            database.getReference().child("posts")
                                                                    .child(postId)
                                                                    .child("commentCounts")
                                                                    .setValue(commentsCount+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            etComment.setText("");
                                                                            Toast.makeText(CommentActivity.this, "commented", Toast.LENGTH_SHORT).show();

                                                                            notificationModel notification = new notificationModel();
                                                                            notification.setNotificationBy(auth.getUid());
                                                                            notification.setNotificatonAt(new Date().getTime());
                                                                            notification.setPostId(postId);
                                                                            notification.setPosdtedBy(postedBy);
                                                                            notification.setType("comment");

                                                                            database.getReference()
                                                                                    .child("notifications")
                                                                                    .child(postedBy)
                                                                                    .push()
                                                                                    .setValue(notification);

                                                                        }
                                                                    });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }
                                    });

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        commentAdapter commentAdapter = new commentAdapter(CommentActivity.this,list,postId,postedBy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CommentActivity.this);
        commentRv.setLayoutManager(layoutManager);
        commentRv.setAdapter(commentAdapter);

        database.getReference().child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                            commentModel comment = dataSnapshot.getValue(commentModel.class);
                            comment.setCommentId(dataSnapshot.getKey());
                            list.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}
