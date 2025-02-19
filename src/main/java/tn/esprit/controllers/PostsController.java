package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import tn.esprit.entities.Posts;
import tn.esprit.services.PostsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class PostsController {

    @FXML
    private VBox postsContainer;

    @FXML
    private TextArea postInput;

    private final PostsService postsService = new PostsService();
    private ObservableList<Posts> postsObservableList = FXCollections.observableArrayList();
    private int currentPostCount = 0;
    private static final int POSTS_BATCH_SIZE = 10;

    @FXML
    public void initialize() {
        loadMorePosts();

        postsContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            ScrollPane scrollPane = (ScrollPane) postsContainer.getParent();
            scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() == 1.0) {
                    loadMorePosts();
                }
            });
        });
    }

    @FXML
    private void handlePostButtonAction() {
        String content = postInput.getText();
        if (content != null && !content.trim().isEmpty()) {
            Posts newPost = new Posts();
            newPost.setText_content(content);
            newPost.setOwner_id(1);

            try {
                postsService.add(newPost);
                postsObservableList.add(0, newPost);
                postInput.clear();
                addPostToContainer(newPost, true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMorePosts() {
        try {
            List<Posts> posts = postsService.fetchPosts(currentPostCount, POSTS_BATCH_SIZE);
            for (Posts post : posts) {
                addPostToContainer(post, false);
            }
            currentPostCount += posts.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPostToContainer(Posts post, boolean addToTop) {
        VBox postBox = new VBox(5);
        postBox.setStyle("-fx-border-color: #ddd; -fx-background-color: #fff; -fx-padding: 10; -fx-border-radius: 5; -fx-spacing: 5;");

        Label posterName = new Label(post.getOwner_name() + " " + post.getOwner_last_name());
        posterName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextArea postContent = new TextArea(post.getText_content());
        postContent.setWrapText(true);
        postContent.setEditable(false);
        postContent.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        postContent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        postBox.getChildren().addAll(posterName, postContent);
        if (addToTop) {
            postsContainer.getChildren().add(0, postBox);
        } else {
            postsContainer.getChildren().add(postBox);
        }
    }
}