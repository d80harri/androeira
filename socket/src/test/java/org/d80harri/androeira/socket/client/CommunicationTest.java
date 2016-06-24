package org.d80harri.androeira.socket.client;

import org.assertj.core.api.Assertions;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.server.Service;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by d80harri on 20.06.16.
 */
public class CommunicationTest {
    private static final long MAX_WAIT_TIMEOUT = 15000;

    private Service service;
    private Client client;

    @Before
    public void init() throws IOException {
        service = new Service();
        service.start();
        client = new Client(service.getInetAddress(), service.getLocalPort());
        client.openConnection();
    }

    @Test(timeout = MAX_WAIT_TIMEOUT)
    public void stopServer() throws Throwable {
        service.stop();
        AcceloratorRawData data = client.read(); // shall not throw exception

        Assertions.assertThat(data).isNull();
        Assertions.assertThat(client.isConnectionOpen()).isFalse();
    }


    @Test(timeout = MAX_WAIT_TIMEOUT)
    public void readData() throws Throwable {
        AcceloratorRawData read;

        service.post(new AcceloratorRawData(0, 1, 2, 3));
        read = client.read();
        Assertions.assertThat(read.getTimestamp()).isEqualTo(0);

        service.post(new AcceloratorRawData(10, 1, 2, 3));
        read = client.read();
        Assertions.assertThat(read.getTimestamp()).isEqualTo(10);
    }
}
