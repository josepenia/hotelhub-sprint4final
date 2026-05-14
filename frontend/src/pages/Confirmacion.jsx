import { useLocation, useNavigate } from 'react-router-dom';
import './Confirmacion.css';

export default function Confirmacion() {
  const { state } = useLocation();
  const navigate = useNavigate();

  if (!state) {
    navigate('/');
    return null;
  }

  const { producto, fechaInicio, fechaFin, user, reservaId } = state;
  const noches = Math.ceil((new Date(fechaFin) - new Date(fechaInicio)) / (1000 * 60 * 60 * 24));

  const formatFecha = (str) => {
    const [y, m, d] = str.split('-');
    return `${d}/${m}/${y}`;
  };

  return (
    <main className="confirmacion-page">
      <div className="confirmacion-card">
        {/* Ícono éxito */}
        <div className="confirm-check">✓</div>
        <h1 className="confirm-title">¡Reserva confirmada!</h1>
        <p className="confirm-sub">Tu reserva fue registrada exitosamente. Te esperamos.</p>

        {/* Detalle reserva */}
        <div className="confirm-detail">
          <div className="confirm-prod">
            <img
              src={producto?.imagenes?.[0] || 'https://via.placeholder.com/400x200'}
              alt={producto?.nombre}
              className="confirm-prod-img"
            />
            <div>
              {producto?.categoriaNombre && <span className="confirm-badge">{producto.categoriaNombre}</span>}
              <h2>{producto?.nombre}</h2>
            </div>
          </div>

          <div className="confirm-info-grid">
            <div className="confirm-info-item">
              <label>Reserva #</label>
              <strong>#{reservaId}</strong>
            </div>
            <div className="confirm-info-item">
              <label>Huésped</label>
              <strong>{user?.nombre} {user?.apellido}</strong>
            </div>
            <div className="confirm-info-item">
              <label>Email</label>
              <strong>{user?.email}</strong>
            </div>
            <div className="confirm-info-item">
              <label>Duración</label>
              <strong>{noches} {noches === 1 ? 'noche' : 'noches'}</strong>
            </div>
            <div className="confirm-info-item confirm-dates">
              <div>
                <label>Check-in</label>
                <strong>{formatFecha(fechaInicio)}</strong>
              </div>
              <span className="confirm-arrow">→</span>
              <div>
                <label>Check-out</label>
                <strong>{formatFecha(fechaFin)}</strong>
              </div>
            </div>
          </div>
        </div>

        {/* Acciones */}
        <div className="confirm-actions">
          <button className="btn btn-primary" onClick={() => navigate('/')}>
            Volver al inicio
          </button>
          <button className="btn btn-outline" onClick={() => navigate('/mis-reservas')}>
            Ver mis reservas
          </button>
        </div>
      </div>
    </main>
  );
}
