import java.util.Date;
import java.util.UUID;

public class User {

    private UUID ID;
    private String name;
    private String password;
    private Date joinedDate;

    public User(UUID id, String name, String password, Date joinedDate) {

        this.ID = id;
        this.name = name;
        this.password = password;
        this.joinedDate = joinedDate;
    }

    public UUID getID() {
        return ID;
    }
    public String getName() {
        return name;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }
}
