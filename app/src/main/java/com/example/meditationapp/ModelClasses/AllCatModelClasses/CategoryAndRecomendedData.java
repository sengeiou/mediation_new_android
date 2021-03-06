package com.example.meditationapp.ModelClasses.AllCatModelClasses;

import com.example.meditationapp.ModelClasses.SessionModelClass;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryAndRecomendedData {

//    @SerializedName("affirmation")
//    @Expose
//    private List<CategoryDataModelClass> affirmation = null;
//    @SerializedName("recomended")
//    @Expose
//    private List<RecomandedModelClass> recomended = null;
//
//    public List<CategoryDataModelClass> getAffirmation() {
//        return affirmation;
//    }
//
//    public void setAffirmation(List<CategoryDataModelClass> affirmation) {
//        this.affirmation = affirmation;
//    }
//
//    public List<RecomandedModelClass> getRecomended() {
//        return recomended;
//    }
//
//    public void setRecomended(List<RecomandedModelClass> recomended) {
//        this.recomended = recomended;
//    }

    @SerializedName("affirmation")
    @Expose
    private List<CategoryDataModelClass> affirmation = null;
    @SerializedName("session")
    @Expose
    private List<SessionModelClass> session = null;
    @SerializedName("recomended")
    @Expose
    private List<RecomandedModelClass> recomended = null;

    public List<CategoryDataModelClass> getAffirmation() {
        return affirmation;
    }

    public void setAffirmation(List<CategoryDataModelClass> affirmation) {
        this.affirmation = affirmation;
    }

    public List<SessionModelClass> getSession() {
        return session;
    }

    public void setSession(List<SessionModelClass> session) {
        this.session = session;
    }

    public List<RecomandedModelClass> getRecomended() {
        return recomended;
    }

    public void setRecomended(List<RecomandedModelClass> recomended) {
        this.recomended = recomended;
    }
}
