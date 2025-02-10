package tn.esprit.entities;

public class Likes {
    private int liker_id;
    private int post_id;

    public Likes(int liker_id, int post_id) {
        this.liker_id = liker_id;
        this.post_id = post_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getLiker_id() {
        return liker_id;
    }

    public void setLiker_id(int liker_id) {
        this.liker_id = liker_id;
    }
}
