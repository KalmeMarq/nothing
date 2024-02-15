package me.kalmemarq.common;

import java.time.Instant;

public record ChatMessage(String message, Instant timestamp) {
}
