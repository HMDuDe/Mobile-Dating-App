package com.example.searchmysoulmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.searchmysoulmate.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPage extends AppCompatActivity {

    private DatabaseReference dbRef;
    private FirebaseAuth authenticate;

    private String fName, lName, email, jobTitle, company, homeCity, password, confirmPassword, gender;
    EditText fNameTxt, lNameTxt, emailTxt, jobTitleTxt, companyTxt, homeCityTxt, passwordTxt, confirmPasswordTxt;
    Switch maleSwitch, femaleSwitch;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);


        intent = new Intent(RegistrationPage.this, registrationPagePart2.class);

        authenticate = FirebaseAuth.getInstance();

        configCancelBtn();
        configNextBtn();
    }

    private void configCancelBtn(){
        Button cancel = findViewById(R.id.CancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationPage.this, MainActivity.class));

            }
        });
    }

    public void configNextBtn(){
        Button nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef = FirebaseDatabase.getInstance().getReference("Users");

                fNameTxt = findViewById(R.id.fNameTxt);
                lNameTxt = findViewById(R.id.surnameTxt);
                emailTxt = findViewById(R.id.emailTxt);
                jobTitleTxt = findViewById(R.id.jobTitleTxt);
                companyTxt = findViewById(R.id.companyTxt);
                homeCityTxt = findViewById(R.id.homeCityTxt);
                passwordTxt = findViewById(R.id.passwordTxt);
                confirmPasswordTxt = findViewById(R.id.confirmPwTxt);
                maleSwitch = findViewById(R.id.maleSwitch);
                femaleSwitch = findViewById(R.id.femaleSwitch);

                fName = String.valueOf(fNameTxt.getText());
                lName = String.valueOf(lNameTxt.getText());
                email = String.valueOf(emailTxt.getText());
                jobTitle = String.valueOf(jobTitleTxt.getText());
                company = String.valueOf(companyTxt.getText());
                homeCity = String.valueOf(homeCityTxt.getText());
                password = String.valueOf(passwordTxt.getText());
                confirmPassword = String.valueOf(confirmPasswordTxt.getText());

                if(fName.equals("") || lName.equals("") || email.equals("") || jobTitle.equals("") || company.equals("") || homeCity.equals("") || password.equals("")){
                    Toast.makeText(RegistrationPage.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                }else{
                    if(password.length() >= 6){
                        if(password.equals(confirmPassword) && confirmPassword != ""){
                            User newUser = new User();

                            newUser.setfName(fName);
                            newUser.setlName(lName);
                            newUser.setJobTitle(jobTitle);
                            newUser.setCompany(company);
                            newUser.setHomeCity(homeCity);

                            if(maleSwitch.isChecked() && femaleSwitch.isChecked()){
                                Toast.makeText(RegistrationPage.this, "Maximum of 1 gender may be selected!", Toast.LENGTH_SHORT).show();
                            }else if(maleSwitch.isChecked()){
                                gender = String.valueOf(maleSwitch.getText());
                                femaleSwitch.setChecked(false);

                            }else if(femaleSwitch.isChecked()){
                                gender = String.valueOf(femaleSwitch.getText());
                                maleSwitch.setChecked(false);
                            }
                            newUser.setGender(gender);

                            newUser.setEmail(email);
                            newUser.setPassword(password);

                            intent.putExtra("userObject", newUser);
                            startActivity(intent);

                        }else{
                            Toast.makeText(RegistrationPage.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        Toast.makeText(RegistrationPage.this, "Password too short! Min 6 characters", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

