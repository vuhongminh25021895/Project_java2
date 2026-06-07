package WebSocket;

import Dto.Response.BidResponse;
import Observer.BidObserver;

import java.util.ArrayList;
import java.util.List;

public class BidNotifier {
    private static final BidNotifier INSTANCE =
            new BidNotifier();

    private final List<BidObserver> observers =
            new ArrayList<>();

    private BidNotifier() {
    }

    public static BidNotifier getInstance() {
        return INSTANCE;
    }

    public void addObserver(
            BidObserver observer
    ) {
        observers.add(observer);
    }

    public void removeObserver(
            BidObserver observer
    ) {
        observers.remove(observer);
    }

    public void notifyObservers(
            BidResponse response
    ) {

        for (BidObserver observer : observers) {

            observer.onNewBid(response);

        }
    }
}
