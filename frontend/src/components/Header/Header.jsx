import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './Header.css';

export default function Header() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate('/');
  };

  const go = (path) => { navigate(path); setMenuOpen(false); };

  return (
    <header className="header">
      <div className="header-inner container">
        <Link to="/" className="header-brand">
          <span className="header-logo">🏨</span>
          <div>
            <span className="header-name">HotelHub</span>
            <span className="header-slogan">Tu descanso, nuestra pasión</span>
          </div>
        </Link>

        <nav className="header-nav">
          {user ? (
            <div className="header-user">
              <div className="header-avatar" onClick={() => setMenuOpen(o => !o)}>
                <span>{user.initials}</span>
              </div>
              {menuOpen && (
                <div className="header-dropdown">
                  <div className="header-dropdown-info">
                    <strong>{user.nombre} {user.apellido}</strong>
                    <span>{user.email}</span>
                    <span className="header-rol-badge">{user.rol}</span>
                  </div>
                  <button onClick={() => go('/favoritos')}>❤️ Mis favoritos</button>
                  <button onClick={() => go('/mis-reservas')}>📋 Mis reservas</button>
                  {isAdmin() && (
                    <button onClick={() => go('/administracion')}>⚙️ Panel Admin</button>
                  )}
                  <button onClick={handleLogout}>🚪 Cerrar sesión</button>
                </div>
              )}
            </div>
          ) : (
            <>
              <button className="btn btn-outline" onClick={() => navigate('/login')}>Iniciar sesión</button>
              <button className="btn btn-primary" onClick={() => navigate('/register')}>Crear cuenta</button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
