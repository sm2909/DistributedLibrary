import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

class Student extends User {

    final String rollNo;
    private Stage primaryStage;
    private Connection conn;

    Student(String name, long contactNo, String password, String rollNo, Stage primaryStage, Connection conn){
        super(name, contactNo, password);
        this.rollNo = rollNo;
        this.primaryStage = primaryStage;
        this.conn = conn;
    }

    public void showStudentMenu() {
        VBox menuLayout = new VBox(20);
        menuLayout.setPadding(new Insets(40));
        menuLayout.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome, " + name);
        welcomeLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button viewBooksButton = new Button("View Books");
        viewBooksButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        viewBooksButton.setOnAction(e -> showViewBooksScene());

        Button donateBookButton = new Button("Donate Book");
        donateBookButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        donateBookButton.setOnAction(e -> showDonateBookScene());

        Button borrowBookButton = new Button("Borrow Book");
        borrowBookButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        borrowBookButton.setOnAction(e -> showBorrowBookScene());

        Button returnBookButton = new Button("Return Book");
        returnBookButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        returnBookButton.setOnAction(e -> showReturnBookScene());
        
        Button showBorrowedButton = new Button("Show Borrowed Books");
        showBorrowedButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        showBorrowedButton.setOnAction(e -> showBorrowedBooksScene());

        Button showTransactionHistoryButton = new Button("Show Transaction History");
        showTransactionHistoryButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        showTransactionHistoryButton.setOnAction(e -> showTransactionHistoryScene());

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 250px; -fx-min-height: 40px;");
        logoutButton.setOnAction(e -> Main.showLoginScene());

        menuLayout.getChildren().addAll(welcomeLabel, viewBooksButton, donateBookButton, 
            borrowBookButton, returnBookButton, showBorrowedButton, showTransactionHistoryButton, logoutButton);
        Scene menuScene = new Scene(menuLayout, 800, 700);
        primaryStage.setScene(menuScene);
    }

    public void showViewBooksScene() {
        VBox viewBooksLayout = new VBox(10);
        viewBooksLayout.setPadding(new Insets(20));

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

        TableColumn<Book, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setMinWidth(300);
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        bookTable.getColumns().addAll(Arrays.asList(idColumn, titleColumn, authorColumn, availabilityColumn, locationColumn));
        ObservableList<Book> books = FXCollections.observableArrayList();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM book");
            ResultSet rs = st.executeQuery();
           
            
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

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> showStudentMenu());
//searching by filters

        //search by title
        HBox titleFilterLayout = new HBox(10);
        titleFilterLayout.setAlignment(Pos.CENTER);

        TextField searchByTitleField = new TextField();
        searchByTitleField.setPromptText("Search By Title");
        searchByTitleField.setMaxWidth(200);

        Button searchByTitleButton = new Button("search");
        searchByTitleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        searchByTitleButton.setOnAction(e -> {
            String title = searchByTitleField.getText();
            for(int i=books.size()-1; i>=0; --i){
                if(!books.get(i).getTitle().contains(title)){
                    books.remove(i);
                }
            }
        });

        titleFilterLayout.getChildren().addAll(searchByTitleField, searchByTitleButton);


        //search by Book ID
        HBox bookIDFilterLayout = new HBox(10);
        bookIDFilterLayout.setAlignment(Pos.CENTER);

        TextField searchByBookIDField = new TextField();
        searchByBookIDField.setPromptText("Search By Book ID");
        searchByBookIDField.setMaxWidth(200);

        Button searchByBookIDButton = new Button("search");
        searchByBookIDButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        searchByBookIDButton.setOnAction(e -> {
            int bookID = Integer.parseInt(searchByBookIDField.getText());
            for(int i=books.size()-1; i>=0; --i){
                if(!(books.get(i).getBookID() == bookID)){
                    books.remove(i);
                }
            }
        });

        bookIDFilterLayout.getChildren().addAll(searchByBookIDField, searchByBookIDButton);

        //checkbox for showing only available
        CheckBox showAvailableCheckBox = new CheckBox("Only Show Available");
        showAvailableCheckBox.setSelected(false);
        showAvailableCheckBox.setOnAction(e -> {
            if(showAvailableCheckBox.isSelected()){
                for(int i=books.size()-1; i>=0; --i){
                    if(!books.get(i).getAvailability()){
                        books.remove(i);
                    }
                }
            }
            else{
                showViewBooksScene();
            }
        });

