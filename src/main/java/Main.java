import static spark.Spark.*;
import java.text.SimpleDateFormat;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
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
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return render(model, "index.html");
        });
        get("/login", (request, response) -> getLogin(request));
        post("/login", (request, response) -> loginPost(request, response));
        get("/register", (request, response) -> getRegistrationForm());
        post("/register", (request, response) -> registerUser(request, response));
        get("/movies/:username", (request, response) -> movieHomeGet(request));
        post("/movies/:username", (request, response) -> userHomePost(request));
        get("/movies/:username/recommendations", (request, response) -> {
            return "<TODO>";
        });
    }

    private static Object movieHomeGet(Request request) {
        User logged_in_user = request.session().attribute("logged_in_user");
        HashMap<String, Object> model = new HashMap<>();
        model.put("user", logged_in_user);
        return render(model, "userhome.html");
    }

    private static String getRegistrationForm() {
        Map<String, Object> model = new HashMap<>();
        return render(model, "newuser.html");
    }

    private static String getLogin(Request request) {
        Map<String, Object> model = new HashMap<>();
        if (request.queryParams("r") != null) {
            model.put("message", "You were successfully registered and can login now.");
        }
        return render(model, "login.html");
    }

    private static String userHomePost(Request request) {
        HashMap<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");

        if (!params.get("movieSearch").isEmpty()) {
            String movieSearch = params.get("movieSearch").get(0);
            Sql2o sql2o = new Sql2o(url, dbuser, password);
            String movieSqlSearch = "select * from movie where title like %:search% limit 20";
            try(Connection conn = sql2o.open()) {
                List<Movie> movies = conn.createQuery(movieSqlSearch)
                        .addParameter("search", movieSearch)
                        .executeAndFetch(Movie.class);

                if (movies.isEmpty()) {
                    model.put("error", "No Movies found with that search. Please try a different search.");
                    return render(model,"userhome.html");
                }

                else {
                    model.put("movies", movies);
                    return render(model, "userhome.html");
                }
            }
        }
        else {
            Set<String> keys = params.keySet();
            String movieToAdd = "";
            for (String key: keys) {
                movieToAdd = key;
            }

            Sql2o sql2o = new Sql2o(url, dbuser, password);
            String addToFave = "insert into likes values (:username, :movieId)";

            try(Connection conn = sql2o.open()) {
                String loggedInUser = request.session().attribute("logged_in_user");
                conn.createQuery(addToFave)
                        .addParameter("username", loggedInUser)
                        .addParameter("movieId", movieToAdd)
                        .executeUpdate();
            }
            model.clear();
            return render(model, "userhome.html");
        }
    }

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
        }
        else {
            Date today = new Date();
            SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-MM-dd");
            int id = (int)(Math.random()*10000);
            User newUser = new User(id, params.get("uname").get(0), params.get("psw").get(0), today);
            String addUser = "INSERT INTO user values (:userID, :userName, :password, :date)";
            try (Connection conn = sql2o.open()) {
                conn.createQuery(addUser)
                        .addParameter("userID", newUser.getID())
                        .addParameter("userName", newUser.getName())
                        .addParameter("password", newUser.password)
                        .addParameter("date", objSDF.format(newUser.getJoinedDate()))
                        .executeUpdate();

                response.redirect("/login?r=1");
                return render(model, "login.html");

            }
            catch (Exception e) {
                return e.getMessage();
            }
        }
    }

    private static String loginPost(Request request, Response response) {
        Map<String, Object> model = new HashMap<>();
        MultiMap<String> params = new MultiMap<>();
        UrlEncoded.decodeTo(request.body(), params, "UTF-8");
        String loginQuery = "select * from user where name = :userName and password = :psw";
        Sql2o sql2o = new Sql2o(url, dbuser, password);
        try(Connection conn = sql2o.open()) {
            List<User> userExist = conn.createQuery(loginQuery)
                    .addParameter("userName", params.get("uname").get(0))
                    .addParameter("psw", params.get("psw").get(0))
                    .executeAndFetch(User.class);

            if (userExist.isEmpty()) {
                model.put("message", "Username and password not found. Please enter a valid username and password or create a new account.");
                return render(model, "login.html");
            }

            else {
                request.session().attribute("logged_in_user", params.getValue("uname", 0));
                String path = "/movies/" + userExist.get(0).name;
                model.put("user", userExist.get(0));
                response.redirect(path);
                return render (model, "userhome.html");
            }
        }
    }


    public static String render(Map<String, Object> model, String templatePath) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    public static List<User> getUserByName(String username) {


        Sql2o sql2o = new Sql2o(url, dbuser, password);
        String sqlquery = "select * from user where name = :username";

        try (org.sql2o.Connection conn = sql2o.open()) {
            return conn.createQuery(sqlquery)
                    .addParameter("username", username)
                    .executeAndFetch(User.class);
        }

        catch(Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return null;
        }
    }
}
