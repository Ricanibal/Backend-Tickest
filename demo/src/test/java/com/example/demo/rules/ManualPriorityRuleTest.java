package com.example.demo.rules;

import com.example.demo.domain.Solicitud;
import com.example.demo.domain.TipoSolicitud;
import com.example.demo.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManualPriorityRuleTest {

    private ManualPriorityRule rule;
    private Solicitud solicitud;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        rule = new ManualPriorityRule();
        now = LocalDateTime.now();
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@example.com");
        
        solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setTipo(TipoSolicitud.INCIDENTE);
        solicitud.setFechaCreacion(now);
    }

    @Test
    void testApply_Priority1_Returns1() {
        solicitud.setPrioridadManual(1);
        int result = rule.apply(solicitud, now);
        assertEquals(1, result);
    }

    @Test
    void testApply_Priority3_Returns3() {
        solicitud.setPrioridadManual(3);
        int result = rule.apply(solicitud, now);
        assertEquals(3, result);
    }

    @Test
    void testApply_Priority5_Returns5() {
        solicitud.setPrioridadManual(5);
        int result = rule.apply(solicitud, now);
        assertEquals(5, result);
    }

    @Test
    void testName_ReturnsManualPriorityRule() {
        assertEquals("ManualPriorityRule", rule.name());
    }
}