        //reset filters button
        Button resetFiltersButton = new Button("Reset Filters");
        resetFiltersButton.setStyle("-fx-background-color:rgb(220, 47, 47); -fx-text-fill: white;");
        resetFiltersButton.setOnAction(e -> showViewBooksScene());

        viewBooksLayout.getChildren().addAll(titleFilterLayout, bookIDFilterLayout, showAvailableCheckBox, resetFiltersButton, bookTable, backButton);
        Scene viewBooksScene = new Scene(viewBooksLayout, 1050, 700);
        primaryStage.setScene(viewBooksScene);
    }

    public void showDonateBookScene() {
        VBox donateLayout = new VBox(10);
        donateLayout.setPadding(new Insets(20));
        donateLayout.setAlignment(Pos.CENTER);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        titleField.setMaxWidth(300);
        titleField.setStyle("-fx-font-size: 20px;");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        authorField.setMaxWidth(300);
        authorField.setStyle("-fx-font-size: 20px;");

        TextField disciplineField = new TextField();
        disciplineField.setPromptText("Discipline");
        disciplineField.setMaxWidth(300);
        disciplineField.setStyle("-fx-font-size: 20px;");

        TextField subDisciplineField = new TextField();
        subDisciplineField.setPromptText("Sub Discipline");
        subDisciplineField.setMaxWidth(300);
        subDisciplineField.setStyle("-fx-font-size: 20px;");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        subjectField.setMaxWidth(300);
        subjectField.setStyle("-fx-font-size: 20px;");

        TextField yearField = new TextField();
        yearField.setPromptText("Year of Publishing");
        yearField.setMaxWidth(300);
        yearField.setStyle("-fx-font-size: 20px;");

        TextField conditionField = new TextField();
        conditionField.setPromptText("Condition");
        conditionField.setMaxWidth(300);
        conditionField.setStyle("-fx-font-size: 20px;");

        Button donateButton = new Button("Donate Book");
        donateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        donateButton.setOnAction(e -> handleDonateBook(
            titleField.getText(),
            authorField.getText(),
            disciplineField.getText(),
            subDisciplineField.getText(),
            subjectField.getText(),
            yearField.getText(),
            conditionField.getText()
        ));

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showStudentMenu());

        donateLayout.getChildren().addAll(titleField, authorField, disciplineField, 
            subDisciplineField, subjectField, yearField, conditionField, donateButton, backButton);
        Scene donateScene = new Scene(donateLayout, 800, 700);
        primaryStage.setScene(donateScene);
    }

    public void showBorrowBookScene() {
        VBox borrowLayout = new VBox(10);
        borrowLayout.setPadding(new Insets(20));
        borrowLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Borrow Book");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");
        bookIdField.setMaxWidth(300);
        bookIdField.setMinHeight(40);
        bookIdField.setStyle("-fx-font-size: 20px;");

        // Add listener to show book details
        Label bookDetailsLabel = new Label();
        bookDetailsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
        bookIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int bookId = Integer.parseInt(newValue);
                    PreparedStatement st = conn.prepareStatement("SELECT title, author, availability FROM book WHERE bookID = ?");
                    st.setInt(1, bookId);
                    ResultSet rs = st.executeQuery();
                    
                    if (rs.next()) {
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        boolean available = rs.getBoolean("availability");
                        String status = available ? "Available" : "Not Available";
                        bookDetailsLabel.setText(String.format("Title: %s\nAuthor: %s\nStatus: %s", 
                            title, author, status));
                    } else {
                        bookDetailsLabel.setText("Book not found");
                    }
                } catch (NumberFormatException e) {
                    bookDetailsLabel.setText("Please enter a valid book ID");
                } catch (SQLException e) {
                    bookDetailsLabel.setText("Error fetching book details");
                    e.printStackTrace();
                }
            } else {
                bookDetailsLabel.setText("");
            }
        });

        Button borrowButton = new Button("Borrow Book");
        borrowButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        borrowButton.setOnAction(e -> handleBorrowBook(bookIdField.getText()));

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showStudentMenu());

        borrowLayout.getChildren().addAll(titleLabel, bookIdField, bookDetailsLabel, borrowButton, backButton);
        Scene borrowScene = new Scene(borrowLayout, 800, 600);
        primaryStage.setScene(borrowScene);
    }

    public void showReturnBookScene() {
        VBox returnLayout = new VBox(10);
        returnLayout.setPadding(new Insets(20));
        returnLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Return Book");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");
        bookIdField.setMaxWidth(300);
        bookIdField.setMinHeight(40);
        bookIdField.setStyle("-fx-font-size: 20px;");

        Button returnButton = new Button("Return Book");
        returnButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        returnButton.setOnAction(e -> handleReturnBook(bookIdField.getText()));

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showStudentMenu());

        returnLayout.getChildren().addAll(titleLabel, bookIdField, returnButton, backButton);
        Scene returnScene = new Scene(returnLayout, 800, 600);
        primaryStage.setScene(returnScene);
    }


    public void handleDonateBook(String title, String author, String discipline, 
            String subDiscipline, String subject, String year, String condition) {
        try {
            // First get shelf number and location information
            PreparedStatement st = conn.prepareStatement(
               "SELECT s.shelfNo, sec.sectionNo, b.branchNo " +
                "FROM shelf s " +
                "JOIN section sec ON s.sectionNo = sec.sectionNo " +
                "JOIN branch b ON sec.branchNo = b.branchNo " +
                "WHERE b.disciplineName = ? AND sec.subDisciplineName = ? AND s.subjectName = ?"
            );
            st.setString(1, discipline);
            st.setString(2, subDiscipline);
            st.setString(3, subject);
            ResultSet rs = st.executeQuery();
            
            int shelfNo = 0;
            String location = null;
            
            if (rs.next()) {
                shelfNo = rs.getInt("shelfNo");
                // Generate location string using numeric IDs
                location = String.format("Shelf %d, Section %d, Branch %d", 
                    shelfNo,
                    rs.getInt("sectionNo"),
                    rs.getInt("branchNo"));
            }

            if (shelfNo == 0) {
                showAlert("Error", "Could not find appropriate shelf");
                return;
            }

            // Insert book with location
            st = conn.prepareStatement(
                "INSERT INTO book(title, author, shelfNo, publishedYear, `condition`, availability, location) " +
                "VALUES (?, ?, ?, ?, ?, true, ?)"
            );
            st.setString(1, title);
            st.setString(2, author);
            st.setInt(3, shelfNo);
            st.setInt(4, Integer.parseInt(year));
            st.setString(5, condition);
            st.setString(6, location);
            st.executeUpdate();

            showAlert("Success", "Book donated successfully!");
            showStudentMenu();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to donate book");
        }
    }

    public void handleBorrowBook(String bookId) {
        if (!Main.isLibraryOpen(conn)) {
            showAlert("Library Closed", "The library is currently closed. Please visit during opening hours.");
            return;
        }

        try {
            int id = Integer.parseInt(bookId);
            PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM book WHERE bookID = ? AND availability = true"
            );
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                showAlert("Error", "Book not available");
                return;
            }

            // Update book availability and borrower
            st = conn.prepareStatement(
                "UPDATE book SET availability = false, borrowerID = ? WHERE bookID = ?"
            );
            st.setString(1, rollNo);
            st.setInt(2, id);
            st.executeUpdate();

            // Insert new transaction record
            st = conn.prepareStatement(
                "INSERT INTO transaction (bookID, borrowerID, borrowDate, returnDate, isReturned) " +
                "VALUES (?, ?, CURDATE(), NULL, false)"
            );
            st.setInt(1, id);
            st.setString(2, rollNo);
            st.executeUpdate();

            showAlert("Success", "Book borrowed successfully!");
            showStudentMenu();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to borrow book: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid book ID number");
        }
    }

    public void handleReturnBook(String bookId) {
        if (!Main.isLibraryOpen(conn)) {
            showAlert("Library Closed", "The library is currently closed. Please visit during opening hours.");
            return;
        }

        try {
            int id = Integer.parseInt(bookId);
            PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM book WHERE bookID = ? AND borrowerID = ?"
            );
            st.setInt(1, id);
            st.setString(2, rollNo);
            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                showAlert("Error", "Book not found or not borrowed by you");
                return;
            }

            // Update book availability
            st = conn.prepareStatement(
                "UPDATE book SET availability = true, borrowerID = NULL WHERE bookID = ?"
            );
            st.setInt(1, id);
            st.executeUpdate();

            // Update transaction record
            st = conn.prepareStatement(
                "UPDATE transaction SET returnDate = CURDATE(), isReturned = true " +
                "WHERE bookID = ? AND borrowerID = ? AND isReturned = false"
            );
            st.setInt(1, id);
            st.setString(2, rollNo);
            st.executeUpdate();

            showAlert("Success", "Book returned successfully!");
            showStudentMenu();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return book: " + e.getMessage());
        }
    }

    public void showBorrowedBooksScene(){
        VBox viewBooksLayout = new VBox(10);
        viewBooksLayout.setPadding(new Insets(20));
        viewBooksLayout.setAlignment(Pos.CENTER);


        TableView<Book> bookTable = new TableView<>();
        
        TableColumn<Book, Integer> idColumn = new TableColumn<>("Book ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        bookTable.getColumns().addAll(Arrays.asList(idColumn, titleColumn, authorColumn));

        ObservableList<Book> books = FXCollections.observableArrayList();
        try {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM book WHERE borrowerID = ?");
            st.setString(1, rollNo);
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("bookID"),
                    rs.getString("title"),
                    rs.getString("author")
                ));
            }
            bookTable.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load books");
        }

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> showStudentMenu());

        viewBooksLayout.getChildren().addAll(bookTable, backButton);
        Scene borrowedBooksScene = new Scene(viewBooksLayout, 800, 700);
        primaryStage.setScene(borrowedBooksScene);
        primaryStage.show();

    }

    public void showTransactionHistoryScene() {
        VBox viewBooksLayout = new VBox(20);
        viewBooksLayout.setPadding(new Insets(40));
        viewBooksLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Transaction History");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        TableView<Transaction> transactionTable = new TableView<>();
        transactionTable.setMinHeight(400);
        
        TableColumn<Transaction, Integer> transactionNoColumn = new TableColumn<>("Transaction No");
        transactionNoColumn.setMinWidth(120);
        transactionNoColumn.setCellValueFactory(new PropertyValueFactory<>("transactionNo"));
        
        TableColumn<Transaction, Integer> bookIDColumn = new TableColumn<>("Book ID");
        bookIDColumn.setMinWidth(100);
        bookIDColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));

        TableColumn<Transaction, LocalDate> borrowDateColumn = new TableColumn<>("Borrow Date");
        borrowDateColumn.setMinWidth(150);
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        TableColumn<Transaction, LocalDate> returnDateColumn = new TableColumn<>("Return Date");
        returnDateColumn.setMinWidth(150);
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        
        TableColumn<Transaction, Boolean> isReturnedColumn = new TableColumn<>("Returned");
        isReturnedColumn.setMinWidth(100);
        isReturnedColumn.setCellValueFactory(new PropertyValueFactory<>("isReturned"));

        transactionTable.getColumns().addAll(Arrays.asList(transactionNoColumn, bookIDColumn, borrowDateColumn, returnDateColumn, isReturnedColumn));

        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        try {
            PreparedStatement st = conn.prepareStatement(
                "SELECT transactionNo, bookID, borrowDate, returnDate, isReturned FROM transaction WHERE borrowerID = ? ORDER BY borrowDate DESC"
            );
            st.setString(1, rollNo);
            ResultSet rs = st.executeQuery();
            
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("transactionNo"),
                    rs.getDate("borrowDate").toLocalDate(),
                    rs.getDate("returnDate") != null ? rs.getDate("returnDate").toLocalDate() : null,
                    rs.getBoolean("isReturned"),
                    rs.getInt("bookID")
                ));
            }
            transactionTable.setItems(transactions);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load transaction history: " + e.getMessage());
        }

        Button backButton = new Button("Back to Menu");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 150px; -fx-min-height: 40px;");
        backButton.setOnAction(e -> showStudentMenu());

        viewBooksLayout.getChildren().addAll(titleLabel, transactionTable, backButton);
        Scene transactionScene = new Scene(viewBooksLayout, 800, 700);
        primaryStage.setScene(transactionScene);
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }   
}