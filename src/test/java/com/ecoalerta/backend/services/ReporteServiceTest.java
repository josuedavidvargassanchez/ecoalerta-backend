package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.ReporteForm;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.Ciudadano;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.models.Reporte;
import com.ecoalerta.backend.repositories.CiudadanoRepository;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import com.ecoalerta.backend.repositories.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas de las reglas de negocio de los reportes, sin levantar el servidor ni la base de datos. */
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;
    @Mock
    private PuntoCriticoRepository puntoRepository;
    @Mock
    private CiudadanoRepository ciudadanoRepository;

    @InjectMocks
    private ReporteService servicio;

    private ReporteForm formulario() {
        ReporteForm form = new ReporteForm();
        form.setPuntoId(1L);
        form.setCiudadanoId(2L);
        form.setDescripcion("Hay basura acumulada");
        return form;
    }

    private Ciudadano ciudadano() {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setId(2L);
        return ciudadano;
    }

    @Test
    void noPermiteReportarUnPuntoResuelto() {
        PuntoCritico punto = new PuntoCritico("Punto", "Calle 1", "Descripción");
        punto.setEstado(EstadoPunto.RESUELTO);
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(formulario()));
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    @Test
    void noPermiteMasDeTresReportesAbiertosPorCiudadano() {
        PuntoCritico punto = new PuntoCritico("Punto", "Calle 1", "Descripción");
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(ciudadanoRepository.findById(2L)).thenReturn(Optional.of(ciudadano()));
        when(reporteRepository.countByCiudadanoIdAndCerradoFalse(2L)).thenReturn(3L);

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(formulario()));
        verify(reporteRepository, never()).save(any(Reporte.class));
    }

    @Test
    void guardaElReporteCuandoSeCumplenLasReglas() {
        PuntoCritico punto = new PuntoCritico("Punto", "Calle 1", "Descripción");
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(ciudadanoRepository.findById(2L)).thenReturn(Optional.of(ciudadano()));
        when(reporteRepository.countByCiudadanoIdAndCerradoFalse(2L)).thenReturn(2L);

        servicio.crear(formulario());

        verify(reporteRepository).save(any(Reporte.class));
    }

    @Test
    void noPermiteCerrarDosVecesElMismoReporte() {
        Reporte reporte = new Reporte();
        reporte.setCerrado(true);
        when(reporteRepository.findById(5L)).thenReturn(Optional.of(reporte));

        assertThrows(ReglaNegocioException.class, () -> servicio.cerrar(5L));
    }

    @Test
    void cerrarMarcaElReporteYGuardaLaFecha() {
        Reporte reporte = new Reporte();
        when(reporteRepository.findById(5L)).thenReturn(Optional.of(reporte));

        servicio.cerrar(5L);

        assertTrue(reporte.isCerrado());
        assertNotNull(reporte.getFechaCierre());
    }
}
