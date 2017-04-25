package sgitg.erypt.test;

/**
 * Created by DELL on 2017/4/17.
 */
public class UserInfo {
    private int id;
    private String name;
    private String score;

    public int getId() {
        return id;
    }

    public UserInfo setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getScore() {
        return score;
    }

    public UserInfo setScore(String score) {
        this.score = score;
        return this;
    }


}
