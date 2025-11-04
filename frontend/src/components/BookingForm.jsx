import { useState, useEffect } from 'react'
import axios from 'axios'

function BookingForm({ onSuccess }) {
  const [formData, setFormData] = useState({
    municipality: '',
    date: '',
    timeslot: '',
    description: ''
  })
  
  const [municipalities, setMunicipalities] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchMunicipalities = async () => {
      try {
        const response = await axios.get('/api/municipalities')
        setMunicipalities(response.data)
      } catch (err) {
        console.error('Erro ao carregar municípios:', err)
        setError('Erro ao carregar municípios')
      }
    }

    fetchMunicipalities()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {
      const response = await axios.post('/api/bookings', formData)
      console.log('Sucesso:', response.data)
      onSuccess(response.data)
      setFormData({ municipality: '', date: '', timeslot: '', description: '' })
    } catch (err) {
      console.error('Erro completo:', err)
      setError('Erro ao criar reserva. Verifique os dados.')
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Nova Reserva</h2>
      
      {error && <div style={{color: 'red'}}>{error}</div>}
      
      <div>
        <label>Município:</label>
        <select 
          name="municipality" 
          value={formData.municipality} 
          onChange={handleChange}
          required
        >
          <option value="">Selecione um município</option>
          {municipalities.map(mun => (
            <option key={mun} value={mun}>{mun}</option>
          ))}
        </select>
      </div>

      <div>
        <label>Data:</label>
        <input 
          type="date" 
          name="date" 
          value={formData.date} 
          onChange={handleChange}
          required
        />
      </div>

      <div>
        <label>Período:</label>
        <select 
          name="timeslot" 
          value={formData.timeslot} 
          onChange={handleChange}
          required
        >
          <option value="">Selecione</option>
          <option value="Manhã">Manhã</option>
          <option value="Tarde">Tarde</option>
        </select>
      </div>

      <div>
        <label>Descrição:</label>
        <textarea 
          name="description" 
          value={formData.description} 
          onChange={handleChange}
          rows="4"
        />
      </div>

      <button type="submit" disabled={loading}>
        {loading ? 'A criar...' : 'Criar Reserva'}
      </button>
    </form>
  )
}

export default BookingForm
