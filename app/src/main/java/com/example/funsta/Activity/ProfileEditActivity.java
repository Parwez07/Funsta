package com.example.funsta.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.funsta.Model.UserModel;
import com.example.funsta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileEditActivity extends AppCompatActivity {

    ImageView nav_profile, nav_coverPic, editNavProfile, changeCoverPhoto, btnCancel, btnSave;

    Button btnLogOut, btnDeActivate;

    EditText idName, idProfession;

    Uri uriProfile, uriCoverPic;
    String preName ="", name="",preProfession="",profession="";

    private ActivityResultLauncher<Intent> ActivityResultSelectImg, ActivityResultProfile;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        nav_profile = findViewById(R.id.nav_profile_image);
        nav_coverPic = findViewById(R.id.nav_idProfile);
        editNavProfile = findViewById(R.id.editNavProfile);
        changeCoverPhoto = findViewById(R.id.changeCoverPhoto);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnDeActivate = findViewById(R.id.btnDeactiviate);
        btnCancel = findViewById(R.id.profile_close);
        btnSave = findViewById(R.id.profile_save);
        idName = findViewById(R.id.idName);
        idProfession = findViewById(R.id.profession);


        fetchUserDetails();
        registerActivityForSelectImg();

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

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileEditActivity.this, "LogOut", Toast.LENGTH_SHORT).show();
                auth.signOut();
                Intent intent = new Intent(ProfileEditActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDeActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCoverPic();
                setProfilePic();
                setName();
                setProfession();
                finish();

            }
        });

        idName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = idName.getText().toString();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        idProfession.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                profession = idProfession.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private  void deleteAccount(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete Your Account?");
        dialog.setMessage("Your Account will be Delete Permanatly ");

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
    private void fetchUserDetails() {

        database.getReference().child("Users")
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            UserModel user = snapshot.getValue(UserModel.class);
                            Log.d("userDetails", user.getFollowingCounts() + " " + user);
                            Picasso.get().load(user.getCover_photo())
                                    .placeholder(R.drawable.picture)
                                    .into(nav_coverPic);
                            //idProfile.setImageDrawable(null);
                            Picasso.get().load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(nav_profile);
                            Picasso.get().load(user.getProfile())
                                    .placeholder(R.drawable.picture)
                                    .into(nav_profile);
                            preName = user.getName();
                            idName.setText(user.getName());
                            idProfession.setText(user.getProfession());
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

                        uriCoverPic = data.getData();
                        nav_coverPic.setImageURI(uriCoverPic);

                    }

                });


        // setting the profile img here
        ActivityResultProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

                result ->

                {

                    Intent data = result.getData();
                    int resultCode = result.getResultCode();

                    if (resultCode == Activity.RESULT_OK && data != null) {

                        uriProfile = data.getData();
                        nav_profile.setImageURI(uriProfile);

                    }


                });
    }

    private void setCoverPic() {

        if (uriCoverPic != null) {
            final StorageReference reference = storage.getReference().child("cover_photo")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            reference.putFile(uriCoverPic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
    }

    private void setProfilePic() {

        if (uriProfile != null) {
            final StorageReference reference = storage.getReference().child("profile_img")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            reference.putFile(uriProfile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
    }

    private void setName() {

        if (!name.isEmpty() && !preName.equals(name)) {
            database.getReference().child("Users")
                    .child(auth.getUid())
                    .child("name")
                    .setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                        }
                    });
        }
    }

    private void setProfession(){
        if (!profession.isEmpty() && !preProfession.equals(profession)) {
            database.getReference().child("Users")
                    .child(auth.getUid())
                    .child("profession")
                    .setValue(profession).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
        }
    }


}