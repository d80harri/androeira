package org.d80harri.androeira.socket.client;

import org.assertj.core.api.Assertions;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.server.Service;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by d80harri on 20.06.16.
 */
public class CommunicationTest {
    private static final long MAX_WAIT_TIMEOUT = 50000;

    private Service service;
    private Client client;

    @Before
    public void init() throws IOException {
        service = new Service();
        service.start();
        client = new Client(service.getInetAddress(), service.getLocalPort());
        client.openConnection();
    }

    @Test
    public void writeData() throws Throwable {
        AcceloratorRawData data = new AcceloratorRawData(0, 1, 2, 3);
        CompletableFuture<AcceloratorRawData> futureData = new CompletableFuture<>();

        service.setSubscriber(futureData::complete);

        client.post(data);

        AcceloratorRawData acceloratorRawData = futureData.get(MAX_WAIT_TIMEOUT, TimeUnit.MILLISECONDS);

        Assertions.assertThat(acceloratorRawData.getTimestamp()).isEqualTo(0);
        Assertions.assertThat(acceloratorRawData.getX()).isEqualTo(1);
        Assertions.assertThat(acceloratorRawData.getY()).isEqualTo(2);
        Assertions.assertThat(acceloratorRawData.getZ()).isEqualTo(3);
    }

    @Test
    public void stopClient() throws Throwable {
        client.closeConnection();
        service.post(new AcceloratorRawData(0, 1, 2, 3)); // shall not throw exception
    }

    @Test
    public void stopServer() throws Throwable {
        service.stop();
        client.read(); // shall not throw exception
    }
}
