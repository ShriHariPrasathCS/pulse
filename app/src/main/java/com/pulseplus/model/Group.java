package com.pulseplus.model;

import java.util.ArrayList;

public class Group {

    public String date;
    public ArrayList<Child> children;

    public String getDate() {
        return date;
    }

    public ArrayList<Child> getChildren() {
        return children;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }
}