import java.sql.*;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class Admin extends User {
    String adminID;
    private Connection conn;
    private Stage primaryStage;

    Admin(String name, long contactNo, String adminID, String password, Stage primaryStage, Connection conn){
        super(name, contactNo, password);
        this.adminID = adminID;
        this.primaryStage = primaryStage;
        this.conn = conn;
    }

    public void showAdminMenu() {
        VBox menuLayout = new VBox(20);
        menuLayout.setPadding(new Insets(40));
        menuLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome, Admin");
        welcomeLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button bookManagementButton = new Button("Book Management");
        bookManagementButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        bookManagementButton.setOnAction(e -> showBookManagementScene());

        Button kioskManagementButton = new Button("Kiosk Management");
        kioskManagementButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        kioskManagementButton.setOnAction(e -> showKioskManagementScene());

        Button timingsManagementButton = new Button("Library Timings");
        timingsManagementButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        timingsManagementButton.setOnAction(e -> showTimingsManagementScene());

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        logoutButton.setOnAction(e -> Main.showLoginScene());

        menuLayout.getChildren().addAll(welcomeLabel, bookManagementButton, 
            kioskManagementButton, timingsManagementButton, logoutButton);
        Scene menuScene = new Scene(menuLayout, 800, 700);
        primaryStage.setScene(menuScene);
    }

    private void showBookManagementScene() {
        VBox bookLayout = new VBox(20);
        bookLayout.setPadding(new Insets(40));

        Label titleLabel = new Label("Book Management");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        TableView<Book> bookTable = new TableView<>();
        bookTable.setMinHeight(400);
        bookTable.setStyle("-fx-font-size: 20px;");
        
        TableColumn<Book, Integer> idColumn = new TableColumn<>("Book ID");
        idColumn.setMinWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(200);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setMinWidth(200);
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        TableColumn<Book, Boolean> availabilityColumn = new TableColumn<>("Available");
        availabilityColumn.setMinWidth(100);
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        TableColumn<Book, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setMinWidth(300);
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        bookTable.getColumns().addAll(Arrays.asList(idColumn, titleColumn, authorColumn, availabilityColumn, locationColumn));

        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM book");
            ResultSet rs = st.executeQuery();
            ObservableList<Book> books = FXCollections.observableArrayList();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("bookID"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("availability"),
                    rs.getString("location")
                ));
            }
            bookTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load books");
        }

        HBox buttonLayout = new HBox(20);
        buttonLayout.setPadding(new Insets(20, 0, 0, 0));
        buttonLayout.setAlignment(Pos.CENTER);

        Button addBookButton = new Button("Add Book");
        addBookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 150px; -fx-min-height: 40px;");
        addBookButton.setOnAction(e -> showAddBookScene());

        Button removeBookButton = new Button("Remove Book");
        removeBookButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 150px; -fx-min-height: 40px;");
        removeBookButton.setOnAction(e -> {
            Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                removeBook(selectedBook.getBookID());
                showBookManagementScene();
            } else {
                showAlert("Error", "Please select a book to remove");
            }
        });

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 150px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showAdminMenu());

        buttonLayout.getChildren().addAll(addBookButton, removeBookButton, backButton);
        bookLayout.getChildren().addAll(titleLabel, bookTable, buttonLayout);

        Scene bookScene = new Scene(bookLayout, 1000, 700);
        primaryStage.setScene(bookScene);
    }

    private void showKioskManagementScene() {
        VBox kioskLayout = new VBox(20);
        kioskLayout.setPadding(new Insets(40));

        TableView<Kiosk> kioskTable = new TableView<>();
        kioskTable.setMinHeight(400);
        kioskTable.setStyle("-fx-font-size: 20px;");
        
        TableColumn<Kiosk, Integer> idColumn = new TableColumn<>("Kiosk ID");
        idColumn.setMinWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("kioskID"));
        
        TableColumn<Kiosk, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setMinWidth(300);
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        TableColumn<Kiosk, Integer> sectionColumn = new TableColumn<>("Section");
        sectionColumn.setMinWidth(100);
        sectionColumn.setCellValueFactory(new PropertyValueFactory<>("sectionNo"));
        
        TableColumn<Kiosk, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setMinWidth(100);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        kioskTable.getColumns().addAll(Arrays.asList(idColumn, locationColumn, sectionColumn, statusColumn));

        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM kiosk");
            ResultSet rs = st.executeQuery();
            ObservableList<Kiosk> kiosks = FXCollections.observableArrayList();
            
            while (rs.next()) {
                kiosks.add(new Kiosk(
                    rs.getInt("kioskID"),
                    rs.getString("location"),
                    rs.getInt("sectionNo"),
                    rs.getString("status")
                ));
            }
            kioskTable.setItems(kiosks);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load kiosks");
        }

        HBox buttonLayout = new HBox(10);
        buttonLayout.setAlignment(Pos.CENTER);

        Button addKioskButton = new Button("Add Kiosk");
        addKioskButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addKioskButton.setOnAction(e -> showAddKioskScene());

        Button removeKioskButton = new Button("Remove Kiosk");
        removeKioskButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeKioskButton.setOnAction(e -> {
            Kiosk selectedKiosk = kioskTable.getSelectionModel().getSelectedItem();
            if (selectedKiosk != null) {
                removeKiosk(selectedKiosk.getKioskID());
                showKioskManagementScene();
            } else {
                showAlert("Error", "Please select a kiosk to remove");
            }
        });

        Button viewBooksButton = new Button("View Kiosk Books");
        viewBooksButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewBooksButton.setOnAction(e -> {
            Kiosk selectedKiosk = kioskTable.getSelectionModel().getSelectedItem();
            if (selectedKiosk != null) {
                showKioskBooksScene(selectedKiosk.getKioskID());
            } else {
                showAlert("Error", "Please select a kiosk to view books");
            }
        });

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> showAdminMenu());

        buttonLayout.getChildren().addAll(addKioskButton, removeKioskButton, viewBooksButton, backButton);
        kioskLayout.getChildren().addAll(kioskTable, buttonLayout);

        Scene kioskScene = new Scene(kioskLayout, 800, 700);
        primaryStage.setScene(kioskScene);
    }

    private void showTimingsManagementScene() {
        VBox timingsLayout = new VBox(20);
        timingsLayout.setPadding(new Insets(40));
        timingsLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Library Timings");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label currentTimingsLabel = new Label("Current Library Timings:");
        currentTimingsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label openingTimeLabel = new Label();
        Label closingTimeLabel = new Label();

        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM library_timings WHERE id = 1");
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                openingTimeLabel.setText("Opening Time: " + rs.getString("opening_time"));
                closingTimeLabel.setText("Closing Time: " + rs.getString("closing_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HBox openingTimeLayout = new HBox(10);
        openingTimeLayout.setAlignment(Pos.CENTER);
        Label openingLabel = new Label("Opening Time:");
        TextField openingTimeField = new TextField();
        openingTimeField.setPromptText("Opening Time (HH:MM)");
        openingTimeField.setMaxWidth(300);
        openingTimeField.setStyle("-fx-font-size: 20px;");
        openingTimeLayout.getChildren().addAll(openingLabel, openingTimeField);

        HBox closingTimeLayout = new HBox(10);
        closingTimeLayout.setAlignment(Pos.CENTER);
        Label closingLabel = new Label("Closing Time:");
        TextField closingTimeField = new TextField();
        closingTimeField.setPromptText("Closing Time (HH:MM)");
        closingTimeField.setMaxWidth(300);
        closingTimeField.setStyle("-fx-font-size: 20px;");
        closingTimeLayout.getChildren().addAll(closingLabel, closingTimeField);

        Button updateButton = new Button("Update Timings");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        updateButton.setOnAction(e -> {
            try {
                PreparedStatement st = conn.prepareStatement(
                    "UPDATE library_timings SET opening_time = ?, closing_time = ? WHERE id = 1"
                );
                st.setString(1, openingTimeField.getText());
                st.setString(2, closingTimeField.getText());
                st.executeUpdate();
                showAlert("Success", "Timings updated successfully");
                showTimingsManagementScene();
            } catch (SQLException ex) {
                showAlert("Error", "Failed to update timings");
                ex.printStackTrace();
            }
        });

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showAdminMenu());

        timingsLayout.getChildren().addAll(titleLabel, currentTimingsLabel, openingTimeLabel, 
            closingTimeLabel, openingTimeLayout, closingTimeLayout, updateButton, backButton);

        Scene timingsScene = new Scene(timingsLayout, 800, 600);
        primaryStage.setScene(timingsScene);
    }

    private void showAddBookScene() {
        VBox addBookLayout = new VBox(10);
        addBookLayout.setPadding(new Insets(20));
        addBookLayout.setAlignment(Pos.CENTER);

        TextField titleField = new TextField();
        titleField.setMaxWidth(300);
        titleField.setStyle("-fx-font-size: 20px;");
        TextField authorField = new TextField();
        authorField.setMaxWidth(300);
        authorField.setStyle("-fx-font-size: 20px;");
        TextField yearField = new TextField();
        yearField.setMaxWidth(300);
        yearField.setStyle("-fx-font-size: 20px;");
        TextField conditionField = new TextField();
        conditionField.setMaxWidth(300);
        conditionField.setStyle("-fx-font-size: 20px;");
        TextField shelfNoField = new TextField();
        shelfNoField.setMaxWidth(300);
        shelfNoField.setStyle("-fx-font-size: 20px;");

        addBookLayout.getChildren().addAll(
            new Label("Title:"), titleField,
            new Label("Author:"), authorField,
            new Label("Published Year:"), yearField,
            new Label("Condition:"), conditionField,
            new Label("Shelf Number:"), shelfNoField
        );

        Button addButton = new Button("Add Book");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        addButton.setOnAction(e -> {
            try {
                int shelfNo = Integer.parseInt(shelfNoField.getText());
                
                // First get section and branch info
                PreparedStatement st = conn.prepareStatement(
                    "SELECT s.shelfNo, sec.sectionNo, b.branchNo " +
                    "FROM shelf s " +
                    "JOIN section sec ON s.sectionNo = sec.sectionNo " +
                    "JOIN branch b ON sec.branchNo = b.branchNo " +
                    "WHERE s.shelfNo = ?"
                );
                st.setInt(1, shelfNo);
                ResultSet rs = st.executeQuery();
                
                String location = null;
                if (rs.next()) {
                    location = String.format("Shelf %d, Section %d, Branch %d",
                        shelfNo,
                        rs.getInt("sectionNo"),
                        rs.getInt("branchNo"));
                } else {
                    showAlert("Error", "Invalid shelf number");
                    return;
                }

                // Insert book with location
                st = conn.prepareStatement(
                    "INSERT INTO book (title, author, publishedYear, `condition`, availability, shelfNo, location) " +
                    "VALUES (?, ?, ?, ?, true, ?, ?)"
                );
                st.setString(1, titleField.getText());
                st.setString(2, authorField.getText());
                st.setInt(3, Integer.parseInt(yearField.getText()));
                st.setString(4, conditionField.getText());
                st.setInt(5, shelfNo);
                st.setString(6, location);
                st.executeUpdate();
                
                showAlert("Success", "Book added successfully");
                showBookManagementScene();
            } catch (SQLException | NumberFormatException ex) {
                showAlert("Error", "Failed to add book");
                ex.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showBookManagementScene());

        addBookLayout.getChildren().addAll(addButton, backButton);

        Scene addBookScene = new Scene(addBookLayout, 800, 600);
        primaryStage.setScene(addBookScene);
    }

    private void showAddKioskScene() {
        VBox addKioskLayout = new VBox(10);
        addKioskLayout.setPadding(new Insets(20));
        addKioskLayout.setAlignment(Pos.CENTER);

        TextField locationField = new TextField();
        locationField.setMaxWidth(300);
        locationField.setStyle("-fx-font-size: 20px;");
        TextField sectionNoField = new TextField();
        sectionNoField.setMaxWidth(300);
        sectionNoField.setStyle("-fx-font-size: 20px;");
        TextField statusField = new TextField();
        statusField.setMaxWidth(300);
        statusField.setStyle("-fx-font-size: 20px;");

        addKioskLayout.getChildren().addAll(
            new Label("Location:"), locationField,
            new Label("Section Number:"), sectionNoField,
            new Label("Status:"), statusField
        );

        Button addButton = new Button("Add Kiosk");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        addButton.setOnAction(e -> {
            try {
                PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO kiosk (location, sectionNo, status) VALUES (?, ?, ?)"
                );
                st.setString(1, locationField.getText());
                st.setInt(2, Integer.parseInt(sectionNoField.getText()));
                st.setString(3, statusField.getText());
                st.executeUpdate();
                showAlert("Success", "Kiosk added successfully");
                showKioskManagementScene();
            } catch (SQLException | NumberFormatException ex) {
                showAlert("Error", "Failed to add kiosk");
                ex.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showKioskManagementScene());

        addKioskLayout.getChildren().addAll(addButton, backButton);

        Scene addKioskScene = new Scene(addKioskLayout, 800, 600);
        primaryStage.setScene(addKioskScene);
    }

    private void showKioskBooksScene(int kioskID) {
        VBox booksLayout = new VBox(10);
        booksLayout.setPadding(new Insets(20));

        TableView<Book> bookTable = new TableView<>();
        bookTable.setMinHeight(400);
        bookTable.setStyle("-fx-font-size: 20px;");
        
        TableColumn<Book, Integer> idColumn = new TableColumn<>("Book ID");
        idColumn.setMinWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(300);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setMinWidth(200);
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        TableColumn<Book, Boolean> availabilityColumn = new TableColumn<>("Available");
        availabilityColumn.setMinWidth(100);
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        bookTable.getColumns().addAll(Arrays.asList(idColumn, titleColumn, authorColumn, availabilityColumn));

        try {
            PreparedStatement st = conn.prepareStatement(
                "SELECT b.* FROM book b " +
                "JOIN shelf s ON b.shelfNo = s.shelfNo " +
                "JOIN kiosk k ON s.sectionNo = k.sectionNo " +
                "WHERE k.kioskID = ?"
            );
            st.setInt(1, kioskID);
            ResultSet rs = st.executeQuery();
            ObservableList<Book> books = FXCollections.observableArrayList();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("bookID"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("availability"),
                    rs.getString("location")
                ));
            }
            bookTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load books");
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showKioskManagementScene());

        booksLayout.getChildren().addAll(bookTable, backButton);

        Scene booksScene = new Scene(booksLayout, 800, 700);
        primaryStage.setScene(booksScene);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void removeBook(int bookId) {
        try {
            String sql = "DELETE FROM book WHERE bookID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Book removed successfully!");
            } else {
                showAlert("Error", "Book not found!");
            }
        } catch (SQLException e) {
            showAlert("Error", "Error removing book: " + e.getMessage());
        }
    }

    public void removeKiosk(int kioskId) {
        try {
            String sql = "DELETE FROM kiosk WHERE kioskID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, kioskId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Kiosk removed successfully!");
            } else {
                showAlert("Error", "Kiosk not found!");
            }
        } catch (SQLException e) {
            showAlert("Error", "Error removing kiosk: " + e.getMessage());
        }
    }
}
