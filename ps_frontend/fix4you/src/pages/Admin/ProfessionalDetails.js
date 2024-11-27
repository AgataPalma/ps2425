import React, { useState } from "react";
import axiosInstance from "../../components/axiosInstance";

const ProfessionalDetails = ({ professional, onClose }) => {
    const [activeTab, setActiveTab] = useState("general"); // Manage active tab
    const [suspensionReason, setSuspensionReason] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSuspendAccount = async () => {
        if (!suspensionReason.trim()) {
            alert("Por favor, insira o motivo da suspensão.");
            return;
        }

        setLoading(true);
        try {
            await axiosInstance.post(`/professionals/${professional.id}/suspend`, { reason: suspensionReason });
            alert("Conta suspensa com sucesso.");
            onClose();
        } catch (error) {
            console.error("Erro ao suspender conta:", error);
            alert("Erro ao suspender a conta. Por favor, tente novamente.");
        } finally {
            setLoading(false);
        }
    };

    const handleActivateAccount = async () => {
        if (!suspensionReason.trim()) {
            alert("Por favor, insira a explicação para remoção da suspensão.");
            return;
        }

        setLoading(true);
        try {
            await axiosInstance.post(`/professionals/${professional.id}/activate`, { reason: suspensionReason });
            alert("Conta ativada com sucesso.");
            onClose();
        } catch (error) {
            console.error("Erro ao ativar conta:", error);
            alert("Erro ao ativar a conta. Por favor, tente novamente.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-4xl w-full relative">
                {/* Close Button */}
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-800 text-2xl font-bold"
                >
                    &times;
                </button>

                <h2 className="text-2xl font-bold mb-4 text-center">Detalhes do Profissional</h2>

                {/* Profile Image */}
                <div className="flex justify-center mb-4">
                    {professional.profileImage ? (
                        <img
                            src={professional.profileImage}
                            alt="Profile"
                            className="w-24 h-24 rounded-full object-cover shadow-lg"
                        />
                    ) : (
                        <div className="w-24 h-24 bg-gray-300 rounded-full flex items-center justify-center text-gray-600">
                            Sem Foto
                        </div>
                    )}
                </div>

                {/* Tabs Navigation */}
                <div className="flex justify-center space-x-4 border-b-2 mb-4 relative">
                    <div className="flex space-x-4">
                        <button
                            onClick={() => setActiveTab("general")}
                            className={`px-4 py-2 text-lg ${
                                activeTab === "general" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                            }`}
                        >
                            Geral
                        </button>
                        <button
                            onClick={() => setActiveTab("account")}
                            className={`px-4 py-2 text-lg ${
                                activeTab === "account" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                            }`}
                        >
                            Gerir Conta
                        </button>
                        <button
                            onClick={() => setActiveTab("reviews")}
                            className={`px-4 py-2 text-lg ${
                                activeTab === "reviews" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                            }`}
                        >
                            Avaliações
                        </button>
                        <button
                            onClick={() => setActiveTab("payments")}
                            className={`px-4 py-2 text-lg ${
                                activeTab === "payments" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                            }`}
                        >
                            Pagamentos
                        </button>
                        <button
                            onClick={() => setActiveTab("portfolio")}
                            className={`px-4 py-2 text-lg ${
                                activeTab === "portfolio" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                            }`}
                        >
                            Portfólio
                        </button>
                        <button
                            onClick={() => setActiveTab("categories")}
                            className={`px-4 py-2 text-lg ${
                                activeTab === "categories" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                            }`}
                        >
                            Categorias
                        </button>
                    </div>
                    {/* Full-width underline */}
                    {/* <div className="absolute bottom-0 left-0 w-full border-b-2 border-gray-300"></div>*/}
                </div>

                {/* Scrollable Content */}
                <div className="max-h-[70vh] overflow-y-auto">
                    {/* Tab Content */}
                    {activeTab === "general" && (
                        <div>
                            <p><strong>Nome:</strong> {professional.name}</p>
                            <p><strong>Email:</strong> {professional.email}</p>
                            <p><strong>Telefone:</strong> {professional.phoneNumber}</p>
                            <p><strong>Localização:</strong> {professional.location}</p>
                            <p><strong>Data de Criação:</strong> {new Date(professional.dateCreation).toLocaleDateString()}</p>
                            <p><strong>Idiomas:</strong></p>
                            <ul className="list-disc pl-6">
                                {professional.languages.map((lang) => (
                                    <li key={lang.id}>{lang.name}</li>
                                ))}
                            </ul>
                            <p><strong>Pagamentos Aceitos:</strong></p>
                            <ul className="list-disc pl-6">
                                {professional.acceptedPayments.map((pay) => (
                                    <li key={pay.id}>{pay.name}</li>
                                ))}
                            </ul>
                            <p><strong>Descrição:</strong> {professional.description}</p>
                            <p><strong>NIF:</strong> {professional.nif}</p>
                            <p><strong>Estado Email:</strong> {professional.isEmailConfirmed ? "Confirmado" : "Não Confirmado"}</p>
                        </div>
                    )}

                    {activeTab === "account" && (
                        <div>
                            <p><strong>Estado da Conta:</strong> {professional.supended ? "Suspensa" : "Ativa"}</p>
                            <p><strong>Strikes:</strong> {professional.strikes}</p>
                            {professional.supended ? (
                                <div>
                                    <textarea
                                        placeholder="Motivo para remoção da suspensão"
                                        value={suspensionReason}
                                        onChange={(e) => setSuspensionReason(e.target.value)}
                                        className="border p-2 w-full rounded"
                                    ></textarea>
                                    <button
                                        onClick={handleActivateAccount}
                                        className="bg-green-500 text-white px-4 py-2 rounded mt-2 hover:bg-green-600"
                                        disabled={loading}
                                    >
                                        {loading ? "A ativar, aguarde..." : "Ativar Conta"}
                                    </button>
                                </div>
                            ) : (
                                <div>
                                    <textarea
                                        placeholder="Motivo para suspensão"
                                        value={suspensionReason}
                                        onChange={(e) => setSuspensionReason(e.target.value)}
                                        className="border p-2 w-full rounded"
                                    ></textarea>
                                    <button
                                        onClick={handleSuspendAccount}
                                        className="bg-red-500 text-white px-4 py-2 rounded mt-2 hover:bg-red-600"
                                        disabled={loading}
                                    >
                                        {loading ? "A suspender a conta, aguarde..." : "Suspender Conta"}
                                    </button>
                                </div>
                            )}
                        </div>
                    )}

                    {activeTab === "portfolio" && (
                        <div>
                            <p><strong>Portfólio:</strong></p>
                            <ul className="list-disc pl-6">
                                {professional.portfolio?.length > 0 ? (
                                    professional.portfolio.map((item, index) => (
                                        <li key={index}>{item}</li>
                                    ))
                                ) : (
                                    <p>Sem registros no portfólio.</p>
                                )}
                            </ul>
                        </div>
                    )}

                    {activeTab === "categories" && (
                        <div>
                            <p><strong>Categorias:</strong></p>
                            <ul className="list-disc pl-6">
                                {professional.categories?.length > 0 ? (
                                    professional.categories.map((category, index) => (
                                        <li key={index}>{category}</li>
                                    ))
                                ) : (
                                    <p>Sem categorias registradas.</p>
                                )}
                            </ul>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfessionalDetails;
