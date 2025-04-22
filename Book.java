import java.sql.*;
import java.time.LocalDate;

public class Book {
    private int bookID;
    private String title;
    private String author;
    private int publishedYear;
    private String condition;
    private int shelfNo;
    private boolean availability;
    private String location;

    // Constructor for GUI TableView
    public Book(int bookID, String title, String author, boolean availability, String location) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.availability = availability;
        this.location = location;
    }

    // Constructor for GUI TableView in show borrowed by a user
    public Book(int bookID, String title, String author) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
    }

    // Original constructor
    public Book(int bookID, String title, String author, int publishedYear, String condition, int shelfNo, String location) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.condition = condition;
        this.shelfNo = shelfNo;
        this.availability = true;
        this.location = location;
    }

    // Getters for TableView
    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
    
    public boolean getAvailability() {
        return availability;
    }

     // Original getters
    public int getPublishedYear() {
        return publishedYear;
    }

    public String getCondition() {
        return condition;
    }

    public int getShelfNo() {
        return shelfNo;
    }

    public String getLocation() {
        return location;
    }

    public boolean checkAvailability() {
        return availability;
    }

    //sql enabled
    public String getBorrowerID(Connection conn){
        
        try{
            PreparedStatement st = conn.prepareStatement(
                "SELECT borrowerID FROM book WHERE bookID = ?"
            );

            st.setInt(1, bookID);

            ResultSet rs = st.executeQuery();
            if(rs.next()){
                return rs.getString("borrowerID");
            }
        }
        catch(SQLException e){
            System.out.println("SQL error in getting borrower ID of a book");
            e.printStackTrace();
        }

        return null;
    }

    public void updateLocation(){

    }

    //sql enabled
    public void markAsReturned(Connection conn){
        //set availability as true in database
        //set returned date of latest transaction

        try{
            PreparedStatement st = conn.prepareStatement(
                "UPDATE book SET availability = true, borrowerID = NULL WHERE bookID = ?"
            );

            st.setInt(1, bookID);
            st.executeUpdate();
            availability = true;
        }
        catch(SQLException e){
            System.out.println("some error setting availability of book in the database.");
            e.printStackTrace();
            
        }

        //update transaction in database
        try{
            PreparedStatement st = conn.prepareStatement(
                "UPDATE transaction SET isReturned = true, returnDate = ? WHERE transactionID = (SELECT MAX(transactionID) FROM transaction WHERE bookID = ?); "
            );

            st.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            st.setInt(2, bookID);

            st.executeUpdate();

            
        }
        catch(SQLException e){
            System.out.println("some error updating transaction info in the database.");
            e.printStackTrace();
            
        }
    }

    //sql enabled
    public void markAsBorrowed(String borrowerID, Connection conn){
        
        //setting as not available
        try{
            PreparedStatement st = conn.prepareStatement(
                "UPDATE book SET availability = false, borrowerID = ? WHERE bookID = ?"
            );

            st.setString(1, borrowerID);
            st.setInt(2, bookID);
            st.executeUpdate();
            availability = false;

            
        }
        catch(SQLException e){
            System.out.println("some error setting availability of book in the database.");
            e.printStackTrace();
            
        }

        //upload data of transaction to database
        try{
            PreparedStatement st = conn.prepareStatement(
                "INSERT INTO transaction(studentID, borrowDate, returnDate, isReturned, bookID) VALUES (?,?,?,?,?);"
            );

            st.setString(1, borrowerID);
            st.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            st.setDate(3, null);
            st.setBoolean(4, false);
            st.setInt(5, bookID);

            st.executeUpdate();

            
        }
        catch(SQLException e){
            System.out.println("some error in making transaction for book borrow in the database.");
            e.printStackTrace();
            
        }

        
    }
}