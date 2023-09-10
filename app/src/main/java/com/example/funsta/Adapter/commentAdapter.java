package com.example.funsta.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.commentModel;
import com.example.funsta.Model.followersModel;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.commentViewHolder> {

    AppCompatActivity context;
    ArrayList<commentModel> list;
    String postedBy;
    String postId;
    Dialog dialog;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public commentAdapter(AppCompatActivity context, ArrayList<commentModel> list, String postId, String postedBy) {
        this.context = context;
        this.list = list;
        this.postId = postId;
        this.postedBy = postedBy;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


    @NonNull
    @Override
    public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.comment_sample, parent, false);
        return new commentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentViewHolder holder, int position) {

        commentModel comment = list.get(position);

        Log.d("comm", comment.getCommentBody() + ' ');
        String time = TimeAgo.using(comment.getCommentedAt());
        holder.time.setText(time);

        database.getReference().child("Users")
                .child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        Picasso.get().load(user.getProfile())
                                .placeholder(R.drawable.picture)
                                .into(holder.profileImg);
                        holder.comments.setText(Html.fromHtml("<b>" + user.getName() + "</b>" + "<br>" + comment.getCommentBody(), 0));
                        Log.d("comme", holder.comments.getText().toString());
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

    public class commentViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg, btnOption;
        TextView time, comments;

        public commentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_image);
            time = itemView.findViewById(R.id.time);
            comments = itemView.findViewById(R.id.profileName);
            btnOption = itemView.findViewById(R.id.options);

            btnOption.setOnClickListener(v -> showPopupMenu(getAdapterPosition(), v));

        }

        private void showPopupMenu(int position, View view) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), view);
            popupMenu.inflate(R.menu.comment_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.btnEdit:

                        if (auth.getUid().equals(list.get(position).getCommentedBy())) {
                            showEditBottomSheet(list.get(position));
                        } else {
                            Toast.makeText(context, "Other user cannot edit other's comments", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("delete", "deleting the commet");
                        return true;
                    case R.id.btnDelete:
                        // Delete the comment from Firebase
                        Log.d("delete", auth.getUid().toString() + " " + list.get(position).getCommentedBy() + " " + postedBy);
                        if (auth.getUid().equals(list.get(position).getCommentedBy()) || auth.getUid().equals(postedBy)) {
                            deleteComment(position);
                        } else {
                            Toast.makeText(context, "Other user cannot delete other's comments", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        }

        private void showEditBottomSheet(commentModel cModel) {



            dialog.setContentView(R.layout.fragment_comment_bottom_sheet);
            EditText updatedComment = dialog.findViewById(R.id.etComment);

            ImageView send = dialog.findViewById(R.id.imgSend);
            String comment = cModel.getCommentBody();
            updatedComment.setText(comment);

            send.setEnabled(false); // Initially disable the send button

            updatedComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Called before the text changes
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Called while the text is changing
                    String newText = charSequence.toString().trim();

                    // Check if the text has actually changed
                    if (!newText.equals(comment)) {
                        send.setEnabled(true);
                        cModel.setCommentBody(newText);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Called after the text has changed
                }
            });

            send.setOnClickListener(v -> {
                updateCommentInFirebase(cModel);
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.getWindow().setGravity(Gravity.BOTTOM);

        }


        public void updateCommentInFirebase(commentModel cModel) {
            database.getReference().child("posts")
                    .child(postId)
                    .child("comments")
                    .child(cModel.getCommentId())
                    .child("commentBody")
                    .setValue(cModel.getCommentBody()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            dialog.dismiss();
                            Toast.makeText(context, "Your comment Updated", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

        public void deleteComment(int position) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("DELETE");
            dialog.setMessage("Delete the Comment permanently ?");

            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database.getReference().child("posts")
                            .child(postId)
                            .child("comments")
                            .child(list.get(position).getCommentId())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Comment deleted from Firebase, update local data and UI
                                        database.getReference().child("posts")
                                                .child(postId)
                                                .child("commentCounts")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        int commentsCount = 0;
                                                        if (snapshot.exists()) {
                                                            commentsCount = snapshot.getValue(Integer.class);
                                                        }
                                                        database.getReference().child("posts")
                                                                .child(postId)
                                                                .child("commentCounts")
                                                                .setValue(commentsCount - 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(context, "Comment deleted Successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }
                                }
                            });
                }
            });
            dialog.show();
            dialog.create();


        }

    }


    public static class followers_followingAdapter extends RecyclerView.Adapter<followers_followingAdapter.followDetails> {

        Context context;
        ArrayList<UserModel> list;

        UserModel user;

        FirebaseDatabase database;

        FirebaseAuth auth;

        public followers_followingAdapter(Context context, ArrayList<UserModel> list) {
            this.context = context;
            this.list = list;
            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();

        }

        @NonNull
        @Override
        public followDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.user_search, parent, false);

            return new followDetails(view);
        }

        @Override
        public void onBindViewHolder(@NonNull followDetails holder, int position) {

            user = list.get(position);
            userDetailsFollowUnfollow(user, holder);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class followDetails extends RecyclerView.ViewHolder {


            Button btnFollow;
            TextView idName, profession;
            ImageView idProfile;

            public followDetails(@NonNull View itemView) {
                super(itemView);

                btnFollow = itemView.findViewById(R.id.btnEdit);
                idName = itemView.findViewById(R.id.idName);
                profession = itemView.findViewById(R.id.idProfession);
                idProfile = itemView.findViewById(R.id.profile_image);
            }
        }


        public void userDetailsFollowUnfollow(UserModel user, followDetails holder) {

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


    }
}
