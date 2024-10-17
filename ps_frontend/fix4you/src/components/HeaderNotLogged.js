// src/components/Header.js

import React from 'react';
import '../index.css'; 
import logo from '../images/logo.png'; 

const Header = () => {

  return (
    <header className="bg-white shadow">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center py-4">
        
        <div className="flex-shrink-0">
          <a href="/Home"><img src={logo} alt="Logo" className="h-10"/></a>
        </div>

        
        <div className="flex items-center space-x-4">
          <a href="/Login" className="text-gray-600 hover:text-gray-900">Login</a>
          <a href="/RegisterChoice" className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-blue-600 transition"> Registo </a>
        </div>
      </div>
    </header>
  );
};

export default Header;
