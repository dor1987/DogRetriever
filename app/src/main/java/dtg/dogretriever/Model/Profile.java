package dtg.dogretriever.Model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Map;

public class Profile {
    private String id;
    private String userName;
    private String fullName;
    private String password;
    private String eMail;
    private String phoneNumber;
    private String address;
   // private ArrayList<String> dogsIDArrayList;
    private Map<String,String> dogsIDMap;
    private Bitmap profileImage;
    private String token;


    public Profile() { }

    private Profile(ProfileBuilder profileBuilder){
        setId(profileBuilder.id);
        setUserName(profileBuilder.userName);
        setFullName(profileBuilder.fullName);
        setPassword(profileBuilder.password);
        seteMail(profileBuilder.eMail);
        setPhoneNumber(profileBuilder.phoneNumber);
        setAddress(profileBuilder.address);
        //setDogsIDArrayList(profileBuilder.dogArrayList);
        setDogsIDMap(profileBuilder.dogIDMap);
        setProfileImage(profileBuilder.profileImage);
    }
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

//    public ArrayList<String> getDogsIDArrayList() {
//        return dogsIDArrayList;
//    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String,String> getDogsIDMap(){
        return dogsIDMap;
    }

    public ArrayList<String> getDogIDAsArrayList(){
        //Give back an arrayList for Dogs Id that register under this profile
        ArrayList<String> dogIDArrayList = new ArrayList<>();

        if(dogsIDMap != null) {
            for (Map.Entry<String, String> entry : dogsIDMap.entrySet()) {
                dogIDArrayList.add(entry.getValue());
            }
        }

        return dogIDArrayList;
    }
    /*
    public void setDogsIDArrayList(ArrayList<String> dogsIDArrayList) {
        this.dogsIDArrayList = dogsIDArrayList;
    }
*/
    public void setDogsIDMap(Map<String,String> dogsIDMap) {
        this.dogsIDMap = dogsIDMap;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public static class ProfileBuilder{
        //required
        private String id;
        private String userName;
        private String fullName;
        private String password;
        private String eMail;


        //optinal
        private String phoneNumber;
        private String address;
      //  private ArrayList<String> dogArrayList;
        private Map<String,String> dogIDMap;
        private Bitmap profileImage;
        private String token;

        public ProfileBuilder(String id, String userName, String fullName, String password, String eMail) {
            this.id = id;
            this.userName = userName;
            this.fullName = fullName;
            this.password = password;
            this.eMail = eMail;
        }


        public void setId(String id) {
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

//        public ProfileBuilder setDogArrayList(ArrayList dogArrayList) {
//            this.dogArrayList = dogArrayList;
//            return this;
//        }

        public ProfileBuilder setProfileImage(Bitmap profileImage) {
            this.profileImage = profileImage;
            return this;
        }

        public ProfileBuilder setdogIDMap(Map<String,String> dogIDMap) {
            this.dogIDMap = dogIDMap;
            return this;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Profile build(){return new Profile(this);}
    }

}


