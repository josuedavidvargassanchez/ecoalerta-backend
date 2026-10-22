package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.ResumenResponse;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import com.ecoalerta.backend.repositories.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cifras de la pantalla de inicio. */
@Service
@Transactional(readOnly = true)
public class ResumenService {

    private final PuntoCriticoRepository puntoRepository;
    private final ReporteRepository reporteRepository;

    public ResumenService(PuntoCriticoRepository puntoRepository, ReporteRepository reporteRepository) {
        this.puntoRepository = puntoRepository;
        this.reporteRepository = reporteRepository;
    }

    public ResumenResponse obtener() {
        return new ResumenResponse(
                puntoRepository.countByEstado(EstadoPunto.PENDIENTE),
                puntoRepository.countByEstado(EstadoPunto.EN_ATENCION),
                puntoRepository.countByEstado(EstadoPunto.RESUELTO),
                reporteRepository.countByCerradoFalse(),
                reporteRepository.countByCerradoFalseAndFechaBefore(ReporteService.limiteVencimiento()));
    }
}
