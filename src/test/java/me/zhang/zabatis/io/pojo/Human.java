package me.zhang.zabatis.io.pojo;

public class Human {
    private String name;
    private double height;
    private int sex;
    private Human child;

    public Human() {
    }

    public Human(String name, double height, int sex, Human child) {
        this.name = name;
        this.height = height;
        this.sex = sex;
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Human getChild() {
        return child;
    }

    public void setChild(Human child) {
        this.child = child;
    }
}
