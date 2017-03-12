package service;

import lombok.Getter;
import model.League;
import model.Team;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.*;

public class TeamService {
    @Getter
    private static Map<Integer, List<Team>> teamsDividedByLeague = null;
    @Getter
    private static Map<Integer, Set<String>> teamNamesDividedByLeague = null;

    public static void loadActiveTeams() {
        if (LeagueService.getActiveLeagues() == null)
            LeagueService.loadActiveLeagues();
        List<Team> activeTeams = getActiveTeamsFromDatabase();
        divideTeamsByLeague(activeTeams);
    }

    @SuppressWarnings("unchecked")
    private static List<Team> getActiveTeamsFromDatabase() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Team> teams = session.createQuery("select t from Team t where t.league in :leagues")
                .setParameterList("leagues", LeagueService.getActiveLeagues())
                .list();
        session.close();
        return teams;
    }

    private static void divideTeamsByLeague(List<Team> activeTeams) {
        constructTeamsDividedByLeague();
        for (Team team : activeTeams) {
            teamsDividedByLeague.get(team.getLeague().getId()).add(team);
            teamNamesDividedByLeague.get(team.getLeague().getId()).add(team.getName());
            teamNamesDividedByLeague.get(team.getLeague().getId()).add(team.getAltName());
        }
    }

    private static void constructTeamsDividedByLeague() {
        teamsDividedByLeague = new HashMap<>();
        teamNamesDividedByLeague = new HashMap<>();
        for (League league : LeagueService.getActiveLeagues()) {
            teamsDividedByLeague.put(league.getId(), new ArrayList<>());
            teamNamesDividedByLeague.put(league.getId(), new HashSet<>());
        }
    }
}
