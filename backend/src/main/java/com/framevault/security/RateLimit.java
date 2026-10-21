package com.framevault.security;

import java.util.concurrent.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RateLimit {

  private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

  private static class Window {

    long start = System.currentTimeMillis();

    int count;
  }

  public void check(String key, int max, long duration) {
    long now = System.currentTimeMillis();
    if (windows.size() > 10000)
      windows.entrySet().removeIf(e -> now - e.getValue().start > 3600000);
    Window w = windows.computeIfAbsent(key, k -> new Window());
    synchronized (w) {
      if (now - w.start > duration) {
        w.start = now;
        w.count = 0;
      }
      if (++w.count > max)
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Thử lại sau ít phút");
    }
  }
}
