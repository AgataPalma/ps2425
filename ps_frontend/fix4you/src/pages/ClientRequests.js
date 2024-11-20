import React, { useEffect, useState } from "react";
import axiosInstance from "../components/axiosInstance";
import Spinner from "../components/Spinner";
import MessageModal from "../components/MessageModal";

function ClientRequests({ id }) {
    const [requests, setRequests] = useState([]);
    const [filteredRequests, setFilteredRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [statusFilter, setStatusFilter] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalType, setModalType] = useState(""); // "success" or "error"
    const [modalTitle, setModalTitle] = useState("");
    const [modalMessage, setModalMessage] = useState("");

    const fetchRequests = async () => {
        setLoading(true);
        try {
            const response = await axiosInstance.get(`/services/client/${id}`);
            const data = Array.isArray(response.data) ? response.data : [response.data];
            const nonCompletedRequests = data.filter(
                (request) => !["completed", "canceled"].includes(request.state.toLowerCase())
            );
            setRequests(nonCompletedRequests);
            setFilteredRequests(nonCompletedRequests);
        } catch (error) {
            console.error("Error fetching client requests:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchRequests();
    }, [id]);

    const handleStatusFilterChange = (e) => {
        const status = e.target.value;
        setStatusFilter(status);
        if (status === "") {
            setFilteredRequests(requests);
        } else {
            setFilteredRequests(requests.filter((request) => request.state.toLowerCase() === status));
        }
    };

    const handleConfirmCompletion = async (serviceId) => {
        try {
            const scheduleResponse = await axiosInstance.get(`/scheduleAppointments`);
            const scheduleAppointments = scheduleResponse.data;

            const matchingAppointment = scheduleAppointments.find((appointment) => appointment.serviceId === serviceId);

            if (!matchingAppointment) {
                setModalTitle("Erro");
                setModalMessage("Não foi possível encontrar o horário associado.");
                setModalType("error");
                setIsModalOpen(true);
                return;
            }

            const scheduleAppointmentId = matchingAppointment.id;

            await axiosInstance.patch(`/scheduleAppointments/${scheduleAppointmentId}`, { state: "COMPLETED" });
            await axiosInstance.patch(`/services/${serviceId}`, { state: "COMPLETED" });

            await fetchRequests();

            setModalTitle("Serviço executado");
            setModalMessage("Serviço registado como concluído!");
            setModalType("success");
            setIsModalOpen(true);
        } catch (error) {
            console.error(`Error confirming completion for serviceId: ${serviceId}`, error);
            setModalTitle("Erro");
            setModalMessage("Ocorreu um erro ao concluir o serviço. Tente novamente.");
            setModalType("error");
            setIsModalOpen(true);
        }
    };

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Pedidos em aberto</h1>
            <div className="mb-6">
                <label htmlFor="statusFilter" className="block text-lg font-medium text-gray-700 mb-2">Filtrar</label>
                <select
                    id="statusFilter"
                    value={statusFilter}
                    onChange={handleStatusFilterChange}
                    className="w-full p-2 border rounded-lg"
                >
                    <option value="">Mostrar tudo</option>
                    <option value="pending">Pendente</option>
                    <option value="accepted">Aceite</option>
                </select>
            </div>
            <div className="space-y-6">
                {filteredRequests.length > 0 ? (
                    filteredRequests.map((request) => (
                        <div key={request.id} className="p-4 bg-gray-100 rounded-lg shadow-md">
                            <h3 className="text-xl font-bold text-gray-800 mb-2">{request.title}</h3>
                            <p className="text-gray-600">Categoria: {request.category.name}</p>
                            <p className="text-gray-600">Preço por hora: ${request.price}</p>
                            <p className="text-gray-600">Estado: {request.state}</p>

                            {request.state.toLowerCase() === "accepted" && (
                                <div className="mt-4 flex justify-left">
                                    <button
                                        onClick={() => handleConfirmCompletion(request.id)}
                                        className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                                    >
                                        Confirmar Conclusão
                                    </button>
                                </div>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">Não foram encontrados pedidos de serviços.</p>
                )}
            </div>
            {/* Success or Error Modal */}
            <MessageModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={modalTitle}
                message={modalMessage}
                type={modalType}
            />
        </div>
    );
}

export default ClientRequests;
