package Connections;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
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
                    }
                }
                
                if (tipo.equalsIgnoreCase("SYSTEM")) {
                    if(conteudo.equalsIgnoreCase("Disconnect")) {
                        String parameter = partes.length > 2 ? partes[2] : "";
                        caller.addMessageLog("Request de Desconexão do jogador playerId: " + parameter);
                        caller.addMessageLog("Contagem parada!");
                        int playerId = Integer.parseInt(parameter);
                        if(this.main.getTimer() != null) {
                            this.main.getTimer().stop();
                        }
                        this.main.sendMessageToOtherPlayer(playerId, "SYSTEM|OtherPlayerDisconnect");
                    }
                }
                
                
                //messageDispatcher(message);
                //caller.addMessageLog(message);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
        encerrar();
    }
}
