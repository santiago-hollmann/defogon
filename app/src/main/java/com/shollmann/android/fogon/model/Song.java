package com.shollmann.android.fogon.model;

import com.parse.ParseObject;

public class Song {
    private String objectId;
    private String name;
    private String author;
    private String chords;

    public Song(String name, String author, String chords) {
        this.name = name;
        this.author = author;
        this.chords = chords;
    }

    public Song(ParseObject object) {
        objectId = object.getString("objectId");
        name = object.getString("name");
        author = object.getString("author");
        chords = object.getString("chords");
    }

    public String getChords() {
        return chords;
    }

    public void setChords(String chords) {
        this.chords = chords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return name + " " + author;
    }
}
