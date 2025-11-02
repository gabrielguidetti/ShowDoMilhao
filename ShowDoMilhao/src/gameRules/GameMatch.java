package gameRules;

import models.Pergunta;
import models.Turn;

public class GameMatch {
    private int p1Id;
    private int p2Id;
    private int levelP1;
    private int levelP2;
    private int turn;
    private PerguntasManager pm;
    private Pergunta actualQuestion;
    
    public GameMatch() {
        p1Id = 0;
        p2Id = 0;
        turn = 1;
        pm = new PerguntasManager();
        levelP1 = 1;
        levelP2 = 1;
    }

    public void setP1Id(int p1Id) {
        this.p1Id = p1Id;
    }

    public void setP2Id(int p2Id) {
        this.p2Id = p2Id;
    }

    public int getP1Id() {
        return p1Id;
    }

    public int getP2Id() {
        return p2Id;
    }

    public Pergunta getActualQuestion() {
        return actualQuestion;
    }
    
    public int getTurnNumber() {
        return turn;
    }
    
    public Turn getTurn() {
        Turn result;
        if(turn == 1) {
            actualQuestion = pm.getPerguntasByNivel(levelP1);
            result = new Turn(p1Id, actualQuestion);
            turn = 2;
        } else {
            actualQuestion = pm.getPerguntasByNivel(levelP2);
            result = new Turn(p2Id, actualQuestion);
            turn = 1;
        }
        
        return result;
    }
    
    public int getActualLevel() {
        if(turn == 2) { //turnos invertidos pois o turno é trocado na função de cima
            return levelP1;
        } else {
            return levelP2;
        }
    }
    
    public void addLevelToActualPlayer() {
        if(turn == 2) { //turnos invertidos pois o turno é trocado na função de cima
            if(levelP1 < 8) {
                levelP1++;
            }
            
        } else if (turn == 1) {
            if(levelP2 < 8) {
                levelP2++;
            }
        }
    }
    
    public void resetGameMatch() {
        p1Id = 0;
        p2Id = 0;
        turn = 1;
        pm = new PerguntasManager();
        levelP1 = 1;
        levelP2 = 1;
    }
}
