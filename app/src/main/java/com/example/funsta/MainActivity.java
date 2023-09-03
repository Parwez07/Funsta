package com.example.funsta;

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

    private static final long DOUBLE_BACK_PRESS_INTERVAL = 2000; // 2 seconds
    private long lastBackPressTime = 0;

    private boolean doubleBackToExitPressedOnce = false;
    public ReadableBottomBar readableBottomBar;

    Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView nav_menu, nav_profile, nav_coverPic, editNavProfile, changeCoverPhoto;
    TextView nav_profileName;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public ActivityResultLauncher<Intent> ActivityResultSelectImg, ActivityResultProfile;

    ActionBarDrawerToggle toggle;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

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
        nav_profile = headerView.findViewById(R.id.nav_profile_image);
        nav_coverPic = headerView.findViewById(R.id.nav_idProfile);
        editNavProfile = headerView.findViewById(R.id.editNavProfile);
        changeCoverPhoto = headerView.findViewById(R.id.changeCoverPhoto);

        setSupportActionBar(toolbar);
        drawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        fetchingCoverPhoto();
        registerActivityForSelectImg();
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
        transaction.add(R.id.container, new HomeFragment());
        transaction.commit();
        readableBottomBar = findViewById(R.id.readableButtomBar);
        readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (i) {

                    case 0:

                        setToolbarAndDrawerState(false);
                        transaction.replace(R.id.container, new HomeFragment());
                        break;
                    case 1:

                        setToolbarAndDrawerState(false);
                        transaction.replace(R.id.container, new NotificationsFragment());

                        break;
                    case 2:

                        setToolbarAndDrawerState(false);
                        transaction.replace(R.id.container, new PostFragment());
                        break;
                    case 3:

                        setToolbarAndDrawerState(false);
                        transaction.replace(R.id.container, new SearchFragment());
                        break;
                    case 4:
                        toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.container, new newProfileFragment());
                        break;
                    default:
                        setToolbarAndDrawerState(false);
                        transaction.replace(R.id.container, new HomeFragment());
                        break;
                }
                transaction.commit();

            }
        });


        changeCoverPhoto.setOnClickListener(v -> {

            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            } else {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra("code", 1);

                //startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                ActivityResultSelectImg.launch(galleryIntent);
                Log.d("incoverpic", "correct");
            }
        });

        editNavProfile.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            } else {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                galleryIntent.putExtra("code", 2);

                //startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                ActivityResultProfile.launch(galleryIntent);
                Log.d("incoverpic", "correct inside the coverPhoto");
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
        long currentTime = System.currentTimeMillis();

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            if (currentTime - lastBackPressTime < DOUBLE_BACK_PRESS_INTERVAL) {
                super.onBackPressed(); // Close the app completely
            } else {
                lastBackPressTime = currentTime;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void fetchingCoverPhoto() {

        database.getReference().child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            UserModel user = snapshot.getValue(UserModel.class);
                            Log.d("userDetails", user.getFollowersCount() + " " + user);
                            Picasso.get().load(user.getCover_photo())
                                    .placeholder(R.drawable.picture)
                                    .into(nav_coverPic);
                            //idProfile.setImageDrawable(null);
                            nav_profileName.setText(user.getName());
                            Picasso.get().load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(nav_profile);
                            Picasso.get().load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(nav_profile);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void registerActivityForSelectImg() {


        // setting the coverPic
        ActivityResultSelectImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

                result -> {

                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    Log.d("incoverpic", "result " + resultCode + " " + data + " " + Activity.RESULT_OK);

                    if (resultCode == Activity.RESULT_OK && data != null) {

                        Uri uri = data.getData();

                        nav_coverPic.setImageURI(uri);
                        final StorageReference reference = storage.getReference().child("cover_photo")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Cover_photo saved", Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(auth.getUid()).child("cover_photo")
                                                .setValue(uri.toString());
                                    }
                                });
                            }
                        });

                    }
                });


        // setting the profile img here
        ActivityResultProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

                result ->

                {

                    Intent data = result.getData();
                    int resultCode = result.getResultCode();

                    if (resultCode == Activity.RESULT_OK && data != null) {

                        Uri uri = data.getData();


                        nav_profile.setImageURI(uri);
                        final StorageReference reference = storage.getReference().child("profile_img")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "profile_img saved", Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(auth.getUid()).child("profile")
                                                .setValue(uri.toString());
                                    }
                                });
                            }
                        });

                    }

                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("navigation", "navigation me aa gai");
        FragmentManager fragmentManager = getSupportFragmentManager();
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


    //@Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()){
//            case R.id.setting:
//                auth.signOut();
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        new ProfileFragment().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}