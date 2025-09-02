package gameRules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.Pergunta;
import models.Resposta;

public class PerguntasManager {
    
    private List<Pergunta> perguntas;
    private List<Pergunta> perguntasNivelAtual;
    private int indexPergunta;
    
    public PerguntasManager() {
        indexPergunta = 0;
        try {
            fillPerguntas();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    private void fillPerguntas() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = PerguntasManager.class.getResourceAsStream("Perguntas.json");
        this.perguntas = mapper.readValue(input, new TypeReference<List<Pergunta>>(){});
    }
    
    public Pergunta getPerguntasByNivel(int nivel) {
        if(perguntasNivelAtual == null || perguntasNivelAtual.getFirst().getNivel() != nivel) {
            perguntasNivelAtual = new ArrayList<>(perguntas.stream().filter(x -> x.getNivel() == nivel).toList());
            Collections.shuffle(perguntasNivelAtual);
            
            for(Pergunta p : perguntasNivelAtual) {
                List<Resposta> temp = new ArrayList<>(p.getRespostas());
                Collections.shuffle(temp);
                p.setRespostas(temp);
            }
        }
        
        if(indexPergunta > 9)
            indexPergunta = 0;
        
        Pergunta result = perguntasNivelAtual.get(indexPergunta);
        indexPergunta++;
        return result;
    }
}
