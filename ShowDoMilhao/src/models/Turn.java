package models;

public class Turn {
    private int playerId;
    private Pergunta pergunta;

    public Turn(int playerId, Pergunta pergunta) {
        this.playerId = playerId;
        this.pergunta = pergunta;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }
    
}
