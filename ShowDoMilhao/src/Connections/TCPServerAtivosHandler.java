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
                                this.main.getGm().addLevelToActualPlayer();
                                if(this.main.getGm().getActualLevel() >= 8) {
                                    caller.addMessageLog("Jogador playerId: " + this.cliente.getPlayerId() + " VENCEU O JOGO!");
                                    this.main.sendMessageToPlayer(this.cliente.getPlayerId(), "GAMERULE|Finish|Win");
                                    this.main.sendMessageToOtherPlayer(this.cliente.getPlayerId(), "GAMERULE|Finish|Lose");
                                    
                                    if(this.main.getTimer() != null) {
                                        this.main.getTimer().stop();
                                    }
                                } else {
                                    this.main.sendTurns("Você acertou!");
                                }
                            } else {
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
