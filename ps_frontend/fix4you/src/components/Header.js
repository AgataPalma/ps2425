import React from 'react';
import '../index.css';
import logo from '../images/logo_blue1.png';
import profileIcon from '../images/profile_icon.png';
import { Link } from 'react-router-dom';

const Header = ({ userType, handleLogout }) => {
    console.log("UserType in Header:", userType);
    const logoLink = userType === 'CLIENT' ? '/PrincipalPageClient' : userType === 'PROFESSIONAL' ? '/PrincipalPageProfessional' : userType === 'ADMIN' ? '/Home' : '/Home';
    const notificationsLink = userType === 'CLIENT' ? '/NotificationsClient' : '/NotificationsProfessional';

    return (
        <header className="bg-white shadow">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center py-4">

                <div className="flex-shrink-0">
                    <Link to={logoLink}>
                        <img src={logo} alt="Logo" className="h-10" />
                    </Link>
                </div>

                <div className="flex-grow flex justify-center items-center space-x-4">
                    {userType === 'CLIENT' && (
                        <>
                            <Link to="/RequestServiceGeneric" className="text-gray-800 hover:text-gray-900">Pedir um Serviço</Link>
                            <Link to="/ClientRequests" className="text-gray-800 hover:text-gray-900">Os meus pedidos</Link>
                            <Link to="/ClientRequestsHistory" className="text-gray-800 hover:text-gray-900">Histórico</Link>
                        </>
                    )}
                    {userType === 'PROFESSIONAL' && (
                        <>
                            <Link to="/NewRequests" className="text-gray-600 hover:text-gray-900">Novos Pedidos</Link>
                            <Link to="/ProfessionalRequests" className="text-gray-600 hover:text-gray-900">Serviços</Link>
                            <Link to="/ProfessionalCalendar" className="text-gray-600 hover:text-gray-900">Calendário</Link>
                        </>
                    )}
                    {userType === 'ADMIN' && (
                        <>
                            <Link to="/Dashboard" className="text-gray-600 hover:text-gray-900">Dashboard</Link>
                            <Link to="/UsersManagement" className="text-gray-600 hover:text-gray-900">Utilizadores</Link>
                            <Link to="/AdminManagement" className="text-gray-600 hover:text-gray-900">Administração</Link>
                            <Link to="/Payments" className="text-gray-600 hover:text-gray-900">Pagamentos</Link>
                            <Link to="/Top10" className="text-gray-600 hover:text-gray-900">Top</Link>
                            <Link to="/PlatformManagement" className="text-gray-600 hover:text-gray-900">Plataforma</Link>
                            <Link to="/Tickets" className="text-gray-600 hover:text-gray-900">Tickets</Link>
                        </>
                    )}
                </div>

                <div className="flex items-center space-x-4">
                    {!userType ? (
                        <>
                            <Link to="/Login" className="text-gray-600 hover:text-gray-900">Login</Link>
                            <Link to="/RegisterChoice" className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Registo</Link>
                        </>
                    ) : (
                        <>
                            {userType === 'PROFESSIONAL' && (
                                <Link to={notificationsLink} className="relative">
                                    <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        className="h-6 w-6 text-gray-800 hover:text-gray-900 transition"
                                        fill="none"
                                        viewBox="0 0 24 24"
                                        stroke="currentColor"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            strokeWidth="2"
                                            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .417-.163.821-.443 1.123L4 17h11z"
                                        />
                                    </svg>
                                </Link>
                            )}

                            {userType !== 'ADMIN' && (
                                <Link to={userType === 'CLIENT' ? "/ClientProfile" : "/ProfessionalProfile"}>
                                    <img src={profileIcon} alt="profile" className="h-10" />
                                </Link>
                            )}

                            <button
                                onClick={handleLogout}
                                className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition"
                            >
                                Logout
                            </button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
};

export default Header;
