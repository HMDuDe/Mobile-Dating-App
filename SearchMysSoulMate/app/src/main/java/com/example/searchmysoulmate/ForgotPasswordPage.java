package com.example.searchmysoulmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ForgotPasswordPage extends AppCompatActivity {

    private EditText emailTxt, secretQuizTxt;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        emailTxt = findViewById(R.id.emailTxt);
        auth = FirebaseAuth.getInstance();

        configDoneBtn();
    }

    private void configDoneBtn(){
        Button done = findViewById(R.id.doneBtn);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Sending password reset email
                 auth.sendPasswordResetEmail(String.valueOf(emailTxt.getText()))
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 Toast.makeText(ForgotPasswordPage.this, "Email Sent", Toast.LENGTH_SHORT).show();
                             }
                         })
                         .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(ForgotPasswordPage.this, "PASSWORD RESET FAILED", Toast.LENGTH_SHORT).show();
                                 Log.d("USER EMAIL: ", "" + emailTxt.getText());
                             }
                         });

                //Returning to login page
                startActivity(new Intent(ForgotPasswordPage.this, MainActivity.class));
            }
        });
    }
}