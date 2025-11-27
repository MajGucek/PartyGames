package org.PartyGames.Common.Scheduler;


import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class ScheduledServiceController {
    private final int TPS;
    private final List<ServiceWrapper> service_list;

    private record ServiceWrapper(String name, Service service, int TPS) {}

    public ScheduledServiceController(int TPS) {
        this.TPS = TPS;
        service_list = new LinkedList<>();
    }

    public interface Service {
        void execute();
    }

    public void addService(String name, Service service, int tps) {
        service_list.add(new ServiceWrapper(name, service, tps));
    }
    public void addService(Service service, int tps) {
        service_list.add(new ServiceWrapper("", service, tps));
    }
    public void removeService(String name) {
        if (Objects.equals(name, "")) {
            throw new IllegalArgumentException("Cannot remove Service with no name!");
        }
        service_list.removeIf((service) -> service.name.equals(name));
    }

    public void executeServices(int tick) {
        service_list.forEach((service) -> {
            if (tick % (TPS / service.TPS) == 0) {
                service.service.execute();
            }});
    }
}

