package org.d80harri.androeira.socket.intf;

import java.net.InetAddress;

/**
 * Created by d80harri on 17.06.16.
 */
public class ServiceLocation {
    private final String name;
    private final int port;
    private final InetAddress address;

    public ServiceLocation(String name, int port, InetAddress address) {
        this.name = name;
        this.port = port;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceLocation that = (ServiceLocation) o;

        if (port != that.port) return false;
        return address.equals(that.address);

    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + address.hashCode();
        return result;
    }
}
