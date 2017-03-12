package service;

import lombok.Getter;
import model.League;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public class LeagueService {
    @Getter
    private static List<League> activeLeagues = null;

    @SuppressWarnings("unchecked")
    public static void loadActiveLeagues() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<League> activeLeagues = session.createQuery("select l" +
                " from League l" +
                " where l.active = true")
                .list();
        session.close();
        LeagueService.activeLeagues = activeLeagues;
    }
}
