import org.sql2o.Sql2o;
import java.util.Scanner;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class databaseSetup {

    static String url = "jdbc:mysql://localhost:3306/moviedb";
    public static void createNewDatabase() {


        try (Connection conn = DriverManager.getConnection(url, "root", "projectdb")) {
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

    public static void createTable(String sql) {

        try {
            Connection conn = DriverManager.getConnection(url, "root", "projectdb");
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void createMovie(String id, String title, String picture_url, String year,
                                   String critic_score, String audience_score ) {
        Sql2o sql2o = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO movie(id, title, picture_url, year, critic_score, audience_score)\n"
                + "VALUES (:id, :title, :picture, :year, :criticScore, :audienceScore);";

        try (org.sql2o.Connection conn = sql2o.open()) {
            conn.createQuery(insertSql)
                    .addParameter("id", id)
                    .addParameter("title", title)
                    .addParameter("picture", picture_url)
                    .addParameter("year", year)
                    .addParameter("criticScore", critic_score)
                    .addParameter("audienceScore", audience_score)
                    .executeUpdate();
        }

    }
    public static void createDirector(String id, String name) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO director\nVALUES(:id, :name);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("id", id)
                    .addParameter("name", name)
                    .executeUpdate();
        }
    }
    public static void createActor(String id, String name) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO actor\nVALUES(:id, :name);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("id", id)
                    .addParameter("name", name)
                    .executeUpdate();
        }
    }
    public static void parseMovieFile(String filepath) {

        File file = new File(filepath);
        try {
            Scanner in = new Scanner(file);

            while (in.hasNext()) {
                String currentMovie = in.nextLine();
                String[] attr = currentMovie.split("\t");
                createMovie(attr[0], attr[1], attr[4], attr[5], attr[7], attr[17]);
            }
            in.close();
        }




        catch(FileNotFoundException e) {
            System.out.println("File not found.");
        }

    }

    public static void parseDirectorFile(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                String currDirect = in.nextLine();
                String[] attr = currDirect.split("\t");
                createDirector(attr[1], attr[2]);
            }
            in.close();
        }

        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }
    public static void parseActorFile(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                String currDirect = in.nextLine();
                String[] attr = currDirect.split("\t");
                createActor(attr[1], attr[2]);
            }
            in.close();
        }

        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }
    public static void main(String[] args) {
        //createNewDatabase();

        String user = "CREATE TABLE IF NOT EXISTS user (\n"
                + " id INT, \n"
                + " name VARCHAR(20), \n"
                + " password VARCHAR(15) NOT NULL, \n"
                + " joined_date DATE, \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(user);
        String movie = "CREATE TABLE IF NOT EXISTS movie (\n"
                + " id INT, \n"
                + " title VARCHAR(255), \n"
                + " picture_url TEXT, \n"
                + " year VARCHAR(4), \n"
                + " country VARCHAR(25), \n"
                + " critic_score DOUBLE, \n"
                + " audience_score DOUBLE, \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(movie);
        String director = "CREATE TABLE IF NOT EXISTS director )\n"
                + " id VARCHAR(255), \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(director);
        String actor = "CREATE TABLE IF NOT EXISTS actor )\n"
                + " id INT, \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(actor);
    }
}
