package tn.esprit.entities;

public class FlaggedContent {
    private int post_id;
    private int flagger_id;

    public FlaggedContent(int post_id, int flagger_id) {
        this.post_id = post_id;
        this.flagger_id = flagger_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getFlagger_id() {
        return flagger_id;
    }

    public void setFlagger_id(int flagger_id) {
        this.flagger_id = flagger_id;
    }
}
