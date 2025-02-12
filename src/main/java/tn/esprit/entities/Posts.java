package tn.esprit.entities;

import java.sql.Date;

public class Posts {
    private int post_id;
    private int owner_id;
    private Date created_at;
    private Date updated_at;
    private String text_content;

    public Posts(int post_id, int owner_id, Date created_at, Date updated_at, String text_content) {
        this.post_id = post_id;
        this.owner_id = owner_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.text_content = text_content;
    }

    public Posts() {

    }

    public String getText_content() {
        return text_content;
    }

    public void setText_content(String text_content) {
        if (text_content != null && text_content.length() > 255) {
            throw new IllegalArgumentException("Text content cannot exceed 255 characters.");
        }
        this.text_content = text_content;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}