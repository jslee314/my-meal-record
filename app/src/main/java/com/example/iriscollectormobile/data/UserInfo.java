package com.example.iriscollectormobile.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String username;
    private String sex;
    private String age;
    private String birthday;

    public UserInfo(String username, String sex, String age, String birthday) {
        this.username = username;
        this.sex = sex;
        this.age = age;
        this.birthday = birthday;
    }

}
