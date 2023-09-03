package com.example.funsta.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsta.Model.UserModel;
import com.example.funsta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    TextView login;
    EditText idName, idEmail, idPassword, idcp;
    Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        idEmail = findViewById(R.id.idEmail);
        idPassword = findViewById(R.id.idPassword);
        idName = findViewById(R.id.idName);
        btnSignUp = findViewById(R.id.btnSignUp);
        login = findViewById(R.id.tvLogin);
        idcp = findViewById(R.id.idCp);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Sign Up process

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = idEmail.getText().toString();
                String password = idPassword.getText().toString();
                String cp = idcp.getText().toString();
                // check for password
                if (password.length() < 8 || !isValidPassword(password) || !password.equals(cp)) {
                    Toast.makeText(SignUpActivity.this, "Enter NumAlphaChar password or Passwords are not matching", Toast.LENGTH_SHORT).show();
                    return;
                }
                register(email, password);

            }
        });

        // when clicking on login textView
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void register(String email, String password) {
        String name = idName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            UserModel user = new UserModel(name, email, password);
                            String id = task.getResult().getUser().getUid().toString();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(SignUpActivity.this, "User added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
}