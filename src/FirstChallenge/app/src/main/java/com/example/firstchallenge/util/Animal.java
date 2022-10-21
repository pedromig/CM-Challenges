package com.example.firstchallenge.util;

public class Animal {

    private String name;
    private String owner;
    private int age;
    private final int avatar;

    public Animal(String name, String owner, int age, int avatar) {
        this.name = name;
        this.owner = owner;
        this.age = age;
        this.avatar = avatar;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
