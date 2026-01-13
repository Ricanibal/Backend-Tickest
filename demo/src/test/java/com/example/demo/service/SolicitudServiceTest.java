package com.example.demo.service;

import com.example.demo.domain.Solicitud;
import com.example.demo.domain.TipoSolicitud;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.CreateSolicitudRequest;
import com.example.demo.dto.SolicitudResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.SolicitudRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.rules.AgeBoostRule;
import com.example.demo.rules.ManualPriorityRule;
import com.example.demo.rules.PriorityRule;
import com.example.demo.rules.TypeWeightRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PriorityEngine priorityEngine;

    @InjectMocks
    private SolicitudService solicitudService;

    private Usuario usuario;
    private Solicitud solicitud;
    private CreateSolicitudRequest request;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setCorreo("test@example.com");
        usuario.setTelefono("123456789");

        solicitud = new Solicitud();
        solicitud.setId(1L);
        solicitud.setTipo(TipoSolicitud.INCIDENTE);
        solicitud.setPrioridadManual(3);
        solicitud.setUsuario(usuario);
        solicitud.setFechaCreacion(LocalDateTime.now());

        request = new CreateSolicitudRequest();
        request.setTipo(TipoSolicitud.INCIDENTE);
        request.setPrioridadManual(3);
        request.setUsuarioId(1L);
    }

    @Test
    void testCreate_Success() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        SolicitudResponse response = solicitudService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(TipoSolicitud.INCIDENTE, response.getTipo());
        assertEquals(3, response.getPrioridadManual());
        assertNotNull(response.getUsuario());
        assertEquals(1L, response.getUsuario().getId());
        assertEquals("Test User", response.getUsuario().getNombre());
        assertEquals("test@example.com", response.getUsuario().getCorreo());

        verify(usuarioRepository).findById(1L);
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    @Test
    void testCreate_UsuarioNotFound_ThrowsException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> solicitudService.create(request));
        verify(usuarioRepository).findById(1L);
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void testListAll_Success() {
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);
        solicitud2.setTipo(TipoSolicitud.CONSULTA);
        solicitud2.setPrioridadManual(2);
        solicitud2.setUsuario(usuario);
        solicitud2.setFechaCreacion(LocalDateTime.now());

        when(solicitudRepository.findAll()).thenReturn(Arrays.asList(solicitud, solicitud2));

        List<SolicitudResponse> responses = solicitudService.listAll();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(solicitudRepository).findAll();
    }

    @Test
    void testListPrioritized_Success() {
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);
        solicitud2.setTipo(TipoSolicitud.CONSULTA);
        solicitud2.setPrioridadManual(2);
        solicitud2.setUsuario(usuario);
        solicitud2.setFechaCreacion(LocalDateTime.now().minusHours(1));

        when(solicitudRepository.findAll()).thenReturn(Arrays.asList(solicitud, solicitud2));
        when(priorityEngine.calculateScore(eq(solicitud), any(LocalDateTime.class))).thenReturn(100);
        when(priorityEngine.calculateScore(eq(solicitud2), any(LocalDateTime.class))).thenReturn(50);

        List<SolicitudResponse> responses = solicitudService.listPrioritized();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(100, responses.get(0).getPrioridadCalculada());
        assertEquals(50, responses.get(1).getPrioridadCalculada());
        verify(solicitudRepository).findAll();
    }
}
