package com.test2.homework_planer;

import androidx.annotation.NonNull;
import java.util.Date;

public class ListItem {

    private String taskId;
    private String title;
    private String subject;
    private String deadline;
    private String comment;

    public ListItem() {
    }

    public ListItem(String taskId, String title, String subject, String deadline, String comment) {
        this.taskId = taskId;
        this.title = title;
        this.subject = subject;
        this.deadline = deadline;
        this.comment = comment;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " | " + subject + " | " + deadline;
    }
}
