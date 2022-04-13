package com.company;

import java.io.Serializable;
import java.util.Date;

public class News implements Serializable {
    private String topic;
    private String title;
    private String content;
    private Person publisher;
    private Date timestamp;

    public News() {

    }

    public News(String topic, String title, String content, Person publisher, Date timestamp) {
        this.topic = topic;
        this.title = title;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return '\n' +
                "Topic - " + topic + '\n' +
                "Title - " + title + '\n' +
                "Content - " + content + '\n' +
                "Publisher - " + publisher.getName() + '\n' +
                "Timestamp - " + timestamp + '\n' +  "-------------";
    }
}
