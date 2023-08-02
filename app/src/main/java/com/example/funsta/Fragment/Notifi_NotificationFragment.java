package com.example.funsta.Fragment;

import android.os.Bundle;

import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funsta.Adapter.notificationAdapter;
import com.example.funsta.Model.notificationModel;
import com.example.funsta.R;

import java.util.ArrayList;

public class Notifi_NotificationFragment extends Fragment {

    RecyclerView notificationRv;
    ArrayList<notificationModel> list;
    public Notifi_NotificationFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifi__notification, container, false);
        notificationRv = view.findViewById(R.id.notificationRv);
        list = new ArrayList<>();
        list.add(new notificationModel(R.drawable.profile_image,"<b>Asif</b> Mention in his comment","1:00"));
        list.add(new notificationModel(R.drawable.inaya,"<b>Inaya </b>Mention to you in status","2:00"));

        list.add(new notificationModel(R.drawable.profile_image,"<b>Asif</b> Mention in his comment","1:00"));
        list.add(new notificationModel(R.drawable.inaya,"<b>Inaya </b>Mention to you in status","2:00"));list.add(new notificationModel(R.drawable.profile_image,"<b>Asif</b> Mention in his comment","1:00"));
        list.add(new notificationModel(R.drawable.inaya,"<b>Inaya </b>Mention to you in status","2:00"));list.add(new notificationModel(R.drawable.profile_image,"<b>Asif</b> Mention in his comment","1:00"));
        list.add(new notificationModel(R.drawable.inaya,"<b>Inaya </b>Mention to you in status","2:00"));
        notificationAdapter adapter = new notificationAdapter(getContext(),list);
        notificationRv.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationRv.setAdapter(adapter);
        return view ;
    }
}