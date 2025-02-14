package tn.esprit.entities;

public class Likes {
    private int liker_id;
    private int post_id;

    public Likes(int liker_id, int post_id) {
        setLiker_id(liker_id);
        setPost_id(post_id);
    }

    public Likes() {
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        if (post_id <= 0) {
            throw new IllegalArgumentException("Post ID must be a positive integer.");
        }
        this.post_id = post_id;
    }

    public int getLiker_id() {
        return liker_id;
    }

    public void setLiker_id(int liker_id) {
        if (liker_id <= 0) {
            throw new IllegalArgumentException("Liker ID must be a positive integer.");
        }
        this.liker_id = liker_id;
    }
}