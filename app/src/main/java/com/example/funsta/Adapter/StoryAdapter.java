package com.example.funsta.Adapter;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.devlomi.circularstatusview.CircularStatusView;
import com.example.funsta.Model.StoryModel;
import com.example.funsta.Model.UserModel;
import com.example.funsta.Model.userStories;
import com.example.funsta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    Context context;
    ArrayList<StoryModel> list;

    public StoryAdapter(Context context, ArrayList<StoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_rv_design, parent, false);
        StoryViewHolder viewHolder = new StoryViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoryModel story = list.get(position);

        if (story.getStories().size() > 0) {
            userStories lastStory = story.getStories().get(story.getStories().size() - 1);
            Picasso.get()
                    .load(lastStory.getStoryImg())
                    .into(holder.storyImg);
            holder.statusCircle.setPortionsCount(story.getStories().size());

            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(story.getStoryBy()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(holder.profileImg);
                            holder.profileName.setText(user.getName());

                            holder.storyImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (userStories story : story.getStories()) {
                                        myStories.add(new MyStory(
                                                story.getStoryImg()

                                        ));
                                    }
                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories) // Required
                                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                            .setTitleText(user.getName()) // Default is Hidden
                                            .setSubtitleText("") // Default is Hidden
                                            .setTitleLogoUrl(user.getProfile()==null?context.getDrawable(R.drawable.picture).toString() :user.getProfile()) // Default is Hidden
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {
                                                    //your action
                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                    //your action
                                                }
                                            }) // Optional Listeners
                                            .build() // Must be called before calling show method
                                            .show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        CircularStatusView statusCircle;
        ImageView storyType, storyImg, profileImg;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            profileName = itemView.findViewById(R.id.profileName);
            storyImg = itemView.findViewById(R.id.story);
            profileImg = itemView.findViewById(R.id.Profile_image);
            storyType = itemView.findViewById(R.id.storyType);
            statusCircle = itemView.findViewById(R.id.statusCircle);
        }
    }
}
