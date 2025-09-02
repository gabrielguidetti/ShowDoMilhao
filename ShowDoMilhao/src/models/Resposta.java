package models;
public class Resposta {
    private String texto;
    private boolean correta;

    public Resposta() {
    }

    public Resposta(String texto, boolean correta) {
        this.texto = texto;
        this.correta = correta;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isCorreta() {
        return correta;
    }
}
