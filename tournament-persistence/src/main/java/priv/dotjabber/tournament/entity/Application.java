package priv.dotjabber.tournament.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "application")
public class Application implements Serializable, Comparable<Application> {
    private Long id;
    private String email;
    private String name;
    private Date creationDate = new Date();
    private boolean verified = false;

    private int score = 0;
    private int wins = 0;
    private int loses = 0;
    private int ties = 0;
    private Date scoreDate = new Date();

    private String playerClassName;
    private String gameName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="email", nullable=false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name="name", nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", length = 35)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "score_date", length = 35)
    public Date getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(Date scoreDate) {
        this.scoreDate = scoreDate;
    }

    @Column(name="score")
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void incScore(int delta) {
        score += delta;
    }

    @Column(name="verified", nullable=false)
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Column(name="game_name", nullable=false)
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Column(name="player_class_name", nullable=false)
    public String getPlayerClassName() {
        return playerClassName;
    }

    public void setPlayerClassName(String playerClassName) {
        this.playerClassName = playerClassName;
    }

    @Column(name="wins")
    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void incWins() {
        wins++;
    }

    @Column(name="loses")
    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public void incLoses() {
        loses++;
    }

    @Column(name="ties")
    public int getTies() {
        return ties;
    }

    public void setTies(int ties) {
        this.ties = ties;
    }

    public void incTies() {
        ties++;
    }

    public void clear() {
        ties = 0;
        wins = 0;
        loses = 0;
        score = 0;
        scoreDate = new Date();
    }

    @Override
    public int compareTo(Application application) {
        return application.score - score;
    }
}
