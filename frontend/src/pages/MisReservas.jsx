import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMisReservas } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './MisReservas.css';

export default function MisReservas() {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoggedIn()) { navigate('/login'); return; }
    getMisReservas()
      .then(setReservas)
      .finally(() => setLoading(false));
  }, []);

  const formatFecha = (str) => {
    const [y, m, d] = str.split('-');
    return `${d}/${m}/${y}`;
  };

  const noches = (ini, fin) =>
    Math.ceil((new Date(fin) - new Date(ini)) / (1000 * 60 * 60 * 24));

  return (
    <main className="mis-reservas-page">
      <div className="container">
        <div className="mis-reservas-header">
          <h1>📋 Mis reservas</h1>
          <button className="btn btn-outline" onClick={() => navigate('/')}>← Inicio</button>
        </div>

        {loading ? (
          <div className="loading">Cargando reservas...</div>
        ) : reservas.length === 0 ? (
          <div className="mis-reservas-empty">
            <p>📭 Todavía no realizaste ninguna reserva.</p>
            <button className="btn btn-primary" onClick={() => navigate('/')}>Explorar habitaciones</button>
          </div>
        ) : (
          <div className="mis-reservas-list">
            {reservas.map(r => (
              <div key={r.id} className="reserva-item-card">
                <div className="reserva-item-img">
                  <img src={r.productoImagen || 'https://via.placeholder.com/120x90'} alt={r.productoNombre} />
                </div>
                <div className="reserva-item-info">
                  <div className="reserva-item-top">
                    {r.categoriaNombre && <span className="reserva-item-badge">{r.categoriaNombre}</span>}
                    <span className={`reserva-item-estado ${r.estado === 'Activa' ? 'activa' : 'finalizada'}`}>
                      {r.estado}
                    </span>
                  </div>
                  <h2 onClick={() => navigate(`/producto/${r.productoId}`)}>
                    {r.productoNombre}
                  </h2>
                  <div className="reserva-item-fechas">
                    <span>📅 {formatFecha(r.fechaInicio)}</span>
                    <span className="reserva-item-sep">→</span>
                    <span>{formatFecha(r.fechaFin)}</span>
                    <span className="reserva-item-noches">
                      · {noches(r.fechaInicio, r.fechaFin)} noches
                    </span>
                  </div>
                </div>
                <div className="reserva-item-actions">
                  <span className="reserva-item-id">Reserva #{r.id}</span>
                  <button className="btn btn-outline reserva-item-btn"
                    onClick={() => navigate(`/producto/${r.productoId}`)}>
                    Ver habitación
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  );
}
