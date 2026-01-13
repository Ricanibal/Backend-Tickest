package com.example.demo.rules;

import com.example.demo.domain.NivelPrioridad;
import com.example.demo.domain.Solicitud;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ManualPriorityRule implements PriorityRule {

    private static final Map<NivelPrioridad, Integer> PRIORITY_VALUES = Map.of(
            NivelPrioridad.BAJA, 1,
            NivelPrioridad.MEDIA, 2,
            NivelPrioridad.ALTA, 4,
            NivelPrioridad.URGENCIA, 5
    );

    @Override
    public int apply(Solicitud solicitud, LocalDateTime now) {
        return PRIORITY_VALUES.getOrDefault(solicitud.getNivelPrioridad(), 1);
    }

    @Override
    public String name() {
        return "ManualPriorityRule";
    }
}
