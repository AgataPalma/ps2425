import React, {useEffect, useState} from "react";
import axiosInstance from "../../components/axiosInstance";
import MessageModal from "../../components/MessageModal";
import Spinner from "../../components/Spinner";

const ClientDetails = ({ client,updateClient, onClose }) => {
    const [suspensionReason, setSuspensionReason] = useState("");
    const [loading, setLoading] = useState(false);
    const [modalState, setModalState] = useState({
        isOpen: false,
        title: "",
        message: "",
        type: "success",
    });

    useEffect(() => {
        console.log("Updated client details:", client);
    }, [client]);

    const showMessage = (title, message, type) => {
        setModalState({ isOpen: true, title, message, type });
    };

    const handleSuspendAccount = async () => {
        if (!suspensionReason.trim()) {
            showMessage("Error", "Por favor, insira o motivo da suspensão.", "error");
            return;
        }

        setLoading(true);
        try {
            const response = await axiosInstance.patch(`/clients/${client.id}`, { isSuspended: true, suspensionReason });
            if (updateClient) {
                updateClient(response.data);
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
            const response = await axiosInstance.patch(`/clients/${client.id}`, { IsEmailConfirmed: true });
            if (updateClient) {
                updateClient(response.data);
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
            const response = await axiosInstance.patch(`/clients/${client.id}`, { isSuspended: false, suspensionReason: " " });
            if (updateClient) {
                updateClient(response.data);
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

                <div className="mt-4">

                    {client.suspended ? (
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
                    {!client.isEmailConfirmed && (
                        <button
                            onClick={handleActivateAccount}
                            className="bg-green-500 text-white px-4 py-2 rounded mt-2 hover:bg-green-600"
                        >
                            Ativar Conta
                        </button>
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

export default ClientDetails;
