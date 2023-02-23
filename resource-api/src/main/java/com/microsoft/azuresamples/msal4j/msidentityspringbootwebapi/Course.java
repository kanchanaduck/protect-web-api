package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

public class Course {
    private String topic;
    private String description;
    private Boolean readwrite=true;

    public Course(
            String topic,
            String description,
            Boolean readwrite
    ) {
        this.topic = topic;
        this.description = description;
        this.readwrite= readwrite;
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

    public Boolean getReadwrite() {
        return this.readwrite;
    }
    
    public void setReadwrite(Boolean readwrite) {
        this.readwrite = readwrite;
    }
}