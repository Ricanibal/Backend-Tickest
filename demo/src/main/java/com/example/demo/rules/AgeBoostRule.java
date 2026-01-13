package com.example.demo.rules;

import com.example.demo.domain.Solicitud;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class AgeBoostRule implements PriorityRule {

    private static final int HOURS_PER_PRIORITY_BOOST = 48;

    @Override
    public int apply(Solicitud solicitud, LocalDateTime now) {
        long hoursSinceCreation = Duration.between(solicitud.getFechaCreacion(), now).toHours();
        return (int) (hoursSinceCreation / HOURS_PER_PRIORITY_BOOST);
    }

    @Override
    public String name() {
        return "AgeBoostRule";
    }
}
