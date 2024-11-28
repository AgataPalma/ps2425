import React, { useState } from 'react';
import CategoriesTab from '../Admin/CategoriesTab';
import LanguagesTab from '../Admin/LanguagesTab';
import PaymentMethodsTab from '../Admin/PaymentMethodsTab';

const PlatformManagement = () => {
    const [activeTab, setActiveTab] = useState('Categories'); // Estado para alternar abas

    const renderActiveTab = () => {
        switch (activeTab) {
            case 'Categories':
                return <CategoriesTab />;
            case 'Languages':
                return <LanguagesTab />;
            case 'PaymentMethods':
                return <PaymentMethodsTab />;
            default:
                return null;
        }
    };

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h2 className="text-2xl font-bold mb-4 text-yellow-600">Gestão da Plataforma</h2>

            {/* Navegação por Tabs */}
            <nav className="flex space-x-8 border-b border-gray-300 mt-4">
                <button
                    className={`px-4 py-2 text-lg ${
                        activeTab === 'Categories'
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                    onClick={() => setActiveTab('Categories')}
                >
                    Categorias
                </button>
                <button
                    className={`px-4 py-2 text-lg ${
                        activeTab === 'Languages'
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                    onClick={() => setActiveTab('Languages')}
                >
                    Línguas
                </button>
                <button
                    className={`px-4 py-2 text-lg ${
                        activeTab === 'PaymentMethods'
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                    onClick={() => setActiveTab('PaymentMethods')}
                >
                    Métodos de Pagamento
                </button>
            </nav>

            {/* Conteúdo Ativo */}
            <main className="p-6">{renderActiveTab()}</main>
        </div>
    );
};

export default PlatformManagement;
