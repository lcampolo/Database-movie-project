import java.util.ArrayList;

public class Movie {

    private int id;
    private String title;
    private String picture_url;
    private String year;
    private String country;
    private Double critic_score;
    private Double audience_score;
    public ArrayList<String> topFiveActors;
    public ArrayList<String> directors;
    public ArrayList<String> genres;
    public ArrayList<String> tags;

    public Movie() {

    }
    public Movie(int id, String title, String picture_url) {
        this.id = id;
        this.title = title;
        this.picture_url = picture_url;
    }
    public Movie(int id, String title, String picture_url, String year, String country, Double critic_score, Double audience_score ) {
        this.id = id;
        this.title = title;
        this.picture_url = picture_url;
        this.year = year;
        this.country = country;
        this.critic_score = critic_score;
        this.audience_score = audience_score;
    }

    public int getId() {
        return this.id;
    }
    public String getTitle() {
        return this.title;
    }
    public String getPicture() {
        return this.picture_url;
    }
    public String getYear() {
        return this.year;
    }
    public String getCountry() {
        return this.country;
    }
    public Double getCritic_score() {
        return this.critic_score;
    }
    public Double getAudience_score() {
        return this.audience_score;
    }
    public ArrayList<String> getTopFiveActors() { return this.topFiveActors; }
    public ArrayList<String> getDirectors() { return this.directors; }
    public ArrayList<String> getGenres() { return this.genres; }
    public ArrayList<String> getTags() { return this.tags; }
}
