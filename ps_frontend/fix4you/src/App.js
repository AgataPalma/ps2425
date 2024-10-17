import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import RegisterChoice from './pages/RegisterChoice';
import Login from './pages/Login';
import RegisterClient from './pages/RegisterClient';
import RegisterProfessional from './pages/RegisterProfessional';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/Home" element={<Home />} />
        <Route path="/RegisterChoice" element={<RegisterChoice />} />
        <Route path="/Login" element={<Login />} />
        <Route path="/RegisterClient" element={<RegisterClient />} />
        <Route path="/RegisterProfessional" element={<RegisterProfessional />} />
      </Routes>
    </Router>
  );
}

export default App;
