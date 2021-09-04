package com.gitlab.sszuev.flashcards.dto;

/**
 * Created by @ssz on 04.09.2021.
 */
@SuppressWarnings("unused")
public class AccountRequest {
    private String login;
    private String password;
    private String matchingPassword;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    @Override
    public String toString() {
        return String.format("User{login='%s', password='%s', matchingPassword='%s'}",
                login, password, matchingPassword);
    }

}
