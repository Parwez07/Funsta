package com.example.funsta.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.funsta.Adapter.searchAdapter;
import com.example.funsta.Model.UserModel;
import com.example.funsta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchFragment extends Fragment {

    ArrayList<UserModel> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;

    ShimmerRecyclerView searchRv;

    EditText searchUser;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchRv = view.findViewById(R.id.searchRv);
        searchUser = view.findViewById(R.id.serch);

        searchRv.showShimmerAdapter();
        searchAdapter adapter = new searchAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        searchRv.setLayoutManager(layoutManager);
        fetchUsers(adapter);
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterSearch(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void fetchUsers(searchAdapter adapter) {

        String[] userProfession = {""};
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    user.setUserId(dataSnapshot.getKey());

                    if (!dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())) {
                        list.add(user);
                    } else {

                        userProfession[0] = user.getProfession().toString();
                        Log.d("userid", "datanapshot key " + userProfession[0] + dataSnapshot.getKey() + "auth currentuser " + auth.getCurrentUser().getUid());
                    }
                }

                sortList(list, userProfession[0]);

                searchRv.setAdapter(adapter);
                searchRv.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sortList(ArrayList<UserModel> list, String profession) {

        if (!profession.isEmpty())
            Collections.sort(list, new ProfessionComparator(profession));
    }


    class ProfessionComparator implements Comparator<UserModel> {
        private String loggedInUserProfession;

        public ProfessionComparator(String loggedInUserProfession) {
            this.loggedInUserProfession = loggedInUserProfession;
        }

        @Override
        public int compare(UserModel user1, UserModel user2) {

            Log.d("compare", user1.getProfession() + " " + user2.getProfession());

            if (user1.getProfession() == null || user1.getProfession().isEmpty())
                return 1;
            else if (user2.getProfession() == null || user2.getProfession().isEmpty())
                return -1;
            else if (user1.getProfession().toLowerCase().equals(loggedInUserProfession.toLowerCase())) {
                return -1;
            } else if (user2.getProfession().toLowerCase().equals(loggedInUserProfession.toLowerCase())) {
                return 1;
            } else {
                return user1.getProfession().toLowerCase().compareTo(user2.getProfession().toLowerCase());
            }

        }
    }
}