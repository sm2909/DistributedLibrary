import java.sql.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;


public class Main extends Application {
    
    // Database credentials
    private static final String URL = "jdbc:mysql://10.25.76.66:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "cs24b037.5"; 

    private static Connection conn;
    private static Stage primaryStage;
    private static Student currentUser;

   
    private static Connection getDatabaseConnection() {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                if (conn != null) {
                    System.out.println("Connected to MySQL database successfully!");
                    return conn;
                }
            } catch (ClassNotFoundException e) {
                showAlert("Database Error", "MySQL JDBC Driver not found. Please install it.");
                return null;
            } catch (SQLException e) {
                retryCount++;
                if (retryCount == maxRetries) {
                    showAlert("Database Error", 
                        "Failed to connect to database after " + maxRetries + " attempts.\n" +
                        "Please check:\n" +
                        "1. MySQL server is running\n" +
                        "2. Connection details are correct\n" +
                        "3. Network connection is stable\n" +
                        "Error: " + e.getMessage());
                    return null;
                }
                try {
                    Thread.sleep(2000); // Wait 2 seconds before retrying
                } 
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return null;
    }

    //@Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        primaryStage.setTitle("Library Management System");
        
        // Initialize database connection
        conn = getDatabaseConnection();
        if (conn == null) {
            showAlert("Fatal Error", "Cannot start application without database connection");
            return;
        }

        showLoginScene();
    }



    public static void showLoginScene() {
        VBox loginLayout = new VBox(20);
        loginLayout.setPadding(new Insets(40));
        loginLayout.setAlignment(Pos.CENTER);

        try
        {
            ImageView logoImage = new ImageView(new Image(Main.class.getResource("/logos/Logo.jpg").toExternalForm()));
            logoImage.setFitWidth(150); // Set the width of the image
            logoImage.setPreserveRatio(true); // Preserve the aspect ratio
            loginLayout.getChildren().add(logoImage);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Error in loading logo image");
        }

        Label titleLabel = new Label("Library Management System");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        TextField rollNoField = new TextField();
        rollNoField.setPromptText("Roll Number");
        rollNoField.setMaxWidth(300);
        rollNoField.setMinHeight(40);
        rollNoField.setStyle("-fx-font-size: 20px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setMinHeight(40);
        passwordField.setStyle("-fx-font-size: 20px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 150px; -fx-min-height: 40px;");
        loginButton.setOnAction(e -> handleLogin(rollNoField.getText(), passwordField.getText()));

        Button signupButton = new Button("Sign Up");
        signupButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 150px; -fx-min-height: 40px;");
        signupButton.setOnAction(e -> showSignupScene());

        loginLayout.getChildren().addAll(titleLabel, rollNoField, passwordField, loginButton, signupButton);
        Scene loginScene = new Scene(loginLayout, 800, 600);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }



    public static void showSignupScene() {
        VBox signupLayout = new VBox(20);
        signupLayout.setPadding(new Insets(40));
        signupLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Sign Up");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-font-size: 20px;");

        TextField rollNoField = new TextField();
        rollNoField.setPromptText("Roll Number");
        rollNoField.setMaxWidth(300);
        rollNoField.setStyle("-fx-font-size: 20px;");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        contactField.setMaxWidth(300);
        contactField.setStyle("-fx-font-size: 20px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-font-size: 20px;");

        Button signupButton = new Button("Create Account");
        signupButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        signupButton.setOnAction(e -> handleSignup(nameField.getText(), rollNoField.getText(), 
            contactField.getText(), passwordField.getText()));

        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showLoginScene());

        signupLayout.getChildren().addAll(titleLabel, nameField, rollNoField, contactField, 
            passwordField, signupButton, backButton);
        Scene signupScene = new Scene(signupLayout, 800, 600);
        primaryStage.setScene(signupScene);
    }

   
    
    public static boolean isLibraryOpen(Connection conn) {
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM library_timings");
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                String openingTime = rs.getString("opening_time");
                String closingTime = rs.getString("closing_time");
                
                // Get current time
                java.time.LocalTime currentTime = java.time.LocalTime.now();
                java.time.LocalTime openTime = java.time.LocalTime.parse(openingTime);
                java.time.LocalTime closeTime = java.time.LocalTime.parse(closingTime);
                
                return !currentTime.isBefore(openTime) && !currentTime.isAfter(closeTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public static void handleLogin(String rollNo, String password) {
        currentUser = getUser(rollNo, conn);

        if (currentUser == null) {
            showAlert("Login Failed", "User not found");
            return;
        }

        if (!currentUser.password.equals(password)) {
            showAlert("Login Failed", "Incorrect password");
            return;
        }

        if (rollNo.equals("admin")) {
            if (password.equals("admin@123")) {
                Admin admin = new Admin("admin", 0, "admin", "admin@123", primaryStage, conn);
                admin.showAdminMenu();
                return;
            } else {
                showAlert("Login Failed", "Incorrect admin password");
                return;
            }
        }

        if (!isLibraryOpen(conn)) {
            showAlert("Library Closed", "The library is currently closed. Please visit during opening hours.");
            return;
        }

        currentUser.showStudentMenu();
    }



    public static void handleSignup(String name, String rollNo, String contact, String password) {
        try {
            PreparedStatement st = conn.prepareStatement(
                "INSERT INTO student(rollNo, name, contactNo, password) VALUES (?,?,?,?)"
            );
            st.setString(1, rollNo);
            st.setString(2, name);
            st.setLong(3, Long.parseLong(contact));
            st.setString(4, password);
            st.executeUpdate();
            showAlert("Success", "Account created successfully!");
            showLoginScene();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create account");
        }
    }

    

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }




    public static void main(String[] args) {
        launch(args);
    }

    //sql enabled
    public static Student getUser(String rollNo, Connection conn) {
        String sql = "SELECT * FROM student WHERE rollNo = ?";  

        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, rollNo);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {  
                // Retrieve user details from database
                String username = rs.getString("name");
                long contactNo = rs.getLong("contactNo");
                String password = rs.getString("password");
                
                // Return a new Student object with the retrieved data
                return new Student(username, contactNo, password, rollNo, primaryStage, conn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Problem in getting user from given roll no from sql");
        }

        return null; // If no user is found, return null
    }
    

}