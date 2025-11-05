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
    private boolean drawP1;
    private boolean drawP2;
    private boolean finishP1;
    private boolean finishP2;
    private boolean firstDraw;
    
    public GameMatch() {
        p1Id = 0;
        p2Id = 0;
        turn = 1;
        pm = new PerguntasManager();
        levelP1 = 1;
        levelP2 = 1;
        drawP1 = false;
        drawP2 = false;
        finishP1 = false;
        finishP2 = false;
        firstDraw = true;
    }

    public boolean isFirstDraw() {
        return firstDraw;
    }

    public void setFirstDraw(boolean firstDraw) {
        this.firstDraw = firstDraw;
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

    public boolean isFinishP1() {
        return finishP1;
    }

    public void setFinishP1(boolean finishP1) {
        this.finishP1 = finishP1;
    }

    public boolean isFinishP2() {
        return finishP2;
    }

    public void setFinishP2(boolean finishP2) {
        this.finishP2 = finishP2;
    }

    public int getP2Id() {
        return p2Id;
    }

    public void setDrawP1(boolean drawP1) {
        this.drawP1 = drawP1;
    }

    public void setDrawP2(boolean drawP2) {
        this.drawP2 = drawP2;
    }

    public boolean isDrawP1() {
        return drawP1;
    }

    public boolean isDrawP2() {
        return drawP2;
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
    
    public void setFinishToActualPlayer() {
        if(turn == 2) { //turnos invertidos pois o turno é trocado na função de cima
            System.out.println("SETADO FINISH PLAYER 1");
            finishP1 = true;
        } else {
            System.out.println("SETADO FINISH PLAYER 2");
            finishP2 = true;
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
    
    public void addDrawToActualPlayer() {
        if(turn == 2) { //turnos invertidos pois o turno é trocado na função de cima
            drawP1 = true;
        } else if (turn == 1) {
            drawP2 = true;
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
    
    public int confirmWin() {
        if(finishP1 && turn == 1 && !finishP2) {
            return p1Id;
        }
        
        if(finishP2 && turn == 1 && !finishP1) {
            return p2Id;
        }
        
        return 0;
    }
    
    public boolean isTied() {
        return finishP1 && finishP2;
    }
}
