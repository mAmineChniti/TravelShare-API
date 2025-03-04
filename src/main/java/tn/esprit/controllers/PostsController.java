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
import tn.esprit.entities.*;
import tn.esprit.services.CommentsService;
import tn.esprit.services.FlaggedContentService;
import tn.esprit.services.LikesService;
import tn.esprit.services.PostsService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PostsController {

    private static final int POSTS_BATCH_SIZE = 10;
    private static final String PROFANITY_API_URL = " https://neutrinoapi.net/bad-word-filter";
    private static final Dotenv dotenv = Dotenv.load();
    private static final String USER_ID = dotenv.get("PROFANITY_USER_ID");
    private static final String API_KEY = dotenv.get("PROFANITY_API_KEY");
    private final PostsService postsService = new PostsService();
    private final ObservableList<Posts> postsObservableList = FXCollections.observableArrayList();
    @FXML
    private VBox postsContainer;
    @FXML
    private TextArea postInput;
    @FXML
    private Button postButton;
    private int currentPostCount = 0;

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
    void SwitchToExcursions(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListExcursionGuide.fxml"));
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
            List<Posts> posts = postsService.fetchPosts(currentPostCount, POSTS_BATCH_SIZE, SessionManager.getInstance().getCurrentUtilisateur().getUser_id());
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

                FlaggedContent flaggedContent = new FlaggedContent(post.getPost_id(), currentUserId, new Date(System.currentTimeMillis()));
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

        VBox commentSection = new VBox(5);
        commentSection.setPadding(new Insets(10, 0, 0, 0));
        commentSection.setVisible(true);
        commentSection.setPrefHeight(Region.USE_COMPUTED_SIZE);

        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Write a comment...");
        commentInput.setWrapText(true);
        commentInput.setMaxHeight(50);
        commentInput.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Button submitCommentButton = new Button("Comment");
        submitCommentButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");

        submitCommentButton.setOnAction(e -> {
            String commentText = commentInput.getText().trim();
            if (commentText != null && !commentText.isEmpty()) {
                checkForProfanity(commentText, (isProfane) -> {
                    if (isProfane) {
                        Platform.runLater(() -> System.out.println("The comment contains bad words and cannot be posted."));
                        commentInput.clear();
                    } else {
                        Platform.runLater(() -> addComment(post.getPost_id(), commentText, commentSection));
                        commentInput.clear();
                    }
                });
            }
        });

        HBox commentBox = new HBox(10, commentInput, submitCommentButton);
        commentBox.setAlignment(Pos.CENTER_LEFT);

        commentSection.getChildren().add(commentBox);
        loadComments(post.getPost_id(), commentSection);
        if (post.getOwner_id() == SessionManager.getInstance().getCurrentUtilisateur().getUser_id()) {
            Button shareButton = new Button("Share");
            shareButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
            shareButton.setOnAction(e -> {
                String encodedContent = URLEncoder.encode(post.getText_content(), StandardCharsets.UTF_8);
                String shareURL = "https://www.x.com/intent/tweet?text=" + encodedContent;
                openURL(shareURL);
            });
            buttonBox.getChildren().add(0,shareButton);
        }
        postBox.getChildren().addAll(headerBox, postContent, buttonBox, commentSection);
        //Platform.runLater(() -> postsContainer.layout());
        if (addToTop) {
            postsContainer.getChildren().add(0, postBox);
        } else {
            postsContainer.getChildren().add(postBox);
        }
    }
    private static void openURL(String url) {
        Platform.runLater(() -> {
            try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        new ProcessBuilder("cmd", "/c", "start", url).start();
                    } else if (os.contains("mac")) {
                        new ProcessBuilder("open", url).start();
                    } else {
                        new ProcessBuilder("xdg-open", url).start();
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    private void loadComments(int postId, VBox commentSection) {
        CommentsService commentsService = new CommentsService();

        try {
            List<Comments> comments = commentsService.fetchById(postId);
            // System.out.println("Loaded " + comments.size() + " comments for post ID: " + postId);

            Platform.runLater(() -> {
                //commentSection.getChildren().clear();

                for (Comments comment : comments) {
                    addCommentToSection(comment, commentSection);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCommentToSection(Comments comment, VBox commentSection) {
        VBox commentBox = new VBox(5);
        commentBox.setAlignment(Pos.TOP_LEFT);
        commentBox.setPadding(new Insets(5, 0, 5, 0));
        commentBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 8; -fx-background-radius: 8;");
        commentBox.setMaxWidth(500);

        Label commenterName = new Label(comment.getCommenter_name() + " " + comment.getCommenter_lastname());
        commenterName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 0 0 5px 0;");
        commenterName.setMaxWidth(500);

        TextArea commentContent = new TextArea(comment.getComment());
        commentContent.setWrapText(true);
        commentContent.setEditable(false);
        commentContent.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-font-size: 13px;");
        commentContent.setPrefWidth(500);
        commentContent.setPrefRowCount(1);
        commentContent.setMinHeight(Region.USE_COMPUTED_SIZE);

        HBox buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        SessionManager session = SessionManager.getInstance();
        int currentUserId = session.getCurrentUtilisateur().getUser_id();
        int userRole = session.getCurrentUtilisateur().getRole();

        if (currentUserId == comment.getCommenter_id() || userRole == 1) {
            Button editButton = new Button("✏");
            Button deleteButton = new Button("🗑");

            editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
            editButton.setOnMouseEntered(e -> editButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;"));
            editButton.setOnMouseExited(e -> editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;"));

            deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 5; -fx-background-radius: 5;");
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #b02a37; -fx-text-fill: white;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"));
            editButton.setOnAction(e -> {
                if (editButton.getText().equals("✏")) {
                    commentContent.setEditable(true);
                    commentContent.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
                    editButton.setText("💾");
                } else {
                    commentContent.setEditable(false);
                    commentContent.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
                    editButton.setText("✏");

                    String updatedComment = commentContent.getText();
                    if (updatedComment != null && !updatedComment.trim().isEmpty()) {
                        checkForProfanity(updatedComment, (isProfane) -> {
                            if (isProfane) {
                                Platform.runLater(() -> {
                                    System.out.println("The comment contains bad words. Reverting to original text.");
                                    commentContent.setText(comment.getComment());
                                });
                            } else {
                                Platform.runLater(() -> {
                                    updateComment(comment.getComment_id(), updatedComment);
                                });
                            }
                        });
                    } else {
                        commentContent.setText(comment.getComment());
                    }
                }
            });

            deleteButton.setOnAction(e -> deleteComment(comment.getComment_id(), commentBox, commentSection));

            buttonBox.getChildren().addAll(editButton, deleteButton);
        }

        commentBox.getChildren().addAll(commenterName, commentContent, buttonBox);

        commentSection.getChildren().add(commentBox);
    }

    private void addComment(int postId, String commentText, VBox commentSection) {
        CommentsService commentsService = new CommentsService();
        SessionManager session = SessionManager.getInstance();
        int currentUserId = session.getCurrentUtilisateur().getUser_id();

        Comments newComment = new Comments();
        newComment.setPost_id(postId);
        newComment.setCommenter_id(currentUserId);
        newComment.setCommenter_name(session.getCurrentUtilisateur().getName());
        newComment.setCommenter_lastname(session.getCurrentUtilisateur().getLast_name());
        newComment.setComment(commentText);
        newComment.setCommented_at(new Date(System.currentTimeMillis()));
        newComment.setUpdated_at(new Date(System.currentTimeMillis()));

        try {
            commentsService.add(newComment);
            addCommentToSection(newComment, commentSection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateComment(int commentId, String updatedComment) {
        CommentsService commentsService = new CommentsService();
        try {
            Comments comment = new Comments();
            comment.setComment_id(commentId);
            comment.setComment(updatedComment);
            comment.setUpdated_at(new Date(System.currentTimeMillis()));
            commentsService.update(comment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteComment(int commentId, VBox commentBox, VBox commentSection) {
        CommentsService commentsService = new CommentsService();
        try {
            commentsService.delete(commentId);
            commentSection.getChildren().remove(commentBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}