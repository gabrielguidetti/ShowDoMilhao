package Connections;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import models.Resposta;
import views.Server;

public class TCPServerAtivosHandler extends Thread {

    private TCPServerConnection cliente;
    private TCPServerAtivosMain main;
    private Server caller;

    public TCPServerAtivosHandler(TCPServerConnection cliente, TCPServerAtivosMain main, Server caller) throws IOException {
        this.cliente = cliente;
        this.main = main;
        this.caller = caller;
    }

    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }

    private void encerrar() {
        this.main.removerCliente(this.cliente);
    }

    public synchronized void messageDispatcher(String message) throws IOException {
        List<TCPServerConnection> clientes = this.main.getClientes();
        for (TCPServerConnection cli : clientes) {
            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getOutput() != null) {
                cli.getOutput().println(message);
                cli.getOutput().flush();
            }
        }
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                if (this.cliente.getSocket().isConnected() && this.cliente.getInput() != null) {
                    message = this.cliente.getInput().readLine();
                } else {
                    break;
                }
                if (message == null || message.equals("")) {
                    break;
                }
                
                String[] partes = message.split("\\|");
                String tipo = partes[0];
                String conteudo = partes.length > 1 ? partes[1] : "";
                
                if (tipo.equalsIgnoreCase("GAMERULE")) {
                    if(conteudo.equalsIgnoreCase("ConfirmStartByPlayer")) {
                        String parameter = partes.length > 2 ? partes[2] : "";
                        caller.addMessageLog("Confirmação de inicio do jogador playerId: " + parameter);
                        
                        if(this.main.getGm().getP1Id() == 0) {
                            this.main.getGm().setP1Id(Integer.parseInt(parameter));
                        } else {
                            this.main.getGm().setP2Id(Integer.parseInt(parameter));
                            this.main.sendTurns("XXX");
                        }
                    }
                }
                
                if (tipo.equalsIgnoreCase("SYSTEM")) {
                    if(conteudo.equalsIgnoreCase("Disconnect")) {
                        String parameter = partes.length > 2 ? partes[2] : "";
                        caller.addMessageLog("Request de Desconexão do jogador playerId: " + parameter);
                        int playerId = Integer.parseInt(parameter);
                        this.main.sendMessageToOtherPlayer(playerId, "SYSTEM|OtherPlayerDisconnect");
                        this.main.getGm().resetGameMatch();
                        caller.addMessageLog("Jogo reiniciado!");
                        if(this.main.getTimer() != null) {
                            this.main.getTimer().stop();
                        }
                    }
                }
                
                if (tipo.equalsIgnoreCase("TURN")) {
                    if(conteudo.equalsIgnoreCase("PlayerResponse")) {
                        String response = partes.length > 2 ? partes[2] : "";
                        caller.addMessageLog("Resposta dada pelo jogador playerId: " + this.cliente.getPlayerId() + ": " + response);
                        
                        Resposta correctResponse = this.main.getGm().getActualQuestion().getRespostas().stream().filter(x -> x.isCorreta()).findFirst().orElse(null);
                        
                        if(correctResponse != null) {
                            if(correctResponse.getTexto().equals(response)) {
                                caller.addMessageLog("Jogador playerId: " + this.cliente.getPlayerId() + " ACERTOU!");
                                
                                if(!this.main.getGm().isTied()) {
                                    if(this.main.getGm().getActualLevel() < 8) {
                                        this.main.getGm().addLevelToActualPlayer();
                                    } else if(this.main.getGm().getActualLevel() == 8) {
                                        this.main.getGm().setFinishToActualPlayer();
                                    }
                                }
                                
                                int playerWin = this.main.getGm().confirmWin();
                                
                                if(playerWin != 0) {
                                    caller.addMessageLog("Jogador playerId: " + playerWin + " VENCEU O JOGO!");
                                    this.main.sendMessageToPlayer(playerWin, "GAMERULE|Finish|Win");
                                    this.main.sendMessageToOtherPlayer(playerWin, "GAMERULE|Finish|Lose");
                                    
                                    if(this.main.getTimer() != null) {
                                        this.main.setTimer(null);
                                    }
                                } else {
                                    if(this.main.getGm().isTied()) {
                                        System.out.println("DEBUG1: ESTÁ EMPATADO!");
                                        if(this.main.getGm().isFirstDraw()) {
                                            System.out.println("DEBUG2: TIROU FIRST DRAW");
                                            this.main.getGm().setFirstDraw(false);
                                        } else {
                                            System.out.println("DEBUG3: adicionou first drawn");
                                            this.main.getGm().addDrawToActualPlayer();
                                        }
                                        
                                        if(this.main.getGm().isDrawP1() && this.main.getGm().isDrawP2() && this.main.getGm().getTurnNumber() == 1) {
                                            System.out.println("DEBUG4: NOVO DESEMPATE");
                                            this.main.getGm().setDrawP1(false);
                                            this.main.getGm().setDrawP2(false);
                                        } else if(!this.main.getGm().isDrawP1() && this.main.getGm().isDrawP2() && this.main.getGm().getTurnNumber() == 1) {
                                            //player 2 win
                                            caller.addMessageLog("Jogador playerId: " + this.main.getGm().getP2Id() + " VENCEU O JOGO!");
                                            this.main.sendMessageToPlayer(this.main.getGm().getP2Id(), "GAMERULE|Finish|Win");
                                            this.main.sendMessageToOtherPlayer(this.main.getGm().getP2Id(), "GAMERULE|Finish|Lose");
                                            return;
                                        } else if(this.main.getGm().isDrawP1() && !this.main.getGm().isDrawP2() && this.main.getGm().getTurnNumber() == 1) {
                                            //player 1 win
                                            caller.addMessageLog("Jogador playerId: " + this.main.getGm().getP1Id() + " VENCEU O JOGO!");
                                            this.main.sendMessageToPlayer(this.main.getGm().getP1Id(), "GAMERULE|Finish|Win");
                                            this.main.sendMessageToOtherPlayer(this.main.getGm().getP1Id(), "GAMERULE|Finish|Lose");
                                            return;
                                        }
                                    }
                                    
                                    this.main.sendTurns("Você acertou!");
                                }
                            } else {
                                // FAZER DESEMPATE DE DERROTA SEPARADO
                                caller.addMessageLog("Jogador playerId: " + this.cliente.getPlayerId() + " ERROU!");
                                this.main.sendTurns("Você errou!");
                            }
                            
                            if(this.main.getTimer() != null) {
                                this.main.getTimer().stop();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                caller.addMessageLog("ERRO: " + ex.getMessage());
                break;
            }
        }
        encerrar();
    }
}
