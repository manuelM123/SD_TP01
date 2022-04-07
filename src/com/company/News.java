package com.company;

import java.util.Date;

public class News {
    private String topic;
    private String content;
    private Person publisher;
    private Date timestamp;

    public News() {

    }

    public News(String topic, String content, Person publisher, Date timestamp) {
        this.topic = topic;
        this.content = content;
        this.publisher = publisher;
        this.timestamp = timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Person getPublisher() {
        return publisher;
    }

    public void setPublisher(Person publisher) {
        this.publisher = publisher;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
