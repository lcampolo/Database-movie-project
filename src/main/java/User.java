import java.util.Date;
import java.util.UUID;

public class User {

    public int ID;
    public String name;
    public String password;
    public Date joined_date;

    public User() {

    }
    public User(String name, String password, Date joinedDate) {
        this.ID = (int)Math.random();
        this.name = name;
        this.password = password;
        this.joined_date = joinedDate;
    }
    public User(int id, String name, String password, Date joinedDate) {

        this.ID = id;
        this.name = name;
        this.password = password;
        this.joined_date = joinedDate;
    }

    public int getID() {
        return ID;
    }
    public String getName() {
        return name;
    }

    public Date getJoinedDate() {
        return joined_date;
    }
}
