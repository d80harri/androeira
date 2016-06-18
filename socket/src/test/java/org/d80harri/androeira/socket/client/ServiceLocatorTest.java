package org.d80harri.androeira.socket.client;

import org.assertj.core.api.Assertions;
import org.d80harri.androeira.socket.intf.ServiceLocation;
import org.d80harri.androeira.socket.server.Service;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by d80harri on 18.06.16.
 */
public class ServiceLocatorTest {
    private static final long MAX_WAIT_FOR_SERVER_SHOWING_UP = 1000000;

    @Test(timeout = MAX_WAIT_FOR_SERVER_SHOWING_UP)
    public void testAddedListener() throws Throwable {
        final String serviceName = UUID.randomUUID().toString();
        ServiceLocator locator = new ServiceLocator();
        Service service = new Service(serviceName);

        Monitor monitor = new Monitor();

        locator.setServiceAddedListener(loc -> {
            System.out.println(loc.getName());
            if (serviceName.equals(loc.getName())) {
                monitor.serviceReceived(loc);
            }
        });
        locator.start();
        service.start();

        ServiceLocation location = monitor.waitForService();
        Assertions.assertThat(location.getName()).isEqualTo(serviceName);
    }

    private static class Monitor {
        private Object monitor = new Object();
        private ServiceLocation receivedService;

        public ServiceLocation waitForService() throws InterruptedException {
            synchronized (monitor) {
                if (receivedService == null) {
                    monitor.wait();
                }
            }
            return receivedService;
        }

        public void serviceReceived(ServiceLocation loc) {
            this.receivedService = loc;
            if (monitor != null) {
                monitor.notify();
            }
        }
    }
}