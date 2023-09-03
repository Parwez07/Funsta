package com.example.funsta;

import static com.example.funsta.R.id.container;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsta.Activity.LoginActivity;
import com.example.funsta.Fragment.HomeFragment;
import com.example.funsta.Fragment.NotificationsFragment;
import com.example.funsta.Fragment.PostFragment;
import com.example.funsta.Fragment.ProfileFragment;
import com.example.funsta.Fragment.SearchFragment;
import com.example.funsta.Fragment.newProfileFragment;
import com.example.funsta.Model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final long BACK_PRESS_INTERVAL = 200; // 2 seconds
    private long lastBackPressTime = 0;

    private boolean doubleBackToExitPressedOnce = false;
    public ReadableBottomBar readableBottomBar;

    Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView nav_menu;
    TextView nav_profileName;



    ActionBarDrawerToggle toggle;

    FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        nav_menu = findViewById(R.id.nav_menu);
        View headerView = navigationView.getHeaderView(0);
        nav_profileName = headerView.findViewById(R.id.NavprofileName);


        setSupportActionBar(toolbar);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();



        nav_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.openDrawer(GravityCompat.END);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        });

        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setVisibility(View.GONE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        setToolbarAndDrawerState(false);
        transaction.add(container, new HomeFragment());
        transaction.commit();
        readableBottomBar = findViewById(R.id.readableButtomBar);
        readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (i) {

                    case 0:

                        setToolbarAndDrawerState(false);
                        transaction.replace(container, new HomeFragment());
                        break;
                    case 1:

                        setToolbarAndDrawerState(false);
                        transaction.replace(container, new NotificationsFragment());

                        break;
                    case 2:

                        setToolbarAndDrawerState(false);
                        transaction.replace(container, new PostFragment());
                        break;
                    case 3:

                        setToolbarAndDrawerState(false);
                        transaction.replace(container, new SearchFragment());
                        break;
                    case 4:
                        toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(container, new newProfileFragment());
                        break;
                    default:
                        setToolbarAndDrawerState(false);
                        transaction.replace(container, new HomeFragment());
                        break;
                }
                transaction.commit();

            }
        });




    }

    // Method to toggle Toolbar and NavigationDrawer
    public void setToolbarAndDrawerState(boolean enabled) {
        if (enabled) {
            getSupportActionBar().show();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        } else {
            getSupportActionBar().hide();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(container);

            if (currentFragment instanceof HomeFragment) {
                if (doubleBackToExitPressedOnce) {
                    finish();

                }

                this.doubleBackToExitPressedOnce = true;
            } else {
                // Navigate to home fragment
                readableBottomBar.selectItem(0);
            }
        }
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("navigation", "navigation me aa gai");
        switch (item.getItemId()) {
            case R.id.home:
                readableBottomBar.selectItem(0);
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack(new newProfileFragment().getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportActionBar().hide();
                break;

            case R.id.logout:
                Toast.makeText(this, "LogOut", Toast.LENGTH_SHORT).show();
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        new ProfileFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}