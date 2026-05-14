import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Header from './components/Header/Header';
import Footer from './components/Footer/Footer';
import WhatsAppButton from './components/WhatsAppButton/WhatsAppButton';
import Home from './pages/Home';
import ProductDetail from './pages/ProductDetail';
import Login from './pages/Login';
import Register from './pages/Register';
import Admin from './pages/Admin';
import Favoritos from './pages/Favoritos';
import Reserva from './pages/Reserva';
import Confirmacion from './pages/Confirmacion';
import MisReservas from './pages/MisReservas';
import './index.css';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/producto/:id" element={<ProductDetail />} />
          <Route path="/reserva/:id" element={<Reserva />} />
          <Route path="/reserva/:id/confirmacion" element={<Confirmacion />} />
          <Route path="/mis-reservas" element={<MisReservas />} />
          <Route path="/favoritos" element={<Favoritos />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/administracion" element={<Admin />} />
        </Routes>
        <Footer />
        <WhatsAppButton />
      </BrowserRouter>
    </AuthProvider>
  );
}
