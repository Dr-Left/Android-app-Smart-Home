package com.chris.smarthome;

public class Appliance {
    private int imageId;
    private String name;
    private Boolean isOn;
    private int currentPower;

    public Appliance(String name, int imageId, Boolean isOn, int currentPower) {
        this.imageId = imageId;
        this.name = name;
        this.isOn = isOn;
        this.currentPower = currentPower;
    }

    public void toggle_power() {
        this.isOn = !this.isOn;
    }

    public Boolean getOn() {
        return isOn;
    }

    public void setOn(Boolean on) {
        isOn = on;
    }

    public int getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
