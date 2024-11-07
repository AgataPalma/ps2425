import React, { useState, useEffect } from 'react';
import '../index.css';
import user from '../images/user.png';
import Footer from '../components/Footer';
import axiosInstance from "../components/axiosInstance";

const PrincipalPageProfessional = ({ id }) => {

    const [requests, SetRequests] = useState([]);
    const [priceRange, setPriceRange] = useState('');
    const [rating, setRating] = useState(null);
    const [includeTravelCost, setIncludeTravelCost] = useState(false);
    const [includeInvoice, setIncludeInvoice] = useState(false);
    const [isFilterModalOpen, setFilterModalOpen] = useState(false);

    const toggleFilterModal = () => {
        setFilterModalOpen(!isFilterModalOpen);
    };

    const filteredServices = Array(6).fill(0).filter((_, index) => {
        return true;
    });

    useEffect(() => {
        axiosInstance.get('/services')
            .then(response => {
                SetRequests(response.data); // Set data with axios response
            })
            .catch(error => {
                console.error('Error fetching professionals:', error);
            });
    }, [id]);

    const capitalizeFirstLetter = (text) => {
        return text.toLowerCase().replace(/\b\w/g, (char) => char.toUpperCase());
    };

    return (
        <div className="h-screen text-black font-sans">
            <div className="bg-gray-800 bg-opacity-15 shadow-lg">
                <br/>

                {isFilterModalOpen && (
                    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
                            <h2 className="text-gray-800 text-2xl font-bold mb-4 text-center">Filtrar por</h2>

                            <select
                                value={priceRange}
                                onChange={(e) => setPriceRange(e.target.value)}
                                className="w-full mb-4 p-2 bg-white border border-gray-300 rounded-lg"
                            >
                                <option value="">Preço</option>
                                <option value="low">Baixo</option>
                                <option value="medium">Médio</option>
                                <option value="high">Alto</option>
                            </select>

                            <select
                                value={rating}
                                onChange={(e) => setRating(Number(e.target.value))}
                                className="w-full mb-4 p-2 bg-white border border-gray-300 rounded-lg"
                            >
                                <option value="">Classificação</option>
                                {[5, 4, 3, 2, 1].map(star => (
                                    <option key={star} value={star}>{star} Estrela{star > 1 && 's'}</option>
                                ))}
                            </select>

                            <label className="flex items-center space-x-2 mb-4">
                                <input
                                    type="checkbox"
                                    checked={includeTravelCost}
                                    onChange={() => setIncludeTravelCost(!includeTravelCost)}
                                    className="form-checkbox h-4 w-4 text-gray-600"
                                />
                                <span>Custo de Viagem</span>
                            </label>

                            <label className="flex items-center space-x-2 mb-4">
                                <input
                                    type="checkbox"
                                    checked={includeInvoice}
                                    onChange={() => setIncludeInvoice(!includeInvoice)}
                                    className="form-checkbox h-4 w-4 text-gray-600"
                                />
                                <span>Passa Fatura</span>
                            </label>

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
                                    <h3 className="text-xl font-semibold">{capitalizeFirstLetter(request.title)}</h3>
                                    <p className="text-sm">{capitalizeFirstLetter(request.category)}</p>
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
                                <div
                                    className="w-full bg-white mt-2 p-2 h-20 placeholder-black border border-black"
                                >
                                    {request.description}
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
                <Footer/>
            </div>
        </div>
    );
};

export default PrincipalPageProfessional;
