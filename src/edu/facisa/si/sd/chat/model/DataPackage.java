package edu.facisa.si.sd.chat.model;

import edu.facisa.si.sd.chat.enumarator.Command;
import java.io.*;
import java.util.*;

/**
 *
 * @author Washington Soares
 */
public class DataPackage implements Serializable {
    
    private String user;
    private String userReserved;
    private Set<String> usersOnLine;
    private String message;    
    private Command action;    

    public DataPackage() {
        this.usersOnLine = new HashSet<String>();
    }    

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserReserved() {
        return userReserved;
    }

    public void setUserReserved(String userReserved) {
        this.userReserved = userReserved;
    }

    public Set<String> getUsersOnLine() {
        return usersOnLine;
    }

    public void setUsersOnLine(Set<String> usersOnLine) {
        this.usersOnLine = usersOnLine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Command getAction() {
        return action;
    }

    public void setAction(Command action) {
        this.action = action;
    }
    
}
