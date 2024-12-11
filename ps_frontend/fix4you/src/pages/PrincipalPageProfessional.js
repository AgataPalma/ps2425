import React, { useState, useEffect } from 'react';
import '../index.css';
import user from '../images/user.png';
import Footer from '../components/Footer';
import axiosInstance from "../components/axiosInstance";
import {FaStar} from "react-icons/fa";
import axios from "axios";
import Select from "react-select";
import Spinner from "../components/Spinner";

const PrincipalPageProfessional = ({ id }) => {

    const [requests, SetRequests] = useState([]);
    const [priceRange, setPriceRange] = useState('');
    const [rating, setRating] = useState(null);
    const [includeTravelCost, setIncludeTravelCost] = useState(false);
    const [includeInvoice, setIncludeInvoice] = useState(false);
    const [isFilterModalOpen, setFilterModalOpen] = useState(false);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [languages, setLanguages] = useState([]);
    const [confirmationModal, setConfirmationModal] = useState({ isVisible: false, request: null });
    const [error, setError] = useState('');
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [location, setLocation] = useState('');
    const [locationOptions, setLocationOptions] = useState([]);
    const [selectedLanguages, setSelectedLanguages] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [professionalCategories, setprofessionalCategories] = useState([]);
    const [includeIsUrgent, setIncludeIsUrgent] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const [filters, setFilters] = useState({
        location: '',
        category: '',
        languages: [],
        classification: 0,
        isUrgent: null
    });

    const handleSearch = async () => {
        if (!searchText.trim()) {
            fetchData();
            return;
        }

        try {
            const response = await axiosInstance.get(
                `/services?filter=description=="${searchText}",title=="${searchText}"`
            );
            const results = response.data;

            SetRequests(results);
        } catch (error) {
            console.error('Erro ao realizar a pesquisa:', error);
        }
    };


    const toggleFilterModal = () => {
        setFilterModalOpen(!isFilterModalOpen);
    };

    const handleLanguagesMethodClick = (language) => {
        setSelectedLanguages((prev) =>
            prev.includes(language)
                ? prev.filter((lang) => lang !== language)
                : [...prev, language]
        );
    };


    const handleIncludeUrgent = (value) => {
        setIncludeIsUrgent((prevValue) => (prevValue === value ? null : value));
    };

    const handleShowDescription = (request) => {
        setSelectedRequest(request);
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

            const professionalCategoriesResponse = await axiosInstance.get(`/professionals/professional-categories/${id}`);
            const professionalCategories = professionalCategoriesResponse.data;

            console.log(professionalCategories)


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


            if (professionalCategories.length > 0) {

                const filterQuery = professionalCategories
                    .map((category) => `category.name=="${category}"`)
                    .join(',');


               await axiosInstance
                    .get(`/services?filter=${filterQuery}`)
                    .then((response) => {
                        SetRequests(response.data); // Definir os dados dos serviços
                    })
                    .catch((error) => {
                        console.error('Erro ao buscar serviços:', error);
                    });
            } else {

                await axiosInstance
                    .get('/services')
                    .then((response) => {
                        SetRequests(response.data);
                    })
                    .catch((error) => {
                        console.error('Erro ao buscar serviços:', error);
                    });
            }


        } catch (error) {
            console.error('Error fetching data:', error);
        }
        finally {
            setIsLoading(false);
        }
    };

    const applyFilters = () => {
        let filterQuery = [];

        // Filtro de Categoria
        if (selectedCategory) {
            filterQuery.push(`category.name=="${selectedCategory.label}"`);
        }

        // Filtro de Localização
        if (location) {
            filterQuery.push(`location=="${location}"`);
        }

        // Filtro de Linguagens
        if (selectedLanguages.length > 0) {
            const languageFilters = selectedLanguages
                .map((lang) => `languages.name=="${lang}"`) // Use selectedLanguages, não languages
                .join(',');
            filterQuery.push(languageFilters);
        }


        // IsUrgent
        if (includeIsUrgent !== null) {
            filterQuery.push(`isUrgent==${includeIsUrgent}`);
        }

        // Gerar a string da query final
        const filterString = filterQuery.join(';');

        // Requisição para buscar profissionais com os filtros
        const fetchFilteredProfessionals = async () => {
            try {
                const response = await axiosInstance.get(`/services?filter=${filterString}`);
                SetRequests(response.data);
            } catch (error) {
                console.error('Erro ao aplicar filtros:', error);
            }
        };

        fetchFilteredProfessionals();
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


    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            handleSearch(); // Realiza a pesquisa após um pequeno atraso
        }, 300); // Aguarda 300ms após o último input para evitar chamadas excessivas

        return () => clearTimeout(delayDebounceFn); // Limpa o timeout se o valor mudar antes de 300ms
    }, [searchText]);


    const handleAcceptRequest = (request) => {
        setConfirmationModal({ isVisible: true, request });
    };

    const confirmAcceptRequest = () => {
        if (confirmationModal.request) {
            const payload = {
                professionalId: id,
                serviceId: confirmationModal.request.id
            };

            axiosInstance.put(`/services/accept-service?professionalId=${id}&serviceId=${confirmationModal.request.id}`)
                .then(response => {
                    console.log('Serviço aceito com sucesso:', response.data);
                    setConfirmationModal({ isVisible: false, request: null });
                })
                .catch(error => {
                    console.error('Erro ao aceitar o serviço:', error);
                    setError(
                        <>
                            Erro ao aceitar o serviço<br />
                            Por favor, tente mais tarde.
                        </>
                    );
                });
        }
    };

    const resetFilters = () => {
        setLocation(null);
        setSelectedCategory(null);
        setLanguages([]);
        setIncludeIsUrgent(null);
        fetchData();
    };


    const cancelAcceptRequest = () => {
        setConfirmationModal({ isVisible: false, request: null });
    };

    if (isLoading) {
        return <Spinner />;
    }

    return (
        <div className="flex flex-col min-h-screen text-black font-sans">
            <div className="bg-gray-800 bg-opacity-15 shadow-lg flex-grow">
                <br/>
                {/* Modal de Confirmação */}
                {confirmationModal.isVisible && (
                    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
                            <h2 className="text-gray-800 text-2xl font-bold mb-4 text-center">Confirmar Aceitação</h2>
                            {error && (
                                <div className="mb-4 p-2 bg-red-200 text-red-800 text-center rounded">
                                    {error}
                                </div>
                            )}
                            <p className="text-sm text-center mb-6">
                                Ao clicar em aceitar, este serviço ficará associado a você e o cliente será notificado.
                            </p>
                            <div className="flex justify-between">
                                <button
                                    onClick={cancelAcceptRequest}
                                    className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                                >
                                    Cancelar
                                </button>
                                <button
                                    onClick={confirmAcceptRequest}
                                    className="px-4 py-2 bg-yellow-600 text-white rounded-lg"
                                >
                                    Aceitar
                                </button>
                            </div>
                        </div>
                    </div>
                )}
                {/* Modal dos filtros */}
                {isFilterModalOpen && (
                    <div className="fixed inset-0 bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-11/12 max-w-sm">
                            <h2 className="text-gray-800 text-2xl font-bold mb-4 text-center flex justify-between items-center">
                                <span>Filtrar por</span>
                                <a
                                    onClick={() => {
                                        resetFilters();
                                        toggleFilterModal();
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
                                <h4 className="font-medium">Urgente</h4>
                                <div className="flex flex-wrap gap-2">
                                    {/* Botão "Sim" */}
                                    <button
                                        onClick={() => handleIncludeUrgent(true)}
                                        className={`px-4 py-2 rounded-full ${includeIsUrgent === true ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
                                    >
                                        Sim
                                    </button>

                                    {/* Botão "Não" */}
                                    <button
                                        onClick={() => handleIncludeUrgent(false)}
                                        className={`px-4 py-2 rounded-full ${includeIsUrgent === false ? 'bg-yellow-600 text-white' : 'bg-gray-300 text-gray-700'}`}
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
                    <h2 className="text-yellow-600 text-3xl mb-4 font-bold">Pesquisar Por Pedido</h2>
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
                    {requests.length > 0 ? (
                        requests
                        .filter(request => request.professionalId === null)
                        .map(request => (
                            <div key={request.id} className="rounded-xl shadow-lg bg-gray-50 p-4">
                                <div className="flex items-center space-x-4">
                                    <img
                                        src={
                                            request.clientProfileImage
                                                ? atob(request.clientProfileImage)
                                                : user
                                        }
                                        alt="Profile"
                                        className="w-20 h-20"
                                    />
                                    <div className="flex-1">
                                        <div className="flex items-center justify-between">
                                            <h3 className="text-xl font-semibold">
                                                {highlightSearchText(request.title, searchText)}
                                            </h3>
                                            <span
                                                className={`px-3 py-1 text-sm font-medium rounded-full ${
                                                    request.urgent ? 'bg-yellow-600 text-black' : 'bg-gray-300 text-black'
                                                }`}
                                            >
                                                {request.urgent ? 'Pedido Urgente' : 'Pedido Normal'}
                                            </span>
                                        </div>

                                        <p className="text-sm">{request.category.name}</p>
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
                                        <p className="text-gray-800 text-sm inline">
                                            {highlightSearchText(request.description.length > 100 ? `${request.description.slice(0, 100)}...` : request.description, searchText)}
                                        </p>
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
                                        onClick={() => handleAcceptRequest(request)}
                                        className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                                        Aceitar
                                    </button>
                                </div>
                            </div>
                        ))
                        ) : (
                        <p className="text-center text-gray-700 font-medium col-span-full">
                        Não foram encontrados serviços.
                        </p>
                        )}
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
