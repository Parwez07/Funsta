package com.example.funsta.Model;

public class StoryModel {

    int story,storyType,profile_img;
    String profile_name;

    public StoryModel(int story, int storyType, int profile_img, String profile_name) {
        this.story = story;
        this.storyType = storyType;
        this.profile_img = profile_img;
        this.profile_name = profile_name;
    }

    public void setStory(int story) {
        this.story = story;
    }

    public void setStoryType(int storyType) {
        this.storyType = storyType;
    }

    public void setProfile_img(int profile_img) {
        this.profile_img = profile_img;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public int getStory() {
        return story;
    }

    public int getStoryType() {
        return storyType;
    }

    public int getProfile_img() {
        return profile_img;
    }

    public String getProfile_name() {
        return profile_name;
    }
}
