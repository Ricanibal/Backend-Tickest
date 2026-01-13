package com.example.demo.dto;

import com.example.demo.domain.NivelPrioridad;
import com.example.demo.domain.TipoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponse {

    private Long id;
    private TipoSolicitud tipo;
    private NivelPrioridad nivelPrioridad;
    private LocalDateTime fechaCreacion;
    private String descripcion;
    private UsuarioResumen usuario;
    private Integer prioridadCalculada;
    private java.util.List<ArchivoInfo> archivos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioResumen {
        private Long id;
        private String nombre;
        private String correo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArchivoInfo {
        private Long id;
        private String nombreOriginal;
        private String nombreArchivo;
        private String url;
        private String tipoMime;
        private Long tamaño;
    }
}
