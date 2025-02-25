package tn.esprit.services;

import tn.esprit.entities.FlaggedContent;
import tn.esprit.utils.dbCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlaggedContentService implements IService<FlaggedContent> {
    Connection con;

    public FlaggedContentService() {
        con = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(FlaggedContent flaggedContent) throws SQLException {
        String checkQuery = "SELECT * FROM flagged_content WHERE post_id = ? AND flagger_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(checkQuery)) {
            stmt.setInt(1, flaggedContent.getPost_id());
            stmt.setInt(2, flaggedContent.getFlagger_id());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }

        String insertQuery = "INSERT INTO flagged_content (post_id, flagger_id, flagged_at) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
            stmt.setInt(1, flaggedContent.getPost_id());
            stmt.setInt(2, flaggedContent.getFlagger_id());
            stmt.setDate(3, flaggedContent.getFlagged_at());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(FlaggedContent flaggedContent) throws SQLException {
    }

    @Override
    public void delete(int postId) throws SQLException {
        String deleteQuery = "DELETE FROM flagged_content WHERE post_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(deleteQuery)) {
            stmt.setInt(1, postId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<FlaggedContent> ListAll() throws SQLException {
        List<FlaggedContent> flaggedContents = new ArrayList<>();
        String listQuery = "SELECT * FROM flagged_content ORDER BY flagged_at DESC";
        try (PreparedStatement stmt = con.prepareStatement(listQuery);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                FlaggedContent flaggedContent = new FlaggedContent();
                flaggedContent.setPost_id(rs.getInt("post_id"));
                flaggedContent.setFlagger_id(rs.getInt("flagger_id"));
                flaggedContent.setFlagged_at(rs.getDate("flagged_at"));
                flaggedContents.add(flaggedContent);
            }
        }
        return flaggedContents;
    }
}