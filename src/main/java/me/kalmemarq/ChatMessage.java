package me.kalmemarq;

import java.time.Instant;

public record ChatMessage(String message, Instant timestamp) {
}
