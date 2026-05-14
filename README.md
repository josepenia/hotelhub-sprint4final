# HotelHub – Sistema de Reservas 🏨
> Proyecto final completo – Sprints 1, 2, 3 y 4

## Estructura del proyecto

```
hotel-reservas/
├── backend/          → API REST con Java Spring Boot
└── frontend/         → Interfaz web con React
```

---

## Backend (Spring Boot)

### Requisitos
- Java 17+
- Maven

### Cómo correr
```bash
cd backend
./mvnw spring-boot:run
```
La API corre en `http://localhost:8080`

### Endpoints principales

| Método | URL | Auth | Descripción |
|--------|-----|------|-------------|
| POST | `/api/auth/register` | — | Registro de usuario |
| POST | `/api/auth/login` | — | Login, devuelve JWT |
| GET | `/api/productos/aleatorios` | — | 10 productos random |
| GET | `/api/productos?page=0&size=10` | — | Listado paginado |
| GET | `/api/productos/{id}` | — | Detalle |
| POST | `/api/productos` | ADMIN | Crear producto |
| DELETE | `/api/productos/{id}` | ADMIN | Eliminar producto |
| GET | `/api/categorias` | — | Listar categorías |
| POST | `/api/categorias` | ADMIN | Crear categoría |
| DELETE | `/api/categorias/{id}` | ADMIN | Eliminar categoría |
| GET | `/api/caracteristicas` | — | Listar características |
| POST | `/api/caracteristicas` | ADMIN | Crear característica |
| PUT | `/api/caracteristicas/{id}` | ADMIN | Editar característica |
| DELETE | `/api/caracteristicas/{id}` | ADMIN | Eliminar característica |
| GET | `/api/reservas/producto/{id}/fechas-ocupadas` | — | Fechas ocupadas |
| POST | `/api/reservas` | USER | Crear reserva |
| GET | `/api/reservas/mis-reservas` | USER | Historial de reservas |
| GET | `/api/favoritos` | USER | Mis favoritos |
| POST | `/api/favoritos/toggle/{productoId}` | USER | Toggle favorito |
| GET | `/api/puntuaciones/producto/{id}` | — | Reseñas del producto |
| POST | `/api/puntuaciones/producto/{id}` | USER | Puntuar producto |
| GET | `/api/politicas/producto/{id}` | — | Políticas del producto |
| POST | `/api/politicas/producto/{id}` | ADMIN | Crear política |
| GET | `/api/usuarios` | ADMIN | Listar usuarios |
| PUT | `/api/usuarios/{id}/rol` | ADMIN | Cambiar rol |

### Usuario admin de prueba
- Email: `admin@hotelhub.com`
- Password: `admin123`

### Consola H2
`http://localhost:8080/h2-console` — JDBC: `jdbc:h2:mem:hoteldb` — User: `sa`

---

## Frontend (React)

### Requisitos
- Node.js 16+
- npm

### Cómo correr
```bash
cd frontend
npm install
npm start
```
La app corre en `http://localhost:3000`

### Páginas

| Ruta | Descripción |
|------|-------------|
| `/` | Home con buscador, fechas, categorías y productos |
| `/producto/:id` | Detalle con galería, características, disponibilidad, reseñas, políticas, compartir |
| `/reserva/:id` | Página de reserva con calendario interactivo |
| `/reserva/:id/confirmacion` | Confirmación exitosa de reserva |
| `/mis-reservas` | Historial de reservas del usuario |
| `/favoritos` | Lista de productos favoritos |
| `/login` | Inicio de sesión |
| `/register` | Registro de usuario |
| `/administracion` | Panel admin completo |

---

## User Stories implementadas (todos los Sprints)

### Sprint 1
- ✅ #1 Header fijo con logo y navegación
- ✅ #2 Main con buscador, categorías y recomendaciones
- ✅ #3 Agregar producto (admin)
- ✅ #4 10 productos aleatorios en el Home
- ✅ #5 Detalle de producto
- ✅ #6 Galería de imágenes con modal
- ✅ #7 Footer con copyright
- ✅ #8 Paginación de productos
- ✅ #9 Panel de administración en `/administracion`
- ✅ #10 Listar productos en admin
- ✅ #11 Eliminar producto con confirmación

### Sprint 2
- ✅ #12 Categorizar productos
- ✅ #13 Registro de usuario
- ✅ #14 Login con avatar e iniciales
- ✅ #15 Cerrar sesión
- ✅ #16 Roles ADMIN / USER
- ✅ #17 CRUD de características
- ✅ #18 Características en detalle del producto
- ✅ #20 Filtrar por categoría
- ✅ #21 Agregar categoría desde admin

### Sprint 3
- ✅ #22 Buscador con rango de fechas
- ✅ #23 Calendario de disponibilidad en detalle
- ✅ #24 Marcar productos como favoritos
- ✅ #25 Lista de favoritos
- ✅ #26 Bloque de políticas del producto
- ✅ #27 Compartir en redes sociales
- ✅ #28 Puntuar con estrellas y reseñas
- ✅ #29 Eliminar categoría con confirmación

### Sprint 4
- ✅ #30 Página de reserva con calendario interactivo y validación de fechas
- ✅ #31 Detalle de reserva con datos del usuario y producto
- ✅ #32 Confirmar reserva con página de éxito
- ✅ #33 Historial de reservas del usuario
- ✅ #34 Botón flotante de WhatsApp

---

## Tecnologías

- **Backend:** Java 17, Spring Boot 3.2, Spring Security, JWT, Spring Data JPA, H2, Lombok
- **Frontend:** React 18, React Router v6, Axios, CSS puro
