package edu.facisa.si.sd.chat.frame;

import edu.facisa.si.sd.chat.model.DataPackage;
import edu.facisa.si.sd.chat.enumarator.Command;
import edu.facisa.si.sd.chat.service.ClientService;
import java.awt.HeadlessException;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

/**
 *
 * @author Washington Soares
 */
public class ClientFrame extends javax.swing.JFrame {

    private Socket socket;
    private ClientService service;
    private DataPackage dataPackage;
    private String nickName;    
    
    /**
     * Creates new form ClientFrame
     */
    public ClientFrame() {
        try {
            connect();
            initComponents();
        } catch (Exception e) {
            System.exit(0);
        }        
                
        
    }
    
    private class Listener implements Runnable {
    
        private ObjectInputStream in;

        public Listener(Socket socket) {
            try {
                this.in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void run() {
            DataPackage dataPackage = null;
            try {
                while ((dataPackage = (DataPackage) in.readObject()) != null) {
                    Command commands = dataPackage.getAction();
                    switch (commands) {
                        case CONNECTED:
                            receiveStatus(dataPackage);
                            break;
                        case DISCONNECTED:
                            disconnected();
                            break;
                        case NOT_CONNECTED:
                            failedConnection();
                            break;                        
                        case RECEIVED:
                            received(dataPackage);
                            break;
                        case SEND_ALL:
                            receiveStatus(dataPackage);
                            break;
                        case USERS_ONLINE:
                            refresh(dataPackage);
                            break;
                        default:
                    }
                }
            } catch (IOException e) {
                disconnected();
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private void connect() throws HeadlessException {
        do {
            this.nickName = showInputDialog(null, "Entre com seu nick-name: ", "", PLAIN_MESSAGE);
        } while (this.nickName.isEmpty());

        super.setTitle("Bate-papo utilizado por " + this.nickName);

        this.service = new ClientService();
        this.socket = this.service.connect();
        new Thread(new Listener(this.socket)).start();

        this.dataPackage = new DataPackage();
        this.dataPackage.setUser(this.nickName);
        this.dataPackage.setMessage(this.nickName + " entrou no bate-papo\n");
        this.dataPackage.setAction(Command.CONNECT);

        this.service.send(this.dataPackage);
    }
      
    private void disconnect() {
        this.dataPackage = new DataPackage();
        this.dataPackage.setUser(this.nickName);
        this.dataPackage.setAction(Command.DISCONNECT);
        this.service.send(this.dataPackage);
    }
    
    private void disconnected() {
        try {
            this.socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());            
        }
    }
    
    private void failedConnection() {
        JOptionPane.showMessageDialog(null, "Conexão Falhou!\nEntre com outro nick-name.", "Falha na Conexão...", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
   
    private void received(DataPackage dataPackage) {
        this.txtReceived.append(dataPackage.getUser() + " diz: " + dataPackage.getMessage() + "\n");
    }
    
    private void receiveStatus(DataPackage dataPackage) {
        this.dataPackage = dataPackage;
        this.txtReceived.append(dataPackage.getMessage());
    }
       
    private void refresh(DataPackage dataPackage) {
        Set<String> users = dataPackage.getUsersOnLine();
        users.remove(dataPackage.getUser());
        String[] array = users.toArray(new String[users.size()]);
        this.lstOnlines.setListData(array);
        this.lstOnlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.lstOnlines.setLayoutOrientation(JList.VERTICAL);        
        System.out.println(dataPackage.getUsersOnLine().toString());
    }

    private void send() {
        String message = this.txtSend.getText();
        if (!message.isEmpty()) {
            this.dataPackage = new DataPackage();
            if (this.lstOnlines.getSelectedIndex() > -1) {
                this.dataPackage.setUserReserved((String) this.lstOnlines.getSelectedValue());
                this.dataPackage.setAction(Command.SEND_RESERVED);
                this.lstOnlines.clearSelection();
            } else {
                this.dataPackage.setAction(Command.SEND_ALL);
            }
            this.dataPackage.setUser(this.nickName);
            this.dataPackage.setMessage(message);
            this.txtReceived.append("Você disse: " + message + "\n");
            this.service.send(this.dataPackage);
        }
        this.txtSend.setText("");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtReceived = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstOnlines = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        btnSend = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSend = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Mensagens:");

        txtReceived.setEditable(false);
        txtReceived.setColumns(20);
        txtReceived.setRows(5);
        jScrollPane1.setViewportView(txtReceived);

        jScrollPane2.setViewportView(lstOnlines);

        jLabel2.setText("On-lines");

        btnSend.setText("Enviar");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        txtSend.setColumns(20);
        txtSend.setRows(5);
        jScrollPane3.setViewportView(txtSend);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(76, 248, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        send();
    }//GEN-LAST:event_btnSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        disconnect();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList lstOnlines;
    private javax.swing.JTextArea txtReceived;
    private javax.swing.JTextArea txtSend;
    // End of variables declaration//GEN-END:variables
    }
