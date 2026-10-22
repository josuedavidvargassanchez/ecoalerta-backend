package com.ecoalerta.backend.config;

import com.ecoalerta.backend.models.*;
import com.ecoalerta.backend.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarDatos(CategoriaRepository categorias,
                                  EntidadRepository entidades,
                                  CiudadanoRepository ciudadanos,
                                  PuntoCriticoRepository puntos,
                                  ReporteRepository reportes) {
        return args -> {
            if (categorias.count() > 0 || puntos.count() > 0) {
                return;
            }

            Categoria residuos = categorias.save(new Categoria("Residuos sólidos", "Basuras, escombros y puntos de acumulación"));
            Categoria agua = categorias.save(new Categoria("Contaminación del agua", "Vertimientos en quebradas, ríos y alcantarillas"));
            Categoria aire = categorias.save(new Categoria("Calidad del aire", "Quemas, humos y emisiones"));
            Categoria ruido = categorias.save(new Categoria("Ruido", "Contaminación auditiva"));
            Categoria verdes = categorias.save(new Categoria("Zonas verdes", "Parques y arborización"));

            Entidad ambiente = entidades.save(new Entidad("Secretaría de Ambiente", "Gubernamental", "ambiente@ciudad.gov"));
            Entidad aseo = entidades.save(new Entidad("Empresa de Aseo Municipal", "Servicio público", "aseo@ciudad.gov"));
            Entidad defensa = entidades.save(new Entidad("Defensa Civil", "Emergencias", "defensacivil@ciudad.gov"));
            Entidad fundacion = entidades.save(new Entidad("Fundación Río Limpio", "ONG", "contacto@riolimpio.org"));

            Ciudadano maria = ciudadano(ciudadanos, "María Gómez", "maria@correo.com", "3001112233", "Calle 10 # 5-20");
            Ciudadano carlos = ciudadano(ciudadanos, "Carlos Rodríguez", "carlos@correo.com", "3104445566", "Carrera 7 # 12-30");
            Ciudadano laura = ciudadano(ciudadanos, "Laura Pérez", "laura@correo.com", "3207778899", "Avenida 3 # 45-10");

            PuntoCritico p1 = punto(puntos, "Acumulación de residuos en sector norte", "Calle 45 con Cra 12",
                    "Basura acumulada hace semanas en la esquina, con malos olores.", EstadoPunto.PENDIENTE, residuos, aseo);
            PuntoCritico p2 = punto(puntos, "Vertimiento en quebrada La Honda", "Barrio El Prado",
                    "Aguas residuales de una fábrica llegan directo a la quebrada.", EstadoPunto.EN_ATENCION, agua, ambiente, fundacion);
            PuntoCritico p3 = punto(puntos, "Quema de llantas", "Vía a la planta de tratamiento",
                    "Se queman llantas por las noches y el humo llega al barrio.", EstadoPunto.PENDIENTE, aire, ambiente, defensa);
            PuntoCritico p4 = punto(puntos, "Ruido de taller industrial", "Carrera 15 # 20-05",
                    "Taller que trabaja de madrugada con maquinaria pesada.", EstadoPunto.RESUELTO, ruido, ambiente);
            punto(puntos, "Parque sin mantenimiento", "Parque Los Almendros",
                    "Árboles sin podar y zonas verdes invadidas por maleza.", EstadoPunto.PENDIENTE, verdes, ambiente);
            PuntoCritico p6 = punto(puntos, "Escombros en la ribera del río", "Puente Viejo",
                    "Volquetas descargan escombros a la orilla del río.", EstadoPunto.EN_ATENCION, residuos, aseo, fundacion);
            punto(puntos, "Basurero satélite en lote baldío", "Calle 80 con Cra 3",
                    "Lote abandonado usado como botadero de residuos.", EstadoPunto.PENDIENTE, residuos, aseo);

            reporte(reportes, p1, maria, "Los vecinos llevamos tres semanas sin recolección.", 20, false);   // vencido
            reporte(reportes, p1, carlos, "Hay ratas y el olor es insoportable.", 3, false);
            reporte(reportes, p2, laura, "El agua de la quebrada cambió de color.", 6, false);
            reporte(reportes, p3, maria, "Anoche volvieron a quemar llantas.", 1, false);
            reporte(reportes, p6, carlos, "Descargaron cuatro volquetas en la madrugada.", 9, false);
            reporte(reportes, p4, laura, "El taller ya respeta el horario, caso cerrado.", 25, true);
        };
    }

    private Ciudadano ciudadano(CiudadanoRepository repo, String nombre, String email, String telefono, String direccion) {
        Ciudadano c = new Ciudadano();
        c.setNombre(nombre);
        c.setEmail(email);
        c.setContacto(new Contacto(telefono, direccion));
        return repo.save(c);
    }

    private PuntoCritico punto(PuntoCriticoRepository repo, String nombre, String ubicacion, String descripcion,
                               EstadoPunto estado, Categoria categoria, Entidad... responsables) {
        PuntoCritico p = new PuntoCritico(nombre, ubicacion, descripcion);
        p.setEstado(estado);
        p.setCategoria(categoria);
        p.getEntidades().addAll(List.of(responsables));
        return repo.save(p);
    }

    private void reporte(ReporteRepository repo, PuntoCritico punto, Ciudadano ciudadano,
                         String descripcion, int diasAtras, boolean cerrado) {
        Reporte r = new Reporte();
        r.setPunto(punto);
        r.setCiudadano(ciudadano);
        r.setDescripcion(descripcion);
        r.setFecha(LocalDate.now().minusDays(diasAtras));
        if (cerrado) {
            r.setCerrado(true);
            r.setFechaCierre(LocalDate.now().minusDays(Math.max(diasAtras - 5, 0)));
        }
        repo.save(r);
    }
}
