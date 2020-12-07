package com.example.iriscollectormobile.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHistory {
    private String username;
    private String irisSide;
    private String irisImageUrl;
    private String acquisitionDate;

    public UserHistory() {
    }

    public UserHistory(String username, String irisSide, String irisImageUrl, String acquisitionDate) {
        this.username = username;
        this.irisSide = irisSide;
        this.irisImageUrl = irisImageUrl;
        this.acquisitionDate = acquisitionDate;
    }

}
