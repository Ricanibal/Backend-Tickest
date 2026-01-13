package com.example.demo.service;

import com.example.demo.domain.ArchivoAdjunto;
import com.example.demo.domain.Solicitud;
import com.example.demo.domain.Usuario;
import com.example.demo.dto.CreateSolicitudRequest;
import com.example.demo.dto.SolicitudResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ArchivoAdjuntoRepository;
import com.example.demo.repository.SolicitudRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final PriorityEngine priorityEngine;
    private final FileStorageService fileStorageService;
    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;

    @Transactional
    public SolicitudResponse create(CreateSolicitudRequest request, List<MultipartFile> archivos) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        Solicitud solicitud = new Solicitud();
        solicitud.setTipo(request.getTipo());
        solicitud.setNivelPrioridad(request.getNivelPrioridad());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setUsuario(usuario);

        // Guardar solicitud primero para obtener el ID
        solicitud = solicitudRepository.save(solicitud);

        // Guardar archivos si existen
        if (archivos != null && !archivos.isEmpty()) {
            List<ArchivoAdjunto> archivosAdjuntos = new ArrayList<>();
            for (MultipartFile archivo : archivos) {
                if (!archivo.isEmpty()) {
                    try {
                        String nombreArchivo = fileStorageService.saveFile(archivo, solicitud.getId());
                        
                        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
                        archivoAdjunto.setNombreOriginal(fileStorageService.sanitizeFilename(archivo.getOriginalFilename()));
                        archivoAdjunto.setNombreArchivo(nombreArchivo);
                        archivoAdjunto.setRuta(solicitud.getId() + "/" + nombreArchivo);
                        archivoAdjunto.setTipoMime(archivo.getContentType());
                        archivoAdjunto.setTamaño(archivo.getSize());
                        archivoAdjunto.setSolicitud(solicitud);
                        
                        archivosAdjuntos.add(archivoAdjunto);
                    } catch (IOException e) {
                        throw new RuntimeException("Error al guardar archivo: " + archivo.getOriginalFilename(), e);
                    }
                }
            }
            if (!archivosAdjuntos.isEmpty()) {
                archivoAdjuntoRepository.saveAll(archivosAdjuntos);
                solicitud.setArchivos(archivosAdjuntos);
            }
        }

        return toResponse(solicitud);
    }

    @Transactional
    public SolicitudResponse createWithDate(CreateSolicitudRequest request, List<MultipartFile> archivos, LocalDateTime fechaCreacion) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        Solicitud solicitud = new Solicitud();
        solicitud.setTipo(request.getTipo());
        solicitud.setNivelPrioridad(request.getNivelPrioridad());
        solicitud.setDescripcion(request.getDescripcion());
        solicitud.setUsuario(usuario);
        solicitud.setFechaCreacion(fechaCreacion); // Establecer fecha antes de guardar

        // Guardar solicitud
        solicitud = solicitudRepository.save(solicitud);

        // Guardar archivos si existen
        if (archivos != null && !archivos.isEmpty()) {
            List<ArchivoAdjunto> archivosAdjuntos = new ArrayList<>();
            for (MultipartFile archivo : archivos) {
                if (!archivo.isEmpty()) {
                    try {
                        String nombreArchivo = fileStorageService.saveFile(archivo, solicitud.getId());
                        
                        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
                        archivoAdjunto.setNombreOriginal(fileStorageService.sanitizeFilename(archivo.getOriginalFilename()));
                        archivoAdjunto.setNombreArchivo(nombreArchivo);
                        archivoAdjunto.setRuta(solicitud.getId() + "/" + nombreArchivo);
                        archivoAdjunto.setTipoMime(archivo.getContentType());
                        archivoAdjunto.setTamaño(archivo.getSize());
                        archivoAdjunto.setSolicitud(solicitud);
                        
                        archivosAdjuntos.add(archivoAdjunto);
                    } catch (IOException e) {
                        throw new RuntimeException("Error al guardar archivo: " + archivo.getOriginalFilename(), e);
                    }
                }
            }
            if (!archivosAdjuntos.isEmpty()) {
                archivoAdjuntoRepository.saveAll(archivosAdjuntos);
                solicitud.setArchivos(archivosAdjuntos);
            }
        }

        return toResponse(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listAll() {
        return solicitudRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listPrioritized() {
        LocalDateTime now = LocalDateTime.now();
        
        return solicitudRepository.findAll().stream()
                .map(solicitud -> {
                    int score = priorityEngine.calculateScore(solicitud, now);
                    SolicitudResponse response = toResponse(solicitud);
                    response.setPrioridadCalculada(score);
                    return response;
                })
                .sorted(Comparator
                        .comparing(SolicitudResponse::getPrioridadCalculada, Comparator.reverseOrder())
                        .thenComparing(SolicitudResponse::getFechaCreacion))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponse> listOrderedByPriority() {
        LocalDateTime now = LocalDateTime.now();
        
        return solicitudRepository.findAll().stream()
                .map(solicitud -> {
                    int score = priorityEngine.calculateScore(solicitud, now);
                    SolicitudResponse response = toResponse(solicitud);
                    response.setPrioridadCalculada(score);
                    return response;
                })
                .sorted(Comparator
                        .comparing(SolicitudResponse::getPrioridadCalculada, Comparator.reverseOrder())
                        .thenComparing(SolicitudResponse::getFechaCreacion))
                .collect(Collectors.toList());
    }

    private SolicitudResponse toResponse(Solicitud solicitud) {
        List<SolicitudResponse.ArchivoInfo> archivosInfo = null;
        if (solicitud.getArchivos() != null && !solicitud.getArchivos().isEmpty()) {
            archivosInfo = solicitud.getArchivos().stream()
                    .map(archivo -> SolicitudResponse.ArchivoInfo.builder()
                            .id(archivo.getId())
                            .nombreOriginal(archivo.getNombreOriginal())
                            .nombreArchivo(archivo.getNombreArchivo())
                            .url("/api/archivos/" + solicitud.getId() + "/" + archivo.getNombreArchivo())
                            .tipoMime(archivo.getTipoMime())
                            .tamaño(archivo.getTamaño())
                            .build())
                    .collect(Collectors.toList());
        }

        return SolicitudResponse.builder()
                .id(solicitud.getId())
                .tipo(solicitud.getTipo())
                .nivelPrioridad(solicitud.getNivelPrioridad())
                .fechaCreacion(solicitud.getFechaCreacion())
                .descripcion(solicitud.getDescripcion())
                .archivos(archivosInfo)
                .usuario(SolicitudResponse.UsuarioResumen.builder()
                        .id(solicitud.getUsuario().getId())
                        .nombre(solicitud.getUsuario().getNombre())
                        .correo(solicitud.getUsuario().getCorreo())
                        .build())
                .build();
    }
}
