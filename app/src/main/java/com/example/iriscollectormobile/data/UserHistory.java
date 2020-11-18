package com.example.iriscollectormobile.data;

public class UserHistory {
    private String username;
    private String irisSide;
    private String irisImageUrl;
    private String acquisitionDate;

    public UserHistory(String username, String irisSide, String irisImageUrl, String acquisitionDate) {
        this.username = username;
        this.irisSide = irisSide;
        this.irisImageUrl = irisImageUrl;
        this.acquisitionDate = acquisitionDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIrisSide() {
        return irisSide;
    }

    public void setIrisSide(String irisSide) {
        this.irisSide = irisSide;
    }

    public String getIrisImageUrl() {
        return irisImageUrl;
    }

    public void setIrisImageUrl(String irisImageUrl) {
        this.irisImageUrl = irisImageUrl;
    }

    public String getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(String acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }
}
