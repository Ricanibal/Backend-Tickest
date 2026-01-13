package com.example.demo.dto;

import com.example.demo.domain.NivelPrioridad;
import com.example.demo.domain.TipoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSolicitudRequest {

    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipo;

    @NotNull(message = "El nivel de prioridad es obligatorio")
    private NivelPrioridad nivelPrioridad;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    private String descripcion;
}
