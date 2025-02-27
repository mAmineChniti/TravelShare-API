package tn.esprit.services;

import tn.esprit.utils.dbCon;
import tn.esprit.entities.Comments;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentsService implements IService<Comments> {
    private Connection connection;

    public CommentsService() {
        connection = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(Comments comment) throws SQLException {
        String req = "INSERT INTO comments (post_id, commenter_id, comment, commented_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, comment.getPost_id());
            ps.setInt(2, comment.getCommenter_id());
            ps.setString(3, comment.getComment());
            ps.setDate(4, comment.getCommented_at());
            ps.setDate(5, comment.getUpdated_at());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Comments comment) throws SQLException {
        String req = "UPDATE comments SET post_id=?, commenter_id=?, comment=?, commented_at=?, updated_at=? WHERE comment_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, comment.getPost_id());
            ps.setInt(2, comment.getCommenter_id());
            ps.setString(3, comment.getComment());
            ps.setDate(4, comment.getCommented_at());
            ps.setDate(5, comment.getUpdated_at());
            ps.setInt(6, comment.getComment_id());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String req = "DELETE FROM comments WHERE comment_id=?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Comments> ListAll() throws SQLException {
        List<Comments> comments = new ArrayList<>();
        String req = "SELECT * FROM comments";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Comments comment = new Comments();
                comment.setComment_id(rs.getInt("comment_id"));
                comment.setPost_id(rs.getInt("post_id"));
                comment.setCommenter_id(rs.getInt("commenter_id"));
                comment.setComment(rs.getString("comment"));
                comment.setCommented_at(rs.getDate("commented_at"));
                comment.setUpdated_at(rs.getDate("updated_at"));
                comments.add(comment);
            }
        }
        return comments;
    }
}