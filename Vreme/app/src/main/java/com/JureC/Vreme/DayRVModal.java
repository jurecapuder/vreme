package com.JureC.Vreme;

public class DayRVModal {

    private String day;
    private String temperature;
    private String icon;
    private String rain;

    public DayRVModal (String day, String temperature, String icon, String rain) {

        this.day = day;
        this.temperature = temperature;
        this.icon = icon;
        this.rain = rain;

    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }
}