import java.util.Date;

public class Comment {
    private String text;
    private Date date;
    private int movie_id;
    private String user_name;
    private Date user_date;


    public String getText() { return text; }
    public Date getDate() { return date; }
    public int getMovie_id() { return movie_id; }
    public String getUser_name() { return user_name; }
    public Date getUser_date() { return user_date ; }

    public Comment (String text, Date date, String user_name, Date user_date) {
        this.text = text;
        this.date = date;
        this.user_name = user_name;
        this.user_date = user_date;

    }
}
