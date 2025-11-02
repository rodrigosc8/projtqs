import { useState, useEffect } from 'react'
import axios from 'axios'
import BookingTable from '../components/BookingTable'
import BookingDetails from '../components/BookingDetails'

export default function StaffPage() {
  const [bookings, setBookings] = useState([])
  const [municipalities, setMunicipalities] = useState([])
  const [filters, setFilters] = useState({
    municipality: '',
    status: '',
    date: ''
  })
  const [selectedToken, setSelectedToken] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadMunicipalities()
    loadBookings()
  }, [])

  const loadMunicipalities = async () => {
    try {
      const response = await axios.get('/api/municipalities')
      setMunicipalities(response.data)
    } catch (err) {
      console.error('Erro:', err)
    }
  }

  const loadBookings = async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams()
      if (filters.municipality) params.append('municipality', filters.municipality)
      if (filters.status) params.append('status', filters.status)
      if (filters.date) params.append('date', filters.date)
      params.append('page', '0')
      params.append('size', '100')

      const response = await axios.get(`/api/bookings?${params}`)
      setBookings(response.data.content || response.data)
    } catch (err) {
      console.error('Erro:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleFilterChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value
    })
  }

  return (
    <div className="staff-page">
      <h1>Gestão de Reservas</h1>

      <div className="card">
        <h3>Filtros</h3>
        <div className="filter-grid">
          <div className="form-group">
            <label>Município</label>
            <select name="municipality" value={filters.municipality} onChange={handleFilterChange}>
              <option value="">Todos</option>
              {municipalities.map(m => (
                <option key={m.id} value={m.name}>{m.name}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Estado</label>
            <select name="status" value={filters.status} onChange={handleFilterChange}>
              <option value="">Todos</option>
              <option value="RECEIVED">RECEIVED</option>
              <option value="ASSIGNED">ASSIGNED</option>
              <option value="IN_PROGRESS">IN_PROGRESS</option>
              <option value="COMPLETED">COMPLETED</option>
              <option value="CANCELLED">CANCELLED</option>
            </select>
          </div>

          <div className="form-group">
            <label>Data</label>
            <input 
              type="date" 
              name="date" 
              value={filters.date} 
              onChange={handleFilterChange}
            />
          </div>

          <div className="form-group-btn">
            <button onClick={loadBookings} className="btn btn-primary">
              {loading ? 'Filtrando...' : 'Filtrar'}
            </button>
          </div>
        </div>
      </div>

      <div className="card">
        <BookingTable 
          bookings={bookings} 
          onSelectBooking={(token) => setSelectedToken(token)}
        />
      </div>

      {selectedToken && (
        <BookingDetails
          token={selectedToken}
          onClose={() => setSelectedToken(null)}
          onUpdate={loadBookings}
        />
      )}
    </div>
  )
}
