package Connections;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import views.Game;

public class TCPClientMain {
    
    private TCPClientHandler handler;
    private Socket socket;
    private PrintWriter output;

    public TCPClientMain(String serverAddress, int serverPort, Game caller) throws UnknownHostException, IOException {
        this.socket = new Socket(serverAddress, serverPort);
        handler = new TCPClientHandler(socket, caller);
        this.handler.start();
        this.output = new PrintWriter(this.socket.getOutputStream(), true);
    }

    public void writeMessage(String outMessage) {
        this.output.println(outMessage);
    }

    public void closeConnection() throws IOException {
        this.output.close();
        this.socket.close();
        this.handler.interrupt();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.closeConnection();
        } finally {
            super.finalize();
        }
    }
}
