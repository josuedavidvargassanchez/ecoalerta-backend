                    // URL base de la API REST en Spring Boot
                    
const API_URL = "http://localhost:8080/api/puntos-criticos";

// Datos de prueba iniciales si el backend aún no está activo
let puntosCriticos = [
  {
    id: 1,
    barrio: "San Isidro",
    direccionReferencia: "Cancha de sóftbol",
    descripcion: "Acumulación masiva de plásticos y restos de poda.",
    nivelSeveridad: "Crítico",
    estado: "Pendiente",
    fechaReporte: "2026-09-20"
  },
  {
    id: 2,
    barrio: "El Socorro",
    direccionReferencia: "Frente al colegio",
    descripcion: "Escombros de construcción abandonados.",
    nivelSeveridad: "Medio",
    estado: "Limpiado",
    fechaReporte: "2026-09-18"
  }
];

let filtroActual = 'Todos';

// Inicializar íconos al cargar
document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
    cargarPuntos();
});

// 1. OBTENER PUNTOS (GET)
async function cargarPuntos() {
  try {
    const res = await fetch(API_URL);
    if (res.ok) {
      puntosCriticos = await res.json();
    }
  } catch (error) {
    console.warn("Spring Boot no detectado aún. Usando datos de prueba locales.");
  }
  renderizarPuntos();
}

// 2. MOSTRAR EN EL HTML
function renderizarPuntos() {
  const container = document.getElementById('puntosContainer');
  container.innerHTML = '';

  const filtrados = puntosCriticos.filter(p => {
    if (filtroActual === 'Todos') return true;
    return p.estado === filtroActual;
  });

  // Actualizar contadores
  document.getElementById('totalCount').innerText = puntosCriticos.length;
  document.getElementById('pendientesCount').innerText = puntosCriticos.filter(p => p.estado === 'Pendiente').length;
  document.getElementById('limpiadosCount').innerText = puntosCriticos.filter(p => p.estado === 'Limpiado').length;

  if (filtrados.length === 0) {
    container.innerHTML = `<p class="text-center text-gray-500 py-8">No hay puntos registrados en esta categoría.</p>`;
    return;
  }

  filtrados.forEach(punto => {
    const badgeSeveridad = getBadgeSeveridad(punto.nivelSeveridad);
    const badgeEstado = punto.estado === 'Limpiado' 
      ? '<span class="bg-emerald-100 text-emerald-800 text-xs font-semibold px-2.5 py-0.5 rounded-full">Limpiado</span>'
      : '<span class="bg-amber-100 text-amber-800 text-xs font-semibold px-2.5 py-0.5 rounded-full">Pendiente</span>';

    const card = document.createElement('div');
    card.className = "border border-gray-200 rounded-lg p-4 bg-gray-50 card-hover space-y-3";
    card.innerHTML = `
      <div class="flex justify-between items-start">
        <div>
          <h3 class="font-bold text-gray-900 text-lg">${punto.barrio}</h3>
          <p class="text-xs text-gray-500 flex items-center gap-1">
            <i data-lucide="map-pin" class="w-3 h-3"></i> ${punto.direccionReferencia}
          </p>
        </div>
        <div class="flex gap-2">
          ${badgeSeveridad}
          ${badgeEstado}
        </div>
      </div>

      <p class="text-sm text-gray-700">${punto.descripcion}</p>

      <div class="flex justify-between items-center pt-2 border-t border-gray-200 text-xs text-gray-500">
        <span>Fecha: ${punto.fechaReporte || 'Hoy'}</span>
        <div class="flex gap-3">
          ${punto.estado === 'Pendiente' ? `
            <button onclick="cambiarEstado(${punto.id}, 'Limpiado')" class="text-emerald-700 hover:text-emerald-900 font-semibold flex items-center gap-1">
              <i data-lucide="check-circle" class="w-4 h-4"></i> Marcar Limpiado
            </button>
          ` : ''}
          <button onclick="eliminarPunto(${punto.id})" class="text-red-600 hover:text-red-800 font-semibold flex items-center gap-1">
            <i data-lucide="trash" class="w-4 h-4"></i> Eliminar
          </button>
        </div>
      </div>
    `;
    container.appendChild(card);
  });

  lucide.createIcons();
}

// 3. REGISTRAR PUNTO (POST)
document.getElementById('reporteForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const nuevoPunto = {
    barrio: document.getElementById('barrio').value,
    direccionReferencia: document.getElementById('direccionReferencia').value,
    descripcion: document.getElementById('descripcion').value,
    nivelSeveridad: document.getElementById('nivelSeveridad').value,
    estado: 'Pendiente',
    fechaReporte: new Date().toISOString().split('T')[0]
  };

  try {
    const res = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(nuevoPunto)
    });

    if (res.ok) {
      const creado = await res.json();
      puntosCriticos.push(creado);
    } else {
      nuevoPunto.id = Date.now();
      puntosCriticos.push(nuevoPunto);
    }
  } catch (err) {
    nuevoPunto.id = Date.now();
    puntosCriticos.push(nuevoPunto);
  }

  document.getElementById('reporteForm').reset();
  renderizarPuntos();
});

// 4. CAMBIAR ESTADO (PUT)
async function cambiarEstado(id, nuevoEstado) {
  try {
    await fetch(`${API_URL}/${id}/estado?estado=${nuevoEstado}`, { method: 'PUT' });
  } catch (err) {}

  const p = puntosCriticos.find(item => item.id === id);
  if (p) p.estado = nuevoEstado;
  renderizarPuntos();
}

// 5. ELIMINAR PUNTO (DELETE)
async function eliminarPunto(id) {
  try {
    await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
  } catch (err) {}

  puntosCriticos = puntosCriticos.filter(item => item.id !== id);
  renderizarPuntos();
}

// FUNCIONES AUXILIARES
function getBadgeSeveridad(nivel) {
  switch(nivel) {
    case 'Crítico': return '<span class="bg-red-100 text-red-800 text-xs font-semibold px-2 py-0.5 rounded">Crítico</span>';
    case 'Alto': return '<span class="bg-orange-100 text-orange-800 text-xs font-semibold px-2 py-0.5 rounded">Alto</span>';
    case 'Medio': return '<span class="bg-yellow-100 text-yellow-800 text-xs font-semibold px-2 py-0.5 rounded">Medio</span>';
    default: return '<span class="bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-0.5 rounded">Bajo</span>';
  }
}

function filtrarPuntos(estado) {
  filtroActual = estado;
  renderizarPuntos();
}