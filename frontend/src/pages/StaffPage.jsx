import { useState, useEffect } from 'react';
import axios from 'axios';
import BookingTable from '../components/BookingTable';
import BookingDetails from '../components/BookingDetails';

export default function StaffPage() {
  const [bookings, setBookings] = useState([]);
  const [municipalities, setMunicipalities] = useState([]);
  const [filters, setFilters] = useState({
    municipality: '',
    status: '',
    date: ''
  });
  const [selectedToken, setSelectedToken] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadMunicipalities();
    loadBookings();
  }, []);

  const loadMunicipalities = async () => {
    try {
      const response = await axios.get('/api/municipalities');
      setMunicipalities(response.data);
    } catch (err) {
      console.error('Erro ao carregar municípios:', err);
    }
  };

  const loadBookings = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.municipality) params.append('municipality', filters.municipality);
      if (filters.status) params.append('status', filters.status);
      if (filters.date) params.append('date', filters.date);
      params.append('page', '0');
      params.append('size', '100');

      const response = await axios.get(`/api/bookings?${params}`);
      setBookings(response.data.content || response.data);
    } catch (err) {
      console.error('Erro ao carregar reservas:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value
    });
  };

  const handleFilter = () => {
    loadBookings();
  };

  const handleRowClick = (token) => {
    setSelectedToken(token);
  };

  const handleCloseDetails = () => {
    setSelectedToken(null);
  };

  return (
    <div className="container">
      <h2>Gestão de Reservas</h2>

      <div className="filters-section" style={{ marginBottom: '2rem' }}>
        <h3>Filtros</h3>
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div style={{ flex: '1', minWidth: '200px' }}>
            <label htmlFor="municipality" className="form-label">Município</label>
            <select
              id="municipality"
              name="municipality"
              value={filters.municipality}
              onChange={handleFilterChange}
              className="form-control"
            >
              <option value="">Todos</option>
              {municipalities.map((mun) => (
                <option key={mun} value={mun}>
                  {mun}
                </option>
              ))}
            </select>
          </div>

          <div style={{ flex: '1', minWidth: '200px' }}>
            <label htmlFor="status" className="form-label">Estado</label>
            <select
              id="status"
              name="status"
              value={filters.status}
              onChange={handleFilterChange}
              className="form-control"
            >
              <option value="">Todos</option>
              <option value="RECEIVED">Recebido</option>  
              <option value="PENDING">Pendente</option>
              <option value="APPROVED">Aprovado</option>
              <option value="COMPLETED">Completo</option>
              <option value="CANCELLED">Cancelado</option>
            </select>
          </div>

          <div style={{ flex: '1', minWidth: '200px' }}>
            <label htmlFor="date" className="form-label">Data</label>
            <input
              type="date"
              id="date"
              name="date"
              value={filters.date}
              onChange={handleFilterChange}
              className="form-control"
            />
          </div>

          <button 
            onClick={handleFilter} 
            className="btn btn--primary"
            disabled={loading}
          >
            {loading ? 'A filtrar...' : 'Filtrar'}
          </button>
        </div>
      </div>

      {loading ? (
        <p>A carregar...</p>
      ) : (
        <BookingTable 
          bookings={bookings} 
          onSelectBooking={handleRowClick}
        />
      )}

      {selectedToken && (
        <BookingDetails 
          token={selectedToken} 
          onClose={handleCloseDetails}
          onUpdate={loadBookings}  

        />
      )}
    </div>
  );
}
