import React from 'react';
import '../index.css';
import logo from '../images/logo_blue1.png';
import profileIcon from '../images/profile_icon.png';
import { Link } from 'react-router-dom';

const Header = ({ userType, handleLogout }) => {
    console.log("UserType in Header:", userType);
    return (
        <header className="bg-white shadow">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center py-4">
                <div className="flex-shrink-0">
                    <Link to="/Home"><img src={logo} alt="Logo" className="h-10" /></Link>
                </div>
                <div className="flex items-center space-x-4">
                    {!userType ? (
                        <>
                            <Link to="/Login" className="text-gray-600 hover:text-gray-900">Login</Link>
                            <Link to="/RegisterChoice" className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Registo</Link>
                        </>

                    ) : (
                        <>
                            {userType === 'client' && (
                                <>
                                    {/*<Link to="/client-profile" className="text-gray-800 hover:text-gray-900">Perfil</Link>*/}
                                    <Link to="/client-requests-history" className="text-gray-800 hover:text-gray-900">Os meus pedidos</Link>
                                    <Link to="/client-profile"><img src={profileIcon} alt="profile" className="h-10" /></Link>
                                </>
                            )}
                            {userType === 'professional' && (
                                <>
                                    {/*<Link to="/professional-profile"
                                       className="text-gray-600 hover:text-gray-900">Perfil</Link>*/}
                                    <Link to="/professional-requests-history"
                                       className="text-gray-600 hover:text-gray-900">Histórico de Serviços</Link>
                                    <Link to="/professional-calendar"
                                       className="text-gray-600 hover:text-gray-900">Calendário</Link>
                                    <Link to="/professional-profile"><img src={profileIcon} alt="profile" className="h-10" /></Link>

                                </>
                            )}
                            <button onClick={handleLogout}
                                    className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Logout</button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
};

export default Header;