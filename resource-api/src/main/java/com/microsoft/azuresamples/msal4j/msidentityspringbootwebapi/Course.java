package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

public class Course {
    private String topic;
    private String description;

    public Course(
            String topic,
            String description
    ) {
        this.topic = topic;
        this.description = description;
    }

    public String getTopic() {
        return this.topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}