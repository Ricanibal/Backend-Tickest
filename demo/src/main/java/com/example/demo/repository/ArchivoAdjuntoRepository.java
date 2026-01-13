package com.example.demo.repository;

import com.example.demo.domain.ArchivoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoAdjuntoRepository extends JpaRepository<ArchivoAdjunto, Long> {
    List<ArchivoAdjunto> findBySolicitudId(Long solicitudId);
}
