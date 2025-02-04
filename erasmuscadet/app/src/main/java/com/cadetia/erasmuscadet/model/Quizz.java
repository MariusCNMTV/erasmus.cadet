package com.cadetia.erasmuscadet.model;

public class Quizz {
    private final String title;
    private final String imageResourceUrl;
    private final String testId;
    private boolean hasQuestions;// Add a field for test ID

    public Quizz(String title, String imageResourceUrl, String testId, boolean hasQuestions) {
        this.title = title;
        this.imageResourceUrl = imageResourceUrl;
        this.testId = testId;
        this.hasQuestions = hasQuestions;
    }

    public String getTitle() {
        return title;
    }

    public String getImageResourceUrl() {
        return imageResourceUrl;
    }

    public String getTestId() {
        return testId;
    }

    public boolean hasQuestions() { return hasQuestions; }

}