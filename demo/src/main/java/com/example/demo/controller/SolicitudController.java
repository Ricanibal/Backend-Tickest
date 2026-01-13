package com.example.demo.controller;

import com.example.demo.dto.CreateSolicitudRequest;
import com.example.demo.dto.SolicitudResponse;
import com.example.demo.service.SolicitudService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SolicitudResponse> createMultipart(
            @RequestPart("solicitud") String solicitudJson,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        try {
            CreateSolicitudRequest request = objectMapper.readValue(solicitudJson, CreateSolicitudRequest.class);
            
            if (archivos == null) {
                archivos = new ArrayList<>();
            }
            
            SolicitudResponse response = solicitudService.create(request, archivos);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Error al procesar JSON de la solicitud: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la solicitud: " + e.getMessage(), e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitudResponse> createJson(@Valid @RequestBody CreateSolicitudRequest request) {
        SolicitudResponse response = solicitudService.create(request, new ArrayList<>());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listAll() {
        List<SolicitudResponse> responses = solicitudService.listAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/priorizadas")
    public ResponseEntity<List<SolicitudResponse>> listPrioritized() {
        List<SolicitudResponse> responses = solicitudService.listPrioritized();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<SolicitudResponse>> listOrderedByPriority() {
        List<SolicitudResponse> responses = solicitudService.listOrderedByPriority();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/prueba")
    public ResponseEntity<SolicitudResponse> createWithDate(
            @RequestBody CreateSolicitudRequest request,
            @RequestParam(required = false, defaultValue = "") String fechaCreacion) {
        java.time.LocalDateTime fecha;
        if (fechaCreacion != null && !fechaCreacion.isEmpty()) {
            fecha = java.time.LocalDateTime.parse(fechaCreacion);
        } else {
            fecha = java.time.LocalDateTime.now();
        }
        SolicitudResponse response = solicitudService.createWithDate(request, new ArrayList<>(), fecha);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
