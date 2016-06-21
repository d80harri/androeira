package org.d80harri.androeira.socket.client;


import org.d80harri.androeira.socket.intf.ServiceLocation;

import javax.jmdns.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by d80harri on 17.06.16.
 */
public class ServiceLocator implements ServiceListener {
    private static final String SERVICE_TYPE = "_http._tcp.local.";
    private static final String SERVICE_NAME = "androeira_acc";

    private JmDNS jmdns;
    private List<ServiceLocation> serviceLocations = new ArrayList<>();
    private Consumer serviceAddedListener;
    private Consumer serviceRemovedListener;

    public void setServiceAddedListener(Consumer serviceAddedListener) {
        this.serviceAddedListener = serviceAddedListener;
    }

    public void setServiceRemovedListener(Consumer serviceRemovedListener) {
        this.serviceRemovedListener = serviceRemovedListener;
    }

    public void start() throws IOException {
        jmdns = JmDNS.create();
        Arrays.stream(jmdns.list(SERVICE_TYPE)).forEach(this::addServiceInfo);
        jmdns.addServiceListener(SERVICE_TYPE, this);
    }

    public void stop() throws IOException {
        jmdns.removeServiceListener(SERVICE_TYPE, this);
        jmdns.close();
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        if (SERVICE_NAME.equals(event.getName())){
            jmdns.requestServiceInfo(SERVICE_TYPE, SERVICE_NAME);
        }

    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        ServiceLocation serviceLocation = new ServiceLocation(event.getInfo().getNiceTextString(), event.getInfo().getPort(), event.getInfo().getInetAddresses()[0]);
        serviceLocations.remove(serviceLocation);
        if (serviceRemovedListener != null){
            serviceRemovedListener.consume(serviceLocation);
        }
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        addServiceInfo(event.getInfo());
    }

    private void addServiceInfo(ServiceInfo info) {
        ServiceLocation serviceLocation = new ServiceLocation(info.getNiceTextString(), info.getPort(), info.getInetAddress());
        addServiceLocation(serviceLocation);
    }
    private void addServiceLocation(ServiceLocation serviceLocation) {
        serviceLocations.add(serviceLocation);
        if (serviceAddedListener != null) {
            serviceAddedListener.consume(serviceLocation);
        }
    }

    public static void main(String[] args) throws IOException {
        ServiceLocator serviceLocator = new ServiceLocator();
        serviceLocator.start();;
    }

    public interface Consumer {
        void consume(ServiceLocation location);
    }
}
