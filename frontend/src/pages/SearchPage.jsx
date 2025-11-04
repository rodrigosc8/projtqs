import { useState } from 'react';
import axios from 'axios';

export default function SearchPage() {
  const [token, setToken] = useState('');
  const [booking, setBooking] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setBooking(null);

    try {
      const response = await axios.get(`/api/bookings/${token}`);
      setBooking(response.data);
    } catch (err) {
      setError('Reserva não encontrada');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <h2>Pesquisar Reserva</h2>
      <form onSubmit={handleSearch}>
        <div className="form-group">
          <label htmlFor="token">Token da Reserva</label>
          <input
            type="text"
            id="token"
            name="token"  
            value={token}
            onChange={(e) => setToken(e.target.value)}
            placeholder="Introduza o token"
            className="form-control"
            required
          />
        </div>
        <button type="submit" className="btn btn--primary" disabled={loading}>
          {loading ? 'A pesquisar...' : 'Pesquisar'}
        </button>
      </form>

      {error && <div className="error-message" style={{color: 'red', marginTop: '1rem'}}>{error}</div>}

      {booking && (
        <div className="booking-result" style={{marginTop: '2rem'}}>
          <h3>Detalhes da Reserva</h3>
          <p><strong>Token:</strong> {booking.token}</p>
          <p><strong>Município:</strong> {booking.municipality}</p>
          <p><strong>Data:</strong> {new Date(booking.date).toLocaleDateString()}</p>
          <p><strong>Período:</strong> {booking.timeslot}</p>
          <p><strong>Estado:</strong> {booking.status}</p>
          <p><strong>Descrição:</strong> {booking.description}</p>
        </div>
      )}
    </div>
  );
}
