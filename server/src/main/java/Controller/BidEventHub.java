package Controller;

import Dto.BidUpdateEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BidEventHub {
    private static final BidEventHub INSTANCE = new BidEventHub();
    private static final long SSE_TIMEOUT_MILLIS = 0L;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private BidEventHub() {
    }

    public static BidEventHub getInstance() {
        return INSTANCE;
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            emitter.complete();
        });
        emitter.onError(error -> emitters.remove(emitter));

        return emitter;
    }

    public void publish(BidUpdateEvent event) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("bid-update").data(event));
            } catch (IOException e) {
                emitters.remove(emitter);
                emitter.completeWithError(e);
            }
        }
    }
}
