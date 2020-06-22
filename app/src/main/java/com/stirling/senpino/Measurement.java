package com.stirling.senpino;

public class Measurement {

    private String user;
    private float weight;
    private String timestamp;

    public Measurement(String user, float weight, String timestamp){

    }

    //Getters and setters
    public String getUser() {return user;}
    public void setUser(String user) {this.user = user;}
    public float getWeight() {return weight;}
    public void setWeight(float weight) {this.weight = weight;}
    public String getTimestamp() {return timestamp;}
    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}

}
