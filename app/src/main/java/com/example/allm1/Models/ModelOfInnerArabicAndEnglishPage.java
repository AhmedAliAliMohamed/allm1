package com.example.allm1.Models;

import java.util.List;

public class ModelOfInnerArabicAndEnglishPage {

    private String image;
    private String title;
    private List<ModelOfInnerArabicAndEnglishPage> model;

    public ModelOfInnerArabicAndEnglishPage() {
    }

    public ModelOfInnerArabicAndEnglishPage(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public List<ModelOfInnerArabicAndEnglishPage> getModel() {
        return model;
    }
}
