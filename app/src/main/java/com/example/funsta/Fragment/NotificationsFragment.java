package com.example.funsta.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.funsta.Adapter.ViewPagerAdapter;
import com.example.funsta.R;
import com.google.android.material.tabs.TabLayout;


public class NotificationsFragment extends Fragment {


    TabLayout tabLayout;
    ViewPager viewPager;
    public NotificationsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notifications, container, false);
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getParentFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}