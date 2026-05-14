import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Attach token automatically
api.interceptors.request.use(config => {
  const saved = localStorage.getItem('hotelhub_user');
  if (saved) {
    const { token } = JSON.parse(saved);
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ── Auth ──
export const register = (data) => api.post('/auth/register', data).then(r => r.data);
export const login = (data) => api.post('/auth/login', data).then(r => r.data);

// ── Productos ──
export const getProductosAleatorios = () => api.get('/productos/aleatorios').then(r => r.data);
export const getAllProductos = (page = 0, size = 10) => api.get(`/productos?page=${page}&size=${size}`).then(r => r.data);
export const getProductoById = (id) => api.get(`/productos/${id}`).then(r => r.data);
export const crearProducto = (data) => api.post('/productos', data).then(r => r.data);
export const eliminarProducto = (id) => api.delete(`/productos/${id}`).then(r => r.data);

// ── Categorías ──
export const getCategorias = () => api.get('/categorias').then(r => r.data);
export const crearCategoria = (data) => api.post('/categorias', data).then(r => r.data);
export const eliminarCategoria = (id) => api.delete(`/categorias/${id}`).then(r => r.data);

// ── Características ──
export const getCaracteristicas = () => api.get('/caracteristicas').then(r => r.data);
export const crearCaracteristica = (data) => api.post('/caracteristicas', data).then(r => r.data);
export const editarCaracteristica = (id, data) => api.put(`/caracteristicas/${id}`, data).then(r => r.data);
export const eliminarCaracteristica = (id) => api.delete(`/caracteristicas/${id}`).then(r => r.data);

// ── Usuarios ──
export const getUsuarios = () => api.get('/usuarios').then(r => r.data);
export const cambiarRolUsuario = (id, rol) => api.put(`/usuarios/${id}/rol`, { rol }).then(r => r.data);

export default api;

// ── Reservas ──
export const getFechasOcupadas = (productoId) => api.get(`/reservas/producto/${productoId}/fechas-ocupadas`).then(r => r.data);
export const crearReserva = (data) => api.post('/reservas', data).then(r => r.data);
export const getMisReservas = () => api.get('/reservas/mis-reservas').then(r => r.data);

// ── Favoritos ──
export const getMisFavoritos = () => api.get('/favoritos').then(r => r.data);
export const getMisFavoritosIds = () => api.get('/favoritos/ids').then(r => r.data);
export const toggleFavorito = (productoId) => api.post(`/favoritos/toggle/${productoId}`).then(r => r.data);

// ── Puntuaciones ──
export const getPuntuaciones = (productoId) => api.get(`/puntuaciones/producto/${productoId}`).then(r => r.data);
export const getPromedioPuntuacion = (productoId) => api.get(`/puntuaciones/producto/${productoId}/promedio`).then(r => r.data);
export const puntuar = (productoId, data) => api.post(`/puntuaciones/producto/${productoId}`, data).then(r => r.data);

// ── Políticas ──
export const getPoliticas = (productoId) => api.get(`/politicas/producto/${productoId}`).then(r => r.data);
export const crearPolitica = (productoId, data) => api.post(`/politicas/producto/${productoId}`, data).then(r => r.data);
export const eliminarPolitica = (id) => api.delete(`/politicas/${id}`).then(r => r.data);

// ── Reservas (actualizados Sprint 4) ──
export const getProductoParaReserva = (id) => api.get(`/productos/${id}`).then(r => r.data);
