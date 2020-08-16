package com.example.searchmysoulmate.Models;

import android.net.Uri;


import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String userID, fName, lName, email, jobTitle, company, homeCity, password, age, aboutMe, gender, mainProfileImgPath;
    private String addImg2, addImg3, addImg4, addImg5, addImg6;
    private String minAge, maxAge;
    private boolean showMales, showFemales;
    private List<String> blockedContacts;

    public User(){
        //default
    }

    public User(String userID,
                String fName,
                String lName,
                String email,
                String jobTitle,
                String company,
                String homeCity,
                String password,
                String age,
                String aboutMe,
                String gender,
                Boolean showMales,
                Boolean showFemales,
                String mainProfileImgPath,
                List<String> blockedContacts,
                String minAge,
                String maxAge,
                String addImg2,
                String addImg3,
                String addImg4,
                String addImg5,
                String addImg6){

        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.jobTitle = jobTitle;
        this.company = company;
        this.homeCity = homeCity;
        this.password = password;
        this.age = age;
        this.aboutMe = aboutMe;
        this.gender = gender;
        this.showMales = showMales;
        this.showFemales = showFemales;
        this.mainProfileImgPath = mainProfileImgPath;
        this.userID = userID;
        this.blockedContacts = blockedContacts;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.addImg2 = addImg2;
        this.addImg3 = addImg3;
        this.addImg4 = addImg4;
        this.addImg5 = addImg5;
        this.addImg6 = addImg6;

    }

    //Setters

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setfName(String fName){
        this.fName = fName;
    }

    public void setlName(String lName){
        this.lName = lName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setJobTitle(String jobTitle){
        this.jobTitle = jobTitle;
    }

    public void setCompany(String company){
        this.company = company;
    }

    public void setHomeCity(String homeCity){
        this.homeCity = homeCity;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setAge(String age) {this.age = age; }

    public void setAboutMe(String aboutMe) {this.aboutMe = aboutMe; }

    //public void setImgPaths(List<Uri> imgPaths){this.imgPaths = imgPaths; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setShowMales(boolean showMales) {
        this.showMales = showMales;
    }

    public void setShowFemales(boolean showFemales) {
        this.showFemales = showFemales;
    }

    public void setMainProfileImgPath(String mainProfileImgPath) {
        this.mainProfileImgPath = mainProfileImgPath;
    }

    public void setBlockedContacts(List<String> blockedContacts) {
        this.blockedContacts = blockedContacts;
    }

    public void setMinAge(String minAge) {
        this.minAge = minAge;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    public void setAddImg2(String addImg2) {
        this.addImg2 = addImg2;
    }

    public void setAddImg3(String addImg3) {
        this.addImg3 = addImg3;
    }

    public void setAddImg4(String addImg4) {
        this.addImg4 = addImg4;
    }

    public void setAddImg5(String addImg5) {
        this.addImg5 = addImg5;
    }

    public void setAddImg6(String addImg6) {
        this.addImg6 = addImg6;
    }

    //Getters
    public String getUserID() {
        return userID;
    }

    public String getfName(){
        return this.fName;
    }

    public String getlName(){
        return this.lName;
    }

    public String getEmail() {
        return email;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public String getPassword() {
        return password;
    }

    public String getAge(){return this.age; }

    public String getAboutMe() {return this.aboutMe; }

    public String getGender() {
        return gender;
    }

    public boolean isShowMales() {
        return showMales;
    }

    public boolean isShowFemales() {
        return showFemales;
    }

    public String getMainProfileImgPath() {
        return mainProfileImgPath;
    }

    public List<String> getBlockedContacts() {
        return blockedContacts;
    }

    public String getMinAge() {
        return minAge;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public String getAddImg2() {
        return addImg2;
    }

    public String getAddImg3() {
        return addImg3;
    }

    public String getAddImg4() {
        return addImg4;
    }

    public String getAddImg5() {
        return addImg5;
    }

    public String getAddImg6() {
        return addImg6;
    }
}
