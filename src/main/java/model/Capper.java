package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Capper {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @ManyToOne
    private League league;

    private String link;

    private Integer picksWon;

    private Integer picksLost;

    private Integer picksTied;

    private Integer sidesWon;

    private Integer sidesLost;

    private Integer sidesTied;

    @Transient
    public Integer getTotalsWon() {
        return picksWon - sidesWon;
    }

    @Transient
    public Integer getTotalsLost() {
        return picksLost - sidesLost;
    }

    @Transient
    public Integer getTotalsTied() {
        return picksTied - sidesTied;
    }

    @Transient
    public Integer getPicksOverall() {
        return picksWon + picksTied + picksLost;
    }

    @Transient
    public Integer getSidesOverall() {
        return sidesWon + sidesTied + sidesLost;
    }

    @Transient
    public Integer getTotalsOverall() {
        return getPicksOverall() - getSidesOverall();
    }

    @Transient
    public Double getPercentWinning() {
        return picksWon.doubleValue() / (picksWon + picksLost);
    }

    @Transient
    public Double getPercentWinningSides() {
        return sidesWon.doubleValue() / (sidesWon + sidesLost);
    }

    @Transient
    public Double getPercentWinningTotals() {
        return getTotalsWon().doubleValue() / (getTotalsWon() + getTotalsLost());
    }

    @Transient
    public boolean worthy() {
        return getPicksOverall() >= 300 && getPercentWinning() >= (0.3 * Math.sqrt(5. / getPicksOverall()) + 1.) * 11./21.;
    }

    @Override
    public String toString() {
        return getName() + ", " + getPicksOverall() + '/' + getPercentWinning();
    }
}
