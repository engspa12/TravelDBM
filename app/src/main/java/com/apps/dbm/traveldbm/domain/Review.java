package com.apps.dbm.traveldbm.domain;

//For future use
public class Review {
    private String reviewAuthor;
    private String reviewBody;

    public Review(String author, String body){
        reviewAuthor = author;
        reviewBody = body;
    }

    public String getReviewAuthor(){
        return reviewAuthor;
    }

    public String getReviewBody(){
        return reviewBody;
    }
}
