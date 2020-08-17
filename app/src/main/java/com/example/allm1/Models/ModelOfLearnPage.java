package com.example.allm1.Models;

import java.util.List;

public class ModelOfLearnPage {

    private String imageForLearn;
    private String textsForLearn;
    private List<ModelOfLearnPage> modelOfLearnPages;




    public ModelOfLearnPage() {
    }

    public ModelOfLearnPage(String imageForLearn, String textsForLearn) {
        this.imageForLearn = imageForLearn;
        this.textsForLearn = textsForLearn;
    }

    public String getImageForLearn() {
        return imageForLearn;
    }

    public String getTextsForLearn() {
        return textsForLearn;
    }

    public List<ModelOfLearnPage> getModelOfLearnPages() {
        return modelOfLearnPages;
    }



}
