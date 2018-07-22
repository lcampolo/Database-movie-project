
public class Entry {
    private String id;
    private String name;

    public Entry(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.id + " " + this.name;
    }
}
