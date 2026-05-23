package Model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Entity {
    private String id = UUID.randomUUID().toString();;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    public abstract String printInfo();

    public String getId() {
        return id;
    }
}
