import React, { useState, useEffect } from 'react';
import '../index.css';
import user from '../images/user.png';
import { FaStar, FaStarHalfAlt } from 'react-icons/fa';
import Footer from '../components/Footer';
import axiosInstance from "../components/axiosInstance";
import { useNavigate } from 'react-router-dom';
import Select from "react-select";
import axios from 'axios';
import Spinner from "../components/Spinner";

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
    const [selectedPaymentMethods, setSelectedPaymentMethods] = useState([]);
    const [selectedLanguages, setSelectedLanguages] = useState([]);
    const [selectedProfessional, setSelectedProfessional] = useState(null);
    const [reviews, setReviews] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [activeTab, setActiveTab] = useState("information");
    const [isLoading, setIsLoading] = useState(true);
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

    const handleSearch = async () => {
        if (!searchText.trim()) {
            fetchData();
            return;
        }

        try {
            const response = await axiosInstance.get(
                `professional-category-views/flattened?filter=description=="${searchText}",name=="${searchText}"`
            );
            const results = response.data;

            setProfessionals(results);
        } catch (error) {
            console.error('Erro ao realizar a pesquisa:', error);
        }
    };

    const highlightSearchText = (text, searchText) => {
        if (!searchText.trim()) return text;

        const parts = text.split(new RegExp(`(${searchText})`, 'gi')); // Divide o texto em partes, preservando as palavras de busca

        return parts.map((part, index) =>
            part.toLowerCase() === searchText.toLowerCase() ? (
                <span key={index} className="bg-yellow-400">{part}</span>
            ) : (
                part
            )
        );
    };


    const handlePaymentMethodClick = (paymentMethod) => {
        setSelectedPaymentMethods((prev) =>
            prev.includes(paymentMethod)
                ? prev.filter((method) => method !== paymentMethod)
                : [...prev, paymentMethod]
        );
    };

    const handleLanguagesMethodClick = (language) => {
        setSelectedLanguages((prev) =>
            prev.includes(language)
                ? prev.filter((lang) => lang !== language)
                : [...prev, language]
        );
    };

    const handleIncludeTravelCostClick = (value) => {
        setIncludeTravelCost((prevValue) => (prevValue === value ? null : value));
    };

    const handleProfessionalClick = async (professional) => {
        try {
            setSelectedProfessional(professional);
            //console.log("Professional clicked:", professional);
            setActiveTab("information");

            const response = await axiosInstance.get(`/portfolioItems/user/${professional.professionalId}`);
            //console.log(professional.professionalId)

            if (response.data && response.data.length > 0) {
                const portfolioItem = response.data[0];
                //console.log("Portfolio Item Description:", portfolioItem.description);

                setSelectedProfessional((prev) => ({
                    ...prev,
                    portfolio: {
                        description: portfolioItem.description || "Sem descrição disponível.",
                        images: portfolioItem.byteContent || [],
                    },
                }));
            } else {
                setSelectedProfessional((prev) => ({
                    ...prev,
                    portfolio: {
                        description: "Nenhum portfólio disponível.",
                        images: [],
                    },
                }));
            }
        } catch (error) {
            console.error("Error fetching portfolio:", error);
            setSelectedProfessional((prev) => ({
                ...prev,
                portfolio: {
                    description: "Erro ao carregar o portfólio.",
                    images: [],
                },
            }));
        }
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
        if (selectedLanguages.length > 0) {
            const languageFilters = selectedLanguages
                .map((lang) => `languages.name=="${lang}"`) // Use selectedLanguages, não languages
                .join(',');
            filterQuery.push(languageFilters);
        }

        // Filtro de Formas de Pagamento (somente se houver seleção)
        if (selectedPaymentMethods.length > 0) {
            const paymentFilters = selectedPaymentMethods
                .map((payment) => `acceptedPayments.name=="${payment}"`) // Use selectedPaymentMethods, não paymentMethods
                .join(',');
            filterQuery.push(paymentFilters);
        }

        if (includeTravelCost !== null) {
            filterQuery.push(`chargesTravels==${includeTravelCost}`);
        }


        const filterString = filterQuery.join(';');


        const fetchFilteredProfessionals = async () => {
            try {
                const response = await axiosInstance.get(`/professional-category-views/flattened?filter=${filterString}`);
                setProfessionals(response.data);
            } catch (error) {
                console.error('Erro ao aplicar filtros:', error);
            }
        };

        fetchFilteredProfessionals();
    };

    const fetchData = async () => {
        try {
            // Fetch location data
            setIsLoading(true);
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

            // Fetch languages
            const languagesResponse = await axiosInstance.get('/languages');
            const languagesData = languagesResponse.data.map((languages) => ({
                value: languages.id,
                label: languages.name,
            }));
            setLanguages(languagesData);


            // Fetch paymentMethod
            const paymentMethodResponse = await axiosInstance.get('/paymentMethods');
            const paymentMethodData = paymentMethodResponse.data.map((paymentMethod) => ({
                value: paymentMethod.id,
                label: paymentMethod.name,
            }));
            setPaymentMethods(paymentMethodData);

            // Fetch professionals
            const professionalsResponse = await axiosInstance.get('/professional-category-views/flattened');
            setProfessionals(professionalsResponse.data);
        } catch (error) {
            console.error('Error fetching data:', error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        const fetchReviews = async () => {
            try {
                if (!selectedProfessional) return;
                const response = await axiosInstance.get(`/reviews?reviewedId=${selectedProfessional.professionalId}`);
                const filteredReviews = response.data.filter(
                    (review) => review.reviewedId === selectedProfessional.professionalId
                );
                setReviews(filteredReviews);
            } catch (error) {
                console.error('Error fetching reviews:', error);
                setReviews([]);
            }
        };

        fetchReviews();
        fetchData();
    }, [selectedProfessional]);

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            handleSearch(); // Realiza a pesquisa após um pequeno atraso
        }, 300); // Aguarda 300ms após o último input para evitar chamadas excessivas

        return () => clearTimeout(delayDebounceFn); // Limpa o timeout se o valor mudar antes de 300ms
    }, [searchText]);



    const resetFilters = () => {
        setLocation(null);
        setSelectedCategory(null);
        setFilters({ priceRange: '', classification: null });
        setLanguages([]);
        setPaymentMethods([]);
        setIncludeTravelCost(false);
        fetchData();
    };


    const handleShowDescription = (description) => {
        setActiveModalDescription(description);
    };

    if (isLoading) {
        return <Spinner />;
    }



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
                                    {languages.map((language) => (
                                        <button
                                            key={language.value}
                                            onClick={() => handleLanguagesMethodClick(language.label)}
                                            className={`px-4 py-2 rounded-full ${
                                                selectedLanguages.includes(language.label)
                                                    ? 'bg-yellow-600 text-white'
                                                    : 'bg-gray-300 text-gray-700'
                                            }`}
                                        >
                                            {language.label}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <div className="mt-4">
                                <h4 className="font-medium">Métodos de Pagamento</h4>
                                <div className="flex flex-wrap gap-2">
                                    {paymentMethods.map((paymentMethod) => (
                                        <button
                                            key={paymentMethod.value}
                                            onClick={() => handlePaymentMethodClick(paymentMethod.label)}
                                            className={`px-4 py-2 rounded-full ${
                                                selectedPaymentMethods.includes(paymentMethod.label)
                                                    ? 'bg-yellow-600 text-white'
                                                    : 'bg-gray-300 text-gray-700'
                                            }`}
                                        >
                                            {paymentMethod.label}
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
                            value={searchText}
                            onChange={(e) => setSearchText(e.target.value)}
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
                                        onClick={() => handleProfessionalClick(professional)}> {highlightSearchText(professional.name, searchText)}</h2>
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
                                <p className="text-gray-800 text-sm inline">
                                    {highlightSearchText(professional.description.length > 100 ? `${professional.description.slice(0, 100)}...` : professional.description, searchText)}
                                </p>
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
                                                professionalId: professional.professionalId,
                                                name: professional.name,
                                                category: { id: professional.categoryId, name: professional.categoryName },
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
                                {/* Modal Header */}
                                <div className="flex justify-between items-center mb-4">
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
                                <div className="border-b mb-4 flex space-x-4">
                                    <button
                                        className={`px-4 py-2 ${activeTab === "information" ? "border-b-2 border-yellow-600 text-yellow-600" : "text-gray-500"}`}
                                        onClick={() => setActiveTab("information")}
                                    >
                                        Information
                                    </button>
                                    <button
                                        className={`px-4 py-2 ${activeTab === "portfolio" ? "border-b-2 border-yellow-600 text-yellow-600" : "text-gray-500"}`}
                                        onClick={() => setActiveTab("portfolio")}
                                    >
                                        Portfolio
                                    </button>
                                    <button
                                        className={`px-4 py-2 ${activeTab === "reviews" ? "border-b-2 border-yellow-600 text-yellow-600" : "text-gray-500"}`}
                                        onClick={() => setActiveTab("reviews")}
                                    >
                                        Reviews
                                    </button>
                                </div>

                                {/* Tab Content */}
                                {activeTab === "information" && (
                                    <div>
                                        <p className="text-sm mb-2"><strong>Descrição:</strong> {selectedProfessional.description}</p>
                                        <p className="text-sm mb-2"><strong>Localização:</strong> {selectedProfessional.location}</p>
                                        <p className="text-sm mb-2"><strong>Idiomas:</strong> {selectedProfessional.languages.map(lang => lang.name).join(', ')}</p>
                                        <p className="text-sm mb-2"><strong>Formas de Pagamento Aceites:</strong> {selectedProfessional.acceptedPayments && selectedProfessional.acceptedPayments.length > 0
                                            ? selectedProfessional.acceptedPayments.map((method, index) => (
                                                <span key={index}>
                                {method.name}{index < selectedProfessional.acceptedPayments.length - 1 ? ', ' : ''}
                            </span>
                                            ))
                                            : 'Nenhuma forma de pagamento disponível'}</p>
                                    </div>
                                )}

                                {activeTab === "portfolio" && selectedProfessional.portfolio && (
                                    <div>
                                        <p className="text-sm mb-4"><strong>Descrição:</strong> {selectedProfessional.portfolio.description || "Sem descrição disponível."}</p>
                                        <div className="grid grid-cols-2 gap-4">
                                            {selectedProfessional.portfolio.images && selectedProfessional.portfolio.images.length > 0 ? (
                                                selectedProfessional.portfolio.images.map((image, index) => (
                                                    <img
                                                        key={index}
                                                        src={`data:image/jpeg;base64,${image}`}
                                                        alt={`Portfolio Image ${index + 1}`}
                                                        className="w-full h-32 object-cover rounded cursor-pointer"
                                                        onClick={() => setActiveModalDescription(`data:image/jpeg;base64,${image}`)}
                                                    />
                                                ))
                                            ) : (
                                                <p className="text-gray-500">Nenhuma imagem disponível.</p>
                                            )}
                                        </div>
                                    </div>
                                )}
                                {activeTab === "reviews" && (
                                    <div>
                                        <h3 className="text-lg font-bold text-gray-800 mb-4">Avaliações</h3>
                                        {reviews.length > 0 ? (
                                            reviews.map((review, index) => (
                                                <div key={index} className="mb-4">
                                                    <div className="flex items-center space-x-2">
                                                        <span className="text-yellow-500">
                                                            {Array.from({ length: 5 }, (_, starIndex) => (
                                                                <span key={starIndex} className={`${starIndex < review.classification ? "text-yellow-500" : "text-gray-300"}`}>
                                                                    ★
                                                                </span>
                                                            ))}
                                                        </span>
                                                        <span className="text-sm text-gray-600">({review.classification}/5)</span>
                                                    </div>
                                                    <p className="text-sm text-gray-800">{review.reviewDescription}</p>
                                                    <p className="text-xs text-gray-500">Data: {new Date(review.date).toLocaleDateString()}</p>
                                                </div>
                                            ))
                                        ) : (
                                            <p className="text-gray-500 italic">Nenhuma avaliação disponível para este profissional.</p>
                                        )}
                                    </div>
                                )}
                                {/* Enlarged Image Modal */}
                                {activeModalDescription && (
                                    <div
                                        className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center z-50"
                                        onClick={() => setActiveModalDescription(null)}
                                    >
                                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-lg relative">
                                            <img
                                                src={activeModalDescription}
                                                alt="Enlarged Portfolio"
                                                className="w-full h-auto object-contain"
                                            />
                                            <button
                                                onClick={() => setActiveModalDescription(null)}
                                                className="absolute top-2 right-2 text-gray-700 hover:text-gray-900"
                                            >
                                                ✕
                                            </button>
                                        </div>
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
