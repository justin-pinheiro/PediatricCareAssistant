package com.example.pediatriccareassistant.model;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class Article implements Serializable {
    String title, imageUrl, accessibleVersion, id, categories;
    ArrayList<String> sectionsContent, sectionsTitle;

    public Article() {
        sectionsContent = new ArrayList<>();
        sectionsTitle = new ArrayList<>();
    }

    public String getCategories() {
        return categories;
    }

    public String getAccessibleVersion() {
        return accessibleVersion;
    }

    public ArrayList<String> getSectionsContent() {
        return sectionsContent;
    }

    public ArrayList<String> getSectionsTitle() {
        return sectionsTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static Article fromSnapshot(DataSnapshot dataSnapshot) {
        Article article = new Article();
        article.title = dataSnapshot.child("Title").getValue(String.class);
        article.imageUrl = dataSnapshot.child("ImageUrl").getValue(String.class);
        article.accessibleVersion = dataSnapshot.child("AccessibleVersion").getValue(String.class);
        article.id = dataSnapshot.child("Id").getValue(String.class);
        article.categories = dataSnapshot.child("Categories").getValue(String.class);

        DataSnapshot sectionsSnapshot = dataSnapshot.child("Sections").child("section");
        for (DataSnapshot sectionSnapshot : sectionsSnapshot.getChildren()) {
            String title = sectionSnapshot.child("Title").getValue(String.class);
            String content = sectionSnapshot.child("Content").getValue(String.class);
            article.sectionsTitle.add(title);
            article.sectionsContent.add(content);
        }

        return article;
    }
}
