package com.example.funsta.Model;

public class notificationModel {

    String notificationBy;
    String notificationId;
    Long notificatonAt;
    String type;
    String postId,posdtedBy;
    Boolean checkOpen = false;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public Long getNotificatonAt() {
        return notificatonAt;
    }

    public void setNotificatonAt(Long notificatonAt) {
        this.notificatonAt = notificatonAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPosdtedBy() {
        return posdtedBy;
    }

    public void setPosdtedBy(String posdtedBy) {
        this.posdtedBy = posdtedBy;
    }

    public Boolean getCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(Boolean checkOpen) {
        this.checkOpen = checkOpen;
    }
}
