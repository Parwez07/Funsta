package com.example.funsta.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.funsta.Adapter.SavePostAdapter;
import com.example.funsta.Model.PostModel;
import com.example.funsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SavedPostFragment extends Fragment {


    ArrayList<String> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView saveRv;

    public SavedPostFragment() {
        // Required empty public constructor
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_post, container, false);
        saveRv = view.findViewById(R.id.savePost);

        SavePostAdapter adapter = new SavePostAdapter(getContext(), list);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        saveRv.setLayoutManager(layoutManager);

        fetchSavedPost(adapter);

        return view;
    }

    public void fetchSavedPost(SavePostAdapter adapter) {

        database.getReference().child("Users")
                .child(auth.getUid())
                .child("savedPosts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String postId = data.getKey();

                                database.getReference().child("posts")
                                        .child(postId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                PostModel model = snapshot.getValue(PostModel.class);
                                                list.add(model.getPostImg());

                                                saveRv.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                Log.d("postId", data.getKey() + "");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}