import React, { useState, useEffect } from 'react';
import '../index.css';
import user from '../images/user.png';
import Footer from '../components/Footer';
import axiosInstance from "../components/axiosInstance";
import {FaStar} from "react-icons/fa";

const PrincipalPageProfessional = ({ id }) => {

    const [requests, SetRequests] = useState([]);
    const [priceRange, setPriceRange] = useState('');
    const [rating, setRating] = useState(null);
    const [includeTravelCost, setIncludeTravelCost] = useState(false);
    const [includeInvoice, setIncludeInvoice] = useState(false);
    const [isFilterModalOpen, setFilterModalOpen] = useState(false);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [languages, setLanguages] = useState([]);
    const [filters, setFilters] = useState({
        location: '',
        category: '',
        languages: [],
        classification: 0,
        urgent: false
    });

    const toggleFilterModal = () => {
        setFilterModalOpen(!isFilterModalOpen);
    };

    const handleShowDescription = (request) => {
        setSelectedRequest(request); // Show the full description in the modal
    };

    useEffect(() => {
        axiosInstance.get('/services')
            .then(response => {
                SetRequests(response.data); // Set data with axios response
            })
            .catch(error => {
                console.error('Error fetching professionals:', error);
            });
    }, [id]);

    return (
        <div className="flex flex-col min-h-screen text-black font-sans">
            <div className="bg-gray-800 bg-opacity-15 shadow-lg flex-grow">
                <br/>

                {isFilterModalOpen && (
                    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
                            <h2 className="text-gray-800 text-2xl font-bold mb-4 text-center">Filtrar por</h2>

                            <div className="mb-4">
                                <label className="text-sm font-medium">Localização</label>
                                <input
                                    type="text"
                                    value={filters.location}
                                    className="w-full p-2 border rounded-lg"
                                    placeholder="Localização"
                                />
                            </div>

                            <div className="mb-4">
                                <label className="text-sm font-medium">Categoria</label>
                                <select
                                    value={filters.category}
                                    className="w-full p-2 border rounded-lg"
                                >
                                    <option value="">Selecione a categoria</option>
                                    <option value="LIMPEZA">Limpeza</option>
                                    <option value="ENCANAMENTO">Encanamento</option>
                                    <option value="ELETRICIDADE">Eletricidade</option>
                                    <option value="JARDINAGEM">Jardinagem</option>
                                    <option value="PINTURA">Pintura</option>
                                    <option value="OUTRO">Outro</option>
                                </select>
                            </div>

                            <div className="mb-4">
                                <label className="text-sm font-medium">Classificação</label>
                                <div className="flex items-center">
                                    {[...Array(5)].map((_, index) => (
                                        <FaStar
                                            key={index}
                                            onClick={() => setFilters(prevFilters => ({
                                                ...prevFilters,
                                                classification: index + 1
                                            }))}
                                            className={index < filters.classification ? "text-yellow-600 w-6 h-6 cursor-pointer" : "text-gray-400 w-6 h-6 cursor-pointer"}
                                        />
                                    ))}
                                </div>
                            </div>

                            <div className="mt-4">
                                <h4 className="font-medium">Linguagens</h4>
                                <div className="flex flex-wrap gap-2">
                                    {['ENGLISH', 'PORTUGUESE', 'SPANISH', 'FRENCH'].map((lang) => (
                                        <button
                                            key={lang}
                                            className={`px-4 py-2 rounded-full ${languages.includes(lang) ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                        >
                                            {lang}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div className="mb-4 flex items-center justify-between mt-4">
                                <label className="text-sm font-medium">Urgente</label>
                                <label className="inline-flex items-center cursor-pointer">
                                    <span className="mr-2">Não</span>
                                    <div className="relative">
                                        <input
                                            type="checkbox"
                                            checked={includeTravelCost}
                                            onChange={() => setIncludeTravelCost(!includeTravelCost)}
                                            className="hidden"
                                        />
                                        <div
                                            className={`toggle-path w-10 h-5 rounded-full transition-all ${includeTravelCost ? 'bg-yellow-600' : 'bg-gray-300'}`}
                                        ></div>
                                        <div
                                            className={`toggle-circle absolute top-0.5 left-0.5 w-4 h-4 bg-white rounded-full transition-all ${includeTravelCost ? 'translate-x-5' : ''}`}
                                        ></div>
                                    </div>
                                    <span className="ml-2">Sim</span>
                                </label>
                            </div>


                            <div className="flex justify-between mt-4">
                                <button
                                    onClick={toggleFilterModal}
                                    className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                                >
                                    Cancelar
                                </button>
                                <button
                                    onClick={toggleFilterModal}
                                    className="px-4 py-2 bg-yellow-600 text-white rounded-lg"
                                >
                                    Aplicar Filtros
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                <section className="mt-6 text-center">
                    <h2 className="text-yellow-600 text-3xl mb-4 font-bold">Pesquisar Por Pedido</h2>
                    <div className="flex justify-center items-center space-x-2">
                        <input
                            type="text"
                            placeholder="Pesquisar..."
                            className="bg-yellow-600 w-96 px-4 py-2 rounded-full focus:outline-none placeholder-black"
                        />
                        <button
                            onClick={toggleFilterModal}
                            className="bg-yellow-600 p-2 rounded-full hover:bg-yellow-700">
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                className="h-6 w-6"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth="2"
                                    d="M4 6h16M4 12h16m-7 6h7"
                                />
                            </svg>
                        </button>
                    </div>
                </section>
                <br/><br/>
                <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                    {requests
                        .filter(request => request.professionalId === null)
                        .map(request => (
                            <div key={request.id} className="rounded-xl shadow-lg bg-gray-50 p-4">
                                <div className="flex items-center space-x-4">
                                    <img src={user} alt="Profile" className="w-20 h-20"/>
                                    <div className="flex-1">
                                        <h3 className="text-xl font-semibold">{request.title}</h3>
                                        <p className="text-sm">{request.category}</p>
                                    </div>
                                </div>
                                <div className="mt-4">
                                    <p className="text-sm font-semibold flex items-center">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            className="h-4 w-4 mr-2"
                                            fill="none"
                                            viewBox="0 0 24 24"
                                            stroke="currentColor"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth="2"
                                                d="M12 2C8.134 2 5 5.134 5 9c0 4 5 11 7 11s7-7 7-11c0-3.866-3.134-7-7-7zm0 7a2 2 0 11-4 0 2 2 0 014 0z"
                                            />
                                        </svg>
                                        {request.location}
                                    </p>

                                    <div className="mb-4">
                                        <p className="text-gray-800 text-sm inline">{request.description.length > 100 ? `${request.description.slice(0, 100)}...` : request.description}</p>

                                        {request.description.length > 100 && (
                                            <button
                                                className="text-yellow-600 text-sm inline ml-2"
                                                onClick={() => handleShowDescription(request)} // Passing the full request object
                                            >Mostrar mais
                                            </button>
                                        )}
                                    </div>
                                </div>
                                <div className="mt-4 flex justify-center">
                                    <button
                                        className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                                        Aceitar
                                    </button>
                                </div>
                            </div>
                        ))}

                </section>

                {selectedRequest && (
                    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-lg max-h-96 overflow-auto">
                            <h2 className="text-2xl font-bold mb-4">Descrição</h2>
                            <p className="text-sm">{selectedRequest.description}</p>
                            <button
                                onClick={() => setSelectedRequest(null)}
                                className="mt-4 px-4 py-2 bg-gray-400 text-white rounded-lg"
                            >
                                Fechar
                            </button>
                        </div>
                    </div>
                )}
            </div>

            <Footer/>
        </div>
    );
};

export default PrincipalPageProfessional;
