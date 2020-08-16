package com.example.searchmysoulmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private String username, password;
    EditText usernameTxt, passwordTxt;

    //Firebase connection needed
    private FirebaseAuth authenticate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticate = FirebaseAuth.getInstance();
        FirebaseUser currentUser = authenticate.getCurrentUser();

        usernameTxt = findViewById(R.id.userNameTxt);
        passwordTxt = findViewById(R.id.passwordTxt);

        if(currentUser != null){
            usernameTxt.setText(currentUser.getEmail());

        }

        configLoginBtn();
        configSignupLbl();
        configForgotPasswordLbl();
    }

    //Sign in method
    private void configLoginBtn(){
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = String.valueOf(usernameTxt.getText());
                password = String.valueOf(passwordTxt.getText());

                authenticate.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this, HomePage.class));
                                }else{
                                    Toast.makeText(MainActivity.this, "Incorrect sing in details", Toast.LENGTH_LONG).show();

                                }

                                Log.d("Sign in Result: ", "" + task);
                            }
                        });

            }
        });
    }

    private void configSignupLbl(){
        TextView regPage = findViewById(R.id.SignUpClickLbl);
        regPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrationPage.class));
            }
        });
    }

    private void configForgotPasswordLbl(){
        TextView forgotPassword = findViewById(R.id.forgetPasswordClickLbl);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordPage.class));

            }
        });
    }
}