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
import RequestService from "./pages/RequestService";

function App() {

    const [userType, setUserType] = useState(null); // 'client' or 'professional' or just null
    const [loading, setLoading] = useState(true);

    const handleLogin = (type) => {
        setUserType(type);
        localStorage.setItem('userType', type);
    };

    const handleLogout = () => {
        setUserType(null);
        localStorage.removeItem('userType');
        localStorage.removeItem('token');
    };

    useEffect(() => {
        handleLogin('professional'); //log in as a client or professional for testing


        const type = localStorage.getItem('userType');

        if (type) {

            setUserType(type);
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
                    <Route path="/Login" element={!userType ? <Login /> : <Navigate to="/Home" />} />
                    <Route path="/RegisterClient" element={!userType ? <RegisterClient /> : <Navigate to="/Home" />} />
                    <Route path="/RegisterProfessional" element={!userType ? <RegisterProfessional /> : <Navigate to="/Home" />} />
                    <Route
                        path="/client-profile/:id"
                        element={
                            <ProtectedRoute allowedUserType="client">
                                {/*<ClientProfile id="672a0106a8d9b243378a0e38" />*/}
                                <ClientProfile/>
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/client-requests-history"
                        element={
                            <ProtectedRoute allowedUserType="client">
                                <ClientRequestsHistory />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/professional-profile"
                        element={
                            <ProtectedRoute allowedUserType="professional">
                                <ProfessionalProfile id="672bc00b8df74a6a477f6405" />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/professional-requests-history"
                        element={
                            <ProtectedRoute allowedUserType="professional">
                                <ProfessionalRequestsHistory />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/professional-calendar"
                        element={
                            <ProtectedRoute allowedUserType="professional">
                                <ProfessionalCalendar />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/PrincipalPageClient"
                        element={
                            <PrincipalPageClient />
                        }
                    />
                    <Route
                        path="/PrincipalPageProfessional"
                        element={
                            <PrincipalPageProfessional />
                        }
                    />
                    <Route
                        path="/NewRequests"
                        element={
                            <NewRequests />
                        }
                    />
                    <Route
                        path="/RequestService"
                        element={
                            <RequestService />
                        }
                    />

                </Routes>
            </div>
        </Router>
    );
}

export default App;
