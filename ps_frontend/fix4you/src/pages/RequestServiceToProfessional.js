import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Footer from "../components/Footer";
import axiosInstance from "../components/axiosInstance";


function RequestServiceToProfessional({ id }) {

    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [location, setLocation] = useState('Lisboa, Portugal');
    const [category, setCategory] = useState('1');
    const [description, setDescription] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        const requestBody = {
            clientId: id,
            professionalId: null,
            price: 0,
            address: "0",
            postalCode: "0000-000",
            category: category,
            description: description,
            title: title,
            location: location,
            languages: ["ENGLISH"],
            state: 0
        };

        try {
            const response = await axiosInstance.post('/services', requestBody);

            if (response.status === 200) {
                navigate('/PrincipalPageClient');
            } else {
                console.error("Failed to create service:", response.statusText);
            }
        } catch (error) {
            console.error("Error creating service:", error);
        }

    };

    return (
        <div className="h-screen bg-gray-200 text-black font-sans">
            <main className="flex-grow bg-gray-800 bg-opacity-15 flex items-center justify-center">
                <div className="relative w-full h-full bg-cover bg-center">
                    <div className="absolute inset-0"></div>
                    <div className="relative z-10 flex justify-center items-center h-full m-8">
                        <div className="bg-white bg-opacity-80 p-8 rounded-lg max-w-lg w-full">
                            <h2 className="text-2xl text-yellow-600 font-bold text-center mb-6 underline">Pedir Um Serviço</h2>
                            <form onSubmit={handleSubmit}>
                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">Titulo *</label>
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
                                        value={location}
                                        onChange={(e) => setLocation(e.target.value)}
                                        placeholder="eg Lisboa, Portugal"
                                        className="w-full p-2 border-b-2 border-gray-400 bg-gray-100 text-gray-600 cursor-not-allowed"
                                    />
                                </div>
                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">Categoria *</label>
                                    <select
                                        value={category}
                                        disabled
                                        onChange={(e) => setCategory(parseInt(e.target.value))}
                                        className="w-full p-2 placeholder-gray-600 border-b-2 border-gray-400 bg-gray-100 text-gray-600 cursor-not-allowed"
                                    >
                                        <option value="">Selecionar</option>
                                        <option value="0">Limpeza</option>
                                        <option value="1">Canalizador</option>
                                        <option value="2">Eletricista</option>
                                        <option value="3">Jardineiro</option>
                                        <option value="4">Pintor</option>
                                        <option value="5">Outro</option>
                                    </select>
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
                                <button
                                    type="submit"
                                    className="w-full px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                                    Publicar
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
