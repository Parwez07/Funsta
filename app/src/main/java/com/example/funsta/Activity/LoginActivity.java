package com.example.funsta.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.funsta.MainActivity;
import com.example.funsta.R;
import com.example.funsta.painter.GradientBackgroundPainter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private GradientBackgroundPainter gradientBackgroundPainter;
    private static final long DOUBLE_BACK_PRESS_INTERVAL = 2000; // 2 seconds
    private long lastBackPressTime = 0;

    TextView register ;
    EditText idemail,idpassword;
    Button btnLogin;

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.tvRegister);
        btnLogin = findViewById(R.id.btnLogin);
        idemail = findViewById(R.id.idEmail);
        idpassword =findViewById(R.id.idPassword);

        auth= FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = idpassword.getText().toString();
                String email = idemail.getText().toString();

                if(!password.isEmpty()&&!email.isEmpty()){
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(LoginActivity.this, "fill correct email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "Please fill the required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBackPressTime < DOUBLE_BACK_PRESS_INTERVAL) {
            super.onBackPressed(); // Close the app completely
        } else {
            lastBackPressTime = currentTime;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}

//    @Override//        View backgroundImage = findViewById(R.id.root_view);
//        final int[] drawables = new int[3];
//        drawables[0] = R.drawable.gradient_1;
//        drawables[1] = R.drawable.gradient_2;
//        drawables[2] = R.drawable.gradient_3;
//
//        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
//        gradientBackgroundPainter.start();
//    protected void onDestroy() {
//        super.onDestroy();
//        gradientBackgroundPainter.stop();
//    }
