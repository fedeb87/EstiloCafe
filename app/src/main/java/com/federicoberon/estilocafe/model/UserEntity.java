package com.federicoberon.estilocafe.model;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class UserEntity {
    @NonNull
    private String idFirebase;
    private String email;
    private String nickname;
    private int topScoreAnime;
    private int topScoreMoviesAndSeries;
    private String image_profile;
    private long timeStamp;

    public UserEntity() {
    }

    public UserEntity(@NonNull String idFirebase, String email, String nickname, int topScoreAnime, int topScoreMoviesAndSeries, long timeStamp) {
        this.idFirebase = idFirebase;
        this.email = email;
        this.nickname = nickname;
        this.topScoreAnime = topScoreAnime;
        this.topScoreMoviesAndSeries = topScoreMoviesAndSeries;
        this.timeStamp = timeStamp;
    }

    @NonNull
    public String getIdFirebase() {
        return idFirebase;
    }

    public void setIdFirebase(@NonNull String idFirebase) {
        this.idFirebase = idFirebase;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTopScoreAnime() {
        return topScoreAnime;
    }

    public void setTopScoreAnime(int topScoreAnime) {
        this.topScoreAnime = topScoreAnime;
    }

    public int getTopScoreMoviesAndSeries() {
        return topScoreMoviesAndSeries;
    }

    public void setTopScoreMoviesAndSeries(int topScoreMoviesAndSeries) {
        this.topScoreMoviesAndSeries = topScoreMoviesAndSeries;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImage_profile() {
        return image_profile;
    }

    public void setImage_profile(String image_profile) {
        this.image_profile = image_profile;
    }
}
