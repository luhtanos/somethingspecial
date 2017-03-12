package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pick {
    public static final Integer TYPE_AWAY = 0;
    public static final Integer TYPE_HOME = 1;
    public static final Integer TYPE_OVER = 2;
    public static final Integer TYPE_UNDER = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private Match match;

    @ManyToOne
    private Capper capper;

    private Integer type;

    private Double odd;

    private Double handicapOrTotal;

    @Override
    public String toString() {
        String p = null;
        switch (type) {
            case 0:
                p = match.getAwayTeam().getAbbreviatedName();
                break;
            case 1:
                p = match.getHomeTeam().getAbbreviatedName();
                break;
            case 2:
                p = "Over";
                break;
            case 3:
                p = "Under";
        }
        return capper.getLeague().getName() + ", " + match.getAwayTeam().getName() + " at " + match.getHomeTeam().getName()
                + ": " + p + " " + handicapOrTotal + " (" + capper.getName() + ", " + capper.getPicksWon() + "-"
                + capper.getPicksLost() + "(" + capper.getPercentWinning() + "), "
                + (type >= 2 ? "totals: " + capper.getTotalsWon() + "-" + capper.getTotalsLost() + "(" + capper.getPercentWinningTotals() + ")"
                : "sides: " + capper.getSidesWon() + "-" + capper.getSidesLost() + "(" + capper.getPercentWinningSides() + "))");
    }
}
