package tn.esprit.entities;

import java.util.Date;

public class FlaggedContent {
    private int post_id;
    private int flagger_id;
    private Date flagged_at;

    public FlaggedContent(int post_id, int flagger_id, Date flaggedAt) {
        setPost_id(post_id);
        setFlagger_id(flagger_id);
        setFlagged_at(flaggedAt);
    }

    public FlaggedContent() {
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

    public int getFlagger_id() {
        return flagger_id;
    }

    public void setFlagger_id(int flagger_id) {
        if (flagger_id <= 0) {
            throw new IllegalArgumentException("Flagger ID must be a positive integer.");
        }
        this.flagger_id = flagger_id;
    }

    public Date getFlagged_at() {
        return flagged_at;
    }

    public void setFlagged_at(Date flagged_at) {
        if (flagged_at == null || flagged_at.after(new Date())) {
            throw new IllegalArgumentException("Flagged date must be a valid past or present date.");
        }
        this.flagged_at = flagged_at;
    }
}