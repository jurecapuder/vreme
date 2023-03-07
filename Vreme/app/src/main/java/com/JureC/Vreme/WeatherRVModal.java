package com.JureC.Vreme;

public class WeatherRVModal {

    private String time;
    private String temperature;
    private String icon;
    private double rain;

    public WeatherRVModal(String time, String temperature, String icon, double rain) {

        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.rain = rain;

    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setRain(double rain) {
        this.rain = rain;
    }

    public String getTime() {

        return time;

    }

    public String getTemperature() {

        return temperature;

    }

    public String getIcon() {

        return icon;

    }

    public double getRain() {

        return rain;

    }

}
