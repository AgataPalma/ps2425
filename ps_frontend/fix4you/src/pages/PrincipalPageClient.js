import React, { useState, useEffect } from 'react';
import '../index.css';
import user from '../images/user.png';
import { FaStar, FaStarHalfAlt } from 'react-icons/fa';
import Footer from '../components/Footer';
import axiosInstance from "../components/axiosInstance";
import { useNavigate } from 'react-router-dom';
import Select from "react-select";
import axios from 'axios';

const PrincipalPageClient = ({ id }) => {
    const [professionals, setProfessionals] = useState([]);
    const [isFilterModalOpen, setFilterModalOpen] = useState(false);
    const [activeModalDescription, setActiveModalDescription] = useState(null);
    const [includeTravelCost, setIncludeTravelCost] = useState(null);
    const [languages, setLanguages] = useState([]);
    const [paymentMethods, setPaymentMethods] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [location, setLocation] = useState('');
    const [locationOptions, setLocationOptions] = useState([]);
    const navigate = useNavigate();
    const [selectedProfessional, setSelectedProfessional] = useState(null);
    const [activeTab, setActiveTab] = useState("information");
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

    const handlePaymentMethodClick = (paymentMethod) => {
        setPaymentMethods((prevMethods) => {
            if (prevMethods.includes(paymentMethod)) {
                return prevMethods.filter((method) => method !== paymentMethod);
            } else {
                return [...prevMethods, paymentMethod];
            }
        });
    };

    const handleLanguagesMethodClick = (language) => {
        setLanguages((prevLanguages) => {
            if (prevLanguages.includes(language)) {
                return prevLanguages.filter((lang) => lang !== language);
            } else {
                return [...prevLanguages, language];
            }
        });
    };


    const handleIncludeTravelCostClick = (value) => {
        setIncludeTravelCost((prevValue) => (prevValue === value ? null : value));
    };



    const handleProfessionalClick = (professional) => {
        setSelectedProfessional(professional);
        setActiveTab("information"); // Reset to information tab on open
    };

    const applyFilters = () => {
        let filterQuery = [];

        // Filtro de Categoria
        if (selectedCategory) {
            filterQuery.push(`categoryName=="${selectedCategory.label}"`);
        }

        // Filtro de Localização
        if (location) {
            filterQuery.push(`location=="${location}"`);
        }

        // Filtro de Preço
        if (filters.priceRange) {
            filterQuery.push(`mediumPricePerService<=${filters.priceRange}`);
        }

        // Filtro de Classificação
        if (filters.classification) {
            filterQuery.push(`rating>=${filters.classification}`);
        }

        // Filtro de Linguagens
        if (languages.length > 0) {
            const languageFilters = languages.map((lang) => `languages=="${lang}"`).join(',');
            filterQuery.push(languageFilters);
        }

        // Filtro de Formas de Pagamento
        if (paymentMethods.length > 0) {
            const paymentFilters = paymentMethods.map((method) => `acceptedPayments=="${method}"`).join(',');
            filterQuery.push(paymentFilters);
        }


        // Filtro de Custos de Deslocação
        if (includeTravelCost !== null) {
            filterQuery.push(`chargesTravels==${includeTravelCost}`);
        }



        // Gerar a string da query final
        const filterString = filterQuery.join(';');

        // Requisição para buscar profissionais com os filtros
        const fetchFilteredProfessionals = async () => {
            try {
                const response = await axiosInstance.get(`/professional-category-views/flattened?filter=${filterString}`);
                setProfessionals(response.data);  // Atualiza a lista de profissionais com os resultados filtrados
            } catch (error) {
                console.error('Erro ao aplicar filtros:', error);
            }
        };

        fetchFilteredProfessionals();
    };

    const fetchData = async () => {
        try {
            // Fetch location data
            const locationResponse = await axios.get('https://json.geoapi.pt/municipios/freguesias');
            const organizedData = locationResponse.data.map((municipio) => ({
                label: municipio.nome,
                options: municipio.freguesias.map((freguesia) => ({
                    label: freguesia,
                    value: `${municipio.nome}, ${freguesia}`,
                })),
            }));
            setLocationOptions(organizedData);

            // Fetch categories
            const categoryResponse = await axiosInstance.get('/categories');
            const categoryData = categoryResponse.data.map((category) => ({
                value: category.id,
                label: category.name,
            }));
            setCategories(categoryData);

            // Fetch professionals
            const professionalsResponse = await axiosInstance.get('/professional-category-views/flattened');
            setProfessionals(professionalsResponse.data);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };


    useEffect(() => {
        fetchData();
    }, []);


    const resetFilters = () => {
        setLocation(null);             // Limpa a seleção de localização
        setSelectedCategory(null);     // Limpa a categoria selecionada
        setFilters({ priceRange: '', classification: null }); // Limpa preço e classificação
        setLanguages([]);              // Limpa as linguagens selecionadas
        setPaymentMethods([]);         // Limpa as formas de pagamento selecionadas
        setIncludeTravelCost(false);
        fetchData();
    };


    const handleShowDescription = (description) => {
        setActiveModalDescription(description);
    };

    return (
        <div className="flex flex-col min-h-screen text-black font-sans">
            <div className="bg-gray-800 bg-opacity-15 shadow-lg flex-grow">
                <br/>
                {/*FILTROS*/}
                {isFilterModalOpen && (
                    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
                            <h2 className="text-gray-800 text-2xl font-bold mb-4 text-center flex justify-between items-center">
                                <span>Filtrar por</span>
                                <a
                                    onClick={() => {
                                        resetFilters();  // Reseta todos os filtros
                                        toggleFilterModal();  // Fecha o modal de filtros
                                    }}
                                    className="text-yellow-600 underline text-sm cursor-pointer"
                                >
                                    Remover Filtros
                                </a>
                            </h2>


                            <div className="mb-4">
                                <label className="block text-black font-semibold mb-2">Localização *</label>
                                <Select
                                    options={locationOptions}
                                    onChange={(selectedOption) => setLocation(selectedOption.value)}
                                    placeholder="Selecione a sua freguesia"
                                    className="w-full p-2 bg-white bg-opacity-50 focus:outline-none focus:border-black"
                                    styles={{
                                        control: (provided) => ({
                                            ...provided,
                                            border: 'none',
                                        }),
                                    }}
                                />
                            </div>

                            <div className="mb-4">
                                <label className="block text-black font-semibold mb-2">Categoria *</label>
                                <Select
                                    options={categories}
                                    onChange={(selectedOption) => setSelectedCategory(selectedOption)}
                                    placeholder="Selecione a categoria"
                                    className="w-full p-2 bg-white bg-opacity-50 focus:outline-none focus:border-black"
                                    styles={{
                                        control: (provided) => ({
                                            ...provided,
                                            border: 'none',
                                        }),
                                    }}
                                />
                            </div>


                            <div className="mb-4">
                                <label className="text-sm font-medium">Intervalo de Preço (€)</label>
                                <input
                                    type="number"
                                    min="5"
                                    max="500"
                                    step="5"
                                    value={filters.priceRange || ''}
                                    onChange={(e) =>
                                        setFilters((prev) => ({
                                            ...prev,
                                            priceRange: e.target.value
                                        }))
                                    }
                                    placeholder="Digite o valor máximo"
                                    className="w-full p-2 bg-white bg-opacity-50 focus:outline-none focus:border-black"
                                />
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
                                    {['PORTUGUESE', 'ENGLISH', 'Espanhol', 'Francês'].map((language) => (
                                        <button
                                            key={language}
                                            onClick={() => handleLanguagesMethodClick(language)}
                                            className={`px-4 py-2 rounded-full ${languages.includes(language) ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                        >
                                            {language}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div className="mt-4">
                                <h4 className="font-medium">Formas de Pagamento</h4>
                                <div className="flex flex-wrap gap-2">
                                    {['Transferencia Bancária', 'CREDIT_CARD', 'CASH'].map((paymentMethod) => (
                                        <button
                                            key={paymentMethod}
                                            onClick={() => handlePaymentMethodClick(paymentMethod)}
                                            className={`px-4 py-2 rounded-full ${paymentMethods.includes(paymentMethod) ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                        >
                                            {paymentMethod}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div className="mt-4">
                                <h4 className="font-medium">Custos de Deslocação</h4>
                                <div className="flex flex-wrap gap-2">
                                    {/* Botão "Sim" */}
                                    <button
                                        onClick={() => handleIncludeTravelCostClick(true)} // Alterna para true ou null
                                        className={`px-4 py-2 rounded-full ${includeTravelCost === true ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                    >
                                        Sim
                                    </button>

                                    {/* Botão "Não" */}
                                    <button
                                        onClick={() => handleIncludeTravelCostClick(false)} // Alterna para false ou null
                                        className={`px-4 py-2 rounded-full ${includeTravelCost === false ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                    >
                                        Não
                                    </button>
                                </div>
                            </div>


                            <div className="flex justify-between mt-4">
                                <button
                                    onClick={toggleFilterModal}
                                    className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                                >
                                    Cancelar
                                </button>
                                <button
                                    onClick={() => {
                                        applyFilters();
                                        toggleFilterModal();
                                    }}
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
                        <div key={professional.id} className="rounded-xl shadow-lg bg-gray-50 p-4">
                            <div className="flex items-center space-x-4">
                                <img
                                    src={professional.profileImage && professional.profileImage !== "base64EncodedImageString" && professional.profileImage.trim() !== ""
                                        ? `data:image/png;base64,${professional.profileImage}`
                                        : user}
                                    alt="Profile"
                                    className="w-20 h-20 rounded-full cursor-pointer"
                                    onClick={() => handleProfessionalClick(professional)}
                                />
                                <div className="flex-1">
                                    <h2 className="text-2xl font-bold capitalize cursor-pointer"
                                        onClick={() => handleProfessionalClick(professional)}>{professional.name}</h2>
                                    <p className="text-sm font-medium capitalize">
                                        {professional.categoryName
                                            ? professional.categoryName.charAt(0).toUpperCase() + professional.categoryName.slice(1).toLowerCase()
                                            : 'Categoria não disponível'}
                                    </p>
                                </div>

                                <div className="flex space-x-1">
                                    {[...Array(5)].map((_, starIndex) => {
                                        const isFullStar = starIndex < Math.floor(professional.rating);
                                        const isHalfStar = starIndex === Math.floor(professional.rating) && professional.rating % 1 !== 0;

                                        return (
                                            <span key={starIndex}>
                            {isFullStar ? (
                                <FaStar className="text-yellow-600 w-6 h-6"/>
                            ) : isHalfStar ? (
                                <FaStarHalfAlt className="text-yellow-600 w-6 h-6"/>
                            ) : (
                                <FaStar className="text-gray-800 w-6 h-6"/>
                            )}
                        </span>
                                        );
                                    })}
                                </div>
                            </div>

                            <div className="mt-2">
                                <p className="text-sm text-gray-800">{`€ ${professional.mediumPricePerService || 'Preço não disponível'}`}</p>
                            </div>

                            <div className="mt-2">
                                <p className="text-sm text-gray-800">{`€ ${professional.location || 'Preço não disponível'}`}</p>
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
                                    className={`flex items-center space-x-1 px-3 py-1 rounded-full ${professional.chargesTravels ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}>
                                    {professional.chargesTravels ? (
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
                                                category: professional.categoryName,
                                                location: professional.location,
                                                languages: professional.languages,
                                                price: professional.mediumPricePerService,
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
                    ))}

                    {selectedProfessional && (
                        <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-md">
                                <div className="flex justify-between items-center mb-4">
                                    {/* Professional's profile picture and name */}
                                    <div className="flex items-center space-x-4">
                                        <img
                                            src={
                                                selectedProfessional.profileImage && selectedProfessional.profileImage !== "base64EncodedImageString" && selectedProfessional.profileImage.trim() !== ""
                                                    ? `data:image/png;base64,${selectedProfessional.profileImage}`
                                                    : user
                                            }
                                            alt="Profile"
                                            className="w-16 h-16 rounded-full"
                                        />
                                        <h2 className="text-2xl font-bold">{selectedProfessional.name}</h2>
                                    </div>
                                    <button
                                        onClick={() => setSelectedProfessional(null)}
                                        className="text-gray-500 hover:text-gray-700"
                                    >
                                        ✕
                                    </button>
                                </div>

                                {/* Tabs */}
                                <div className="border-b mb-4">
                                    <button
                                        className={`px-4 py-2 ${activeTab === "information" ? "border-b-2 border-yellow-600 text-yellow-600" : "text-gray-500"}`}
                                        onClick={() => setActiveTab("information")}
                                    >
                                        Information
                                    </button>
                                    {selectedProfessional.portfolio && (
                                        <button
                                            className={`px-4 py-2 ${activeTab === "portfolio" ? "border-b-2 border-yellow-600 text-yellow-600" : "text-gray-500"}`}
                                            onClick={() => setActiveTab("portfolio")}
                                        >
                                            Portfolio
                                        </button>
                                    )}
                                </div>

                                {/* Tab Content */}
                                {activeTab === "information" && (
                                    <div>
                                        <p className="text-sm mb-2"><strong>Descrição:</strong> {selectedProfessional.description}</p>
                                        <p className="text-sm mb-2"><strong>Localização:</strong> {selectedProfessional.location}</p>
                                        <p className="text-sm mb-2"><strong>Idiomas:</strong> {selectedProfessional.languages?.join(', ')}</p>
                                        <p className="text-sm mb-2"><strong>Formas de Pagamento Aceitas:</strong> {selectedProfessional.paymentMethods}</p>
                                    </div>
                                )}

                                {activeTab === "portfolio" && selectedProfessional.portfolio && (
                                    <div>
                                        {/* Display portfolio items here, e.g., images or project details */}
                                        <p className="text-sm mb-2">Portfolio Content Here</p>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}
                </section>


            </div>
            <Footer/>
        </div>
    );
};

export default PrincipalPageClient;
