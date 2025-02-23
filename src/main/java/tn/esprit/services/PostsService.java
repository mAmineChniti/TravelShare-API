package tn.esprit.services;

import tn.esprit.entities.Posts;
import tn.esprit.utils.dbCon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostsService implements IService<Posts> {
    Connection con;

    public PostsService() {
        con = dbCon.getInstance().getConnection();
    }

    @Override
    public void add(Posts post) throws SQLException {
        String addReq = "INSERT INTO posts (Owner_id, created_at, updated_at, text_content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement prepStat = con.prepareStatement(addReq)) {
            prepStat.setInt(1, post.getOwner_id());
            prepStat.setDate(2, post.getCreated_at());
            prepStat.setDate(3, post.getUpdated_at());
            prepStat.setString(4, post.getText_content());
            prepStat.executeUpdate();
        }
    }

    public int addAndId(Posts post) throws SQLException {
        String addReq = "INSERT INTO posts (Owner_id, created_at, updated_at, text_content) VALUES (?, ?, ?, ?)";
        try (PreparedStatement prepStat = con.prepareStatement(addReq, Statement.RETURN_GENERATED_KEYS)) {
            prepStat.setInt(1, post.getOwner_id());
            prepStat.setDate(2, post.getCreated_at());
            prepStat.setDate(3, post.getUpdated_at());
            prepStat.setString(4, post.getText_content());

            int affectedRows = prepStat.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = prepStat.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }


    @Override
    public void update(Posts post) throws SQLException {
        String updateReq = "UPDATE posts SET text_content = ?, updated_at = ? WHERE Post_id = ?";
        try (PreparedStatement prepStat = con.prepareStatement(updateReq)) {
            prepStat.setString(1, post.getText_content());
            prepStat.setDate(2, post.getUpdated_at());
            prepStat.setInt(3, post.getPost_id());
            prepStat.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String deleteReq = "DELETE FROM posts WHERE Post_id = ?";
        try (PreparedStatement prepStat = con.prepareStatement(deleteReq)) {
            prepStat.setInt(1, id);
            prepStat.executeUpdate();
        }
    }

    @Override
    public List<Posts> ListAll() throws SQLException {
        List<Posts> posts = new ArrayList<>();
        String listReq = "SELECT * FROM posts";
        try (PreparedStatement prepStat = con.prepareStatement(listReq);
             ResultSet rs = prepStat.executeQuery()) {
            while (rs.next()) {
                Posts post = new Posts();
                post.setPost_id(rs.getInt("Post_id"));
                post.setOwner_id(rs.getInt("Owner_id"));
                post.setCreated_at(rs.getDate("created_at"));
                post.setUpdated_at(rs.getDate("updated_at"));
                post.setText_content(rs.getString("text_content"));
                posts.add(post);
            }
        }
        return posts;
    }

    public List<Posts> fetchPosts(int offset, int limit) throws SQLException {
        List<Posts> posts = new ArrayList<>();
        String query = "SELECT p.*, u.name, u.last_name FROM posts p " +
                "JOIN users u ON p.Owner_id = u.user_id " +
                "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement prepStat = con.prepareStatement(query)) {
            prepStat.setInt(1, limit);
            prepStat.setInt(2, offset);
            try (ResultSet rs = prepStat.executeQuery()) {
                while (rs.next()) {
                    Posts post = new Posts();
                    post.setPost_id(rs.getInt("Post_id"));
                    post.setOwner_id(rs.getInt("Owner_id"));
                    post.setCreated_at(rs.getDate("created_at"));
                    post.setUpdated_at(rs.getDate("updated_at"));
                    post.setText_content(rs.getString("text_content"));
                    post.setOwner_name(rs.getString("name"));
                    post.setOwner_last_name(rs.getString("last_name"));
                    posts.add(post);
                }
            }
        }
        return posts;
    }
}