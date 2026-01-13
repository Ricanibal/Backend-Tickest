package com.example.demo.service;

import com.example.demo.domain.Solicitud;
import com.example.demo.domain.TipoSolicitud;
import com.example.demo.domain.Usuario;
import com.example.demo.rules.AgeBoostRule;
import com.example.demo.rules.ManualPriorityRule;
import com.example.demo.rules.PriorityRule;
import com.example.demo.rules.TypeWeightRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriorityEngineTest {

    private PriorityEngine priorityEngine;
    private Solicitud solicitud;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        List<PriorityRule> rules = Arrays.asList(
                new TypeWeightRule(),
                new ManualPriorityRule(),
                new AgeBoostRule()
        );
        priorityEngine = new PriorityEngine(rules);
        now = LocalDateTime.now();
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@example.com");
        
        solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setTipo(TipoSolicitud.INCIDENTE);
        solicitud.setPrioridadManual(3);
        solicitud.setFechaCreacion(now.minusHours(24));
    }

    @Test
    void testCalculateScore_IncidentePriority3_24HoursOld() {

        int score = priorityEngine.calculateScore(solicitud, now);
        assertEquals(8, score);
    }

    @Test
    void testCalculateScore_ConsultaPriority1_New() {
        solicitud.setTipo(TipoSolicitud.CONSULTA);
        solicitud.setPrioridadManual(1);
        solicitud.setFechaCreacion(now);
        
        int score = priorityEngine.calculateScore(solicitud, now);
        assertEquals(2, score);
    }

    @Test
    void testCalculateScore_RequerimientoPriority5_48HoursOld() {
        solicitud.setTipo(TipoSolicitud.REQUERIMIENTO);
        solicitud.setPrioridadManual(5);
        solicitud.setFechaCreacion(now.minusHours(48));
        
        int score = priorityEngine.calculateScore(solicitud, now);
        assertEquals(9, score);
    }
}
