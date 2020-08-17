package com.example.allm1.Models;



public class ModelOfArabicAndEnglishPage {

    private static ModelOfArabicAndEnglishPage model = null;
    private String imageOfAll ;
    private String nameOfTitle;

    public ModelOfArabicAndEnglishPage() {
    }


    public ModelOfArabicAndEnglishPage(String nameOfTitle, String imageOfAll) {
        this.imageOfAll = imageOfAll;
        this.nameOfTitle = nameOfTitle;
    }

    public String getImageOfAll() {
        return imageOfAll;
    }

    public String getNameOfTitle() {
        return nameOfTitle;
    }
}
