package com.example.pediatriccareassistant.model;

public class Message
{

    public enum SenderType {
        USER,
        BOT
    }

    SenderType sender;
    String content;
    Article article;

    public Message() {}

    public Message(SenderType sender, String content) {
        this.sender = sender;
        this.content = content;
        this.article = null;
    }

    public Message(SenderType sender, String content, Article article) {
        this.sender = sender;
        this.content = content;
        this.article = article;
    }

    public SenderType getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Article getArticle() {
        return article;
    }
}
