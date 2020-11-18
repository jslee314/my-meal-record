package com.example.iriscollectormobile.data;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
