package org.d80harri.androeira.socket.server;

import org.d80harri.androeira.socket.intf.AcceloratorRawData;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by d80harri on 17.06.16.
 */
public class Service {
    private static final String SERVICE_TYPE = "_http._tcp.local.";
    private static final String SERVICE_NAME = "androeira_acc";

    private final String description;

    private JmDNS jmdns;
    private ServerSocket serverSocket;
    private boolean started = false;
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    private Consumer<AcceloratorRawData> subscriber;

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
    }

    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    public void setSubscriber(Consumer<AcceloratorRawData> subscriber) {
        this.subscriber = subscriber;
    }

    private void socketLoop() {
        while (started) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                executor.submit(() -> processStream(is));
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        }
    }

    private void processStream(ObjectInputStream is) {
        try {
            AcceloratorRawData data = (AcceloratorRawData) is.readObject();
            if (subscriber != null) {
                subscriber.accept(data);
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // TODO
        }
    }

    public static void main(String[] args) {
        Service consoleService = new Service("Console Service");
        consoleService.setSubscriber(data -> System.out.print(data));
    }
}
