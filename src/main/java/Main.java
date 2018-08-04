import static spark.Spark.*;
import java.text.SimpleDateFormat;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import java.sql.*;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;
import java.util.*;
import spark.ModelAndView;

public class Main {

    private static final int PORT = 8000;
    private static String url = "jdbc:mysql://localhost:3306/moviedb";
    private static String dbuser = "root";
    private static String password = "projectdb";

    public static void main(String[] args) {

        port(PORT);

        /*
        All the routes for get and post requests
         */
        get("/", (request, response) -> getLogin(request));
        post("/", (request, response) -> loginPost(request, response));
        get("/register", (request, response) -> getRegistrationForm());
        post("/register", (request, response) -> registerUser(request, response));
        get("/movies/:username", (request, response) -> movieHomeGet(request, response));
        post("/movies/:username", (request, response) -> userHomePost(request, response));
        get("/movies/:username/recommendations", (request, response) -> userRecommendationGet(request, response));
        post("/movies/:username/recommendations", (request, response) -> userRecommendationPost(request, response));
        get("/movies/:username/favorites", (request, response) -> userFavoriteGet(request, response));
        post("/movies/:username/favorites", (request, response) -> userFavoritePost(request, response));
        get("/movies/comments/:movieId", (request, response) -> movieCommentGet(request, response));
        post("/movies/comments/:movieId", (request, response) -> movieCommentPost(request, response));
        get("/movies/:username/attrrank", (request, response) -> userAttributeRankingGet(request, response));
        post("/movies/:username/attrrank", (request, response) -> userAttributeRankingPost(request, response));
    }

    /*
    Get a user's list of favorites, displaying the title and picture.
     */
    private static Object userFavoriteGet(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        String uname = request.session().attribute("currentUser");
        if (uname == "") {
            response.redirect("/");
            render(model, "login.html");
        }
        String query = "select m.id, m.title, m.picture_url from movie m, likes l where l.movieId = m.id and l.uname = \'" + uname + "\'";
        try {
            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ArrayList<Movie> movies = new ArrayList<>();
            while (rs.next()) {
                Movie currMovie = new Movie(rs.getInt("id"), rs.getString("m.title"), rs.getString("m.picture_url"));
                movies.add(currMovie);
            }

            model.put("movies", movies);
            model.put("user", uname);
            return render(model, "userfavorites.html");


        } catch (SQLException e) {
            return "";
        }


    }

    private static Object userFavoritePost(Request request, Response response) {
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");
        Set<String> keys = params.keySet();
        String logged_in_user = request.session().attribute("currentUser");
        String movieToRemove = "";
        for (String key : keys) {
            movieToRemove = key;
        }

        String query = "delete from likes where uname = :username and movieId = :movieId";

        Sql2o sql2o = new Sql2o(url, dbuser, password);
        try (Connection conn = sql2o.open()) {
            conn.createQuery(query)
                    .addParameter("username", logged_in_user)
                    .addParameter("movieId", movieToRemove)
                    .executeUpdate();

            return userFavoriteGet(request, response);
        }
    }

