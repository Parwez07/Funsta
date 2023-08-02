package com.example.funsta.Fragment;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.funsta.Adapter.DashboardAdapter;
import com.example.funsta.Adapter.StoryAdapter;
import com.example.funsta.Model.DashboardModel;
import com.example.funsta.Model.StoryModel;
import com.example.funsta.R;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {


    TextView textView;
    RecyclerView storyRv, dashboardRv;
    ArrayList<StoryModel> list;


    ArrayList<DashboardModel> dashboardList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

            // setting the gradient color to the text "funsta"
            textView = view.findViewById(R.id.textView);
            textView.setText(getResources().getString(R.string.funsta));

            TextPaint paint = textView.getPaint();
            float width = paint.measureText(getResources().getString(R.string.funsta));

            Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                    new int[]{
                            Color.parseColor("#DD129D"),
                            Color.parseColor("#FBC02D"),
                    }, null, Shader.TileMode.CLAMP);
            textView.getPaint().setShader(textShader);

            // recyclerView story

            storyRv = view.findViewById(R.id.storyRv);
            list = new ArrayList<>();

            list.add(new StoryModel(R.drawable.inaya, R.drawable.live, R.drawable.profile_image, "Asif"));
            list.add(new StoryModel(R.drawable.profile_image, R.drawable.live, R.drawable.inaya, "inaya"));
            list.add(new StoryModel(R.drawable.inaya, R.drawable.live, R.drawable.profile_image, "Asif"));
            list.add(new StoryModel(R.drawable.profile_image, R.drawable.live, R.drawable.inaya, "inaya"));

            StoryAdapter adapter = new StoryAdapter(getContext(), list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            storyRv.setLayoutManager(layoutManager);
            storyRv.setNestedScrollingEnabled(true);
            storyRv.setAdapter(adapter);

            // dashboard recyclerview

            dashboardList = new ArrayList<>();
            dashboardRv = view.findViewById(R.id.dashboardRv);

            dashboardList.add(new DashboardModel(R.drawable.profile_image, R.drawable.inaya, R.drawable.bookmark, "Inaya", "Dr", "250",
                    "2", "12"));
            dashboardList.add(new DashboardModel(R.drawable.profile_image, R.drawable.inaya, R.drawable.saved, "Inaya", "Dr", "250",
                    "2", "12"));
            dashboardList.add(new DashboardModel(R.drawable.profile_image, R.drawable.inaya, R.drawable.bookmark, "Inaya", "Dr", "250",
                    "2", "12"));
            dashboardList.add(new DashboardModel(R.drawable.profile_image, R.drawable.inaya, R.drawable.bookmark, "Inaya", "Dr", "250",
                    "2", "12"));

            DashboardAdapter dashboardAdapter = new DashboardAdapter(getContext(), dashboardList);
        LinearLayoutManager dashLinearLayoutManager = new LinearLayoutManager(getContext());
            dashboardRv.setLayoutManager(dashLinearLayoutManager);
            dashboardRv.setNestedScrollingEnabled(false);
            dashboardRv.setAdapter(dashboardAdapter);


            return view;
        }


}

