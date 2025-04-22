import java.time.*;

public class Transaction {
    
    private int transactionNo;
    private String studentID;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isReturned;
    private int bookID;

    //no constructor required as of now
    Transaction(int transactionNo, LocalDate borrowDate, LocalDate returnDate, boolean isReturned, int bookID){
        this.transactionNo = transactionNo;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
        this.bookID = bookID;
    }

    public void extendBorrow(){
        //extend the borrowing period
    }

    public void setReturnDate(){
        returnDate = LocalDate.now();
    }
    public int getTransactionNo(){return transactionNo;}
    public String studentID(){return studentID;}
    public LocalDate getBorrowDate(){return borrowDate;}
    public LocalDate getReturnDate(){return returnDate;}
    public Boolean getIsReturned(){return isReturned;}
    public int getBookID(){return bookID;}
}
