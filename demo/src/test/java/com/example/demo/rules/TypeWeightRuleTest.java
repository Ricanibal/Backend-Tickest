package com.example.demo.rules;

import com.example.demo.domain.Solicitud;
import com.example.demo.domain.TipoSolicitud;
import com.example.demo.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeWeightRuleTest {

    private TypeWeightRule rule;
    private Solicitud solicitud;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        rule = new TypeWeightRule();
        now = LocalDateTime.now();
        
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@example.com");
        
        solicitud = new Solicitud();
        solicitud.setUsuario(usuario);
        solicitud.setPrioridadManual(3);
        solicitud.setFechaCreacion(now);
    }

    @Test
    void testApply_Incidente_Returns5() {
        solicitud.setTipo(TipoSolicitud.INCIDENTE);
        int result = rule.apply(solicitud, now);
        assertEquals(5, result);
    }

    @Test
    void testApply_Requerimiento_Returns3() {
        solicitud.setTipo(TipoSolicitud.REQUERIMIENTO);
        int result = rule.apply(solicitud, now);
        assertEquals(3, result);
    }

    @Test
    void testApply_Consulta_Returns1() {
        solicitud.setTipo(TipoSolicitud.CONSULTA);
        int result = rule.apply(solicitud, now);
        assertEquals(1, result);
    }

    @Test
    void testName_ReturnsTypeWeightRule() {
        assertEquals("TypeWeightRule", rule.name());
    }
}
