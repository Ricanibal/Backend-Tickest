package com.example.demo.rules;

import com.example.demo.domain.Solicitud;

import java.time.LocalDateTime;

public interface PriorityRule {
    int apply(Solicitud solicitud, LocalDateTime now);
    String name();
}
