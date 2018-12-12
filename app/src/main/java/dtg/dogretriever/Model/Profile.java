package dtg.dogretriever.Model;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.ArrayList;

public class Profile {
    private int id;
    private String userName;
    private String fullName;
    private String password;
    private String eMail;
    private String phoneNumber;
    private String address;
    private ArrayList dogArrayList;
    private Bitmap profileImage;

    private Profile(ProfileBuilder profileBuilder){
        setId(profileBuilder.id);
        setUserName(profileBuilder.userName);
        setFullName(profileBuilder.fullName);
        setPassword(profileBuilder.password);
        seteMail(profileBuilder.eMail);
        setPhoneNumber(profileBuilder.phoneNumber);
        setAddress(profileBuilder.address);
        setDogArrayList(profileBuilder.dogArrayList);
        setProfileImage(profileBuilder.profileImage);
    }

    public Profile(int id, String userName, String fullName, String password, String eMail, String phoneNumber, String address, ArrayList dogArrayList, Bitmap profileImage) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.eMail = eMail;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dogArrayList = dogArrayList;
        this.profileImage = profileImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList getDogArrayList() {
        return dogArrayList;
    }

    public void setDogArrayList(ArrayList dogArrayList) {
        this.dogArrayList = dogArrayList;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public static class ProfileBuilder{
        //required
        private int id;
        private String userName;
        private String fullName;
        private String password;

        //optinal
        private String eMail;
        private String phoneNumber;
        private String address;
        private ArrayList dogArrayList;
        private Bitmap profileImage;

        public ProfileBuilder(int id, String userName, String fullName, String password) {
            this.id = id;
            this.userName = userName;
            this.fullName = fullName;
            this.password = password;
        }


        public void setId(int id) {
            this.id = id;
        }

        public ProfileBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public ProfileBuilder setFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public ProfileBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public ProfileBuilder seteMail(String eMail) {
            this.eMail = eMail;
            return this;
        }

        public ProfileBuilder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public ProfileBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public ProfileBuilder setDogArrayList(ArrayList dogArrayList) {
            this.dogArrayList = dogArrayList;
            return this;
        }

        public ProfileBuilder setProfileImage(Bitmap profileImage) {
            this.profileImage = profileImage;
            return this;
        }

        public Profile build(){return new Profile(this);}
    }

}