    /*
    The tag performs the post, which will save the input comment with a post request to the comments table
     */
    private static Object movieCommentPost(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        String movie_id = request.uri().split("/")[3];
        Sql2o sql2o = new Sql2o(url, dbuser, password);

        UrlEncoded.decodeTo(request.body(), params, "UTF-8");
        String text = params.get("comment").get(0);
        java.util.Date today = new java.util.Date();
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
        String uname = request.session().attribute("currentUser");
        if (uname.equals("")) {
            response.redirect("/");
            model.put("message", "Must be logged in to leave comments.");
            render(model, "login.html");
        }
        String commentSql = "insert into comment (text, date, movie_id, user_name) values (:text, :date, :movie_id, :user_name)";
        try (Connection conn = sql2o.open()) {
            conn.createQuery(commentSql, true)
                    .addParameter("text", text)
                    .addParameter("date", objSDF.format(today))
                    .addParameter("movie_id", movie_id)
                    .addParameter("user_name", uname)
                    .executeUpdate()
                    .getKey();

            return movieCommentGet(request, response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /*
    Performs the get request for getting and displaying the comments for a specific movie, as well
    as information about that movie.
     */
    private static Object movieCommentGet(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        String uname = request.session().attribute("currentUser");
        if (uname.equals("")) {
            response.redirect("/");
            model.put("message", "Must be logged in to leave comments.");
            render(model, "login.html");
        }
        String movie_id = request.uri().split("/")[3];
        try {
            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            String query = "select m.id as movie_id, title, picture_url, year, country, critic_score, audience_score, text, date, name, joined_date\n"
                    + "from movie m, comment c, user u\n"
                    + "where m.id = c.movie_id and c.user_name = u.name and m.id = " + movie_id;
            ResultSet rs = stmt.executeQuery(query);
            Movie thisMovie = new Movie();
            ArrayList<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                if (thisMovie.getId() == 0) {
                    thisMovie = new Movie(rs.getInt("movie_id"), rs.getString("title"), rs.getString("picture_url"), rs.getString("year")
                            , rs.getString("country"), rs.getDouble("critic_score"), rs.getDouble("audience_score"));
                    model.put("movie", thisMovie);
                }
                Comment thisComment = new Comment(rs.getString("text"), rs.getDate("date"), rs.getString("name"), rs.getDate("joined_date"));
                comments.add(thisComment);
            }
            if (thisMovie.getId() == 0) {
                Sql2o sql2o = new Sql2o(url, dbuser, password);
                String movieSqlSearch = "select * from movie where id = :movieId";
                try (Connection con = sql2o.open()) {
                    List<Movie> movies = con.createQuery(movieSqlSearch)
                            .addParameter("movieId", movie_id)
                            .executeAndFetch(Movie.class);

                    model.put("movie", movies.get(0));
                }
            }
            model.put("comments", comments);
            model.put("user", uname);
            return render(model, "comments.html");
        } catch (SQLException e) {
            return "";
        }


    }

    /*
    Tag for get request to change logged in user preferences
     */
    private static Object userAttributeRankingGet(Request request, Response response) {
        String logged_in_user = request.session().attribute("currentUser");
        HashMap<String, Object> model = new HashMap<>();
        if (logged_in_user == "" || logged_in_user == null) {
            response.redirect("/");
            return render(model, "login.html");
        }
        User user = getUserByName(logged_in_user).get(0);
        model.put("user", user);

        return render(model, "attributes.html");
    }

    /*
    Tag for saving the modified attribute rankings in a post request
     */
    private static Object userAttributeRankingPost(Request request, Response response) {
        String logged_in_user = request.session().attribute("currentUser");
        HashMap<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");
        String actorRank = params.get("actor").get(0);
        String directorRank = params.get("director").get(0);
        String countryRank = params.get("country").get(0);
        String u_rank = params.get("u_rank").get(0);
        String c_rank = params.get("c_rank").get(0);
        String genreRank = params.get("genre").get(0);
        String tagRank = params.get("tag").get(0);
        String rankUpdate = "update user set actor_rank = :actor, director_rank = :director, country_rank = :country, u_rank = :u_rank, c_rank = :c_rank, genre_rank = :genre, tag_rank = :tag where name = :userName";
        Sql2o sql2o = new Sql2o(url, dbuser, password);
        try (Connection conn = sql2o.open()) {
            conn.createQuery(rankUpdate)
                    .addParameter("actor", actorRank)
                    .addParameter("director", directorRank)
                    .addParameter("country", countryRank)
                    .addParameter("u_rank", u_rank)
                    .addParameter("c_rank", c_rank)
                    .addParameter("genre", genreRank)
                    .addParameter("tag", tagRank)
                    .addParameter("userName", logged_in_user)
                    .executeUpdate();

            model.put("movie_message", "Recommendation preferences updated");
            response.redirect("/movies/" + logged_in_user);
            return render(model, "userhome.html");
        }
    }

    /*
    get for homepage of a user
     */
    private static Object movieHomeGet(Request request, Response response) {
        String logged_in_user = request.session().attribute("currentUser");
        HashMap<String, Object> model = new HashMap<>();
        if (logged_in_user == "" || logged_in_user == null) {
            response.redirect("/");
            return render(model, "login.html");
        }

        model.put("user", logged_in_user);
        return render(model, "userhome.html");
    }

    /*
    Get request for registration form
     */
    private static String getRegistrationForm() {
        Map<String, Object> model = new HashMap<>();
        return render(model, "newuser.html");
    }

    /*
    Get for login page
     */
    private static String getLogin(Request request) {
        Map<String, Object> model = new HashMap<>();
        if (request.queryParams("r") != null) {
            model.put("message", "You were successfully registered and can login now.");
        }
        return render(model, "login.html");
    }

    /*
    Post request for home page
     */
    private static String userHomePost(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        model.put("user", request.session().attribute("currentUser"));
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");

        try {
            if (params.get("movieSearch") != null) {
                String movieSearch = params.get("movieSearch").get(0);
                Sql2o sql2o = new Sql2o(url, dbuser, password);
                String movieSqlSearch = "select * from movie where title like :search limit 20";
                try (Connection conn = sql2o.open()) {
                    List<Movie> movies = conn.createQuery(movieSqlSearch)
                            .addParameter("search", "%" + movieSearch + "%")
                            .executeAndFetch(Movie.class);
                    if (movies.isEmpty()) {
                        model.put("error", "No Movies found with that search. Please try a different search.");
                        return render(model, "userhome.html");
                    } else {
                        model.put("movies", movies);
                        //get actors, directors, movies, and tags for the returned movies
                        for (Movie movie : movies) {
                            movie.topFiveActors = getTop5Actors(movie.getId());
                            movie.directors = getDirectors(movie.getId());
                            movie.genres = getGenresByMovie(movie.getId());
                            movie.tags = getTagsByMovie(movie.getId());
                        }
                        return render(model, "userhome.html");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return "";
                }
            }

            if (params.get("logout") != null) {
                request.session().attribute("currentUser", "");
                response.redirect("/");
                return render(model, "login.html");
            } else {
                Set<String> keys = params.keySet();
                String movieToAdd = "";
                for (String key : keys) {
                    movieToAdd = key;
                }

                Sql2o sql2o = new Sql2o(url, dbuser, password);
                String addToFave = "insert into likes values (:username, :movieId)";

                try (Connection conn = sql2o.open()) {
                    String loggedInUser = request.session().attribute("currentUser");
                    conn.createQuery(addToFave)
                            .addParameter("username", loggedInUser)
                            .addParameter("movieId", movieToAdd)
                            .executeUpdate();
                    model.put("movie_message", "Movie successfully added, add some more!");
                    return render(model, "userhome.html");
                } catch (Exception e) {
                    model.put("movie_message", "That movie is already in your favorites, try another one.");
                    return render(model, "userhome.html");
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }

    }

    /*
    Post request, will save the input username and password to the database
     */
    private static String registerUser(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");
        String username = params.get("uname").get(0);
        Sql2o sql2o = new Sql2o(url, dbuser, password);
        List<User> userExist = getUserByName(username);

        if (!userExist.isEmpty()) {
            String error = "Username " + username + " is already in use. Please choose a different username.";
            model.put("error", error);
            return render(model, "newuser.html");
        } else {
            java.util.Date today = new java.util.Date();
            SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
            User newUser = new User(params.get("uname").get(0), params.get("psw").get(0), today, 1, 1, 1, 1, 1, 1, 1);
            String addUser = "call createUser(:userName,:password,:date)";
            try (Connection conn = sql2o.open()) {
                conn.createQuery(addUser)
                        .addParameter("userName", newUser.getName())
                        .addParameter("password", newUser.password)
                        .addParameter("date", objSDF.format(newUser.getJoinedDate()))
                        .executeUpdate();

                response.redirect("/?r=1");
                return render(model, "login.html");

            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }

    /*
    Post for log in, will log user in or re-prompt if credentials are invalid
     */
    private static String loginPost(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");
        String loginQuery = "select * from user where name = :userName and password = :psw";
        Sql2o sql2o = new Sql2o(url, dbuser, password);
        String uname = params.get("uname").get(0);
        try (Connection conn = sql2o.open()) {
            List<User> userExist = conn.createQuery(loginQuery)
                    .addParameter("userName", uname)
                    .addParameter("psw", params.get("psw").get(0))
                    .executeAndFetch(User.class);

            if (userExist.isEmpty()) {
                model.put("message", "Username and password not found. Please enter a valid username and password or create a new account.");
                return render(model, "login.html");
            } else {
                request.session().attribute("currentUser", uname);
                String path = "/movies/" + uname;
                model.put("user", uname);
                response.redirect(path);
                return render(model, "userhome.html");
            }
        }
    }

    /*
    This method gets 30 movies that are recommended for a user. It serves as the wrapper for the get request, and
    executes the main movie recommendation algorithm.
     */
    private static String userRecommendationGet(Request request, Response response) {
        HashMap<String, Object> model = new HashMap<>();
        String uname = request.session().attribute("currentUser");
        if (uname == "") {
            response.redirect("/");
            render(model, "login.html");
        }
        try {

            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            String query = "call getRecommendationsByLikedCriteria(\'" + uname + "\')";
            ResultSet rs = stmt.executeQuery(query);
            ArrayList<Movie> movies = new ArrayList<>();

            while (rs.next()) {
                //initialize a movie for each tuples in the response set
                Movie currMovie = new Movie(rs.getInt("id"), rs.getString("title"), rs.getString("picture"), rs.getString("year"), rs.getString("country"),
                        rs.getDouble("critic_score"), rs.getDouble("audience_score"));

                //get actors, directors, genres, and tags for movie
                currMovie.topFiveActors = getTop5Actors(currMovie.getId());
                currMovie.directors = getDirectors(currMovie.getId());
                currMovie.genres = getGenresByMovie(currMovie.getId());
                currMovie.tags = getTagsByMovie(currMovie.getId());
                movies.add(currMovie);
            }
            model.put("movies", movies);
            model.put("user", uname);
            return render(model, "recommendations.html");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    /*
    This tag is the post request for the recommendations page, and allows a user to add the movie to their list of
    unapproved recommendations.
     */
    private static String userRecommendationPost(Request request, Response response) {
        String logged_in_user = request.session().attribute("currentUser");
        HashMap<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");

        //get movie from the parameters
        Set<String> keys = params.keySet();
        String movieToAdd = "";
        for (String key : keys) {
            movieToAdd = key;
        }

        Sql2o sql2o = new Sql2o(url, dbuser, password);
        String addToFave = "call createDisliked(:username, :movieId)";

        try (Connection conn = sql2o.open()) {
            String loggedInUser = request.session().attribute("currentUser");
            conn.createQuery(addToFave)
                    .addParameter("username", loggedInUser)
                    .addParameter("movieId", movieToAdd)
                    .executeUpdate();

            return userRecommendationGet(request, response);
        }
    }

    /*
    Wrapper for the velocity template engine. This will render the html file at template path, and
    pass in information in the model parameter for use in the html
     */
    public static String render(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    /*
    Will query the database and return a list with one entry with the username specified
     */
    public static List<User> getUserByName(String username) {


        Sql2o sql2o = new Sql2o(url, dbuser, password);
        String sqlquery = "call getUserByName(:username)";

        try (org.sql2o.Connection conn = sql2o.open()) {
            return conn.createQuery(sqlquery)
                    .addParameter("username", username)
                    .executeAndFetch(User.class);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
    Returns an ArrayList of the top 5 actors by position for a movie.
     */
    public static ArrayList<String> getTop5Actors(int movieId) {
        ArrayList<String> actors = new ArrayList<>();
        try {

            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            String query = "select name from actor a, movie_actor ma where a.id = ma.actor_id and ma.movie_id = " + movieId + " order by actor_rank limit 5";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                actors.add(rs.getString("name"));
            }
            return actors;
        } catch (SQLException e) {
            return actors;
        }
    }
    /*
    Returns an ArrayList of a movie's directors for the movie ID passed in.
     */
    public static ArrayList<String> getDirectors(int movieId) {
        ArrayList<String> directors = new ArrayList<>();
        try {

            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            String query = "select name from director d, movie_director da where d.id = da.director_id and da.movie_id = " + movieId;
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                directors.add(rs.getString("name"));
            }
            return directors;

        }
        catch (SQLException e) {
            return directors;
        }

    }
    /*
        Returns an ArrayList of a movie's genres for the movie ID passed in.
     */


    public static ArrayList<String> getGenresByMovie(int movieId) {
        ArrayList<String> genres = new ArrayList<>();
        try {

            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            String query = "select genre_id from movie_genre where movie_id = " + movieId;
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                genres.add(rs.getString("genre_id"));
            }
            return genres;

        }
        catch (SQLException e) {
            return genres;
        }
    }
        /*
        Returns an ArrayList of a movie's top 5 tags for the movie ID passed in.
     */

    public static ArrayList<String> getTagsByMovie(int movieId) {
        ArrayList<String> tags = new ArrayList<>();
        try {

            java.sql.Connection conn = DriverManager.getConnection(url, dbuser, password);
            Statement stmt = conn.createStatement();
            String query = "select name from movie_tag, tag where tag.id = movie_tag.tag and movie_id = " + movieId + " order by weight limit 5";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                tags.add(rs.getString("name"));
            }
            return tags;

        }
        catch (SQLException e) {
            return tags;
        }
    }



}


