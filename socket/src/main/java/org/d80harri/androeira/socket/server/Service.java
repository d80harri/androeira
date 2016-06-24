package org.d80harri.androeira.socket.server;

import org.apache.log4j.Logger;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by d80harri on 17.06.16.
 */
public class Service {
    private static final Logger logger = Logger.getLogger(Service.class);

    private static final String SERVICE_TYPE = "_http._tcp.local.";
    private static final String SERVICE_NAME = "androeira_acc";

    private final String description;

    private JmDNS jmdns;
    private ServerSocket serverSocket;
    protected Map<Socket, SocketContext> sockets = new HashMap<>();
    private boolean started = false;
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    public Service() {
        this.description = "Androeira Communication Socket";
    }


    public Service(String description) {
        this.description = description;
    }

    public void start() throws IOException {
        jmdns = JmDNS.create();
        serverSocket = new ServerSocket(0);

        ServiceInfo info = ServiceInfo.create(SERVICE_TYPE, SERVICE_NAME, serverSocket.getLocalPort(), description);
        jmdns.registerService(info);

        started = true;
        executor.submit(this::socketLoop);
    }

    public void stop() throws IOException {
        started = false;
        jmdns.unregisterAllServices();
        jmdns.close();
        serverSocket.close();
        closeSockets();
    }

    private void closeSockets() throws IOException {
        this.sockets.keySet().forEach(Service::closeAll);
    }

    private static void closeAll(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    private void socketLoop() {
        while (started) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                this.sockets.put(socket, new SocketContext(socket));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void post(AcceloratorRawData acceloratorRawData) throws IOException {
        for (SocketContext socketContext : sockets.values()) {
                ObjectOutputStream oos = socketContext.getOos();
            try {
                oos.writeObject(acceloratorRawData);
            } catch (SocketException ex) {
                this.sockets.remove(socketContext.socket);
            }
        }
    }

    private class SocketContext {
        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;

        public SocketContext(Socket socket) {
            this.socket = socket;
        }

        public ObjectOutputStream getOos() throws IOException {
            if (oos == null) {
                oos = new ObjectOutputStream(socket.getOutputStream());
            }
            return oos;
        }
    }


    public static void main(String[] args) throws Throwable {
        Service consoleService = new Service("Console Service");
        consoleService.start();

        AcceloratorRawData data = new AcceloratorRawData(-1, 0, 0, 0);
        Random random = new Random();
        for (;;){
            data.setTimestamp(System.nanoTime());
            data.setX(data.getX() + (float)(random.nextGaussian()*2f));
            data.setY(data.getY() + (float)(random.nextGaussian()*2f));
            data.setZ(data.getZ() + (float)(random.nextGaussian()*2f));
            consoleService.post(data);
            Thread.sleep(100);
        }
    }
}
