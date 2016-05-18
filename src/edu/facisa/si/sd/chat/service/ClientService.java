package edu.facisa.si.sd.chat.service;

import edu.facisa.si.sd.chat.model.DataPackage;
import java.io.*;
import java.net.*;

/**
 *
 * @author Washington Soares
 */
public class ClientService {
    
    //Insira seu IP, caso execute em uma máquina diferente do servidor... 
    private static final String IP_CLIENT = "localhost";
    private static final int PORT = 7896;
    
    private Socket socket;
    private ObjectOutputStream output;
    
    public Socket connect() {
        try {
            this.socket = new Socket(IP_CLIENT, PORT);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return socket;
    }
    
    public void send(DataPackage dataPackage) {
        try {
            output.writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
