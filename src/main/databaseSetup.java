import org.sql2o.Sql2o;
import java.util.Scanner;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class databaseSetup {

    static String url = "jdbc:mysql://localhost:3306/moviedb";


    /**
     * Takes a SQL command to create a table as a parameter and executes it.
     */
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
    /**
     * Adds a movie to the movie table
     */
    public static void createMovie(String id, String title, String picture_url, String year,
                                   String critic_score, String audience_score ) {
        Sql2o sql2o = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO movie(id, title, picture_url, year, critic_score, audience_score)\n"
                + "VALUES (:id, ':title', ':picture', ':year', :criticScore, :audienceScore);";

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
    /**
     * Adds a director to the director table
     */
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
    /**
     * Adds an actor to the actor table
     */
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
    /**
     * Adds a genre to the genre table
     */
    public static void createGenre(String id, String name) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO genre\nVALUES(:id, :name);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("id", id)
                    .addParameter("name", name)
                    .executeUpdate();
        }
    }

    /**
     * Adds a tag to the tag table
     */
    public static void createTag(String id, String name) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO tag\nVALUES(:id, :name);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("id", id)
                    .addParameter("name", name)
                    .executeUpdate();
        }
    }

    /**
     * Adds a movie_actor relation to the movie_actor table
     */
    public static void createMovie_Actor(String actor_id, String movie_id, String rank) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO movie_actor\nVALUES(:actor_id, :movie_id, :rank);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("actor_id", actor_id)
                    .addParameter("movie_id", movie_id)
                    .addParameter("rank", rank)
                    .executeUpdate();
        }
    }

    /**
     * Adds a movie_director relation to the movie_director table
     */
    public static void createMovie_Director(String director_id, String movie_id) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO movie_director\nVALUES(:director_id, :movie_id);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("director_id", director_id)
                    .addParameter("movie_id", movie_id)
                    .executeUpdate();
        }
    }

    /**
     * Adds a movie_tag relation to the movie_tag table
     */
    public static void createMovie_Tag(String tag_id, String movie_id, String weight) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO movie_tag\nVALUES(:tag_id, :movie_id, :weight);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("tag_id", tag_id)
                    .addParameter("movie_id", movie_id)
                    .addParameter("weight", weight)
                    .executeUpdate();
        }
    }

    /**
     * Adds a movie_genre relation to the movie_genre table
     */
    public static void createMovie_Genre(String genre_id, String movie_id) {
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO movie_genre\nVALUES(:genre_id, :movie_id);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
                    .addParameter("genre_id", genre_id)
                    .addParameter("movie_id", movie_id)
                    .executeUpdate();
        }
    }

    /**
     * Parse through movies.dat and add movies to movie table.
     */
    public static void parseMovie(String filepath) {
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
    public static void parseMovieCountryFile(String filepath) {
        File file = new File(filepath);
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "UPDATE movie set country = :country\n WHERE id=:movieid;";
        try (org.sql2o.Connection conn = sql.open()) {
            try {
                Scanner in = new Scanner(file);
                while (in.hasNext()) {
                    String currMovie = in.nextLine();
                    String[] movie = currMovie.split("\t");
                    conn.createQuery(insertSql)
                            .addParameter("country", movie[1])
                            .addParameter("movieid", movie[0])
                            .executeUpdate();
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found.");
            }
        }
    }
    /*
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
    */
    /**
     * Parse through movie_genres.dat and update genres and movie_genre relationship.
     *
     * Requires all movies to be initialized.
     */
    public static void parseGenre(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);

            in.nextLine(); // Skip title header

            while(in.hasNextLine()) {
                String curr = in.nextLine();
                String[] attribs = curr.split("\t");
                Entry thisGenre = null;

                // Check that we have all required attributes
                if (attribs.length != 2)
                    System.out.println("ERROR: parseGenre requires two incoming attributes, but there are " + attribs.length);

                // If this is a new genre, add it to the genre list and the genre table. Else, find the genre we wish to link to.
                for (Entry genre : genres) {
                    if (genre.getName().equals(attribs[1])) {
                        thisGenre = genre;
                        break;
                    }
                }
                if (thisGenre == null) {
                    Entry genre = new Entry(Integer.toString(genres.size() + 1), attribs[1]);
                    genres.add(genre);
                    createGenre(genre.getID(), genre.getName());
                    thisGenre = genre;
                }

                // Link this genre to the movie
                createMovie_Genre(thisGenre.getID(), attribs[0]);
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Parse through tags.dat and update tags.
     */
    public static void parseTag(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);

            in.nextLine(); // Skip title header

            while(in.hasNextLine()) {
                String curr = in.nextLine();
                String[] attribs = curr.split("\t");

                // Check that we have all required attributes
                if (attribs.length != 2)
                    System.out.println("ERROR: parseTag requires two incoming attributes, but there are " + attribs.length);

                // Link this tag to the movie
                createTag(attribs[0], attribs[1]);
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Parse through movie_actors.dat and update actors and movie_actors relationship.
     *
     * Requires all movies to be initialized.
     */
    public static void parseActor(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);

            in.nextLine(); // Skip title header

            while(in.hasNextLine()) {
                String curr = in.nextLine();
                String[] attribs = curr.split("\t");
                Entry thisActor = null;

                // Check that we have all required attributes
                if (attribs.length != 4)
                    System.out.println("ERROR: parseActor requires four incoming attributes, but there are " + attribs.length);

                // If this is a new actor, add him or her to the actor list and the actor table. Else, find the actor we wish to link to.
                for (Entry actor : actors) {
                    if (actor.getID().equals(attribs[1])) {
                        thisActor = actor;
                        break;
                    }
                }
                if (thisActor == null) {
                    Entry actor = new Entry(attribs[1], attribs[2]);
                    actors.add(actor);
                    createActor(actor.getID(), actor.getName());
                    thisActor = actor;
                }

                // Link this actor to the movie
                createMovie_Actor(thisActor.getID(), attribs[0], attribs[3]);
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Parse through movie_directos.dat and update directors and movie_directors relationship.
     *
     * Requires all movies to be initialized.
     */
    public static void parseDirector(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);

            in.nextLine(); // Skip title header

            while(in.hasNextLine()) {
                String curr = in.nextLine();
                String[] attribs = curr.split("\t");
                Entry thisDirector = null;

                // Check that we have all required attributes
                if (attribs.length != 3)
                    System.out.println("ERROR: parseDirector requires three incoming attributes, but there are " + attribs.length);

                // If this is a new director, add him or her to the director list and the director table. Else, find the director we wish to link to.
                for (Entry director : directors) {
                    if (director.getID().equals(attribs[1])) {
                        thisDirector = director;
                        break;
                    }
                }
                if (thisDirector == null) {
                    Entry director = new Entry(attribs[1], attribs[2]);
                    directors.add(director);
                    createDirector(director.getID(), director.getName());
                    thisDirector = director;
                }

                // Link this director to the movie
                createMovie_Director(thisDirector.getID(), attribs[0]);
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Parse through movie_tags.dat and update movie_tags relationship.
     *
     * Requires all movies and tags to be initialized.
     */
    public static void parseMovie_Tags(String filepath) {
        File file = new File(filepath);

        try {
            Scanner in = new Scanner(file);

            in.nextLine(); // Skip title header

            while(in.hasNextLine()) {
                String curr = in.nextLine();
                String[] attribs = curr.split("\t");

                // Check that we have all required attributes
                if (attribs.length != 3)
                    System.out.println("ERROR: parseMovie_Tags requires three incoming attributes, but there are " + attribs.length);

                // Link this tag to the movie with given weight
                createMovie_Tag(attribs[1], attribs[0], attribs[2]);
            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    public static void parseFiles() {
        String movies = "./movies.dat";
        String tags = "./tags.dat";
        String movie_tags = "./movie_tags.dat";
        String movie_actors = "./movie_actors.dat";
        String movie_directors = "./movie_directors.dat";
        String movie_genres = "./movie_genres.dat";
        string movie_country = "./movie_country.dat"

        parseMovie(movies);
        parseTag(tags);
        parseGenre(movie_genres);
        parseActor(movie_actors);
        parseDirector(movie_directors);
        parseMovie_Tags(movie_tags);
        parseMovieCountryFile();
    }

    public static void main(String[] args) {
        //createNewDatabase();

        String user = "CREATE TABLE IF NOT EXISTS user (\n"
                + " name VARCHAR(20), \n"
                + " password VARCHAR(15) NOT NULL, \n"
                + " joined_date DATE, \n"
                + " PRIMARY KEY (name) \n"
                + ");";
        createTable(user);
        String movie = "CREATE TABLE IF NOT EXISTS movie (\n"
                + " id BIGINT, \n"
                + " title VARCHAR(255), \n"
                + " picture_url TEXT, \n"
                + " year VARCHAR(4), \n"
                + " country VARCHAR(25), \n"
                + " critic_score DOUBLE, \n"
                + " audience_score DOUBLE, \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(movie);
        String director = "CREATE TABLE IF NOT EXISTS director (\n"
                + " id VARCHAR(255), \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(director);
        String actor = "CREATE TABLE IF NOT EXISTS actor (\n"
                + " id VARCHAR(255), \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(actor);
        String recommendation = "CREATE TABLE IF NOT EXISTS recommendation (\n"
                + " id BIGING, \n"
                + " user_id BIGINT, \n"
                + " movie_id BIGINT, \n"
                + " approved BIT, \n"
                + " PRIMARY KEY(id), \n"
                + " FOREIGN KEY(user_id) REFERENCES user(id) ON UPDATE CASCADE\n"
                + " FOREIGN KEY (movie)id) REFERENCES movie(id) ON UPDATE CASCADE\n"
                + " );";
        createTable(recommendation);
        String genre = "CREATE TABLE IF NOT EXISTS genre (\n"
                + " id INT, \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(genre);

        String tag = "CREATE TABLE IF NOT EXISTS tag (\n"
                + " id INT, \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(tag);

        String movie_actor = "CREATE TABLE IF NOT EXISTS movie_actor (\n"
                + " actor_id INT, \n"
                + " movie_id INT, \n"
                + " rank INT, \n"
                + " PRIMARY KEY (actor_id, movie_id) \n"
                + ");";
        createTable(movie_actor);

        String movie_director = "CREATE TABLE IF NOT EXISTS movie_director (\n"
                + " director_id INT, \n"
                + " movie_id INT, \n"
                + " PRIMARY KEY (director_id, movie_id) \n"
                + ");";
        createTable(movie_director);

        String movie_tag = "CREATE TABLE IF NOT EXISTS movie_tag (\n"
                + " tag_id INT, \n"
                + " movie_id INT, \n"
                + " weight INT, \n"
                + " PRIMARY KEY (tag_id, movie_id) \n"
                + ");";
        createTable(movie_tag);

        String movie_genre = "CREATE TABLE IF NOT EXISTS movie_genre (\n"
                + " genre_id INT, \n"
                + " movie_id INT, \n"
                + " PRIMARY KEY (genre_id, movie_id) \n"
                + ");";
        createTable(movie_genre);

        String comment = "CREATE TABLE IF NOT EXISTS comment (\n"
                + " id INT, \n"
                + " text VARCHAR(255), \n"
                + " date DATE, \n"
                + " movie_id INT, \n"
                + " user_id INT, \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(comment);

        String userCriteria = "CREATE TABLE IF NOT EXISTS userCriteria (\n"
                + " user_id INT, \n"
                + " criteria_name VARCHAR(255), \n"
                + " rank INT, \n"
                + " PRIMARY KEY (user_id) \n"
                + ");";
        createTable(userCriteria);

        parseFiles();


    }
}
