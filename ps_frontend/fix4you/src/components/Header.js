import React from 'react';
import '../index.css';
import logo from '../images/logo_blue1.png';
import profileIcon from '../images/profile_icon.png';
import { Link } from 'react-router-dom';

const Header = ({ userType, handleLogout }) => {

    console.log("UserType in Header:", userType);
    const logoLink = userType === 'CLIENT' ? '/PrincipalPageClient' : userType === 'PROFESSIONAL' ? '/PrincipalPageProfessional' : '/Home';


    return (
        <header className="bg-white shadow">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center py-4">

                <div className="flex-shrink-0">
                    <Link to={logoLink}>
                        <img src={logo} alt="Logo" className="h-10"/>
                    </Link>
                </div>

                <div className="flex-grow flex justify-center items-center space-x-4">
                    {userType === 'CLIENT' && (
                        <>
                            <Link to="/RequestServiceGeneric" className="text-gray-800 hover:text-gray-900">Pedir um
                                Serviço</Link>
                            <Link to="/ClientRequests" className="text-gray-800 hover:text-gray-900">Os meus pedidos</Link>
                            <Link to="/ClientRequestsHistory" className="text-gray-800 hover:text-gray-900">Histórico</Link>
                        </>
                    )}
                    {userType === 'PROFESSIONAL' && (
                        <>
                            <Link to="/NewRequests" className="text-gray-600 hover:text-gray-900">Novos Pedidos</Link>
                            <Link to="/ProfessionalRequests" className="text-gray-600 hover:text-gray-900">Histórico de Serviços</Link>
                            <Link to="/ProfessionalCalendar" className="text-gray-600 hover:text-gray-900">Calendário</Link>
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
                            <Link to={userType === 'CLIENT' ? "/ClientProfile" : "/ProfessionalProfile"}>
                                <img src={profileIcon} alt="profile" className="h-10" />
                            </Link>
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
