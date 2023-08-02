package com.example.funsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.funsta.Fragment.HomeFragment;
import com.example.funsta.Fragment.NotificationsFragment;
import com.example.funsta.Fragment.PostFragment;
import com.example.funsta.Fragment.ProfileFragment;
import com.example.funsta.Fragment.SearchFragment;
import com.iammert.library.readablebottombar.ReadableBottomBar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public ReadableBottomBar readableBottomBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MainActivity.this.setTitle("My profiles");

        toolbar.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();
        readableBottomBar = findViewById(R.id.readableButtomBar);
        readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (i) {

                    case 0:
                        toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new HomeFragment());
                        break;
                    case 1:
                        toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new NotificationsFragment());
                        break;
                    case 2:
                        toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new PostFragment());
                        break;
                    case 3:
                        toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container, new SearchFragment());
                        break;
                    case 4:
                        toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.container, new ProfileFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        new ProfileFragment().onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}