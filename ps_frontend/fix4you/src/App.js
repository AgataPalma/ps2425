import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import ClientProfile from './pages/ClientProfile';
import ProfessionalProfile from './pages/ProfessionalProfile';
import ClientRequestsHistory from './pages/ClientRequestsHistory';
import ProfessionalRequestsHistory from './pages/ProfessionalRequestsHistory';
import ProfessionalCalendar from './pages/ProfessionalCalendar';
import Home from './pages/Home';
import RegisterChoice from './pages/RegisterChoice';
import Login from './pages/Login';
import RegisterClient from './pages/RegisterClient';
import RegisterProfessional from './pages/RegisterProfessional';
import Header from './components/Header';
import 'tailwindcss/tailwind.css';
import PrincipalPageClient from "./pages/PrincipalPageClient";
import PrincipalPageProfessional from "./pages/PrincipalPageProfessional";
import NewRequests from "./pages/NewRequests";
import RequestServiceGeneric from "./pages/RequestServiceGeneric";
import RequestServiceToProfessional from "./pages/RequestServiceToProfessional";

function App() {

    const [userType, setUserType] = useState(null);
    const [userId, setUserId] = useState(null);
    const [loading, setLoading] = useState(true);

    const handleLogin = (type, id) => {
        setUserType(type);
        setUserId(id);
        localStorage.setItem('userType', type);
        localStorage.setItem('userId', id);
    };

    const handleLogout = () => {
        setUserType(null);
        setUserId(null);
        localStorage.removeItem('userType');
        localStorage.removeItem('userId');
        localStorage.removeItem('token');
    };

    useEffect(() => {
        const type = localStorage.getItem('userType');
        const id = localStorage.getItem('userId');

        if (type && id) {
            setUserType(type);
            setUserId(id);
        }
        setLoading(false);
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    const ProtectedRoute = ({ children, allowedUserType }) => {
        if (!userType) {
            return <Navigate to="/Login" />;
        }
        if (allowedUserType && userType !== allowedUserType) {
            return <Navigate to="/Home" />;
        }
        return children;
    };

    return (
        <Router>
            <div>
                <Header userType={userType} handleLogout={handleLogout} />
                <Routes>
                    <Route path="/" element={<Navigate to="/Home" />} />
                    <Route path="/Home" element={<Home />} />
                    <Route path="/RegisterChoice" element={!userType ? <RegisterChoice /> : <Navigate to="/Home" />} />
                    <Route
                        path="/Login"
                        element={
                            !userType ? <Login onLogin={handleLogin} /> : <Navigate to="/Home"  />
                        }
                    />
                    <Route
                        path="/RegisterClient"
                        element={
                            !userType ? <RegisterClient /> : <Navigate to="/Home" />
                        }
                    />
                    <Route path="/RegisterProfessional" element={!userType ? <RegisterProfessional /> : <Navigate to="/Home" />} />
                    <Route
                        path="/client-profile"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <ClientProfile id={userId}/>
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/client-requests-history"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <ClientRequestsHistory id={userId}/>
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/professional-profile"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <ProfessionalProfile id={userId}/>
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/professional-requests-history"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <ProfessionalRequestsHistory id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/professional-calendar"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <ProfessionalCalendar id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/PrincipalPageClient"
                        element={
                            <PrincipalPageClient id={userId}/>
                        }
                    />
                    <Route
                        path="/PrincipalPageProfessional/:id"
                        element={
                            <PrincipalPageProfessional id={userId} />
                        }
                    />
                    <Route
                        path="/NewRequests"
                        element={
                            <NewRequests id={userId}/>
                        }
                    />
                    <Route
                        path="/RequestServiceGeneric"
                        element={
<<<<<<< Updated upstream
                            <RequestServiceGeneric />
                        }
                    />
                    <Route
                        path="/RequestServiceToProfessional"
                        element={
                            <RequestServiceToProfessional />
=======
                            <RequestService id={userId}/>
>>>>>>> Stashed changes
                        }
                    />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
