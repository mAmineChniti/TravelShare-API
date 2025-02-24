package tn.esprit.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;
import org.json.JSONObject;
import tn.esprit.entities.FlaggedContent;
import tn.esprit.entities.Likes;
import tn.esprit.entities.Posts;
import tn.esprit.entities.SessionManager;
import tn.esprit.services.FlaggedContentService;
import tn.esprit.services.LikesService;
import tn.esprit.services.PostsService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PostsController {

    private static final int POSTS_BATCH_SIZE = 10;
    private final PostsService postsService = new PostsService();
    private final ObservableList<Posts> postsObservableList = FXCollections.observableArrayList();
    @FXML
    private VBox postsContainer;
    @FXML
    private TextArea postInput;
    @FXML
    private Button postButton;
    private int currentPostCount = 0;
    private static final String PROFANITY_API_URL = " https://neutrinoapi.net/bad-word-filter";
    private static final String USER_ID = Dotenv.load().get("PROFANITY_USER_ID");
    private static final String API_KEY = Dotenv.load().get("PROFANITY_API_KEY");
    @FXML
    public void SwitchToAccueil(ActionEvent actionEvent) {
        try {
            String AccueilLink = SessionManager.getInstance().getCurrentUtilisateur().getRole() == 1 ? "/AccueilAdmin.fxml" : "/Accueil.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AccueilLink));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void SwitchToVoyages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Voyages.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SwitchToHotels(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Hotel.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileUtilisateur.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deconnexion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Connecter.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadMorePosts();

        postButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 14px; -fx-padding: 7px 15px;");

        postButton.setOnMouseEntered(e -> postButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white;"));
        postButton.setOnMouseExited(e -> postButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"));

        postsContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            Parent parent = postsContainer.getParent();
            while (parent != null && !(parent instanceof ScrollPane)) {
                parent = parent.getParent();
            }
            ScrollPane scrollPane = (ScrollPane) parent;
            if (scrollPane != null) {
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
        if (content == null || content.trim().isEmpty()) {
            System.out.println("Post content cannot be empty.");
            return;
        }

        checkForProfanity(content, (isProfane) -> {
            if (isProfane) {
                Platform.runLater(() -> System.out.println("The content contains bad words and cannot be posted."));
            } else {
                Platform.runLater(() -> addPost(content));
            }
        });
    }
    private void addPost(String content) {
        Posts newPost = new Posts();
        SessionManager session = SessionManager.getInstance();
        newPost.setOwner_id(session.getCurrentUtilisateur().getUser_id());
        newPost.setOwner_name(session.getCurrentUtilisateur().getName());
        newPost.setOwner_last_name(session.getCurrentUtilisateur().getLast_name());
        newPost.setText_content(content);
        newPost.setCreated_at(new Date(System.currentTimeMillis()));
        newPost.setUpdated_at(new Date(System.currentTimeMillis()));

        try {
            int postId = postsService.addAndId(newPost);
            if (postId != -1) {
                newPost.setPost_id(postId);
                postsObservableList.add(0, newPost);
                postInput.clear();
                addPostToContainer(newPost, true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save the post: " + e.getMessage(), e);
        }
    }

    private void checkForProfanity(String content, java.util.function.Consumer<Boolean> callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("content", content)
                .build();
        Request request = new Request.Builder()
                .url(PROFANITY_API_URL)
                .post(formBody)
                .addHeader("User-ID", USER_ID)
                .addHeader("API-Key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Failed to connect: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response: " + response.message());
                }

                String responseBody = response.body().string();
                boolean isProfane = new JSONObject(responseBody).getBoolean("is-bad");
                callback.accept(isProfane);
            }
        });
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

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);

        Label posterName = new Label(post.getOwner_name() + " " + post.getOwner_last_name());
        posterName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button flagButton = new Button("❌");
        flagButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-size: 12px; -fx-padding: 2px 5px;");

        flagButton.setOnMouseEntered(e -> flagButton.setStyle("-fx-background-color: #b02a37; -fx-text-fill: white;"));
        flagButton.setOnMouseExited(e -> flagButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"));

        flagButton.setOnAction(event -> {
            try {
                FlaggedContentService flaggedContentService = new FlaggedContentService();
                SessionManager session = SessionManager.getInstance();
                int currentUserId = session.getCurrentUtilisateur().getUser_id();

                FlaggedContent flaggedContent = new FlaggedContent(post.getPost_id(), currentUserId,new Date(System.currentTimeMillis()));
                flaggedContentService.add(flaggedContent);

                postsContainer.getChildren().remove(postBox);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        headerBox.getChildren().addAll(posterName, flagButton);
        HBox.setHgrow(posterName, javafx.scene.layout.Priority.ALWAYS);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        TextArea postContent = new TextArea(post.getText_content());
        postContent.setWrapText(true);
        postContent.setEditable(false);
        postContent.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 13px;");
        postContent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

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

        if (session.getCurrentUtilisateur().getRole() == 0) {
            buttonBox.getChildren().add(likeButton);
        }

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

                    String newContent = postContent.getText();
                    if (newContent != null && !newContent.trim().isEmpty()) {
                        checkForProfanity(newContent, (isProfane) -> {
                            if (isProfane) {
                                Platform.runLater(() -> {
                                    System.out.println("The content contains bad words. Reverting to original text.");
                                    postContent.setText(post.getText_content());
                                });
                            } else {
                                Platform.runLater(() -> {
                                    updatePost(post, newContent);
                                });
                            }
                        });
                    } else {
                        postContent.setText(post.getText_content());
                    }
                }
            });

            deleteButton.setOnAction(event -> deletePost(post, postBox));

            buttonBox.getChildren().addAll(editButton, deleteButton);
        }

        postBox.getChildren().addAll(headerBox, postContent, buttonBox);
        if (addToTop) {
            postsContainer.getChildren().add(0, postBox);
        } else {
            postsContainer.getChildren().add(postBox);
        }
    }

    private void updateLikeButtonStyle(Button likeButton, boolean isLiked) {
        if (isLiked) {
            likeButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        } else {
            likeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        }
    }

    private void updatePost(Posts post, String newContent) {
        if (newContent != null && !newContent.trim().isEmpty()) {
            post.setText_content(newContent);
            post.setUpdated_at(new Date(System.currentTimeMillis()));

            try {
                postsService.update(post);
            } catch (SQLException e) {
                e.printStackTrace();
            }
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