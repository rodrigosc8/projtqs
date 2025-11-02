import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage'
import CitizenPage from './pages/CitizenPage'
import StaffPage from './pages/StaffPage'
import SearchPage from './pages/SearchPage'
import NavBar from './components/NavBar'

function App() {
  return (
    <Router>
      <div className="app">
        <NavBar />
        <div className="container">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/citizen" element={<CitizenPage />} />
            <Route path="/staff" element={<StaffPage />} />
            <Route path="/search" element={<SearchPage />} />
          </Routes>
        </div>
      </div>
    </Router>
  )
}

export default App
