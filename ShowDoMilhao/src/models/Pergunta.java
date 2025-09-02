package models;

import java.util.List;

public class Pergunta {
    private String pergunta;
    private int nivel;
    private List<Resposta> respostas;

    public Pergunta() {
    }
    
    public Pergunta(String pergunta, int nivel, List<Resposta> respostas) {
        this.pergunta = pergunta;
        this.nivel = nivel;
        this.respostas = respostas;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setRespostas(List<Resposta> respostas) {
        this.respostas = respostas;
    }
    
    public int getNivel() {
        return nivel;
    }

    public List<Resposta> getRespostas() {
        return respostas;
    }
}
