package Service;

import Dto.BidUpdateEvent;
import Network.HttpClient;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BidRealtimeService {
    private static final String STREAM_URL = "http://100.64.15.22:8080/api/products";
    private static final long RECONNECT_DELAY_MILLIS = 1000L;
    private static final BidRealtimeService INSTANCE = new BidRealtimeService();

    public interface BidUpdateListener {
        void onBidUpdate(BidUpdateEvent event);
    }

    private final OkHttpClient streamClient = HttpClient.getClient().newBuilder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();
    private final Gson gson = new Gson();
    private final Set<BidUpdateListener> listeners = new CopyOnWriteArraySet<>();
    private final AtomicBoolean started = new AtomicBoolean(false);

    private BidRealtimeService() {
    }

    public static BidRealtimeService getInstance() {
        return INSTANCE;
    }

    public void addListener(BidUpdateListener listener) {
        if (listener == null) {
            return;
        }

        listeners.add(listener);
        startIfNeeded();
    }

    public void removeListener(BidUpdateListener listener) {
        if (listener == null) {
            return;
        }

        listeners.remove(listener);
    }

    private void startIfNeeded() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        Thread streamThread = new Thread(this::listenLoop, "bid-realtime-stream");
        streamThread.setDaemon(true);
        streamThread.start();
    }

    private void listenLoop() {
        while (true) {
            Request request = new Request.Builder()
                    .url(STREAM_URL)
                    .get()
                    .build();

            try (Response response = streamClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    sleepBeforeReconnect();
                    continue;
                }

                readEventStream(response.body());
            } catch (IOException e) {
                sleepBeforeReconnect();
            }
        }
    }

    private void readEventStream(ResponseBody responseBody) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder dataBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    dispatchEvent(dataBuilder);
                    dataBuilder.setLength(0);
                    continue;
                }

                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    if (!data.isEmpty()) {
                        if (dataBuilder.length() > 0) {
                            dataBuilder.append('\n');
                        }
                        dataBuilder.append(data);
                    }
                }
            }

            dispatchEvent(dataBuilder);
        }
    }

    private void dispatchEvent(StringBuilder dataBuilder) {
        if (dataBuilder.isEmpty()) {
            return;
        }

        try {
            BidUpdateEvent event = gson.fromJson(dataBuilder.toString(), BidUpdateEvent.class);
            if (event == null || event.getProductId() == null) {
                return;
            }

            for (BidUpdateListener listener : listeners) {
                listener.onBidUpdate(event);
            }
        } catch (RuntimeException ignored) {
        }
    }

    private void sleepBeforeReconnect() {
        try {
            Thread.sleep(RECONNECT_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
