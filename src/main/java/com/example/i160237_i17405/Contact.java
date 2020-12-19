package com.example.i160237_i17405;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Contact implements Parcelable {
    String userId;
    String userEmail;
    String userPassword;
    String firstName;
    String lastName;
    String phoneNo;
    String dateOfBirth;
    String gender;
    String bio;
    String imageUrl;
    String onlineStatus;

    Contact(){
        this.userId = null;
        this.userEmail = null;
        this.userPassword = null;
        this.firstName = null;
        this.lastName = null;
        this.phoneNo = null;
        this.dateOfBirth = null;
        this.gender = null;
        this.bio = null;
        this.imageUrl = null;
        this.onlineStatus = null;
    }

    public Contact(String userId, String userEmail, String userPassword, String firstName, String lastName, String phoneNo, String dateOfBirth, String gender, String bio, String imageUrl, String onlineStatus) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.onlineStatus = onlineStatus;
    }

    public Contact(com.example.i160237_i17405.Contact contact) {
        this.userId = contact.userId;
        this.userEmail = contact.userEmail;
        this.userPassword = contact.userPassword;
        this.firstName = contact.firstName;
        this.lastName = contact.lastName;
        this.phoneNo = contact.phoneNo;
        this.dateOfBirth = contact.dateOfBirth;
        this.gender = contact.gender;
        this.bio = contact.bio;
        this.imageUrl = contact.imageUrl;
        this.onlineStatus = contact.onlineStatus;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        System.out.println("XXXXgetting image url " + imageUrl + "for " + firstName);
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    //FOR PARCELLABLE
    protected Contact(Parcel in){
        this.userId= in.readString();
        this.userEmail= in.readString();
        this.userPassword= in.readString();
        this.firstName= in.readString();
        this.lastName= in.readString();
        this.phoneNo= in.readString();
        this.dateOfBirth= in.readString();
        this.gender= in.readString();
        this.bio= in.readString();
        this.imageUrl= in.readString();
        this.onlineStatus= in.readString();
    }

    //Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userEmail);
        dest.writeString(userPassword);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNo);
        dest.writeString(dateOfBirth);
        dest.writeString(gender);
        dest.writeString(bio);
        dest.writeString(imageUrl);
        dest.writeString(onlineStatus);

    }


    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public com.example.i160237_i17405.Contact createFromParcel(Parcel in) {
            return new com.example.i160237_i17405.Contact(in);
        }

        @Override
        public com.example.i160237_i17405.Contact[] newArray(int size) {
            return new com.example.i160237_i17405.Contact[size];
        }
    };


    String getAge(){

        if(dateOfBirth==null){
            return "0";
        }

        String[] dateStrings= dateOfBirth.split("/");
        for (String token : dateStrings)
        {
            System.out.println(token);
        }
        int day = Integer.parseInt(dateStrings[0]);
        int month = Integer.parseInt(dateStrings[1]);
        int year = Integer.parseInt(dateStrings[2]);


        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageString = ageInt.toString();
        return ageString;
    }

    String getFullName(){
        if (this.firstName==null || this.lastName==null){
            return null;
        }
        return (firstName + " " + lastName);
    }

    String getGenderAndAge(){
        if (this.dateOfBirth==null || this.gender==null){
            return null;
        }
        return (gender + ", " + getAge());
    }


    //Display function
    void display()
    {
        System.out.println("USER ID: " + this.userId);
        System.out.println("Email: " + this.userEmail);
        System.out.println("First Name: " + this.firstName);
        System.out.println("Last Name: " + this.lastName);
        System.out.println("Phone No: " + this.phoneNo);
        System.out.println("Gender: " + this.gender);
        System.out.println("Bio: " + this.bio);
        System.out.println("DateOfBirth: " + this.dateOfBirth);
        System.out.println("image: " + this.imageUrl);
        System.out.println("onlineStatus: " + this.onlineStatus);
    }

}
