package service;

import model.Capper;
import model.League;
import model.Pick;
import org.hibernate.Session;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DocumentUtil;
import util.HibernateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CappersService {
    private static Document doc;
    private static int numberOfPages;
    private static League league;
    private static final int CAPPER_NOT_FOUND = -1;

    @SuppressWarnings("unchecked")
    public static List<Capper> getCappers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Capper> cappers = session.createQuery("select c from Capper c").list();
        session.close();
        return cappers;
    }

    public static void updateCappers() {
        if (LeagueService.getActiveLeagues() == null)
            LeagueService.loadActiveLeagues();
        for (League league : LeagueService.getActiveLeagues())
            try {
                updateCappers(league);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void updateCappers(League league) throws IOException {
        String url = league.getLeaderboardLink();
        doc = DocumentUtil.get(url + '1');
        numberOfPages = getNumberOfPages();
        CappersService.league = league;
        List<Capper> allCappers = getAllCappers(url);
        List<Capper> cappersInDatabase = getCappersFromDatabase();
        int count = 1;
        for (Capper capper : allCappers) {
            try {
                getHistoryDoc(capper);
                List<Integer> overallStats = getOverallStats();
                capper.setPicksWon(overallStats.get(0));
                capper.setPicksLost(overallStats.get(1));
                capper.setPicksTied(overallStats.get(2));
                Integer idFromDatabase = getCapperIdFromDatabase(capper, cappersInDatabase);
                if (idFromDatabase == CAPPER_NOT_FOUND) {
                    if (!capper.worthy())
                        continue;
                } else {
                    capper.setId(idFromDatabase);
                }
                List<Integer> sidesStats = getSidesStats();
                capper.setSidesWon(sidesStats.get(0));
                capper.setSidesLost(sidesStats.get(1));
                capper.setSidesTied(sidesStats.get(2));
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.saveOrUpdate(capper);
                session.getTransaction().commit();
                session.close();
                if (count % 100 == 0)
                    System.out.println(Integer.toString(count++) + "/" + allCappers.size());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(capper.getName());
            }
        }
        deleteGarbage();
    }

    @SuppressWarnings("unchecked")
    public static void deleteGarbage() {
        List<Capper> cappersInDatabase = getCappersFromDatabase();
        for (Capper capper : cappersInDatabase)
            if (!capper.worthy()) {
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                List<Pick> relatedPicks = session.createQuery("select p" +
                        " from Pick p" +
                        " where p.capper.id = :capperId")
                        .setParameter("capperId", capper.getId())
                        .list();
                session.close();
                for (Pick pick : relatedPicks) {
                    session = HibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    session.delete(pick);
                    session.getTransaction().commit();
                    session.close();
                }
                session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                session.delete(capper);
                session.getTransaction().commit();
                session.close();
            }
    }


    private static int getNumberOfPages() {
        return Integer.parseInt(doc.select("#content > div.cmg_l_row > div.main_bar > div:nth-child(3) " +
                "> div:nth-child(1) > span")
                .text().substring(10));
    }

    private static List<Capper> getAllCappers(String url) throws IOException {
        List<Capper> cappers = new ArrayList<>(50 * numberOfPages);
        for (Integer i = 1; i <= numberOfPages; i++) {
            doc = DocumentUtil.get(url + i.toString());
            Elements rows = doc.select("#leaderboard_container > table > tbody > tr");
            for (Element row : rows)
                cappers.add(getCapperFromElement(row));
            System.out.println(Integer.toString(i) + '/' + Integer.toString(numberOfPages));
        }
        System.out.println("---------------------------");
        return cappers;
    }

    private static Capper getCapperFromElement(Element div) {
        Element child = div.child(1).child(0);
        Capper capper = new Capper();
        capper.setLeague(league);
        capper.setName(child.text());
        capper.setLink("http://contests.covers.com" + child.attr("href"));
        return capper;
    }

    @SuppressWarnings("unchecked")
    private static List<Capper> getCappersFromDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Capper> cappers = session.createQuery("select c from Capper c where c.league = :leagueId")
                .setParameter("leagueId", league.getId())
                .list();
        session.close();
        return cappers;
    }

    private static void getHistoryDoc(Capper capper) throws IOException {
        String historyURL = "http://contests.covers.com/KingOfCovers/Contestant/History/" + capper.getName() + '/'
                + capper.getLeague().getName();
        doc = DocumentUtil.get(historyURL);
    }

    private static List<Integer> getOverallStats() {
        List<Integer> stats = new ArrayList<>(3);
        Element div = doc.select("#content > div.cmg_l_row > div.main_bar " +
                "> div.cmg_l_row.cmg_contests_contestant " +
                "> div.cmg_l_col.cmg_l_span_10.cmg_contests_contestant_stats.all_history > div:nth-child(3)" +
                " > div:nth-child(1)").first();
        String overallStatsText = div.text();
        int firstHyphen = overallStatsText.indexOf('-');
        stats.add(Integer.parseInt(overallStatsText.substring(9, firstHyphen)));
        int secondHyphen = overallStatsText.indexOf('-', firstHyphen + 1);
        stats.add(Integer.parseInt(overallStatsText.substring(firstHyphen + 1, secondHyphen)));
        stats.add(Integer.parseInt(overallStatsText.substring(secondHyphen + 1)));
        return stats;
    }

    private static List<Integer> getSidesStats() {
        List<Integer> stats = new ArrayList<>(3);
        Element div = doc.select("#content > div.cmg_l_row > div.main_bar " +
                "> div.cmg_l_row.cmg_contests_contestant " +
                "> div.cmg_l_col.cmg_l_span_10.cmg_contests_contestant_stats.all_history > div:nth-child(3)" +
                " > div:nth-child(2)").first();
        String sides = div.text();
        int firstHyphen = sides.indexOf('-');
        stats.add(Integer.parseInt(sides.substring(7, firstHyphen)));
        int secondHyphen = sides.indexOf('-', firstHyphen + 1);
        stats.add(Integer.parseInt(sides.substring(firstHyphen + 1, secondHyphen)));
        stats.add(Integer.parseInt(sides.substring(secondHyphen + 1)));
        return stats;
    }

    private static Integer getCapperIdFromDatabase(Capper capper, List<Capper> cappersInDB) {
        for (Capper c : cappersInDB) {
            if (c.getName().equals(capper.getName())) {
                return c.getId();
            }
        }
        return CAPPER_NOT_FOUND;
    }

}
