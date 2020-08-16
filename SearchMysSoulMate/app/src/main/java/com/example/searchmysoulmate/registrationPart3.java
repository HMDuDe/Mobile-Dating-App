package com.example.searchmysoulmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class registrationPart3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registraton_part_3);

        configProceedButtons();
        configBackBtn();
        configHomeBtn();
    }

    public void configBackBtn(){
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registrationPart3.this, registrationPagePart2.class));
            }
        });
    }

    public void configHomeBtn(){
        Button homeBtn = findViewById(R.id.HomeBTN3);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registrationPart3.this, HomePage.class));
            }
        });
    }

    public void configProceedButtons(){
        Button continue1 = findViewById(R.id.yes_emphasis);
        continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registrationPart3.this, HomePage.class));
            }
        });

        Button continue2 = findViewById(R.id.relaxed_yes);
        continue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registrationPart3.this, HomePage.class));
            }
        });

        Button continue3 = findViewById(R.id.AllHopeLost);
        continue3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registrationPart3.this, HomePage.class));
            }
        });
    }
}
