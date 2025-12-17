package org.PartyGames.Common.Scheduler;


import javax.naming.NameNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/** Class for executing tasks at fixed Intervals */
@SuppressWarnings("unused")
public class ScheduledServiceController {
    private final List<ServiceWrapper> service_list;
    private boolean initialized;
    private int TPS;

    private record ServiceWrapper(String name, Service service, int TPS) { }

    public ScheduledServiceController() {
        this.TPS = 0;
        service_list = new LinkedList<>();
        this.initialized = false;
    }
    /** DO NOT call this method in Constructor, since by then the object creation hasn't been finalized.
     * @param tps The tick-rate of the process.
     * @throws IllegalCallerException Called with tps == 0. */
    public void start(int tps) {
        if (tps == 0) {
            throw new IllegalCallerException(
                    "Please do NOT call start() method in constructor, The GameController data has not been finalized!"
            );
        }
        this.initialized = true;
        this.TPS = tps;
    }
    /** Lambda expression wrapper. */
    public interface Service {
        void execute();
    }
    /**
     * @param name The name of the service, usefull for removing it later.
     * @param service Lambda expression.
     * @param tps How many times per second to execute the service at.
     * */
    public void addService(String name, Service service, int tps) {
        service_list.add(new ServiceWrapper(name, service, tps));
    }
    /** Shorthand if you don't care about the name of the service. */
    public void addService(Service service, int tps) {
        addService("", service, tps);
    }
    /** Tries to remove service that matches the name.
     * @throws NameNotFoundException If name not found. */
    public void removeService(String name) throws NameNotFoundException {
        if (Objects.equals(name, "")) {
            throw new NameNotFoundException("Cannot remove Service with no name!");
        }
        service_list.removeIf(service -> service.name.equals(name));
    }

    /** Should be called somewhere inside the process loop.
     * @param tick The current tick the process is in.*/
    public void executeServices(int tick) {
        if (!initialized) {
            throw new IllegalStateException("Scheduler not started! Please call .start()");
        }
        service_list.forEach(service -> {
            if (tick % (TPS / service.TPS) == 0) {
                service.service.execute();
            }
        });
    }
}

