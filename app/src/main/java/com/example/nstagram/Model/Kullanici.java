package com.example.nstagram.Model;

public class Kullanici {
    private String id;
    private String kullaniciadi;
    private String ad;
    private String resimurl;
    private String bio;

    public Kullanici() {

    }

    public Kullanici(String id, String kullaniciadi, String ad, String resimurl, String bio) {
        this.id = id;
        this.kullaniciadi = kullaniciadi;
        this.ad = ad;
        this.resimurl = resimurl;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public String getAd() {
        return ad;
    }

    public String getResimurl() {
        return resimurl;
    }

    public String getBio() {
        return bio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public void setResimurl(String resimurl) {
        this.resimurl = resimurl;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
