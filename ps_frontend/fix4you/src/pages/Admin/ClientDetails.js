import React, { useState } from "react";
import axiosInstance from "../../components/axiosInstance";

const ClientDetails = ({ client, onClose }) => {
    const [suspensionReason, setSuspensionReason] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSuspendAccount = async () => {
        if (!suspensionReason.trim()) {
            alert("Por favor, insira o motivo da suspensão.");
            return;
        }

        setLoading(true);
        try {
            await axiosInstance.post(`/clients/${client.id}/suspend`, { reason: suspensionReason });
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
        setLoading(true);
        try {
            await axiosInstance.post(`/clients/${client.id}/activate`);
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
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full relative">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-800 text-2xl font-bold"
                >
                    &times;
                </button>
                <h2 className="text-2xl font-bold mb-4">Detalhes do Cliente</h2>
                <p><strong>Nome:</strong> {client.name}</p>
                <p><strong>Email:</strong> {client.email}</p>
                <p><strong>Telefone:</strong> {client.phoneNumber}</p>
                <p><strong>Localização:</strong> {client.location}</p>
                <p><strong>Data de Criação:</strong> {new Date(client.dateCreation).toLocaleDateString()}</p>
                <p><strong>Estado Email:</strong> {client.isEmailConfirmed ? "Confirmado" : "Não Confirmado"}</p>
                <p><strong>Rating:</strong> {client.rating.toFixed(1)}</p>

                {client.status === "active" ? (
                    <div className="mt-4">
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
                ) : (
                    <button
                        onClick={handleActivateAccount}
                        className="bg-green-500 text-white px-4 py-2 rounded mt-2 hover:bg-green-600"
                    >
                        Ativar Conta
                    </button>
                )}
            </div>
        </div>
    );
};

export default ClientDetails;
