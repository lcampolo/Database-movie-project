import org.sql2o.Sql2o;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class databaseSetup {

    static String url = "jdbc:mysql://localhost:3306/moviedb";
    private static ArrayList<String> genres = new ArrayList<String>();
    private static ArrayList<Entry> actors = new ArrayList<Entry>();
    private static ArrayList<Entry> directors = new ArrayList<Entry>();

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
                                   String critic_score, String audience_score) {

        if ((picture_url=="\\N")) {
            picture_url = "";
        }
        if (critic_score == "\\N") {
            critic_score = "";
        }
        if (audience_score == "\\N") {
            audience_score = "";
        }
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
        catch (Exception e) {
            return;
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
        catch (Exception e) {
            return;
        }
    }
    
    /**
     * Adds a genre to the genre table
     */
    public static void createGenre(String name) {
    	Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "INSERT INTO genre\nVALUES(:name);";

        try (org.sql2o.Connection conn = sql.open()) {
            conn.createQuery(insertSql)
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
        catch (Exception e) {
            System.out.println(actor_id + " " + movie_id);
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
        catch (Exception e) {
            System.out.println(director_id + " " + movie_id);
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
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.readLine();
            String currentMovie;
            while ((currentMovie=in.readLine())!=null) {

                String[] attr = currentMovie.split("\t");

                createMovie(attr[0], attr[1], attr[4], attr[5], attr[7], attr[17]);


            }
            in.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch(IOException exc) {
            System.out.println("IO exc");
        }
    }

    /**
     * Parse through movie_countries.dat and update movies with country.
     */
    public static void parseMovie_Country(String filepath) {
        File file = new File(filepath);
        Sql2o sql = new Sql2o(url, "root", "projectdb");
        String insertSql = "UPDATE movie set country = :country\n WHERE id=:movieid;";
        try (org.sql2o.Connection conn = sql.open()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                in.readLine();
                String currMovie;
                while ((currMovie=in.readLine())!=null) {
                    String[] movie = currMovie.split("\t");
                    if (movie.length == 2) {
                        conn.createQuery(insertSql)
                                .addParameter("country", movie[1])
                                .addParameter("movieid", movie[0])
                                .executeUpdate();
                    }
                    else {
                        System.out.println(movie.length + " " + movie[0]);
                    }

                }
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found.");
            }
            catch (IOException e) {
                return;
            }
        }
    }
    
    /**
     * Parse through movie_genres.dat and update genres and movie_genre relationship.
     * 
     * Requires all movies to be initialized. 
     */
    public static void parseGenre(String filepath) {
    	File file = new File(filepath);
    	
    	try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.readLine();
            String curr;
            while ((curr=in.readLine())!=null) {

    			String[] attribs = curr.split("\t");
    			String thisGenre = null;
    			
    			// Check that we have all required attributes
    			if (attribs.length != 2)
    				System.out.println("ERROR: parseGenre requires two incoming attributes, but there are " + attribs.length);
    			
    			// If this is a new genre, add it to the genre list and the genre table. Else, find the genre we wish to link to.
    			for (String genre : genres) {
    				if (genre.contains(attribs[1])) {
    					thisGenre = genre;
    					break;
    				}
    			}
    			if (thisGenre == null) {
    				String genre = attribs[1];
    				genres.add(genre);
    				//createGenre(genre);
    				thisGenre = genre;
    			}
    			
    			// Link this genre to the movie
    			createMovie_Genre(thisGenre, attribs[0]);
    		}
    		
    		in.close();
    	} catch (FileNotFoundException e) {
    		System.out.println("File not found.");
    	}
    	catch (IOException e) {
    	    System.out.println("IO Exception");
        }
    }

    /**
     * Parse through tags.dat and update tags.
     */
    public static void parseTag(String filepath) {
    	File file = new File(filepath);

    	try {
    		Scanner in = new Scanner(file);

            in.nextLine(); //skip first line
    		while(in.hasNextLine()) {
    			String curr = in.nextLine();
    			String[] attribs = curr.split("\t");

    			// Check that we have all required attributes
    			if (attribs.length != 2) {
                    System.out.println("ERROR: parseTag requires two incoming attributes, but there are " + attribs.length);
                    continue;
                }


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
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.readLine();
            String curr;
            while ((curr=in.readLine())!=null) {
    			String[] attribs = curr.split("\t");
    			Entry thisActor = null;
    			
    			// Check that we have all required attributes
    			if (attribs.length != 4)
    				System.out.println("ERROR: parseActor requires four incoming attributes, but there are " + attribs.length);
    			
    			// If this is a new actor, add him or her to the actor list and the actor table. Else, find the actor we wish to link to.
                if (attribs[1].equals("robbie_coltrane")) {
                    System.out.println("Here");
                }
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
    			if (attribs.length == 4) {
    			    createMovie_Actor(thisActor.getID(), attribs[0], attribs[3]);
                }
    		}
    		
    		in.close();
    	} catch (FileNotFoundException e) {
    		System.out.println("File not found.");
    	}
    	catch (IOException e) {
    	    System.out.println("IO Error");
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
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.readLine();
            String curr;
            while ((curr=in.readLine())!=null) {
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
    	catch (IOException e) {
    	    System.out.println("IO Error");
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
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.readLine();
            String curr;
            while ((curr=in.readLine())!=null) {
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
    	catch (IOException e) {
    	    System.out.println("IO Exception");
        }
    }
    
    public static void parseFiles() {
    	String movies = "src/main/movies.dat";
    	String tags = "src/main/tags.dat";
    	String movie_countries = "src/main/movie_countries.dat";
    	String movie_tags = "src/main/movie_tags.dat";
    	String movie_actors = "src/main/movie_actors.dat";
    	String movie_directors = "src/main/movie_directors.dat";
    	String movie_genres = "src/main/movie_genres.dat";
    	
    	//parseMovie(movies);
    	parseTag(tags);
    	//parseMovie_Country(movie_countries);
    	//parseGenre(movie_genres);
    	//parseActor(movie_actors);
    	//parseDirector(movie_directors);
    	//parseMovie_Tags(movie_tags);
    }
    
    public static void main(String[] args) {
        // Create tables for each entity and relationship
        String user = "CREATE TABLE IF NOT EXISTS user (\n"
                + " name VARCHAR(20), \n"
                + " password VARCHAR(15) NOT NULL, \n"
                + " joined_date DATE, \n"
                + " actor_rank int, \n"
                + " director_rank int, \n"
                + " country_rank int, \n"
                + " u_rank int, \n"
                + " c_rank int, \n"
                + " genre_rank int, \n"
                + " tag_rank int, \n"
                + " PRIMARY KEY (name) \n"
                + ");";
        createTable(user);

        String movie = "CREATE TABLE IF NOT EXISTS movie (\n"
                + " id INT, \n"
                + " title VARCHAR(255), \n"
                + " picture_url VARCHAR(255), \n"
                + " year VARCHAR(4), \n"
                + " country VARCHAR(255), \n"
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
                + " id VARCHAR(25), \n"
                + " name VARCHAR(255), \n"
                + " PRIMARY KEY (id) \n"
                + ");";
        createTable(actor);

        String movie_actor = "CREATE TABLE IF NOT EXISTS movie_actor (\n"
                + " actor_id VARCHAR(25), \n"
                + " movie_id INT, \n"
                + " actor_rank INT, \n"
                + " PRIMARY KEY(actor_id,movie_id), \n"
                + " foreign key(actor_id) references actor(id), \n"
                + " foreign key(movie_id) references movie(id) \n"
                + ");";
        createTable(movie_actor);
        
        String movie_director = "CREATE TABLE IF NOT EXISTS movie_director (\n"
                + " director_id VARCHAR(255), \n"
                + " movie_id INT, \n"
                + " PRIMARY KEY(director_id,movie_id), \n"
                + " foreign key(director_id) references director(id), \n"
                + " foreign key(movie_id) references movie(id) \n"
                + ");";
        createTable(movie_director);
        
        String movie_tag = "CREATE TABLE IF NOT EXISTS movie_tag (\n"
                + " tag VARCHAR(255), \n"
                + " movie_id INT, \n"
                + " weight INT, \n"
                + " PRIMARY KEY (tag, movie_id) \n"
                + ");";
        createTable(movie_tag);
        
        String movie_genre = "CREATE TABLE IF NOT EXISTS movie_genre (\n"
                + " genre_id VARCHAR(255), \n"
                + " movie_id INT, \n"
                + " PRIMARY KEY (genre_id, movie_id) \n"
                + ");";
        createTable(movie_genre);
        
        String comment = "CREATE TABLE IF NOT EXISTS comment (\n"
                + " id INT AUTO_INCREMENT, \n"
                + " text VARCHAR(255), \n"
                + " date DATE, \n"
                + " movie_id INT, \n"
                + " user_name VARCHAR(20), \n"
                + " PRIMARY KEY (id), \n"
                + " foreign key(user_name) references user(name), \n"
                + " foreign key(movie_id) references movie(id) \n"
                + ");";
        createTable(comment);
        
        String recommendation = "CREATE TABLE IF NOT EXISTS recommendation (\n"
                + " id BIGINT, \n"
                + " user_name VARCHAR(20), \n"
                + " movie_id BIGINT, \n"
                + " approved BIT, \n"
                + " PRIMARY KEY(id), \n"
                + " FOREIGN KEY(user_name) REFERENCES user(name) ON UPDATE CASCADE,\n"
                + " FOREIGN KEY(movie_id) REFERENCES movie(id) ON UPDATE CASCADE\n"
                + " );";
        createTable(recommendation);
        
        String userCriteria = "CREATE TABLE IF NOT EXISTS userCriteria (\n"
                + " user_name VARCHAR(20), \n"
                + " criteria_name VARCHAR(255),\n"
                + " criteria_rank INT, \n"
                + " PRIMARY KEY(user_name),\n"
                + " foreign key(user_name) references user(name)\n"
                + ");";
        createTable(userCriteria);
        String Likes = "CREATE TABLE IF NOT EXISTS likes (\n"
                + " uname VARCHAR(20), \n"
                + " movieId INT, \n"
                + " PRIMARY KEY (uname, movieId), \n"
                + " foreign key(uname) references user(name), \n"
                + " foreign key(movieId) references movie(id));";
        createTable(Likes);

        parseFiles();
    }
}