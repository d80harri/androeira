package org.d80harri.androeira.analyzer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by d80harri on 17.06.16.
 */
public class ConsumerService {
    private static final String SERVICE_TYPE = "_http._tcp.local.";
    private static final String SERVICE_NAME = "androeira_acc";

    private JmDNS jmdns;
    private ServerSocket serverSocket;
    private boolean started = false;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Consumer<AcceloratorRawData> subscriber;

    public void start() throws IOException {
        jmdns = JmDNS.create();
        serverSocket = new ServerSocket(0);

        ServiceInfo info = ServiceInfo.create(SERVICE_TYPE, SERVICE_NAME, serverSocket.getLocalPort(), "Androeira consumer service");
        jmdns.registerService(info);

        started = true;
        executor.submit(this::socketLoop);
    }

    public void stop() throws IOException {
        jmdns.unregisterAllServices();
        jmdns.close();
        serverSocket.close();
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

    public static void main(String[] args) throws IOException {
        ConsumerService service = new ConsumerService();
        service.start();
        System.out.println("Startee");
    }
}
