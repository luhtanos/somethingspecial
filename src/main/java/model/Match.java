package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table (name = "`Match`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private League league;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    private Date date;

    private Integer homeScore;

    private Integer awayScore;

    @Override
    public String toString() {
        return date + " " + league.getName() + ", " + awayTeam.getName() + " at " + homeTeam.getName()
                + " " + (awayScore == null ? "" : awayScore.toString() + ":" + homeScore.toString());
    }
}
