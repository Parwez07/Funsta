package com.example.funsta.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.funsta.Fragment.Notifi_NotificationFragment;
import com.example.funsta.Fragment.RequestFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Notifi_NotificationFragment();
            case 1:
                return  new RequestFragment();
            default:return  new Notifi_NotificationFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String tilte =null;
        if(position==0)
            tilte ="NOTIFICATION";
        else if (position==1)
            tilte="REQUEST";
        return tilte;
    }
}
