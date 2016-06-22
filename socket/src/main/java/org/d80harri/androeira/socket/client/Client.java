package org.d80harri.androeira.socket.client;

import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.intf.ServiceLocation;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by d80harri on 20.06.16.
 */
public class Client {
    private InetAddress inetAddress;
    private int port;
    private Socket socket;

    public Client(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public void openConnection() throws IOException {
        socket = new Socket(inetAddress, port);
    }

    public void post(AcceloratorRawData rawData) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(rawData);
    }

    public boolean isConnectionOpen() {
        return socket != null;
    }

    public AcceloratorRawData read() throws IOException {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (EOFException ex) {
            socket = null;
            return null;
        }
        try {
            return (AcceloratorRawData) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("NYI");
        }
    }

    public void closeConnection() throws IOException {
        this.socket.close();
    }
}
