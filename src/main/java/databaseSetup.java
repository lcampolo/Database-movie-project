import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseSetup {

    static String urlRoot = "jdbc:sqlite:C:/Users/My/IdeaProjects/movieApplication/src/main/";
    public static void createNewDatabase(String filename) {
        String url = urlRoot + filename;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());

                System.out.println("A new database has been created.");
            }

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public static void createNewTable(String filepath) {
        String url = urlRoot + filepath;

        String sql = "CREATE TABLE IF NOT EXISTS user (\n"
                + " id integer PRIMARY KEY, \n"
                + " name text, \n"
                + " password text NOT NULL, \n"
                + " joined_date text\n"
                + ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createNewDatabase("moviedb.db");
        createNewTable("moviedb.db");
    }
}
