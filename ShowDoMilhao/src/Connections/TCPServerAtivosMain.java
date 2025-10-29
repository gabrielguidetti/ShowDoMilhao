package Connections;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import views.Server;

public class TCPServerAtivosMain extends Thread {

    private List<TCPServerConnection> clientes;
    private ServerSocket server;
    private Server caller;
    private Timer timer;
    private int startTimeSeconds = 5;
    private int playerId = 1;

    public TCPServerAtivosMain(int porta, Server caller) throws IOException {
        this.server = new ServerSocket(porta);
        this.clientes = new ArrayList<>();
        this.caller = caller;
    }

    public Timer getTimer() {
        return timer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = server.accept();
                synchronized (clientes) {
                    if (clientes.size() >= 2) {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("ERRO|Servidor cheio! Máximo de jogadores atingido.");
                        socket.close();
                        caller.addMessageLog("Tentativa de conexão recusada: máximo de jogadores atingido.");
                        continue;
                    }

                    TCPServerConnection cliente = new TCPServerConnection(socket);
                    cliente.setPlayerId(playerId);
                    novoCliente(cliente);
                    
                    if (cliente.getSocket() != null && cliente.getSocket().isConnected() && cliente.getOutput() != null) {
                        cliente.getOutput().println("PARAMETER|PlayerId|" + playerId);
                        playerId++;
                        cliente.getOutput().flush();
                    }
                    
                    new TCPServerAtivosHandler(cliente, this, caller).start();
                    
                    if(clientes.size() == 2) {
                        for (TCPServerConnection cli : clientes) {
                            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getOutput() != null) {
                                cli.getOutput().println("GAMERULE|InitialTime");
                                cli.getOutput().flush();
                            }
                        }
                        caller.addMessageLog("Contagem inicial para inicio de jogo!");
                        
                        startTimeSeconds = 5;
                        timer = new Timer(1000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                startTimeSeconds--;
                                
                                if(startTimeSeconds != 0) {
                                    caller.addMessageLog("Jogo iniciando em " + Integer.toString(startTimeSeconds) + " segundos!");
                                }
                                
                                if (startTimeSeconds <= 0) {
                                    timer.stop();
                                    caller.addMessageLog("O jogo irá começar! Aguardando confirmação de tempo dos clientes!");
                                }
                            }
                        });

                        timer.start();
                    }
                }
            } catch (IOException ex) {
                caller.addMessageLog("ERRO: " + ex.getMessage());
            }
        }
    }

    public synchronized void novoCliente(TCPServerConnection cliente) throws IOException {
        clientes.add(cliente);
        this.caller.addMessageLog("Jogador conectado! PlayerId: " + cliente.getPlayerId());
    }

    public synchronized void removerCliente(TCPServerConnection cliente) {
        clientes.remove(cliente);
        try {
            cliente.getInput().close();
        } catch (IOException ex) {
            caller.addMessageLog("ERRO: " + ex.getMessage());
        }
        cliente.getOutput().close();
        try {
            cliente.getSocket().close();
            caller.addMessageLog("Jogador desconectado! playerId: " + cliente.getPlayerId());
        } catch (IOException ex) {
            caller.addMessageLog("ERRO: " + ex.getMessage());
        }
    }
    
    public synchronized void sendMessageToOtherPlayer(int playerIdFrom, String message) {
        TCPServerConnection otherClient = clientes.stream().filter(x -> x.getPlayerId() != playerIdFrom).findFirst().orElse(null);
        
        if(otherClient != null) {
            if (otherClient.getSocket() != null && otherClient.getSocket().isConnected() && otherClient.getOutput() != null) {
                otherClient.getOutput().println(message);
                otherClient.getOutput().flush();
                removerCliente(otherClient);
            }
        }
    }

    public List getClientes() {
        return clientes;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.server.close();
    }


}
