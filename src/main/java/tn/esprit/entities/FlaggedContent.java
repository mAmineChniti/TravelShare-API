package tn.esprit.entities;

import java.util.Date;

public class FlaggedContent {
    private int post_id;
    private int flagger_id;
    private Date flagged_at;

    public FlaggedContent(int post_id, int flagger_id, Date flaggedAt) {
        this.post_id = post_id;
        this.flagger_id = flagger_id;
        this.flagged_at = flaggedAt;
    }

    public FlaggedContent() {

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

    public Date getFlagged_at() {
        return flagged_at;
    }

    public void setFlagged_at(Date flagged_at) {
        this.flagged_at = flagged_at;
    }
}