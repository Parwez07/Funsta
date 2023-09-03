package com.example.funsta.Model;

public class UserModel {
    private String name;
    private String email;
    private String password;

    private String profession;
    private String cover_photo, profile, userId;
    int followingCounts,followersCount;
    int postCounts;

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getPostCounts() {
        return postCounts;
    }

    public void setPostCounts(int postCounts) {
        this.postCounts = postCounts;
    }

    public int getFollowingCounts() {
        return followingCounts;
    }

    public void setFollowingCounts(int followingCounts) {
        this.followingCounts = followingCounts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {

        this.profile = profile;

    }


    public UserModel() {
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }


    public UserModel(String name, String email, String password, String profession) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profession = profession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
