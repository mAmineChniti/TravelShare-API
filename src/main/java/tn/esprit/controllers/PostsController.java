package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.ScrollPane;
import tn.esprit.entities.Likes;
import tn.esprit.entities.Posts;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.LikesService;
import tn.esprit.services.PostsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PostsController {

    @FXML
    private VBox postsContainer;

    @FXML
    private TextArea postInput;

    @FXML
    private Button postButton;

    private final PostsService postsService = new PostsService();
    private ObservableList<Posts> postsObservableList = FXCollections.observableArrayList();
    private int currentPostCount = 0;
    private static final int POSTS_BATCH_SIZE = 10;

    @FXML
    public void initialize() {
        loadMorePosts();

        // Styling the Post button
        postButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 7px 15px;");

        postButton.setOnMouseEntered(e -> postButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white;"));
        postButton.setOnMouseExited(e -> postButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"));

        postsContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            Parent parent = postsContainer.getParent();
            while (parent != null && !(parent instanceof ScrollPane)) {
                parent = parent.getParent();
            }
            ScrollPane scrollPane = (ScrollPane) parent;
            if (scrollPane!=null) {
                scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 1.0) {
                        loadMorePosts();
                    }
                });
            }
        });
    }

    @FXML
    private void handlePostButtonAction() {
        String content = postInput.getText();
        if (content != null && !content.trim().isEmpty()) {
            Posts newPost = new Posts();
            SessionManager session = SessionManager.getInstance();
            newPost.setOwner_id(session.getCurrentUtilisateur().getUser_id());
            newPost.setOwner_name(session.getCurrentUtilisateur().getName());
            newPost.setOwner_last_name(session.getCurrentUtilisateur().getLast_name());
            newPost.setText_content(content);
            newPost.setCreated_at(new Date(System.currentTimeMillis()));
            newPost.setUpdated_at(new Date(System.currentTimeMillis()));
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
        VBox postBox = new VBox(10);
        postBox.setPadding(new Insets(15));
        postBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");
        postBox.setMaxWidth(500);

        Label posterName = new Label(post.getOwner_name() + " " + post.getOwner_last_name());
        posterName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextArea postContent = new TextArea(post.getText_content());
        postContent.setWrapText(true);
        postContent.setEditable(false);
        postContent.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 13px;");
        postContent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        // Like Button & Counter
        Button likeButton = new Button();
        LikesService likesService = new LikesService();
        SessionManager session = SessionManager.getInstance();
        int currentUserId = session.getCurrentUtilisateur().getUser_id();

        try {
            int likesCount = likesService.likesCounter(post.getPost_id());
            likeButton.setText("❤️ " + likesCount);
            boolean isLiked = likesService.isLikedByUser(currentUserId, post.getPost_id());

            updateLikeButtonStyle(likeButton, isLiked);

            likeButton.setOnAction(e -> {
                try {
                    likesService.addOrRemove(new Likes(currentUserId, post.getPost_id()));
                    int newLikesCount = likesService.likesCounter(post.getPost_id());
                    likeButton.setText("❤️ " + newLikesCount);
                    boolean updatedLikeStatus = likesService.isLikedByUser(currentUserId, post.getPost_id());
                    updateLikeButtonStyle(likeButton, updatedLikeStatus);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        buttonBox.getChildren().add(likeButton);

        // Admin & Owner Controls (Edit/Delete)
        int currentUserRole = session.getCurrentUtilisateur().getRole();
        if (currentUserRole == 1 || currentUserId == post.getOwner_id()) {
            Button editButton = new Button("✏ Edit");
            Button deleteButton = new Button("🗑 Delete");

            editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
            deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");

            editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;"));
            editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;"));

            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #b02a37; -fx-text-fill: white;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"));

            editButton.setOnAction(event -> {
                if (editButton.getText().equals("✏ Edit")) {
                    postContent.setEditable(true);
                    postContent.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
                    editButton.setText("💾 Save");
                } else {
                    postContent.setEditable(false);
                    postContent.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
                    editButton.setText("✏ Edit");
                    updatePost(post, postContent.getText());
                }
            });

            deleteButton.setOnAction(event -> deletePost(post, postBox));

            buttonBox.getChildren().addAll(editButton, deleteButton);
        }

        postBox.getChildren().addAll(posterName, postContent, buttonBox);
        if (addToTop) {
            postsContainer.getChildren().add(0, postBox);
        } else {
            postsContainer.getChildren().add(postBox);
        }
    }

    /**
     * Updates the like button style based on the like status.
     */
    private void updateLikeButtonStyle(Button likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        } else {
            likeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        }
    }

    private void updatePost(Posts post, String newContent) {
        post.setText_content(newContent);
        post.setUpdated_at(new Date(System.currentTimeMillis()));

        try {
            postsService.update(post);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deletePost(Posts post, VBox postBox) {
        try {
            postsService.delete(post.getPost_id());
            postsContainer.getChildren().remove(postBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}