package com.example.demo.rules;

import com.example.demo.domain.Solicitud;
import com.example.demo.domain.TipoSolicitud;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class TypeWeightRule implements PriorityRule {

    private static final Map<TipoSolicitud, Integer> WEIGHTS = Map.of(
            TipoSolicitud.INCIDENTE, 5,
            TipoSolicitud.REQUERIMIENTO, 3,
            TipoSolicitud.CONSULTA, 1
    );

    @Override
    public int apply(Solicitud solicitud, LocalDateTime now) {
        return WEIGHTS.getOrDefault(solicitud.getTipo(), 0);
    }

    @Override
    public String name() {
        return "TypeWeightRule";
    }
}
