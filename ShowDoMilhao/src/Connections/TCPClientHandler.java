package Connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JOptionPane;
import views.Game;

public class TCPClientHandler extends Thread {

    private Socket socket;
    private Game caller;
    private BufferedReader input;

    public TCPClientHandler(Socket socket, Game caller) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                if (this.socket.isConnected() && this.input != null) {
                    message = this.input.readLine();
                } else {
                    break;
                }
                if (message == null || message.equals("")) {
                    break;
                }
                
                String[] partes = message.split("\\|");
                String tipo = partes[0];
                String conteudo = partes.length > 1 ? partes[1] : "";
                
                
                if (tipo.equalsIgnoreCase("ERRO")) {
                    JOptionPane.showMessageDialog(null, conteudo, tipo, JOptionPane.WARNING_MESSAGE);
                    socket.close();
                    caller.closeConnection(true);
                    break;
                }
                
                if (tipo.equalsIgnoreCase("GAMERULE")) {
                    if(conteudo.equalsIgnoreCase("InitialTime")) {
                        caller.setInitialTimer();
                    }
                    
                    if(conteudo.equalsIgnoreCase("WaitOpponent")) {
                        caller.setWaitOpponent();
                    }
                }
                
                if (tipo.equalsIgnoreCase("TURN")) {
                    if(conteudo.equalsIgnoreCase("ShowQuestion")) {
                        String question = partes[2];
                        int level = Integer.parseInt(partes[3]);
                        String r1 = partes[4];
                        String r2 = partes[5];
                        String r3 = partes[6];
                        String r4 = partes[7];
                        caller.printPergunta(question, r1, r2, r3, r4, level);
                    }
                }
                
                if (tipo.equalsIgnoreCase("SYSTEM")) {
                    if(conteudo.equalsIgnoreCase("OtherPlayerDisconnect")) {
                        if(caller.getTimer()!= null) {
                            caller.getTimer().stop();
                        }
                        
                        JOptionPane.showMessageDialog(null, "O outro jogador foi desconectado! Jogo encerrado!", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
                        caller.resetLayout();
                        caller.closeConnection(true);
                    }
                }
                
                if (tipo.equalsIgnoreCase("PARAMETER")) {
                    if(conteudo.equalsIgnoreCase("PlayerId")) {
                        String parameter = partes.length > 2 ? partes[2] : "";
                        caller.setPlayerId(Integer.parseInt(parameter));
                        System.out.println("PlayerId definido para: " + parameter);
                    }
                }
                
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
    }
}
