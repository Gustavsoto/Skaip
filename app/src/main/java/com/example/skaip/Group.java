package com.example.skaip;

import java.io.Serializable;

public class Group {
    private String name;
    private String encodedImage;

    private Group(String name, String encodedImage) {
        this.name = name;
        this.encodedImage = encodedImage;
    }
    public Group(){}

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setImage(String encodedImage){
        this.encodedImage = encodedImage;
    }

    public String getEncodedImage() {
        return encodedImage;
    }
}
