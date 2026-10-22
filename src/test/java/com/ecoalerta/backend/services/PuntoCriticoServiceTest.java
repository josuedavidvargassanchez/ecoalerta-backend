package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.PuntoCriticoRequest;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.repositories.CategoriaRepository;
import com.ecoalerta.backend.repositories.EntidadRepository;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import com.ecoalerta.backend.repositories.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas de las reglas de negocio de los puntos críticos, sin levantar el servidor. */
@ExtendWith(MockitoExtension.class)
class PuntoCriticoServiceTest {

    @Mock
    private PuntoCriticoRepository puntoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private EntidadRepository entidadRepository;
    @Mock
    private ReporteRepository reporteRepository;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private PuntoCriticoService servicio;

    @Test
    void noPermiteEliminarUnPuntoConReportesAbiertos() {
        PuntoCritico punto = new PuntoCritico("Punto", "Calle 1", "Descripción");
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(reporteRepository.existsByPuntoIdAndCerradoFalse(1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(1L));
        verify(puntoRepository, never()).delete(any(PuntoCritico.class));
    }

    @Test
    void noPermiteResolverUnPuntoConReportesAbiertos() {
        PuntoCritico punto = new PuntoCritico("Punto", "Calle 1", "Descripción");
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(reporteRepository.existsByPuntoIdAndCerradoFalse(1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> servicio.cambiarEstado(1L, EstadoPunto.RESUELTO));
        assertEquals(EstadoPunto.PENDIENTE, punto.getEstado());
    }

    @Test
    void resuelveElPuntoCuandoYaNoTieneReportesAbiertos() {
        PuntoCritico punto = new PuntoCritico("Punto", "Calle 1", "Descripción");
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(reporteRepository.existsByPuntoIdAndCerradoFalse(1L)).thenReturn(false);

        servicio.cambiarEstado(1L, EstadoPunto.RESUELTO);

        assertEquals(EstadoPunto.RESUELTO, punto.getEstado());
    }

    @Test
    void noPermiteDosPuntosConElMismoNombreEnLaMismaUbicacion() {
        PuntoCriticoRequest req = new PuntoCriticoRequest("Basurero", "Calle 1", "Descripción", 1L, null, null);
        when(puntoRepository.existsByNombreIgnoreCaseAndUbicacionIgnoreCase("Basurero", "Calle 1")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> servicio.crear(req, null));
        verify(puntoRepository, never()).save(any(PuntoCritico.class));
    }
}
