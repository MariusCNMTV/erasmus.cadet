// CategoryModel.java
package com.cadetia.erasmuscadet.model;

import java.util.List;

public class CategoryModel {

    private String docID;
    private String name;
    private int noOfTests;
    private List<Quizz> quizzList; // Add a list of quizzes

    public CategoryModel(String docID, String name, int noOfTests, List<Quizz> quizzList) {
        this.docID = docID;
        this.name = name;
        this.noOfTests = noOfTests;
        this.quizzList = quizzList;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoOfTests() {
        return noOfTests;
    }

    public void setNoOfTests(int noOfTests) {
        this.noOfTests = noOfTests;
    }

    public List<Quizz> getQuizzList() {
        return quizzList;
    }

    public void setQuizzList(List<Quizz> quizzList) {
        this.quizzList = quizzList;
    }
}
