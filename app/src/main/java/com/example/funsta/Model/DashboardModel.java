package com.example.funsta.Model;

public class DashboardModel {

    int profile,postImg,save;
    String name,about,like,share,comment;

    public DashboardModel(int profile, int postImg, int save, String name, String about, String like, String share, String comment) {
        this.profile = profile;
        this.postImg = postImg;
        this.save = save;
        this.name = name;
        this.about = about;
        this.like = like;
        this.share = share;
        this.comment = comment;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getPostImg() {
        return postImg;
    }

    public void setPostImg(int postImg) {
        this.postImg = postImg;
    }

    public int getSave() {
        return save;
    }

    public void setSave(int save) {
        this.save = save;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
