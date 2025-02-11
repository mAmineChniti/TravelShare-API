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

    public void addOrRemove(Likes like) throws SQLException {
        String checkLikeQuery = "SELECT COUNT(*) FROM likes WHERE Liker_id = ? AND Post_id = ?";
        String insertLikeQuery = "INSERT INTO likes (Liker_id, Post_id, created_at) VALUES (?, ?, NOW())";
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