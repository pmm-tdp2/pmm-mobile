package com.uberpets.util;

import android.graphics.Bitmap;

public class AccountImages {
    private static  AccountImages ourInstance = new AccountImages();

    public static AccountImages getInstance() {
        return ourInstance;
    }

    private AccountImages() {
    }

    private Bitmap photoProfile;
    private Bitmap photoCar;
    private Bitmap photoInsurance;
    private Bitmap photoLicence;
    private Bitmap photoPatent;

    public void setPhotoProfile(Bitmap photoProfile) {
        this.photoProfile = photoProfile;
    }

    public void setPhotoCar(Bitmap photoCar) {
        this.photoCar = photoCar;
    }

    public void setPhotoInsurance(Bitmap photoInsurance) {
        this.photoInsurance = photoInsurance;
    }

    public void setPhotoLicence(Bitmap photoLicence) {
        this.photoLicence = photoLicence;
    }

    public void setPhotoPatent(Bitmap photoPatent) {
        this.photoPatent = photoPatent;
    }

    public Bitmap getPhotoProfile() {
        return photoProfile;
    }

    public Bitmap getPhotoCar() {
        return photoCar;
    }

    public Bitmap getPhotoInsurance() {
        return photoInsurance;
    }

    public Bitmap getPhotoLicence() {
        return photoLicence;
    }

    public Bitmap getPhotoPatent() {
        return photoPatent;
    }

}
