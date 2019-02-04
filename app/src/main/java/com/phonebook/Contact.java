package com.phonebook;

import java.util.ArrayList;

/**
 * Model class of contact
 */
public class Contact {
    private String name = "";
    private String email = "";
    private ArrayList<String> phones = new ArrayList<>();
    private String imageLetter = "";
    private String lookUp = "";
    private String key = "";

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

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }

    public String getImageLetter() {
        return imageLetter;
    }

    public void setImageLetter(String imageLetter) {
        this.imageLetter = imageLetter;
    }

    public String getLookUp() {
        return lookUp;
    }

    public void setLookUp(String lookUp) {
        this.lookUp = lookUp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
