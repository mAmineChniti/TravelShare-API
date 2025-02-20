package tn.esprit.services;

import tn.esprit.entities.Likes;
import tn.esprit.utils.dbCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikesService {
    Connection con;

    public LikesService() {
        con = dbCon.getInstance().getConnection();
    }

    public int likesCounter(int post_id) throws SQLException {
        String likesReq = "SELECT count(*) FROM likes WHERE post_id=?";
        try (PreparedStatement countStmt = con.prepareStatement(likesReq)){
            countStmt.setInt(1, post_id);
            try (ResultSet rs = countStmt.executeQuery()){
                if (rs.next()){
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean isLikedByUser(int userId, int postId) throws SQLException {
        String query = "SELECT COUNT(*) FROM likes WHERE Liker_id = ? AND Post_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void addOrRemove(Likes like) throws SQLException {
        String checkLikeQuery = "SELECT COUNT(*) FROM likes WHERE Liker_id = ? AND Post_id = ?";
        String insertLikeQuery = "INSERT INTO likes (Liker_id, Post_id) VALUES (?, ?)";
        String deleteLikeQuery = "DELETE FROM likes WHERE Liker_id = ? AND Post_id = ?";

        try (PreparedStatement checkStmt = con.prepareStatement(checkLikeQuery)) {
            checkStmt.setInt(1, like.getLiker_id());
            checkStmt.setInt(2, like.getPost_id());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    try (PreparedStatement deleteStmt = con.prepareStatement(deleteLikeQuery)) {
                        deleteStmt.setInt(1, like.getLiker_id());
                        deleteStmt.setInt(2, like.getPost_id());
                        deleteStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = con.prepareStatement(insertLikeQuery)) {
                        insertStmt.setInt(1, like.getLiker_id());
                        insertStmt.setInt(2, like.getPost_id());
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
    }
}