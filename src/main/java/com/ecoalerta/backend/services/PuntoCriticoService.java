package com.ecoalerta.backend.services;

import com.ecoalerta.backend.dto.PuntoCriticoRequest;
import com.ecoalerta.backend.dto.PuntoCriticoResponse;
import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import com.ecoalerta.backend.models.EstadoPunto;
import com.ecoalerta.backend.models.PuntoCritico;
import com.ecoalerta.backend.repositories.CategoriaRepository;
import com.ecoalerta.backend.repositories.EntidadRepository;
import com.ecoalerta.backend.repositories.PuntoCriticoRepository;
import com.ecoalerta.backend.repositories.ReporteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class PuntoCriticoService {

    private static final int TAMANO_PAGINA = 6;

    private final PuntoCriticoRepository puntoRepository;
    private final CategoriaRepository categoriaRepository;
    private final EntidadRepository entidadRepository;
    private final ReporteRepository reporteRepository;
    private final StorageService storageService;

    public PuntoCriticoService(PuntoCriticoRepository puntoRepository,
                               CategoriaRepository categoriaRepository,
                               EntidadRepository entidadRepository,
                               ReporteRepository reporteRepository,
                               StorageService storageService) {
        this.puntoRepository = puntoRepository;
        this.categoriaRepository = categoriaRepository;
        this.entidadRepository = entidadRepository;
        this.reporteRepository = reporteRepository;
        this.storageService = storageService;
    }


    @Transactional(readOnly = true)
    public Page<PuntoCritico> buscar(String texto, EstadoPunto estado, Long categoriaId, int pagina) {
        String filtro = (texto == null || texto.isBlank()) ? null : texto.trim();
        return puntoRepository.buscar(filtro, estado, categoriaId,
                PageRequest.of(Math.max(pagina, 0), TAMANO_PAGINA));
    }

    @Transactional(readOnly = true)
    public PuntoCritico obtenerDetalle(Long id) {
        return puntoRepository.findConRelacionesById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el punto crítico " + id));
    }

    @Transactional(readOnly = true)
    public List<PuntoCritico> ultimos() {
        return puntoRepository.findTop5ByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public List<PuntoCritico> listarParaReportar() {
        return puntoRepository.findByEstadoNotOrderByNombreAsc(EstadoPunto.RESUELTO);
    }


    public Long crear(PuntoCriticoRequest req, MultipartFile imagen) {
        PuntoCritico punto = nuevoPunto(req);
        punto.setImagenRuta(storageService.store(imagen));
        return puntoRepository.save(punto).getId();
    }

    public void actualizar(Long id, PuntoCriticoRequest req, MultipartFile imagen) {
        aplicarActualizacion(id, req, imagen);
    }

    public void cambiarEstado(Long id, EstadoPunto nuevo) {
        PuntoCritico punto = puntoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el punto crítico " + id));
        if (punto.getEstado() == nuevo) {
            throw new ReglaNegocioException("El punto ya está en estado " + nuevo.getEtiqueta());
        }
        if (nuevo == EstadoPunto.RESUELTO && reporteRepository.existsByPuntoIdAndCerradoFalse(id)) {
            throw new ReglaNegocioException("No se puede marcar como resuelto: aún tiene reportes abiertos");
        }
        punto.setEstado(nuevo);
    }

    public void eliminar(Long id) {
        PuntoCritico punto = puntoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el punto crítico " + id));
        if (reporteRepository.existsByPuntoIdAndCerradoFalse(id)) {
            throw new ReglaNegocioException("No se puede eliminar un punto con reportes abiertos");
        }
        String imagen = punto.getImagenRuta();
        puntoRepository.delete(punto);
        storageService.eliminar(imagen);
    }


    @Transactional(readOnly = true)
    public List<PuntoCriticoResponse> listarRespuestas(EstadoPunto estado) {
        return puntoRepository.listarConRelaciones(estado).stream()
                .map(PuntoCriticoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PuntoCriticoResponse obtenerRespuesta(Long id) {
        return PuntoCriticoResponse.from(obtenerDetalle(id));
    }

    public PuntoCriticoResponse crearRespuesta(PuntoCriticoRequest req) {
        PuntoCritico punto = nuevoPunto(req);
        return PuntoCriticoResponse.from(puntoRepository.save(punto));
    }

    public PuntoCriticoResponse actualizarRespuesta(Long id, PuntoCriticoRequest req) {
        return PuntoCriticoResponse.from(aplicarActualizacion(id, req, null));
    }


    private PuntoCritico nuevoPunto(PuntoCriticoRequest req) {
        validarDuplicado(req, null);
        PuntoCritico punto = new PuntoCritico();
        copiarDatos(punto, req);
        return punto;
    }

    private PuntoCritico aplicarActualizacion(Long id, PuntoCriticoRequest req, MultipartFile imagen) {
        PuntoCritico punto = obtenerDetalle(id);
        validarDuplicado(req, id);
        copiarDatos(punto, req);
        if (imagen != null && !imagen.isEmpty()) {
            String anterior = punto.getImagenRuta();
            punto.setImagenRuta(storageService.store(imagen));
            storageService.eliminar(anterior);
        }
        return punto;
    }

    private void validarDuplicado(PuntoCriticoRequest req, Long idActual) {
        String nombre = req.nombre().trim();
        String ubicacion = req.ubicacion().trim();
        boolean existe = (idActual == null)
                ? puntoRepository.existsByNombreIgnoreCaseAndUbicacionIgnoreCase(nombre, ubicacion)
                : puntoRepository.existsByNombreIgnoreCaseAndUbicacionIgnoreCaseAndIdNot(nombre, ubicacion, idActual);
        if (existe) {
            throw new ReglaNegocioException("Ya existe un punto crítico con ese nombre en esa ubicación");
        }
    }

    private void copiarDatos(PuntoCritico punto, PuntoCriticoRequest req) {
        punto.setNombre(req.nombre().trim());
        punto.setUbicacion(req.ubicacion().trim());
        punto.setDescripcion(req.descripcion().trim());
        punto.setEstado(req.estado());
        punto.setCategoria(categoriaRepository.findById(req.categoriaId())
                .orElseThrow(() -> new ReglaNegocioException("La categoría seleccionada no existe")));
        punto.getEntidades().clear();
        punto.getEntidades().addAll(entidadRepository.findAllById(req.entidadIds()));
    }
}
