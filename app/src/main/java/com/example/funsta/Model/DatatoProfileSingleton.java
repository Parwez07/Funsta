package com.example.funsta.Model;

public class DatatoProfileSingleton {
    private static DatatoProfileSingleton instance;
    private UserModel sharedUserData;

    private DatatoProfileSingleton() {
    }

    public static synchronized DatatoProfileSingleton getInstance() {
        if (instance == null) {
            instance = new DatatoProfileSingleton();
        }
        return instance;
    }

    public UserModel getSharedData() {
        return sharedUserData;
    }

    public void setSharedData(UserModel data) {
       sharedUserData = data;
    }
}
