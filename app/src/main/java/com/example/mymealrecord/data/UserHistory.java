package com.example.mymealrecord.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHistory {
    private String userHistoryName;
    private String email;
    private String irisSide;
    private String irisImageUrl;
    private String acquisitionDate;

    public UserHistory() {
    }

    public UserHistory(String userHistoryName, String email, String irisSide, String irisImageUrl, String acquisitionDate) {
        this.userHistoryName = userHistoryName;
        this.email = email;
        this.irisSide = irisSide;
        this.irisImageUrl = irisImageUrl;
        this.acquisitionDate = acquisitionDate;
    }

}
