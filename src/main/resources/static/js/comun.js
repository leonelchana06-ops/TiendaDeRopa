/* Funciones compartidas por todas las páginas */

const MENU = [
  ["index.html", "Inicio"],
  ["productos.html", "Productos"],
  ["clientes.html", "Clientes"],
  ["empleados.html", "Empleados"],
  ["ventas.html", "Historial"],
  ["venta.html", "+ Nueva venta", "destacado"],
];

function dibujarMenu() {
  const actual = location.pathname.split("/").pop() || "index.html";
  const barra = document.createElement("header");
  barra.className = "barra";
  barra.innerHTML = `
    <div class="barra-interior">
      <a class="marca" href="index.html">
        <span class="marca-icono">👕</span>
        <span>Tienda de Ropa<small>Gestión y Ventas</small></span>
      </a>
      <nav class="menu">
        ${MENU.map(([href, texto, clase]) =>
          `<a href="${href}" class="${clase || ""} ${href === actual ? "activo" : ""}">${texto}</a>`).join("")}
      </nav>
    </div>`;
  document.body.prepend(barra);
  const avisos = document.createElement("div");
  avisos.className = "avisos";
  avisos.id = "avisos";
  document.body.append(avisos);
}

/** Llama a la API REST. Lanza Error con el mensaje del servidor si falla. */
async function api(metodo, url, cuerpo) {
  const opciones = { method: metodo, headers: {} };
  if (cuerpo !== undefined) {
    opciones.headers["Content-Type"] = "application/json";
    opciones.body = JSON.stringify(cuerpo);
  }
  let resp;
  try {
    resp = await fetch(url, opciones);
  } catch {
    throw new Error("No se pudo conectar con el servidor. ¿Está en ejecución?");
  }
  if (resp.status === 204) return null;
  let datos = null;
  try { datos = await resp.json(); } catch { /* sin cuerpo */ }
  if (!resp.ok) throw new Error((datos && datos.error) || `Error ${resp.status}`);
  return datos;
}

const formatoDinero = new Intl.NumberFormat("es-AR", { style: "currency", currency: "ARS" });
const dinero = (n) => formatoDinero.format(Number(n) || 0);

function fechaHora(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  return d.toLocaleDateString("es-AR") + " " + d.toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" });
}

/** Escapa texto para insertarlo en HTML (evita inyección de código). */
function esc(t) {
  return String(t ?? "").replace(/[&<>"']/g, (c) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[c]);
}

function aviso(mensaje, tipo = "ok") {
  const el = document.createElement("div");
  el.className = "aviso " + tipo;
  el.textContent = mensaje;
  document.getElementById("avisos").append(el);
  setTimeout(() => el.remove(), tipo === "error" ? 6000 : 3500);
}

function chipStock(stock) {
  if (stock <= 0) return `<span class="chip chip-agotado">Agotado</span>`;
  if (stock <= 5) return `<span class="chip chip-bajo">${stock} · bajo</span>`;
  return `<span class="chip chip-ok">${stock}</span>`;
}

/** Convierte un formulario en objeto {campo: valor}. */
function datosFormulario(form) {
  return Object.fromEntries(new FormData(form).entries());
}

/** Mostrar/ocultar el mensaje de error dentro de un formulario. */
function errorEn(form, mensaje) {
  const caja = form.querySelector(".error-form");
  if (!caja) return;
  caja.textContent = mensaje || "";
  caja.classList.toggle("oculto", !mensaje);
}

/** Retrasa la ejecución (para buscar mientras se escribe). */
function demorar(fn, ms = 250) {
  let t;
  return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
}

/**
 * Configura una página de alta/modificación/baja (ABM) genérica.
 *  cfg = { url, idCampo, dialogo, form, titulo: [nuevo, editar], cargar(), nombre(obj) }
 */
function abm(cfg) {
  const dlg = document.getElementById(cfg.dialogo);
  const form = document.getElementById(cfg.form);
  let editando = null;

  dlg.querySelectorAll("[data-cerrar]").forEach((b) => b.addEventListener("click", () => dlg.close()));

  const abrir = (obj) => {
    editando = obj || null;
    form.reset();
    errorEn(form, "");
    dlg.querySelector("h2").textContent = obj ? cfg.titulo[1] : cfg.titulo[0];
    if (obj) {
      for (const el of form.elements) if (el.name && obj[el.name] != null) el.value = obj[el.name];
    }
    dlg.showModal();
    form.querySelector("input,select,textarea")?.focus();
  };

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!form.reportValidity()) return;
    const boton = form.querySelector("[type=submit]");
    boton.disabled = true;
    try {
      const datos = cfg.preparar ? cfg.preparar(datosFormulario(form)) : datosFormulario(form);
      if (editando) await api("PUT", `${cfg.url}/${editando[cfg.idCampo]}`, datos);
      else await api("POST", cfg.url, datos);
      dlg.close();
      aviso(editando ? "Cambios guardados." : "Registro creado.");
      cfg.cargar();
    } catch (err) {
      errorEn(form, err.message);
    } finally {
      boton.disabled = false;
    }
  });

  const eliminar = async (obj) => {
    if (!confirm(`¿Eliminar «${cfg.nombre(obj)}»? Esta acción no se puede deshacer.`)) return;
    try {
      await api("DELETE", `${cfg.url}/${obj[cfg.idCampo]}`);
      aviso("Registro eliminado.");
      cfg.cargar();
    } catch (err) {
      aviso(err.message, "error");
    }
  };

  return { abrir, eliminar };
}

document.addEventListener("DOMContentLoaded", dibujarMenu);
