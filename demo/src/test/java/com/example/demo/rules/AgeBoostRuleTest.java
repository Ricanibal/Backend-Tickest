package com.example.demo.rules;

import com.example.demo.domain.Solicitud;
import com.example.demo.domain.TipoSolicitud;
import com.example.demo.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgeBoostRuleTest {

    private AgeBoostRule rule;
    private Solicitud solicitud;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        rule = new AgeBoostRule();
        now = LocalDateTime.now();
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@example.com");
        
        solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setTipo(TipoSolicitud.INCIDENTE);
        solicitud.setPrioridadManual(3);
    }

    @Test
    void testApply_NewSolicitud_Returns0() {
        solicitud.setFechaCreacion(now);
        int result = rule.apply(solicitud, now);
        assertEquals(0, result);
    }

    @Test
    void testApply_24HoursOld_Returns0() {
        solicitud.setFechaCreacion(now.minusHours(24));
        int result = rule.apply(solicitud, now);
        assertEquals(0, result); // Menos de 48 horas
    }

    @Test
    void testApply_48HoursOld_Returns1() {
        solicitud.setFechaCreacion(now.minusHours(48));
        int result = rule.apply(solicitud, now);
        assertEquals(1, result); // Exactamente 48 horas = 1 boost
    }

    @Test
    void testApply_96HoursOld_Returns2() {
        solicitud.setFechaCreacion(now.minusHours(96));
        int result = rule.apply(solicitud, now);
        assertEquals(2, result); // 96 horas = 2 * 48 = 2 boosts
    }

    @Test
    void testApply_100HoursOld_Returns2() {
        solicitud.setFechaCreacion(now.minusHours(100));
        int result = rule.apply(solicitud, now);
        assertEquals(2, result); // 100 / 48 = 2.08, truncado a 2
    }

    @Test
    void testName_ReturnsAgeBoostRule() {
        assertEquals("AgeBoostRule", rule.name());
    }
}
