package edu.facisa.si.sd.chat.service;

import edu.facisa.si.sd.chat.enumarator.Command;
import edu.facisa.si.sd.chat.model.DataPackage;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Washington Soares
 */
public class ServerService {

    private static ServerService instance;
    private static final int PORT = 7896;

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> usersMap;

    private ServerService() {
        this.usersMap = new HashMap<String, ObjectOutputStream>();        
        startServer();
    }
    
    public static synchronized ServerService getInstance() {
        if (instance == null) {
            instance = new ServerService();
        }
        return instance;
    }

    private class Listener implements Runnable {

        private ObjectInputStream input;
        private ObjectOutputStream output;

        public Listener(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
                this.output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            DataPackage dataPackage = null;
            try {
                while ((dataPackage = (DataPackage) this.input.readObject()) != null) {
                    Command commands = dataPackage.getAction();
                    switch (commands) {
                        case CONNECT:                            
                            connect(dataPackage, this.output);
                            break;
                        case DISCONNECT:
                            disconnect(dataPackage, this.output);                            
                            break;
                        case SEND_RESERVED:
                            sendReserved(dataPackage);
                            break;
                        case SEND_ALL:
                            sendAll(dataPackage);
                            break;
                        default:
                    }
                }
            } catch (IOException | ClassNotFoundException e) {             
                System.out.println(e.getMessage());
            }
        }
    }

    private void startServer() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("server on\nExecute manualmente a classe 'Client' para realizar a conexão com o servidor");
            while (true) {
                this.socket = this.serverSocket.accept();
                new Thread(new Listener(this.socket)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void connect(DataPackage dataPackage, ObjectOutputStream output) {
        if (!this.usersMap.containsKey(dataPackage.getUser())) {
            dataPackage.setAction(Command.CONNECTED);
            send(dataPackage, output);
            this.usersMap.put(dataPackage.getUser(), output);            
            refreshOnlines();
            for (Map.Entry<String, ObjectOutputStream> outputMap : this.usersMap.entrySet()) {
                if (!outputMap.getKey().equals(dataPackage.getUser())) {
                    send(dataPackage, outputMap);
                }
            }
            System.out.println(dataPackage.getUser() + " está online");
        } else {
            dataPackage.setAction(Command.NOT_CONNECTED);
            send(dataPackage, output);
        }
    }
    
    private void disconnect(DataPackage dataPackage, ObjectOutputStream output) {
        dataPackage.setAction(Command.DISCONNECTED);
        send(dataPackage, output);
        this.usersMap.remove(dataPackage.getUser());
        refreshOnlines();        
        for (Map.Entry<String, ObjectOutputStream> outputMap : this.usersMap.entrySet()) {
            if (!outputMap.getKey().equals(dataPackage.getUser())) {                
                dataPackage.setMessage(dataPackage.getUser() + " saiu do bate-papo\n");
                dataPackage.setAction(Command.SEND_ALL);
                send(dataPackage, outputMap);
            }
        }       
        System.out.println(dataPackage.getUser() + " saiu do chat");
    }

    private void send(DataPackage dataPackage, ObjectOutputStream output) {
        try {
            output.writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());            
        }
    }
    
    private void send(DataPackage dataPackage, Map.Entry<String, ObjectOutputStream> outputMap) {
        try {
            outputMap.getValue().writeObject(dataPackage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void sendReserved(DataPackage dataPackage) {
        for (Map.Entry<String, ObjectOutputStream> outputMap : this.usersMap.entrySet()) {
            if (outputMap.getKey().equals(dataPackage.getUserReserved())) {
                dataPackage.setAction(Command.RECEIVED);
                send(dataPackage, outputMap);
            }
        }
    }

    private void sendAll(DataPackage dataPackage) {
        for (Map.Entry<String, ObjectOutputStream> outputMap : this.usersMap.entrySet()) {
            if (!outputMap.getKey().equals(dataPackage.getUser())) {
                dataPackage.setAction(Command.RECEIVED);
                send(dataPackage, outputMap);
            }
        }
    }
    
    private void refreshOnlines() {
        Set<String> users = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> keysMap : this.usersMap.entrySet()) {
            users.add(keysMap.getKey());
        }        
        DataPackage dataPackage = new DataPackage();
        dataPackage.setAction(Command.USERS_ONLINE);
        dataPackage.setUsersOnLine(users);
        for (Map.Entry<String, ObjectOutputStream> outputMap : this.usersMap.entrySet()) {
            dataPackage.setUser(outputMap.getKey());
            send(dataPackage, outputMap);
        }        
    }
}