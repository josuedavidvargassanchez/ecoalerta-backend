package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.ReporteForm;
import com.ecoalerta.backend.dto.ReporteResponse;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Ciudadano;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.models.Reporte;
import com.ecoalerta.backend.repositories.CiudadanoRepository;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import com.ecoalerta.backend.repositories.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReporteService {

    public static final int DIAS_VENCIMIENTO = 15;
    public static final int MAX_ABIERTOS_POR_CIUDADANO = 3;

    private final ReporteRepository reporteRepository;
    private final PuntoCriticoRepository puntoRepository;
    private final CiudadanoRepository ciudadanoRepository;

    public ReporteService(ReporteRepository reporteRepository,
                          PuntoCriticoRepository puntoRepository,
                          CiudadanoRepository ciudadanoRepository) {
        this.reporteRepository = reporteRepository;
        this.puntoRepository = puntoRepository;
        this.ciudadanoRepository = ciudadanoRepository;
    }


    public static LocalDate limiteVencimiento() {
        return LocalDate.now().minusDays(DIAS_VENCIMIENTO);
    }

    public void crear(ReporteForm form) {
        PuntoCritico punto = puntoRepository.findById(form.getPuntoId())
                .orElseThrow(() -> new NoSuchElementException("No existe el punto crítico " + form.getPuntoId()));
        if (punto.getEstado() == EstadoPunto.RESUELTO) {
            throw new ReglaNegocioException("El punto ya está resuelto y no admite nuevos reportes");
        }
        Ciudadano ciudadano = ciudadanoRepository.findById(form.getCiudadanoId())
                .orElseThrow(() -> new NoSuchElementException("No existe el ciudadano " + form.getCiudadanoId()));
        if (reporteRepository.countByCiudadanoIdAndCerradoFalse(ciudadano.getId()) >= MAX_ABIERTOS_POR_CIUDADANO) {
            throw new ReglaNegocioException("El ciudadano ya tiene " + MAX_ABIERTOS_POR_CIUDADANO
                    + " reportes abiertos; debe cerrar alguno antes de crear otro");
        }
        Reporte reporte = new Reporte();
        reporte.setDescripcion(form.getDescripcion().trim());
        reporte.setPunto(punto);
        reporte.setCiudadano(ciudadano);
        reporteRepository.save(reporte);
    }

    public void cerrar(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el reporte " + id));
        if (reporte.isCerrado()) {
            throw new ReglaNegocioException("El reporte ya estaba cerrado");
        }
        reporte.setCerrado(true);
        reporte.setFechaCierre(LocalDate.now()); // sin save: JPA detecta el cambio
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> listar(String filtro) {
        List<Reporte> reportes = switch (filtro == null ? "abiertos" : filtro) {
            case "vencidos" -> reporteRepository.vencidos(limiteVencimiento());
            case "cerrados" -> reporteRepository.findByCerradoTrueOrderByFechaCierreDesc();
            default -> reporteRepository.findByCerradoFalseOrderByFechaAsc();
        };
        return aRespuestas(reportes);
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> dePunto(Long puntoId) {
        return aRespuestas(reporteRepository.findByPuntoIdOrderByFechaDesc(puntoId));
    }

    @Transactional(readOnly = true)
    public List<ReporteResponse> deCiudadano(Long ciudadanoId) {
        return aRespuestas(reporteRepository.findByCiudadanoIdOrderByFechaDesc(ciudadanoId));
    }

    /** Listado de reportes vencidos en formato CSV (se abre bien en Excel). */
    @Transactional(readOnly = true)
    public String exportarVencidosCsv() {
        StringBuilder csv = new StringBuilder("\uFEFFid,punto,ciudadano,fecha,descripcion\n");
        for (ReporteResponse r : listar("vencidos")) {
            csv.append(r.id()).append(',')
               .append(celda(r.puntoNombre())).append(',')
               .append(celda(r.ciudadanoNombre())).append(',')
               .append(r.fecha()).append(',')
               .append(celda(r.descripcion())).append('\n');
        }
        return csv.toString();
    }
    private List<ReporteResponse> aRespuestas(List<Reporte> reportes) {
        LocalDate limite = limiteVencimiento();
        return reportes.stream().map(r -> new ReporteResponse(
                r.getId(),
                r.getDescripcion(),
                r.getFecha(),
                r.isCerrado(),
                r.getFechaCierre(),
                !r.isCerrado() && r.getFecha().isBefore(limite),
                r.getPunto().getId(),
                r.getPunto().getNombre(),
                r.getCiudadano().getId(),
                r.getCiudadano().getNombre())).toList();
    }

    private String celda(String valor) {
        return "\"" + (valor == null ? "" : valor.replace("\"", "\"\"")) + "\"";
    }
}
