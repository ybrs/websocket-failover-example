package nl.ybrs.eventserver;
import java.io.Serializable;


public class RoomState implements Serializable{

    public RoomState(String name) {
        this.name = name;
        this.setScore(0);
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void incrScore(){
        this.score++;
    }

    Integer score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
}
