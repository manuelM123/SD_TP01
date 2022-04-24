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

    /**
     * get topic of the news
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * set topic of the news
     * @param topic the topic to set
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * get the content of a news
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * set the content of the news
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * get the publisher
     * @return the publisher
     */
    public Person getPublisher() {
        return publisher;
    }

    /**
     * set the publisher of the news
     * @param publisher the publisher to set
     */
    public void setPublisher(Person publisher) {
        this.publisher = publisher;
    }

    /**
     * get the news timestamp
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * set timestamp to a specific news
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * get the news's title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * set the new's title
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Does the printing format
     * @return the string with the output
     */
    @Override
    public String toString() {
        if(timestamp != null)
        return '\n' +
                "Topic - " + topic + '\n' +
                "Title - " + title + '\n' +
                "Content - " + content + '\n' +
                "Publisher - " + publisher.getName() + '\n' +
                "Timestamp - " + timestamp + '\n' +  "-----------------------";
        else
            return '\n' +
                    "Topic - " + topic + '\n' +
                    "Title - " + title + '\n' +
                    "Content - " + content + '\n' +
                    "Publisher - " + publisher.getName() + '\n' +
                    "-------------";
    }
}
