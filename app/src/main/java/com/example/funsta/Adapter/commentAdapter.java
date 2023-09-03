package com.example.funsta.Adapter;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.commentModel;
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


}
