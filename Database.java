import java.sql.*;

public class Database {
    public static String getSubjectFromShelfNo(int shelfNo, Connection conn){
        
        //getting subject from shelfNo
        try{
            PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM shelf WHERE shelfNo = ?;"
            );

            st.setInt(1, shelfNo);
            ResultSet rs = st.executeQuery();
            
            if(rs.next()){
                return rs.getString("subjectName");
            }
        }
        catch(SQLException e){
            System.out.println("Error in getting subject name from given shelf number");
            e.printStackTrace();
        }

        return null;
    }

    //sql enabled
    public static Book getBookByBookID(int bookID, Connection conn){
        try{
           PreparedStatement st = conn.prepareStatement(
               "SELECT * FROM book WHERE bookID = ?;"
           );

           st.setInt(1, bookID);
           ResultSet rs = st.executeQuery();

           if(!rs.next()){
               //nothing found in database
               return null;
           }

           Book currentBook = new Book(rs.getInt("bookID"), rs.getString("title"), rs.getString("author"), rs.getInt("publishedYear"), rs.getString("condition"), rs.getInt("shelfNo"), rs.getString("location"));

           return currentBook;


       }
       catch(Exception e){
           System.out.println("Some error occurred in getting data from sql");
           e.printStackTrace();
       }

       return null;
   }
}
