import React, { useState, useEffect } from 'react';
import '../index.css';
import user from '../images/user.png';
import { FaStar } from 'react-icons/fa';
import Footer from '../components/Footer';
import axiosInstance from "../components/axiosInstance";
import { useNavigate } from 'react-router-dom';

const PrincipalPageClient = ({ id }) => {
    const [professionals, setProfessionals] = useState([]);
    const [isFilterModalOpen, setFilterModalOpen] = useState(false);
    const [activeModalDescription, setActiveModalDescription] = useState(null);
    const [includeTravelCost, setIncludeTravelCost] = useState(false);
    const [languages, setLanguages] = useState([]);
    const [paymentMethods, setPaymentMethods] = useState([]);

    const navigate = useNavigate();
    const [filters, setFilters] = useState({
        priceRange: '',
        location: '',
        category: '',
        languages: [],
        classification: 0,
        acceptedPayments: [],
        includeTravelCost: false
    });

    const toggleFilterModal = () => {
        setFilterModalOpen(!isFilterModalOpen);
    };

    useEffect(() => {
        axiosInstance.get('/professional-category-views')
            .then(response => {
                let filteredProfessionals = response.data;
                setProfessionals(filteredProfessionals);
            })
            .catch(error => {
                console.error('Error fetching professionals:', error);
            });
    }, []);


    const handleShowDescription = (description) => {
        setActiveModalDescription(description);
    };

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
                                    placeholder="Digite a localização"
                                />
                            </div>

                            <div className="mb-4">
                                <label className="text-sm font-medium">Preço</label>
                                <select
                                    value={filters.priceRange}
                                    className="w-full p-2 border rounded-lg"
                                >
                                    <option value="">Selecionar</option>
                                    <option value="LIMPEZA">51-100</option>
                                    <option value="ENCANAMENTO">100-200</option>
                                    <option value="LIMPEZA">51-100</option>
                                    <option value="ENCANAMENTO">100-200</option>
                                </select>
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
                            {/* Filtro de Linguagens */}
                            <div className="mt-4">
                                <h4 className="font-medium">Linguagens</h4>
                                <div className="flex flex-wrap gap-2">
                                    {['ENGLISH', 'PORTUGUESE', 'SPANISH', 'FRENCH'].map((lang) => (
                                        <button
                                            key={lang}
                                            className={`px-4 py-2 rounded-full ${languages.includes(lang) ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                        >
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Filtro de Formas de Pagamento */}
                            <div className="mt-4">
                                <h4 className="font-medium">Formas de Pagamento</h4>
                                <div className="flex flex-wrap gap-2">
                                    {['CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'CASH', 'BANK_TRANSFER'].map((paymentMethod) => (
                                        <button
                                            key={paymentMethod}
                                            className={`px-4 py-2 rounded-full ${paymentMethods.includes(paymentMethod) ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                        >

                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div className="mb-4 flex items-center justify-between mt-4">
                                <label className="text-sm font-medium">Custos de Deslocação</label>
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
                    <h2 className="text-yellow-600 text-3xl mb-4 font-bold">Pesquisar Por Serviço</h2>
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
                    {professionals.map((professional) => (
                        professional.categoryDescriptions.map((categoryDescription, index) => (
                            <div key={`${professional.id}-${index}`} className="rounded-xl shadow-lg bg-gray-50 p-4">
                                <div className="flex items-center space-x-4">
                                    <img
                                        src={professional.profileImage && professional.profileImage !== "base64EncodedImageString" && professional.profileImage.trim() !== ""
                                            ? `data:image/png;base64,${professional.profileImage}`
                                            : user}
                                        alt="Profile"
                                        className="w-20 h-20 rounded-full"
                                    />
                                    <div className="flex-1">
                                        <h2 className="text-2xl font-bold capitalize">{professional.name}</h2>
                                        <p className="text-sm font-medium capitalize">{categoryDescription.category}</p>
                                    </div>
                                    <div className="flex space-x-1">
                                        {[...Array(5)].map((_, starIndex) => (
                                            <FaStar
                                                key={starIndex}
                                                className={starIndex < professional.rating ? "text-yellow-600 w-6 h-6" : "text-gray-800 w-6 h-6"}
                                            />
                                        ))}
                                    </div>
                                </div>

                                <div className="mt-2">
                                    <p className="text-sm text-gray-800">{`€ ${categoryDescription.mediumPricePerService}`}</p>
                                </div>

                                <div className="mb-4">
                                    <p className="text-gray-800 text-sm inline">{professional.description.length > 100 ? `${professional.description.slice(0, 100)}...` : professional.description}</p>
                                    {professional.description.length > 100 && (
                                        <button
                                            className="text-yellow-600 text-sm inline ml-2"
                                            onClick={() => handleShowDescription(professional.description)}
                                        >
                                            Mostrar mais
                                        </button>
                                    )}
                                </div>

                                <div className="flex justify-center items-center mb-6 space-x-4 m-4">
                                    <div
                                        className={`flex items-center space-x-1 px-3 py-1 rounded-full ${categoryDescription.chargesTravels ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}>
                                        {categoryDescription.chargesTravels ? (
                                            <>
                                                <span className="text-white">✓</span>
                                                <span>Custos de deslocação</span>
                                            </>
                                        ) : (
                                            <>
                                                <span className="text-gray-700">✘</span>
                                                <span>Sem custo de viagem</span>
                                            </>
                                        )}
                                    </div>
                                </div>

                                <div className="flex justify-center">
                                    <button
                                        onClick={() => {
                                            navigate('/RequestServiceToProfessional', {
                                                state: {
                                                    professionalId: professional.id,
                                                    name: professional.name,
                                                    category: categoryDescription.category,
                                                    location: professional.location,
                                                    languages: professional.languages,
                                                    price: categoryDescription.mediumPricePerService,
                                                }
                                            });
                                        }}
                                        className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                                        Contactar
                                    </button>
                                </div>

                                {activeModalDescription && (
                                    <div
                                        className="fixed inset-0 bg-gray-800 bg-opacity-15 flex justify-center items-center z-50">
                                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
                                            <h2 className="text-2xl font-bold mb-4">Descrição</h2>
                                            <p className="text-sm">{activeModalDescription}</p>
                                            <button
                                                onClick={() => setActiveModalDescription(null)}
                                                className="mt-4 px-4 py-2 bg-gray-400 text-white rounded-lg"
                                            >
                                                Fechar
                                            </button>
                                        </div>
                                    </div>
                                )}
                            </div>
                        ))
                    ))}
                </section>


            </div>
            <Footer/>
        </div>
    );
};

export default PrincipalPageClient;
