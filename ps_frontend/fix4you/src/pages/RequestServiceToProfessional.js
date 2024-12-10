import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Footer from "../components/Footer";
import axiosInstance from "../components/axiosInstance";

const languageMap = {
    ENGLISH: "Inglês",
    PORTUGUESE: "Português",
    SPANISH: "Espanhol",
    FRENCH: "Francês",
    GERMAN: "Alemão"
};

function RequestServiceToProfessional({ id }) {
    const location = useLocation();
    const { state } = location;
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [local, setLocation] = useState('');
    const [category, setCategory] = useState('');
    const [description, setDescription] = useState('');
    const [professional, setProfessional] = useState(null);
    const [error, setError] = useState('');
    const [urgent, setUrgent] = useState(false);

    useEffect(() => {
        if (state) {
            console.log(state);
            setProfessional(state);
        }
    }, [state]);

    if (!professional) return <div>Loading...</div>;

    const handleSubmit = async (e) => {
        e.preventDefault();

        const selectedLanguages = professional.languages || [];

        const requestBody = {
            clientId: id,
            professionalId: professional.professionalId,
            price: 0,
            address: "0",
            postalCode: "0000-000",
            category: {
                id: professional.category.id,
                name: professional.category.name,
            },
            description: description,
            title: title,
            location: professional.location,
            languages: selectedLanguages.map(lang => ({
                id: lang.id,
                name: languageMap[lang.name] || lang.name
            })),
            state: 0,
            urgent: urgent
        };

        try {
            const response = await axiosInstance.post('/services', requestBody);

            if (response.status === 200) {
                const serviceId = response.data;
                const professionalId = professional.professionalId
                navigate(`/ScheduleAppointments?clientId=${id}&professionalId=${professionalId}&serviceId=${serviceId}`);
            } else {
                console.error("Failed to create service:", response.statusText);
                setError(
                    <>
                        Falha ao criar o serviço. Por favor, tente novamente.<br />
                        Todos os campos são obrigatórios!
                    </>
                );
            }
        } catch (error) {
            console.error("Error creating service:", error);
            setError(
                <>
                    Falha ao criar o serviço. Por favor, tente novamente.<br />
                    Todos os campos são obrigatórios!
                </>
            );
        }
    };

    return (
        <div className="h-screen bg-gray-200 text-black font-sans">
            <main className="flex-grow bg-gray-800 bg-opacity-15 flex items-center justify-center">
                <div className="relative w-full h-full bg-cover bg-center">
                    <div className="absolute inset-0"></div>
                    <div className="relative z-10 flex justify-center items-center h-full m-8">
                        <div className="bg-white bg-opacity-80 p-8 rounded-lg max-w-lg w-full">
                            <h2 className="text-2xl text-yellow-600 font-bold text-center mb-6 underline">Pedir um Serviço a {professional.name}</h2>
                            {error && (
                                <div className="mb-4 p-2 bg-red-200 text-red-800 text-center rounded">
                                    {error}
                                </div>
                            )}
                            <form onSubmit={handleSubmit}>
                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">Título *</label>
                                    <input
                                        type="text"
                                        value={title}
                                        onChange={(e) => setTitle(e.target.value)}
                                        placeholder="eg. Preciso de um canalizador"
                                        className="w-full p-2 border-b-2 border-black placeholder-black placeholder-opacity-80 bg-transparent focus:outline-none focus:border-black"
                                    />
                                </div>
                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">Localização *</label>
                                    <input
                                        type="text"
                                        disabled
                                        value={professional.location || ""}
                                        className="w-full p-2 border-b-2 border-gray-400 bg-gray-100 text-gray-600 cursor-not-allowed"
                                    />
                                </div>
                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">Categoria *</label>
                                    <p className="w-full p-2 placeholder-gray-600 border-b-2 border-gray-400 bg-gray-100 text-gray-600 cursor-default">
                                        {professional.category.name && professional.category.name
                                            ? professional.category.name.charAt(0).toUpperCase() +
                                            professional.category.name.slice(1).toLowerCase()
                                            : "Nenhuma categoria disponível"}
                                    </p>
                                </div>

                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">Idiomas *</label>
                                    <div className="flex flex-wrap gap-2">
                                        {professional.languages.map((language, index) => (
                                            <span
                                                key={index}
                                                className="bg-yellow-600 text-white px-3 py-1 rounded-full text-sm font-medium cursor-not-allowed">
                                                    {languageMap[language.name] || language.name}
                                            </span>
                                        ))}
                                    </div>
                                </div>

                                <div className="mb-6">
                                    <label className="block text-black font-semibold mb-2">Descrição *</label>
                                    <textarea
                                        value={description}
                                        onChange={(e) => setDescription(e.target.value)}
                                        className="w-full bg-white bg-opacity-50 mt-2 p-2 h-20 placeholder-black placeholder-opacity-80 border border-black"
                                        placeholder="Descrição"
                                    ></textarea>
                                </div>

                                <div className="mb-4 flex items-center justify-between">
                                    <span className="text-black font-semibold">Pedido Urgente</span>
                                    <div
                                        onClick={() => setUrgent(!urgent)}
                                        className={`relative inline-flex items-center cursor-pointer w-12 h-6 rounded-full ${
                                            urgent ? 'bg-yellow-600' : 'bg-gray-300'
                                        }`}
                                    >
    <span
        className={`absolute left-1 top-1 w-4 h-4 bg-white rounded-full shadow transform transition-transform ${
            urgent ? 'translate-x-6' : 'translate-x-0'
        }`}
    ></span>
                                    </div>
                                </div>
                                <p className="text-sm text-gray-700 mt-2">
                                    {urgent ? 'Este pedido será tratado como urgente.' : 'Este pedido não é urgente.'}
                                </p>

                                <br/>

                                <button
                                    type="submit"
                                    className="w-full px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                >
                                    Avançar
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </main>
            <Footer/>
        </div>
    );
}

export default RequestServiceToProfessional;
