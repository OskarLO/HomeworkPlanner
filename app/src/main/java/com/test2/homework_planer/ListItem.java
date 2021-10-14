package com.test2.homework_planer;

import androidx.annotation.NonNull;
import java.util.Date;

public class ListItem {

    private String title;
    private String subject;
    private int deadline;
    private String comment;

    public ListItem(String title, String subject, int deadline, String comment) {
        this.title = title;
        this.subject = subject;
        this.deadline = deadline;
        this.comment = comment;
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

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
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
