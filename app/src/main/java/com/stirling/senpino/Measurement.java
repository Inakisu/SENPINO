package com.stirling.senpino;

public class Measurement {

    private String user;
    private String weight;
    private String timestamp;

    public Measurement(String user, String weight, String timestamp){
        this.user = user;
        this.weight = weight;
        this.timestamp = timestamp;
    }

    //Getters and setters
    public String getUser() {return user;}
    public void setUser(String user) {this.user = user;}
    public String getWeight() {return weight;}
    public void setWeight(String weight) {this.weight = weight;}
    public String getTimestamp() {return timestamp;}
    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}

}
