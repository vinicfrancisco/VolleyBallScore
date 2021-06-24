package Controller;

import Model.Team;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Vinicius
 */
public class GameController {

    private static GameController instance = null;

    public static GameController getIntance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    private GameController() {
        team1 = new Team();
        team2 = new Team();
    }

    private int set = 1;
    private String[] list = new String[5];
    private Team team1;
    private Team team2;

    private int getSet() {
        return set;
    }

    private void addSet() {
        this.set++;
    }

    //Aumenta a pontuação
    public void addScore(int i) {
        switch (i) {
            case 1:
                team1.addScore();
                break;
            case 2:
                team2.addScore();
                break;
        }
        if (this.set < 5) {
            verifyScore(25, 30);
        } else {
            verifyScore(15, 15);
        }
        notifyRefreshScore();

        verifyWonGame();
    }

    //Reduz a pontuação
    public void subScore(int i) {
        switch (i) {
            case 1:
                team1.subScore();
                break;
            case 2:
                team2.subScore();
                break;
        }

        notifyRefreshScore();

    }

    /*
    *   Verifica se a pontuação é suficiente para ganhar um set
    *
     */
    private void verifyScore(int min, int max) {

        if (team1.getScore() >= min && team1.getScore() > team2.getScore() + 1) {
            team1.wonSet();
            saveData(team1.getScore(), team2.getScore());
            notifyWonSet(1);
            return;
        }

        if (team2.getScore() >= min && team2.getScore() > team1.getScore() + 1) {
            team2.wonSet();
            saveData(team1.getScore(), team2.getScore());
            notifyWonSet(2);
            return;
        }

        if (team1.getScore() == max) {
            team1.wonSet();
            saveData(team1.getScore(), team2.getScore());
            notifyWonSet(1);
            return;
        }

        if (team2.getScore() == max) {
            team2.wonSet();
            saveData(team1.getScore(), team2.getScore());
            notifyWonSet(2);
            return;
        }

    }

    //Retorna vencedor
    private void verifyWonGame() {
        if (team1.getSetsWons() == 3) {
            notifyWhoWon(1);
            return;
        }
        if (team2.getSetsWons() == 3) {
            notifyWhoWon(2);
            return;
        }
    }

    //Salva o placar do set
    private void saveData(int score1, int score2) {
        list[getSet() - 1] = "Set " + getSet() + " -> Time 1 | " + score1 + " X " + score2 + " | Time 2";
    }

    //Reseta todo o placar
    public void resetGame() {
        team1 = new Team();
        team2 = new Team();
        list = new String[5];

        notifyRefreshScoreboard();

    }

    private List<GameScoreObserver> addGameScoreObserver = new ArrayList<>();

    public void attach(GameScoreObserver obs) {
        this.addGameScoreObserver.add(obs);
    }

    public void detach(GameScoreObserver obs) {
        this.addGameScoreObserver.remove(obs);
    }

    private void notifyRefreshScore() {
        for (GameScoreObserver GameScore : addGameScoreObserver) {
            GameScore.refreshScore(team1.getScore(), team2.getScore());
        }
    }

    private void notifyWonSet(int i) {
        addSet();
        team1.setScore(0);
        team2.setScore(0);
        for (GameScoreObserver GameScore : addGameScoreObserver) {
            GameScore.wonSet(i, getSet(), list);
        }
    }

    private void notifyRefreshScoreboard() {
        for (GameScoreObserver GameScore : addGameScoreObserver) {
            GameScore.resetGame();
        }

    }

    private void notifyWhoWon(int i) {
        for (GameScoreObserver GameScore : addGameScoreObserver) {
            GameScore.finishGame(i);
        }

    }

}
