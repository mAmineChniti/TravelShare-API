package tn.esprit.tests;

import tn.esprit.entities.FlaggedContent;
import tn.esprit.entities.Likes;
import tn.esprit.entities.Posts;
import tn.esprit.services.FlaggedContentService;
import tn.esprit.services.LikesService;
import tn.esprit.services.PostsService;

import java.sql.SQLException;
import java.util.List;

public class ForumTests {
    public static void main(String[] args) {
        testPostsService();

        testLikesService();

        testFlaggedContentService();
    }

    private static void testFlaggedContentService() {
        FlaggedContentService service = new FlaggedContentService();

        try {
            FlaggedContent flaggedContent = new FlaggedContent();
            flaggedContent.setPost_id(1);
            flaggedContent.setFlagger_id(2);
            service.add(flaggedContent);
            System.out.println("Flagged content added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding flagged content: " + e.getMessage());
        }

        try {
            List<FlaggedContent> flaggedContents = service.ListAll();
            if (!flaggedContents.isEmpty()) {
                System.out.println("Flagged content listed successfully.");
            } else {
                System.out.println("No flagged content found.");
            }
        } catch (SQLException e) {
            System.out.println("Error listing flagged content: " + e.getMessage());
        }

        try {
            service.delete(1);
            System.out.println("Flagged content deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting flagged content: " + e.getMessage());
        }
    }

    private static void testPostsService() {
        PostsService service = new PostsService();

        try {
            Posts post = new Posts();
            post.setOwner_id(1);
            post.setCreated_at(new java.sql.Date(System.currentTimeMillis()));
            post.setUpdated_at(new java.sql.Date(System.currentTimeMillis()));
            post.setText_content("Test post content");
            service.add(post);
            System.out.println("Post added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding post: " + e.getMessage());
        }

        try {
            List<Posts> posts = service.ListAll();
            if (!posts.isEmpty()) {
                System.out.println("Posts listed successfully.");
            } else {
                System.out.println("No posts found.");
            }
        } catch (SQLException e) {
            System.out.println("Error listing posts: " + e.getMessage());
        }

        try {
            Posts postToUpdate = new Posts();
            postToUpdate.setPost_id(1);
            postToUpdate.setText_content("Updated post content");
            postToUpdate.setUpdated_at(new java.sql.Date(System.currentTimeMillis()));
            service.update(postToUpdate);
            System.out.println("Post updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating post: " + e.getMessage());
        }

        try {
            service.delete(5);
            System.out.println("Post deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting post: " + e.getMessage());
        }
    }

    private static void testLikesService() {
        LikesService service = new LikesService();

        try {
            Likes like = new Likes();
            like.setLiker_id(1);
            like.setPost_id(1);
            service.addOrRemove(like);
            System.out.println("Like added/removed successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding/removing like: " + e.getMessage());
        }
    }
}
