package com.somcrea.smartads.models;

/**
 * Created by Carles.
 */

public class Place {
    private String name;
    private String url;
    private String enterHour;
    private String exitHour;

    public Place(String name, String url, String enterHour, String exitHour) {
        this.setName(name);
        this.setUrl(url);
        this.setEnterHour(enterHour);
        this.setExitHour(exitHour);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEnterHour() {
        return enterHour;
    }

    public void setEnterHour(String enterHour) {
        this.enterHour = enterHour;
    }

    public String getExitHour() {
        return exitHour;
    }

    public void setExitHour(String exitHour) {
        this.exitHour = exitHour;
    }

}