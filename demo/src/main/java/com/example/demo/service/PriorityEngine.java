package com.example.demo.service;

import com.example.demo.domain.Solicitud;
import com.example.demo.rules.PriorityRule;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriorityEngine {

    private final List<PriorityRule> rules;

    public PriorityEngine(List<PriorityRule> rules) {
        this.rules = rules;
    }

    public int calculateScore(Solicitud solicitud, LocalDateTime now) {
        return rules.stream()
                .mapToInt(rule -> rule.apply(solicitud, now))
                .sum();
    }
}
