package service;

import exception.TeamNotFoundException;
import exception.WrongLinkException;
import model.League;
import model.Match;
import model.Team;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.DocumentUtil;
import util.HibernateUtil;

import javax.persistence.TemporalType;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MatchesService {
    private static Document doc;
    private static League league;
    private static List<Match> matches = null;

    @SuppressWarnings("unchecked")
    public static List<Match> getLatestMatches() {
        Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.HOUR, -24-8); //as we are in Belarus
        Calendar now = new GregorianCalendar();
        now.add(Calendar.HOUR, -8);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Match> latestMatches = session.createQuery("select m" +
                " from Match m" +
                " where m.date > :yesterday" +
                "   and m.date < :now")
                .setParameter("yesterday", yesterday.getTime(), TemporalType.TIMESTAMP)
                .setParameter("now", now.getTime(), TemporalType.TIMESTAMP)
                .list();
        session.close();
        return latestMatches;
    }

    @SuppressWarnings("unchecked")
    public static List<Match> getUpcomingMatches() {
        Calendar now = new GregorianCalendar();
        now.add(Calendar.HOUR, -8);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Match> upcomingMatches = session.createQuery("select m" +
                " from Match m" +
                " where m.date > :now")
                .setParameter("now", now.getTime(), TemporalType.TIMESTAMP)
                .list();
        session.close();
        return upcomingMatches;
    }

    @SuppressWarnings("unchecked")
    public static void updateMatches() {
        if (TeamService.getTeamsDividedByLeague() == null)
            TeamService.loadActiveTeams();
        Calendar twoDaysBefore = new GregorianCalendar();
        twoDaysBefore.add(Calendar.HOUR, -48);
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        matches = session.createQuery("select m" +
                " from Match m" +
                " where m.date > :date" +
                " order by m.date")
                .setParameter("date", twoDaysBefore.getTime(), TemporalType.TIMESTAMP)
                .list();
        session.close();
        for (League league : LeagueService.getActiveLeagues()) {
            MatchesService.league = league;
            Calendar now = new GregorianCalendar();
            now.add(Calendar.HOUR, -8); //as we are in Belarus
            try {
                String urlMatchesYesterday = "http://www.covers.com/Sports/" + league.getName()
                        + "/Matchups?selectedDate="
                        + Integer.toString(now.get(Calendar.YEAR)) + '-'
                        + Integer.toString(now.get(Calendar.MONTH) + 1) + '-'
                        + Integer.toString(now.get(Calendar.DAY_OF_MONTH) - 1);
                updateMatches(urlMatchesYesterday);
                String urlMatchesToday = "http://www.covers.com/Sports/" + league.getName()
                        + "/Matchups?selectedDate="
                        + Integer.toString(now.get(Calendar.YEAR)) + '-'
                        + Integer.toString(now.get(Calendar.MONTH) + 1) + '-'
                        + Integer.toString(now.get(Calendar.DAY_OF_MONTH));
                updateMatches(urlMatchesToday);
            } catch (WrongLinkException e) {
                System.out.print("wrong link, motherfucker");
            } catch (TeamNotFoundException e) {
                System.out.print("one of the teams in the match " + e.getMessage() + " was not found, you asshole");
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void updateMatches(String url)
            throws WrongLinkException, IOException, ParseException, TeamNotFoundException {
        doc = DocumentUtil.get(url);
        updatePregameMatches();
        updatePostgameMatches();
    }

    private static void updatePregameMatches() throws ParseException, TeamNotFoundException {
        Elements pregame = doc.select(".cmg_pregame");
        for (Element div : pregame) {
            Match match = extractMatchWithoutScores(div);
            updateMatch(match);
        }
    }

    private static void updatePostgameMatches() throws ParseException, TeamNotFoundException {
        Elements postgame = doc.select(".cmg_postgame");
        for (Element div : postgame) {
            Match match = extractMatchWithScores(div);
            updateMatch(match);
        }
    }

    @SuppressWarnings("unchecked")
    private static void updateMatch(Match match) throws ParseException {
        Integer id = getMatchId(match);
        if (id != null) {
            match.setId(id);
            //System.out.println(m + " - updating...");
            //} else {
            //System.out.println(m + " - saving...");
        }
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(match);
        } catch (NonUniqueObjectException e) {
            e.printStackTrace();
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    private static Integer getMatchId(Match match) {
        for (Match m : matches)
            if (m.getAwayTeam().getId().equals(match.getAwayTeam().getId())
                    && m.getHomeTeam().getId().equals(match.getHomeTeam().getId())
                    && m.getDate().getTime() == match.getDate().getTime())
                return m.getId();
        return null;
    }

    private static Match extractMatchWithScores(Element div) throws ParseException, TeamNotFoundException {
        Match match = extractMatchWithoutScores(div);
        match.setAwayScore(Integer.parseInt(div.select(".cmg_matchup_list_score_away").first().text()));
        match.setHomeScore(Integer.parseInt(div.select(".cmg_matchup_list_score_home").first().text()));
        return match;
    }

    private static Match extractMatchWithoutScores(Element div) throws ParseException, TeamNotFoundException {
        Match match = new Match();
        match.setDate(getDateFromElement(div));
        match.setLeague(league);
        List<Team> matchTeams = getTeamsFromElement(div);
        match.setAwayTeam(matchTeams.get(0));
        match.setHomeTeam(matchTeams.get(1));
        return match;
    }

    private static List<Team> getTeamsFromElement(Element div) throws TeamNotFoundException {
        String matchTeamNames = handleMatchTeamNames(div.select(".cmg_matchup_header_team_names").first().text());
        int split = Math.max(matchTeamNames.indexOf(" at "), matchTeamNames.indexOf(" vs "));
        String awayTeamName = matchTeamNames.substring(0, split);
        String homeTeamName = matchTeamNames.substring(split + 4);
        List<Team> teams = new ArrayList<>(2);
        teams.add(getTeam(awayTeamName));
        teams.add(getTeam(homeTeamName));
        return teams;
    }

    private static Team getTeam(String teamName) throws TeamNotFoundException {
        for (Team team : TeamService.getTeamsDividedByLeague().get(league.getId()))
            if (team.getName().equals(teamName))
                return team;
        throw new TeamNotFoundException(teamName);
    }

    private static String handleMatchTeamNames(String matchTeamNames) {
        matchTeamNames = removePlaceNumbers(matchTeamNames);
        while (matchTeamNames.charAt(matchTeamNames.length() - 1) == ' ') {
            matchTeamNames = matchTeamNames.substring(0, matchTeamNames.length() - 1);
        }
        matchTeamNames = matchTeamNames.replace("&amp;", "&");
        matchTeamNames = matchTeamNames.replace("&#39;", "'");
        return matchTeamNames;
    }

    private static String removePlaceNumbers(String matchTeamNames) {
        int left = 0;
        while (containsParentheses(matchTeamNames, left)) {
            if (isNumba(matchTeamNames.charAt(matchTeamNames.indexOf('(', left) + 1)))
                matchTeamNames = matchTeamNames.replace(matchTeamNames.substring(matchTeamNames.indexOf('(', left), matchTeamNames.indexOf(')', left) + 2), "");
            else
                left = matchTeamNames.indexOf(')', left) + 1;
        }
        return matchTeamNames;
    }

    private static boolean isNumba(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean containsParentheses(String s, int left) {
        return s.indexOf('(', left) != -1;
    }

    private static Date getDateFromElement(Element div) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.parse(div.select(".cmg_matchup_game_box")
                .first()
                .attr("data-game-date"));
    }
}
