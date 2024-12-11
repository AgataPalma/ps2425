import React, {useEffect, useState } from "react";
import axiosInstance from "../../components/axiosInstance";
import MessageModal from "../../components/MessageModal";
import Spinner from "../../components/Spinner";


const ProfessionalDetails = ({ professional, updateProfessional, onClose }) => {
    const [activeTab, setActiveTab] = useState("general"); // Manage active tab
    const [suspensionReason, setSuspensionReason] = useState("");
    const [loading, setLoading] = useState(false);
    const [modalState, setModalState] = useState({
        isOpen: false,
        title: "",
        message: "",
        type: "success",
    });

    const showMessage = (title, message, type) => {
        setModalState({ isOpen: true, title, message, type });
    };

    const handleSuspendAccount = async () => {
        if (!suspensionReason.trim()) {
            showMessage("Erro", "Por favor, insira o motivo da suspensão.", "error");
            return;
        }

        setLoading(true);
        try {
            const response= await axiosInstance.patch(`/professionals/${professional.id}`, { isSuspended: true, suspensionReason });
            if (updateProfessional) {
                updateProfessional(response.data);
            }
            showMessage("Success", "Conta suspensa com sucesso.", "success");
        } catch (error) {
            showMessage("Erro", error.message, "error");
        } finally {
            setLoading(false);
        }
    };

    const handleActivateAccount = async () => {

        setLoading(true);
        try {
            const response = await axiosInstance.patch(`/professionals/${professional.id}`, { IsEmailConfirmed: true });
            if (updateProfessional) {
                updateProfessional(response.data);
            }
            showMessage("Success", "Conta ativada com sucesso.", "success");

        } catch (error) {
            showMessage("Erro", error.message, "error");
        } finally {
            setLoading(false);
        }
    };

    const handleRemoveSuspension = async () => {
        setLoading(true);
        try {
            const response = await axiosInstance.patch(`/professionals/${professional.id}`, { isSuspended: false, suspensionReason: " " });
            if (updateProfessional) {
                updateProfessional(response.data);
            }
            showMessage("Success", "Suspensão removida com sucesso.", "success");

        } catch (error) {
            showMessage("Erro", error.message, "error");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }
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
                            src={`data:image/jpeg;base64,${professional.profileImage}`}
                            alt="Profile"
                            className="w-24 h-24 rounded-full object-cover shadow-lg"
                        />
                    ) : (
                        <div
                            className="w-24 h-24 bg-gray-300 rounded-full flex items-center justify-center text-gray-600">
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

                    </div>

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
                            <p><strong>Formas de pagamento aceites:</strong></p>
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
                            <p><strong>Estado da Conta:</strong> {professional.isSuspended ? "Suspensa" : "Ativa"}</p>
                            <p><strong>Strikes:</strong> {professional.strikes}</p>
                            {professional.isSuspended ? (
                                <button
                                    onClick={handleRemoveSuspension}
                                    className="bg-blue-500 text-white px-4 py-2 rounded mt-2 hover:bg-blue-600"
                                >
                                    Remover Suspensão
                                </button>
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
                                    >
                                        Suspender Conta
                                    </button>
                                </div>
                            )}
                            {!professional.isEmailConfirmed && (
                                <button
                                    onClick={handleActivateAccount}
                                    className="bg-green-500 text-white px-4 py-2 rounded mt-2 hover:bg-green-600"
                                >
                                    Ativar Conta
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </div>
            <MessageModal
                isOpen={modalState.isOpen}
                onClose={() => setModalState({ ...modalState, isOpen: false })}
                title={modalState.title}
                message={modalState.message}
                type={modalState.type}
            />
        </div>
    );
};

export default ProfessionalDetails;
