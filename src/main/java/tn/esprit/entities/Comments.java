package tn.esprit.entities;

import java.sql.Date;

public class Comments {
    private int comment_id;
    private int post_id;
    private int commenter_id;
    private String comment;
    private Date commented_at;
    private Date updated_at;
    private String commenter_name;
    private String commenter_lastname;

    public Comments() {
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        if (comment_id < 0) {
            throw new IllegalArgumentException("comment_id cannot be negative");
        }
        this.comment_id = comment_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        if (post_id < 0) {
            throw new IllegalArgumentException("post_id cannot be negative");
        }
        this.post_id = post_id;
    }

    public int getCommenter_id() {
        return commenter_id;
    }

    public void setCommenter_id(int commenter_id) {
        if (commenter_id < 0) {
            throw new IllegalArgumentException("commenter_id cannot be negative");
        }
        this.commenter_id = commenter_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be an empty string");
        }
        this.comment = comment;
    }

    public Date getCommented_at() {
        return commented_at;
    }

    public void setCommented_at(Date commented_at) {
        this.commented_at = commented_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getCommenter_name() {
        return commenter_name;
    }

    public void setCommenter_name(String commenter_name) {
        if (commenter_name == null || commenter_name.trim().isEmpty()) {
            throw new IllegalArgumentException("Commenter name cannot be an empty string");
        }
        this.commenter_name = commenter_name;
    }

    public String getCommenter_lastname() {
        return commenter_lastname;
    }

    public void setCommenter_lastname(String commenter_lastname) {
        if (commenter_lastname == null || commenter_lastname.trim().isEmpty()) {
            throw new IllegalArgumentException("Commenter lastname cannot be an empty string");
        }
        this.commenter_lastname = commenter_lastname;
    }
}