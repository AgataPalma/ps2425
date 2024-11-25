import React, { useState } from "react";
import axiosInstance from "../../components/axiosInstance";

const ProfessionalDetails = ({ professional, onClose }) => {
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
        setLoading(true);
        try {
            await axiosInstance.post(`/professionals/${professional.id}/activate`);
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
                <h2 className="text-2xl font-bold mb-4">Detalhes do Profissional</h2>
                <p><strong>Nome:</strong> {professional.name}</p>
                <p><strong>Email:</strong> {professional.email}</p>
                <p><strong>Telefone:</strong> {professional.phoneNumber}</p>
                <p><strong>Localização:</strong> {professional.location}</p>
                <p><strong>Data de Criação:</strong> {new Date(professional.dateCreation).toLocaleDateString()}</p>
                <p><strong>Rating:</strong> {professional.rating.toFixed(1)}</p>
                <p><strong>Descrição:</strong> {professional.description}</p>
                <p><strong>NIF:</strong> {professional.nif}</p>
                <p><strong>Idiomas:</strong> {professional.languages.map((lang) => lang.name).join(", ")}</p>
                <p><strong>Pagamentos Aceitos:</strong> {professional.acceptedPayments.map((pay) => pay.name).join(", ")}</p>
                <p><strong>Strikes:</strong> {professional.strikes}</p>
                <p><strong>Estado Email:</strong> {professional.isEmailConfirmed ? "Confirmado" : "Não Confirmado"}</p>
                <p><strong>Estado da Conta:</strong> {professional.supended ? "Suspensa" : "Ativa"}</p>

                {professional.supended ? (
                    <button
                        onClick={handleActivateAccount}
                        className="bg-green-500 text-white px-4 py-2 rounded mt-4 hover:bg-green-600"
                        disabled={loading}
                    >
                        {loading ? "A ativar, aguarde..." : "Ativar Conta"}
                    </button>
                ) : (
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
                            disabled={loading}
                        >
                            {loading ? "A suspender a conta, aguarde..." : "Suspender Conta"}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProfessionalDetails;
