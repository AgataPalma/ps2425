import React, { useState, useEffect } from "react";
import axios from "axios";

const PaymentsManagement = () => {
    const [activeTab, setActiveTab] = useState("pending");
    const [payments, setPayments] = useState([]);
    const [selectedProfessional, setSelectedProfessional] = useState(null); // Para detalhes do profissional
    const [error, setError] = useState("");

    useEffect(() => {
        fetchPayments();
    }, []);

    const fetchPayments = async () => {
        try {
            const response = await axios.get("http://localhost:8080/professionalFees");
            setPayments(response.data);
        } catch (error) {
            console.error("Erro ao buscar pagamentos:", error);
            setError("Não foi possível carregar os pagamentos.");
        }
    };

    const fetchProfessionalDetails = async (professionalId) => {
        try {
            const response = await axios.get(`http://localhost:8080/users/${professionalId}`);
            setSelectedProfessional(response.data);
        } catch (error) {
            console.error("Erro ao buscar detalhes do profissional:", error);
            setError("Não foi possível carregar os detalhes do profissional.");
        }
    };

    const filteredPayments = (status) => {
        return payments.filter(payment => payment.paymentStatus === status);
    };

    const renderTable = (data) => (
        <table className="table-auto w-full text-left border-collapse border border-gray-300">
            <thead>
                <tr>
                    <th className="border border-gray-300 px-4 py-2">ID</th>
                    <th className="border border-gray-300 px-4 py-2">Profissional ID</th>
                    <th className="border border-gray-300 px-4 py-2">Mês/Ano</th>
                    <th className="border border-gray-300 px-4 py-2">Valor (€)</th>
                    <th className="border border-gray-300 px-4 py-2">Serviços</th>
                    <th className="border border-gray-300 px-4 py-2">Status</th>
                    <th className="border border-gray-300 px-4 py-2">Ações</th>
                </tr>
            </thead>
            <tbody>
                {data.map(payment => (
                    <tr key={payment.id} className="hover:bg-gray-100">
                        <td className="border border-gray-300 px-4 py-2">{payment.id}</td>
                        <td className="border border-gray-300 px-4 py-2">{payment.professional?.id || "N/A"}</td>
                        <td className="border border-gray-300 px-4 py-2">{payment.relatedMonthYear}</td>
                        <td className="border border-gray-300 px-4 py-2">{payment.value}</td>
                        <td className="border border-gray-300 px-4 py-2">{payment.numberServices}</td>
                        <td className="border border-gray-300 px-4 py-2">{payment.paymentStatus}</td>
                        <td className="border border-gray-300 px-4 py-2">
                            {payment.professional?.id && (
                                <button
                                    className="text-blue-500 hover:underline"
                                    onClick={() => fetchProfessionalDetails(payment.professional.id)}
                                >
                                    Ver Profissional
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Gestão de Pagamentos</h1>

            {/* Tabs Navigation */}
            <div className="flex space-x-4 border-b-2 mb-4">
                <button
                    onClick={() => setActiveTab("pending")}
                    className={`px-4 py-2 text-lg ${
                        activeTab === "pending"
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                >
                    Pagamentos Pendentes
                </button>
                <button
                    onClick={() => setActiveTab("completed")}
                    className={`px-4 py-2 text-lg ${
                        activeTab === "completed"
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                >
                    Pagamentos Efetuados
                </button>
            </div>

            {/* Error Message */}
            {error && <div className="text-red-600 mb-4">{error}</div>}

            {/* Tab Content */}
            {activeTab === "pending" && (
                <div>
                    <h2 className="text-xl font-semibold mb-4">Pagamentos Pendentes</h2>
                    {renderTable(filteredPayments("PENDING"))}
                </div>
            )}

            {activeTab === "completed" && (
                <div>
                    <h2 className="text-xl font-semibold mb-4">Pagamentos Efetuados</h2>
                    {renderTable(filteredPayments("COMPLETED"))}
                </div>
            )}

            {/* Modal for Professional Details */}
            {selectedProfessional && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
                    <div className="bg-white rounded-lg shadow-lg p-6 max-w-lg w-full">
                        <h2 className="text-xl font-bold mb-4 text-gray-900">Detalhes do Profissional</h2>
                        <p><strong>ID:</strong> {selectedProfessional.id}</p>
                        <p><strong>Nome:</strong> {selectedProfessional.name || "Não disponível"}</p>
                        <p><strong>Email:</strong> {selectedProfessional.email || "Não disponível"}</p>
                        <p><strong>NIF:</strong> {selectedProfessional.nif || "Não disponível"}</p>
                        <div className="mt-4 flex justify-end">
                            <button
                                className="px-4 py-2 text-white bg-red-600 rounded-md hover:bg-red-500"
                                onClick={() => setSelectedProfessional(null)}
                            >
                                Fechar
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PaymentsManagement;
