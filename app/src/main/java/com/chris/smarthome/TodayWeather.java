package com.chris.smarthome;

public class TodayWeather {


    private String city;
    private String updatetime;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    private String wendu;

    void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    void setCity(String city) {
        this.city = city;
    }

    void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    void setShidu(String shidu) {
        this.shidu = shidu;
    }

    void setQuality(String quality) {
        this.quality = quality;
    }

    void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    void setFengli(String fengli) {
        this.fengli = fengli;
    }

    void setDate(String date) {
        this.date = date;
    }

    void setHigh(String high) {
        this.high = high;
    }

    void setLow(String low) {
        this.low = low;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public String getShidu() {
        return shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public String getQuality() {
        return quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public String getDate() {
        return date;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getType() {
        return type;
    }
}
