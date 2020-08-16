package com.example.searchmysoulmate.Models;

import android.net.Uri;

import java.util.List;

public class Image {
    private Uri imgUri;

    public Image(){

    }

    public Image(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }
}
