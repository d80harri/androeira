package d80harri.org.app.socket;

import java.net.InetAddress;

/**
 * Created by d80harri on 17.06.16.
 */
public class ServiceLocation {
    private int port;
    private InetAddress address;

    public ServiceLocation(int port, InetAddress address) {
        this.port = port;
        this.address = address;
    }

    public InetAddress getAddress() {

        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
