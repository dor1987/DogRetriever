package dtg.dogretriever.Model;

import android.media.Image;

import java.util.ArrayList;

public class Profile {
    private String id;
    private String userName;
    private String fullName;
    private String password;
    private String eMail;
    private String phoneNumber;
    private String address;
    private ArrayList<Dog> dogArrayList;
    private Image profileImage;

    public Profile(String id, String userName, String fullName, String password, String eMail, String phoneNumber, String address, ArrayList<Dog> dogArrayList, Image profileImage) {
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

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public String geteMail() {
        return eMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<Dog> getDogArrayList() {
        return dogArrayList;
    }

    public Image getProfileImage() {
        return profileImage;
    }
}
