package com.example.springRest.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String username) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(username, emitter);

        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(()    -> emitters.remove(username));
        emitter.onError(e       -> emitters.remove(username));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(username);
        }

        return emitter;
    }

    public void sendNotificationCount(String username, long count) {
        SseEmitter emitter = emitters.get(username);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name("notification-count")
                    .data(count));
        } catch (IOException e) {
            emitters.remove(username);
        }
    }
}
