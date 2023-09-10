package com.example.funsta.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.funsta.Adapter.userPostAdapter;
import com.example.funsta.Model.PostModel;
import com.example.funsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyPostFragment extends Fragment {


    ArrayList<String> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView userPostRv;
    String userId;

    public MyPostFragment(String userId) {
        this.userId = userId;
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
        View view = inflater.inflate(R.layout.fragment_my_post, container, false);

        userPostRv = view.findViewById(R.id.userPostRv);

        userPostAdapter adapter = new userPostAdapter(getContext(),list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        userPostRv.setLayoutManager(layoutManager);

        fetchPost(adapter);
        return view;
    }

    public void fetchPost(userPostAdapter adapter){

        database.getReference().child("posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot data: snapshot.getChildren()){

                            PostModel model = data.getValue(PostModel.class);

                            if(userId.equals(model.getPostedBy())){
                                if(model.getPostImg()!=null && !model.getPostImg().isEmpty()){
                                    list.add(model.getPostImg());
                                    Log.d("posts",model.getPostImg()+" "+model.getPostedBy());
                                }
                            }
                        }

                        userPostRv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}