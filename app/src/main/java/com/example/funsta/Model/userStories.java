package com.example.funsta.Model;

public class userStories {
    String storyImg;
    long sotryAt;

    public userStories() {
    }

    public userStories(String storyImg, long sotryAt) {
        this.storyImg = storyImg;
        this.sotryAt = sotryAt;
    }

    public String getStoryImg() {
        return storyImg;
    }

    public void setStoryImg(String storyImg) {
        this.storyImg = storyImg;
    }

    public long getSotryAt() {
        return sotryAt;
    }

    public void setSotryAt(long sotryAt) {
        this.sotryAt = sotryAt;
    }
}
