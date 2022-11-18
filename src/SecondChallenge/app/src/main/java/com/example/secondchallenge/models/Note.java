package com.example.secondchallenge.models;

public class Note {

    public static final int NEW_NOTE = -1;

    private String title;
    private String body;
    private String origin;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
        this.origin = "self";
    }

    public Note(String title, String body, String origin) {
        this(title, body);
        this.origin = origin;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
