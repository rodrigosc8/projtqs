export default function BookingTable({ bookings, onSelectBooking }) {
  if (!bookings || bookings.length === 0) {
    return <p className="text-center">Nenhuma reserva encontrada</p>
  }

  return (
    <div className="table-container">
      <table className="booking-table">
        <thead>
          <tr>
            <th>Token</th>
            <th>Município</th>
            <th>Data</th>
            <th>Período</th>
            <th>Estado</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {bookings.map(booking => (
            <tr key={booking.token}>
              <td>{booking.token.substring(0, 8)}...</td>
              <td>{booking.municipality}</td>
              <td>{booking.date}</td>
              <td>{booking.timeslot}</td>
              <td>
                <span className={`badge badge-${booking.status.toLowerCase()}`}>
                  {booking.status}
                </span>
              </td>
              <td>
                <button 
                  onClick={() => onSelectBooking(booking.token)}
                  className="btn btn-sm"
                >
                  Ver Detalhes
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
