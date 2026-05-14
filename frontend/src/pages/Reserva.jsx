import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { getProductoById, getFechasOcupadas, crearReserva } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './Reserva.css';

function esFechaOcupada(dateStr, fechasOcupadas) {
  const fecha = new Date(dateStr + 'T00:00:00');
  return fechasOcupadas.some(r => {
    const ini = new Date(r.inicio + 'T00:00:00');
    const fin = new Date(r.fin + 'T00:00:00');
    return fecha >= ini && fecha <= fin;
  });
}

function generarMeses(cantidad = 2) {
  const hoy = new Date();
  const meses = [];
  for (let m = 0; m < cantidad; m++) {
    const fecha = new Date(hoy.getFullYear(), hoy.getMonth() + m, 1);
    const diasEnMes = new Date(fecha.getFullYear(), fecha.getMonth() + 1, 0).getDate();
    const primerDia = fecha.getDay();
    meses.push({ year: fecha.getFullYear(), month: fecha.getMonth(), diasEnMes, primerDia });
  }
  return meses;
}

export default function Reserva() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { user, isLoggedIn } = useAuth();

  const [producto, setProducto] = useState(null);
  const [fechasOcupadas, setFechasOcupadas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');
  const [hover, setHover] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [reservaError, setReservaError] = useState('');

  const meses = generarMeses(2);
  const hoy = new Date().toISOString().split('T')[0];

  useEffect(() => {
    Promise.all([getProductoById(id), getFechasOcupadas(id)])
      .then(([prod, fechas]) => { setProducto(prod); setFechasOcupadas(fechas); })
      .catch(() => setError('No se pudo cargar el producto.'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleDiaClick = (dateStr) => {
    if (dateStr < hoy) return;
    if (esFechaOcupada(dateStr, fechasOcupadas)) return;
    if (!fechaInicio || (fechaInicio && fechaFin)) {
      setFechaInicio(dateStr);
      setFechaFin('');
    } else {
      if (dateStr <= fechaInicio) {
        setFechaInicio(dateStr);
        setFechaFin('');
        return;
      }
      // Verificar que no haya fechas ocupadas en el rango
      let cursor = new Date(fechaInicio + 'T00:00:00');
      const end = new Date(dateStr + 'T00:00:00');
      let hayConflicto = false;
      while (cursor <= end) {
        const ds = cursor.toISOString().split('T')[0];
        if (esFechaOcupada(ds, fechasOcupadas)) { hayConflicto = true; break; }
        cursor.setDate(cursor.getDate() + 1);
      }
      if (hayConflicto) {
        setReservaError('El rango seleccionado incluye fechas no disponibles.');
        return;
      }
      setReservaError('');
      setFechaFin(dateStr);
    }
  };

  const estaEnRango = (dateStr) => {
    if (!fechaInicio) return false;
    const fin = fechaFin || hover;
    if (!fin) return false;
    return dateStr > fechaInicio && dateStr < fin;
  };

  const handleConfirmar = async () => {
    if (!fechaInicio || !fechaFin) { setReservaError('Seleccioná las fechas de inicio y fin.'); return; }
    setEnviando(true);
    setReservaError('');
    try {
      const res = await crearReserva({ productoId: id, fechaInicio, fechaFin });
      navigate(`/reserva/${id}/confirmacion`, {
        state: { producto, fechaInicio, fechaFin, user, reservaId: res.id }
      });
    } catch (err) {
      setReservaError(err.response?.data?.error || 'Error al realizar la reserva.');
    } finally {
      setEnviando(false);
    }
  };

  if (!isLoggedIn()) {
    return (
      <main className="reserva-page">
        <div className="reserva-login-required">
          <div className="reserva-login-card">
            <h2>🔒 Iniciá sesión para reservar</h2>
            <p>Para poder realizar una reserva necesitás tener una cuenta. Si aún no estás registrado, podés crear una gratis.</p>
            <div className="reserva-login-btns">
              <button className="btn btn-primary" onClick={() => navigate('/login', { state: { from: location.pathname } })}>
                Iniciar sesión
              </button>
              <button className="btn btn-outline" onClick={() => navigate('/register')}>
                Crear cuenta
              </button>
            </div>
          </div>
        </div>
      </main>
    );
  }

  if (loading) return <div className="loading" style={{ paddingTop: '6rem' }}>Cargando...</div>;
  if (error) return <div className="container" style={{ paddingTop: '6rem' }}><div className="error-msg">{error}</div></div>;

  return (
    <main className="reserva-page">
      <div className="container reserva-inner">

        {/* Header */}
        <div className="reserva-top">
          <button className="btn btn-outline reserva-back" onClick={() => navigate(`/producto/${id}`)}>← Volver al producto</button>
          <h1 className="reserva-title">Reservar habitación</h1>
        </div>

        <div className="reserva-layout">

          {/* Columna izquierda: info producto + datos usuario */}
          <aside className="reserva-aside">
            {/* Producto */}
            <div className="reserva-card">
              <div className="reserva-prod-img">
                <img src={producto?.imagenes?.[0] || 'https://via.placeholder.com/400x200'} alt={producto?.nombre} />
              </div>
              <div className="reserva-prod-info">
                {producto?.categoriaNombre && <span className="reserva-badge">{producto.categoriaNombre}</span>}
                <h2>{producto?.nombre}</h2>
                <p>{producto?.descripcion}</p>
                {producto?.caracteristicas?.length > 0 && (
                  <div className="reserva-caracts">
                    {producto.caracteristicas.map(c => (
                      <span key={c.id} className="reserva-caract">{c.icono} {c.nombre}</span>
                    ))}
                  </div>
                )}
              </div>
            </div>

            {/* Datos del usuario */}
            <div className="reserva-card reserva-user-card">
              <h3>👤 Tus datos</h3>
              <div className="reserva-user-info">
                <div className="reserva-user-avatar">{user?.initials}</div>
                <div>
                  <strong>{user?.nombre} {user?.apellido}</strong>
                  <span>{user?.email}</span>
                </div>
              </div>
            </div>

            {/* Resumen fechas */}
            {(fechaInicio || fechaFin) && (
              <div className="reserva-card reserva-summary">
                <h3>📅 Fechas seleccionadas</h3>
                <div className="reserva-summary-dates">
                  <div>
                    <label>Check-in</label>
                    <strong>{fechaInicio || '—'}</strong>
                  </div>
                  <div className="reserva-arrow">→</div>
                  <div>
                    <label>Check-out</label>
                    <strong>{fechaFin || '—'}</strong>
                  </div>
                </div>
                {fechaInicio && fechaFin && (
                  <p className="reserva-noches">
                    {Math.ceil((new Date(fechaFin) - new Date(fechaInicio)) / (1000 * 60 * 60 * 24))} noches
                  </p>
                )}
              </div>
            )}
          </aside>

          {/* Columna derecha: calendario */}
          <div className="reserva-main">
            <div className="reserva-cal-header">
              <h3>Seleccioná las fechas</h3>
              <p>Hacé clic en el día de llegada y luego en el día de salida.</p>
              <div className="reserva-cal-legend">
                <span><span className="leg-libre">■</span> Disponible</span>
                <span><span className="leg-ocupado">■</span> Ocupado</span>
                <span><span className="leg-sel">■</span> Seleccionado</span>
              </div>
            </div>

            <div className="reserva-calendar-double">
              {meses.map(({ year, month, diasEnMes, primerDia }) => {
                const nombreMes = new Date(year, month).toLocaleString('es-AR', { month: 'long', year: 'numeric' });
                const dias = [];
                for (let i = 0; i < primerDia; i++) dias.push(null);
                for (let d = 1; d <= diasEnMes; d++) dias.push(d);

                return (
                  <div key={`${year}-${month}`} className="res-cal-month">
                    <div className="res-cal-title">{nombreMes.charAt(0).toUpperCase() + nombreMes.slice(1)}</div>
                    <div className="res-cal-weekdays">
                      {['Do','Lu','Ma','Mi','Ju','Vi','Sa'].map(d => <span key={d}>{d}</span>)}
                    </div>
                    <div className="res-cal-days">
                      {dias.map((d, i) => {
                        if (!d) return <span key={`e-${i}`} />;
                        const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
                        const ocupado = esFechaOcupada(dateStr, fechasOcupadas);
                        const pasado = dateStr < hoy;
                        const esInicio = dateStr === fechaInicio;
                        const esFin = dateStr === fechaFin;
                        const enRango = estaEnRango(dateStr);
                        return (
                          <span
                            key={d}
                            className={`res-cal-day
                              ${ocupado ? 'ocupado' : ''}
                              ${pasado ? 'pasado' : ''}
                              ${esInicio || esFin ? 'seleccionado' : ''}
                              ${enRango ? 'en-rango' : ''}
                              ${!ocupado && !pasado ? 'clickable' : ''}
                            `}
                            onClick={() => !ocupado && !pasado && handleDiaClick(dateStr)}
                            onMouseEnter={() => fechaInicio && !fechaFin && setHover(dateStr)}
                            onMouseLeave={() => setHover('')}
                          >
                            {d}
                          </span>
                        );
                      })}
                    </div>
                  </div>
                );
              })}
            </div>

            {reservaError && <div className="error-msg" style={{ marginTop: '1rem' }}>{reservaError}</div>}

            <button
              className="btn btn-primary reserva-confirm-btn"
              onClick={handleConfirmar}
              disabled={!fechaInicio || !fechaFin || enviando}
            >
              {enviando ? 'Procesando...' : 'Confirmar reserva →'}
            </button>
          </div>
        </div>
      </div>
    </main>
  );
}
