# 🌿 EcoAlerta

Sistema de **gestión de puntos críticos y monitoreo ambiental**. Los ciudadanos reportan lugares con problemas
(basura acumulada, vertimientos, quemas, ruido…) y las entidades responsables les hacen seguimiento.

Proyecto en **Spring Boot 3.2 · Java 21 · Thymeleaf · Spring Data JPA · MySQL**, con vistas de servidor (Thymeleaf)
y una API REST de apoyo.

## Cómo arrancarlo

1. Tener MySQL encendido en el puerto 3306 y una base de datos vacía llamada `ecoalerta`:
   ```sql
   DROP DATABASE IF EXISTS ecoalerta;
   CREATE DATABASE ecoalerta;
   ```
2. Revisar usuario y contraseña en `src/main/resources/application.properties` (por defecto `root` sin contraseña).
3. Desde la carpeta del proyecto:
   ```
   mvnw.cmd spring-boot:run        (Windows)
   ./mvnw spring-boot:run          (Linux / Mac)
   ```
4. Abrir **http://localhost:8080**

Al primer arranque se cargan **datos de ejemplo** (categorías, entidades, ciudadanos, puntos y reportes) solo si la
base está vacía. Las tablas las crea Hibernate a partir de las entidades (`ddl-auto=update`).

## Arquitectura

```
controllers/web   → vistas Thymeleaf (controladores delgados, solo reciben y devuelven)
controllers/api   → API REST (@RestController)
services          → TODAS las reglas de negocio + @Transactional
repositories      → JpaRepository, métodos derivados y @Query
models            → entidades JPA y relaciones
dto               → records (Request / Response) y objetos de formulario
exceptions        → ReglaNegocioException + @ControllerAdvice (web y API)
config            → recursos estáticos de /subidas y datos de ejemplo
```

## Modelo relacional

```
Categoria 1 ──── N PuntoCritico N ──── N Entidad      (@OneToMany/@ManyToOne y @ManyToMany)
PuntoCritico 1 ── N Reporte N ──── 1 Ciudadano        (@OneToMany/@ManyToOne)
Ciudadano 1 ──── 1 Contacto                           (@OneToOne)
```

## Pantallas (Thymeleaf)

| Ruta | Qué hace |
|---|---|
| `/` | Inicio con cifras y últimos puntos |
| `/puntos` | Catálogo con búsqueda, filtros por estado y categoría, y paginación |
| `/puntos/{id}` | Ficha: imagen, categoría, entidades, cambio de estado y reportes |
| `/puntos/nuevo`, `/puntos/{id}/editar` | Formulario con validación y subida de imagen |
| `/reportes` | Reportes abiertos, vencidos y cerrados, con exportación CSV |
| `/reportes/nuevo` | Crear un reporte |
| `/ciudadanos`, `/ciudadanos/{id}` | Listado y ficha con historial de reportes |
| `/categorias`, `/entidades` | Alta, edición y borrado |

Cabecera, menú y pie son **fragmentos** (`templates/fragmentos/comunes.html`) reutilizados en todas las vistas.
Los cambios (cerrar, borrar, cambiar estado) van siempre por **POST**, nunca por enlaces GET.

## API REST

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/puntos-criticos` (`?estado=PENDIENTE`) | 200 |
| GET | `/api/puntos-criticos/{id}` | 200 / 404 |
| POST | `/api/puntos-criticos` | 201 / 400 / 409 |
| PUT | `/api/puntos-criticos/{id}` | 200 / 400 / 404 / 409 |
| DELETE | `/api/puntos-criticos/{id}` | 204 / 404 / 409 |
| GET | `/api/categorias` | 200 |
| GET | `/api/reportes?filtro=abiertos\|vencidos\|cerrados` | 200 |
| POST | `/api/reportes/{id}/cerrar` | 200 / 404 / 409 |

La API devuelve **records** (nunca entidades JPA), así que no hay bucles de JSON. Ejemplo de cuerpo para el POST:

```json
{
  "nombre": "Basura en el parque",
  "ubicacion": "Calle 10 con Cra 4",
  "descripcion": "Acumulación de residuos desde hace días",
  "categoriaId": 1,
  "estado": "PENDIENTE",
  "entidadIds": [1, 2]
}
```

Los errores salen como JSON: `{ "status": 400, "mensaje": "...", "errores": { "campo": "motivo" } }`.

## Reglas de negocio (todas en la capa de servicio)

1. No puede haber dos puntos con el mismo nombre en la misma ubicación.
2. Un punto no se puede marcar como *Resuelto* si tiene reportes abiertos.
3. Un punto con reportes abiertos no se puede eliminar.
4. No se puede reportar sobre un punto ya *Resuelto*.
5. Un ciudadano no puede tener más de **3 reportes abiertos** a la vez.
6. Un reporte abierto con más de **15 días** cuenta como *vencido*.
7. Un reporte cerrado no se puede cerrar otra vez.
8. El nombre de categoría, el nombre de entidad y el correo de ciudadano son únicos.
9. No se elimina una categoría con puntos, una entidad asignada a puntos ni un ciudadano con reportes.

## Qué se usa del temario

`@Controller` · `@RestController` · `@RequestMapping` · `@GetMapping` · `@PostMapping` · `@PathVariable` ·
`@RequestParam` · `@RequestBody` · `@Service` · inyección por constructor · `@Entity` · `@Id` · `@GeneratedValue` ·
`@Column` · `@Transactional` · `@OneToMany` · `@ManyToOne` · `@ManyToMany` · `@OneToOne` · `JpaRepository` ·
métodos derivados · `@Query` (JPQL con constructor de records) · `@EntityGraph` (sin N+1) · `Pageable` ·
`th:each` · `th:if` · `th:object` · `th:field` · `th:fragment` · `@{…}` · `MultipartFile` · `@Valid` ·
`BindingResult` · `RedirectAttributes` · `@ControllerAdvice` · records · JUnit + Mockito.

## Pruebas

```
mvnw.cmd test -Dtest=ReporteServiceTest,PuntoCriticoServiceTest
```
Prueban las reglas de negocio de los servicios con Mockito, sin servidor ni base de datos.

## Qué falta o no llegó a funcionar

- **No hay login** (Spring Security): cualquier persona puede usar todas las pantallas.
- La API recibe JSON, así que **no sube imágenes**; las imágenes se suben solo desde el formulario web.
- El test `BackendApplicationTests` (que arranca toda la aplicación) necesita MySQL encendido.
- Con MySQL 5.5, Hibernate muestra un aviso de dialecto no soportado; la aplicación funciona igual.
