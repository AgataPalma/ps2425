import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import ClientProfile from './pages/ClientProfile';
import ProfessionalProfile from './pages/ProfessionalProfile';
import ClientRequests from './pages/ClientRequests';
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
import Recover from "./pages/Recover";
import PasswordReset from "./pages/PasswordReset";
import ScheduleAppointments from "./pages/ScheduleAppointments";
import NotificationsClient from './pages/NotificationsClient';
import NotificationsProfessional from './pages/NotificationsProfessional';
import Dashboard from './pages/Admin/Dashboard';
import UsersManagement from "./pages/Admin/UsersManagement";
import AdminManagement from "./pages/Admin/AdminManagement";
import Payments from "./pages/Admin/Payments";
import Top10 from "./pages/Admin/Top10";
import PlatformManagement from "./pages/Admin/PlatformManagement";
import LanguageTab from "./pages/Admin/CategoriesTab";
import CategoriesTab from "./pages/Admin/LanguagesTab";
import PaymentMethodsTab from "./pages/Admin/PaymentMethodsTab";
import Tickets from "./pages/Admin/Tickets";
import Support from "./pages/Support";


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
        window.location.href = '/Home';
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
                    <Route path="/Recover" element={!userType ? <Recover /> : <Navigate to="/Home" />} />
                    <Route path="/PasswordReset" element={!userType ? <PasswordReset /> : <Navigate to="/Home" />} />
                    <Route
                        path="/Login"
                        element={
                            !userType
                                ? <Login onLogin={handleLogin} />
                                : userType === 'PROFESSIONAL'
                                    ? <Navigate to="/PrincipalPageProfessional" />
                                    : userType === 'CLIENT'
                                        ? <Navigate to="/PrincipalPageClient" />
                                        : userType === 'ADMIN'
                                            ? <Navigate to="/Dashboard" />
                                            : <Navigate to="/Home" />
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
                        path="/ClientProfile"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <ClientProfile id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ClientRequestsHistory"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <ClientRequestsHistory id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ClientRequests"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <ClientRequests id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ProfessionalProfile"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <ProfessionalProfile id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ProfessionalRequests"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <ProfessionalRequestsHistory id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ProfessionalCalendar"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <ProfessionalCalendar id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/PrincipalPageClient"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <PrincipalPageClient id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/PrincipalPageProfessional"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <PrincipalPageProfessional id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/NewRequests"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <NewRequests id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/RequestServiceGeneric"

                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <RequestServiceGeneric id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/RequestServiceToProfessional"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <RequestServiceToProfessional id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/ScheduleAppointments"
                        element={
                            <ScheduleAppointments id={userId} />

                        }
                    />
                    <Route
                        path="/NotificationsClient"
                        element={
                            <ProtectedRoute allowedUserType="CLIENT">
                                <NotificationsClient id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/NotificationsProfessional"
                        element={
                            <ProtectedRoute allowedUserType="PROFESSIONAL">
                                <NotificationsProfessional id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/Dashboard"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <Dashboard id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/UsersManagement"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <UsersManagement id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/AdminManagement"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <AdminManagement id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/Payments"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <Payments id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/Top10"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <Top10 id={userId} />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/Tickets"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <Tickets id={userId} />
                            </ProtectedRoute>
                        }
                    />
                    <Route
                        path="/PlatformManagement"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <PlatformManagement id={userId} />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/LanguageTab"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <LanguageTab />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/CategoriesTab"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <CategoriesTab />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/PaymentMethodsTab"
                        element={
                            <ProtectedRoute allowedUserType="ADMIN">
                                <PaymentMethodsTab />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/Support"
                        element={
                            <Support id={userId} userType={userType} />
                        }
                    />

                </Routes>
            </div>
        </Router>
    );
}

export default App;
