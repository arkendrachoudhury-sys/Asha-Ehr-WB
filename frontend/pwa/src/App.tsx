import { useState } from 'react'
import './App.css'

function App() {
  const [activeTab, setActiveTab] = useState('dashboard')

  return (
    <div className="app-container">
      <header className="top-bar">
        <h1>ASHA CLINICAL EMR v1.1</h1>
        <p>Govt. of West Bengal • Health & FW Dept.</p>
      </header>

      <main className="content">
        <section className="bento-grid">
          <div className="card maternal">
            <h2>Maternal Health</h2>
            <p className="stat">Dynamic ANC tracking active</p>
          </div>
          <div className="card child">
            <h2>Child Immunization</h2>
            <p className="stat">WHO Growth Evaluator</p>
          </div>
          <div className="card ncd">
            <h2>NCD Screening</h2>
            <p className="stat">FHIR R4 Compliant</p>
          </div>
        </section>

        <nav className="tabs">
          <button
            className={activeTab === 'dashboard' ? 'active' : ''}
            onClick={() => setActiveTab('dashboard')}
          >
            Dashboard
          </button>
          <button
            className={activeTab === 'patients' ? 'active' : ''}
            onClick={() => setActiveTab('patients')}
          >
            Patients
          </button>
        </nav>

        {activeTab === 'dashboard' ? (
          <div className="tab-content">
            <h3>Welcome, Susmita Banerjee</h3>
            <p>Birbhum Sector 4 • Offline Mode Active</p>
            <div className="alert">
              <span className="icon">🚨</span>
              ABDM & ABHA ID Integration Enabled
            </div>
          </div>
        ) : (
          <div className="tab-content">
            <h3>Patient Directory</h3>
            <p>Standardized with FHIR R4 Resources</p>
          </div>
        )}
      </main>

      <footer className="footer">
        <button className="sync-btn">Sync Cloud Gateway</button>
      </footer>
    </div>
  )
}

export default App
