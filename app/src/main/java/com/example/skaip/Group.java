package com.example.skaip;

import java.io.Serializable;

public class Group {
    private String name;
    private String encodedImage;
    private String id;

    //overloaded for testing passing of ID
    private Group(String name, String encodedImage, String id) {
        this.name = name;
        this.encodedImage = encodedImage;
        this.id = id;
    }
    private Group(String name, String encodedImage) {
        this.name = name;
        this.encodedImage = encodedImage;
    }

    public Group(){}

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setImage(String encodedImage){
        this.encodedImage = encodedImage;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getEncodedImage() {
        return encodedImage;
    }
}
