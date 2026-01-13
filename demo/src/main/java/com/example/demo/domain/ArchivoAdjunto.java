package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "archivos_adjuntos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoAdjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreOriginal;

    @Column(nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String ruta;

    @Column(nullable = false)
    private String tipoMime;

    @Column(nullable = false)
    private Long tamaño;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;
}
