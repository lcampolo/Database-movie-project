import java.util.Date;
import java.util.UUID;

public class User {
    public String name;
    public String password;
    public Date joined_date;
    public int actor_rank;
    public int director_rank;
    public int country_rank;
    public int u_rank;
    public int c_rank;
    public int genre_rank;
    public int tag_rank;

    public User() {

    }
    public User(String name, Date joinedDate) {
        this.name = name;
        this.joined_date = joinedDate;
    }
    public User(String name, String password, Date joinedDate, int actor_rank, int director_rank, int country_rank, int u_rank, int c_rank, int genre_rank, int tag_rank) {
        this.name = name;
        this.password = password;
        this.joined_date = joinedDate;
        this.actor_rank = actor_rank;
        this.director_rank = director_rank;
        this.country_rank = country_rank;
        this.u_rank = u_rank;
        this.c_rank = c_rank;
        this.genre_rank = genre_rank;
        this.tag_rank = tag_rank;
    }


    public String getName() {
        return name;
    }

    public Date getJoinedDate() {
        return joined_date;
    }

    public int getActor_rank() { return actor_rank;}
    public int getDirector_rank() { return director_rank;}
    public int getCountry_rank() { return country_rank;}
    public int getU_rank() { return u_rank;}
    public int getC_rank() { return c_rank; }
    public int getGenre_rank() { return genre_rank; }
    public int getTag_rank() { return tag_rank; }
}
