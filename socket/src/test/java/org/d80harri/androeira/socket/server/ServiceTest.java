package org.d80harri.androeira.socket.server;

import org.assertj.core.api.Assertions;
import org.d80harri.androeira.socket.client.Client;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by d80harri on 22.06.16.
 */
public class ServiceTest {

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
    public void stopClient() throws Throwable {
        Assertions.assertThat(service.sockets.keySet()).hasSize(1);

        client.closeConnection();
        service.post(new AcceloratorRawData(0, 1, 2, 3)); // shall not throw exception

        Assertions.assertThat(service.sockets.keySet()).isEmpty();
    }

}
