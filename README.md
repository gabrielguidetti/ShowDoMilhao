🎮 Regras do Jogo do Milhão (2 Jogadores, via Sockets, por turnos)

1. Estrutura

1 servidor que controla perguntas, respostas e placar.

2 clientes (Jogador 1 e Jogador 2).

O jogo roda por turnos alternados:

Jogador 1 responde → depois Jogador 2 → e assim sucessivamente.




---

2. Fluxo da Partida

1. Início

Servidor aguarda os 2 jogadores conectarem.

Quando ambos entram, envia: "Jogo começando!".

Sorteio define quem começa o primeiro turno.



2. Rodadas

O servidor envia uma pergunta apenas para o jogador da vez.

O outro jogador fica em espera.

O jogador tem até 30 segundos para responder.



3. Resposta

Se acertar → ganha os pontos da pergunta.

Se errar → não ganha nada naquela rodada (opcional: pode perder pontos).



4. Troca de Turno

Após a resposta (certa ou errada), a vez passa para o outro jogador.





---

3. Pontuação

Perguntas têm valores progressivos, por exemplo:


Nível	Valor

1	1.000
2	5.000
3	10.000
4	50.000
5	100.000
6	250.000
7	500.000
8	1.000.000


Vence quem atingir 1 milhão primeiro.

Se acabarem as perguntas → vence quem tiver maior pontuação.



---

4. Fim do Jogo

O jogo termina quando:

Um jogador chega a 1 milhão.

Ou quando não houver mais perguntas.


O servidor então envia o placar final e o vencedor.


---

5. Empate

Se ao final:

Os dois jogadores tiverem a mesma pontuação, aplica-se o desempate:


Critério 1 – Pergunta de Ouro (Morte Súbita):

O servidor envia uma pergunta extra para ambos ao mesmo tempo.

Quem responder corretamente primeiro vence.


Critério 2 – Tempo de resposta:

Se ambos acertarem a mesma pergunta, vence o que responder em menos tempo.


Critério 3 – Novo empate:

Se ainda empatarem, segue-se com novas perguntas de ouro até alguém vencer.



---

6. Exemplo de Mensagens no Socket

Servidor → Cliente

PERGUNTA|Qual a capital do Brasil?|A) Rio|B) Brasília|C) SP|D) BH

RESULTADO|CORRETA

RESULTADO|ERRADA

PLACAR|J1:5000|J2:1000

FIM|Vencedor: Jogador 1


Cliente → Servidor

RESPOSTA|B