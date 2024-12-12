import React, { useState, useEffect } from "react";
import Footer from "../components/Footer";
import axiosInstance from "../components/axiosInstance";
import { useNavigate } from "react-router-dom"; // Importa useNavigate

const Support = ({ id, userType }) => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false); // Estado para rastrear o envio
    const navigate = useNavigate(); // Hook para redirecionamento

    // Verifica o ID e redireciona se for null
    useEffect(() => {
        if (!id) {
            navigate("/login"); // Redireciona para login
        }
    }, [id, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true); // Define o estado como "enviando"

        const ticketData = {
            userId: id,
            title: title,
            description: description,
        };

        try {
            const response = await axiosInstance.post("/tickets", ticketData); // Usa o axiosInstance
            console.log("Ticket enviado com sucesso:", response.data);

            setTitle("");
            setDescription("");
            console.log(userType);

            if (userType === "CLIENT") {
                navigate("/PrincipalPageClient");
            } else if (userType === "PROFESSIONAL") {
                navigate("/PrincipalPageProfessional");
            } else {
                navigate("/Home");
            }
        } catch (error) {
            console.error("Erro ao enviar o ticket:", error.response || error.message);
        } finally {
            setIsLoading(false); // Redefine o estado para "não enviando"
        }
    };

    return (
        <div className="min-h-screen flex flex-col bg-gray-200 text-black font-sans">
            <main className="flex-grow bg-gray-800 bg-opacity-15 flex items-center justify-center">
                <div className="relative w-full h-full bg-cover bg-center">
                    <div className="absolute inset-0"></div>
                    <div className="relative z-10 flex justify-center items-center h-full m-8">
                        <div className="bg-white bg-opacity-80 p-8 rounded-lg max-w-lg w-full">
                            <h2 className="text-2xl text-yellow-600 font-bold text-center mb-6 underline">
                                Pedido de Suporte
                            </h2>
                            <form onSubmit={handleSubmit}>
                                <div className="mb-4">
                                    <label className="block text-black font-semibold mb-2">
                                        Titulo *
                                    </label>
                                    <input
                                        type="text"
                                        value={title}
                                        onChange={(e) => setTitle(e.target.value)}
                                        placeholder="eg. Titulo"
                                        className="w-full p-2 border-b-2 border-black placeholder-black placeholder-opacity-80 bg-transparent focus:outline-none focus:border-black"
                                        required
                                    />
                                </div>
                                <div className="mb-6">
                                    <label className="block text-black font-semibold mb-2">
                                        Descrição *
                                    </label>
                                    <textarea
                                        value={description}
                                        onChange={(e) => setDescription(e.target.value)}
                                        className="w-full bg-white bg-opacity-50 mt-2 p-2 h-20 placeholder-black placeholder-opacity-80 border border-black"
                                        placeholder="Descrição"
                                        required
                                    ></textarea>
                                </div>
                                <button
                                    type="submit"
                                    className="w-full px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                    disabled={isLoading} // Desabilita o botão enquanto carrega
                                >
                                    {isLoading ? "Enviando..." : "Pedir Ajuda"}
                                </button>
                            </form>
                            {isLoading && (
                                <p className="text-center text-yellow-600 mt-4">Enviando o pedido, por favor aguarde...</p>
                            )}
                        </div>
                    </div>
                </div>
            </main>
            <Footer />
        </div>
    );
};

export default Support;
