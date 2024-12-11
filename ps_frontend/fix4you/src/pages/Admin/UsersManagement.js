import React, { useState, useEffect } from "react";
import axiosInstance from "../../components/axiosInstance";
import ClientDetails from "./ClientDetails";
import ProfessionalDetails from "./ProfessionalDetails";
import SuspiciousActivities from "./SuspiciousActivities";
import Spinner from "../../components/Spinner";

const UsersManagement = () => {
    const [activeTab, setActiveTab] = useState("clients"); // Active tab state
    const [clients, setClients] = useState([]);
    const [professionals, setProfessionals] = useState([]);
    const [selectedClient, setSelectedClient] = useState(null);
    const [selectedProfessional, setSelectedProfessional] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchUsers = async (includeSuspended = true) => {
        try {
            setLoading(true);
            const [clientsResponse, professionalsResponse] = await Promise.all([
                axiosInstance.get(`/clients?includeSuspended=${includeSuspended}`),
                axiosInstance.get(`/professionals?includeSuspended=${includeSuspended}`),
            ]);
            setClients(clientsResponse.data);
            setProfessionals(professionalsResponse.data);
        } catch (error) {
            console.error("Error fetching users:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    const updateClient = (updatedClient) => {
        setClients((prevClients) =>
            prevClients.map((client) => (client.id === updatedClient.id ? updatedClient : client))
        );
        setSelectedClient((prevSelected) =>
            prevSelected && prevSelected.id === updatedClient.id ? updatedClient : prevSelected
        );
    };

    const updateProfessional = (updatedProfessional) => {
        setProfessionals((prevProfessionals) =>
            prevProfessionals.map((professional) => (professional.id === updatedProfessional.id ? updatedProfessional : professional))
        );
        setSelectedProfessional((prevSelected)=>
            prevSelected && prevSelected.id === updatedProfessional.id ? updatedProfessional : prevSelected
        );
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Gestão de Utilizadores</h1>

            {/* Tabs Navigation */}
            <div className="flex space-x-4 border-b-2 mb-4">
                <button
                    onClick={() => setActiveTab("clients")}
                    className={`px-4 py-2 text-lg ${
                        activeTab === "clients" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                    }`}
                >
                    Clientes
                </button>
                <button
                    onClick={() => setActiveTab("professionals")}
                    className={`px-4 py-2 text-lg ${
                        activeTab === "professionals" ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"
                    }`}
                >
                    Profissionais
                </button>
            </div>

            {/* Tab Content */}
            {activeTab === "clients" && (
                <div>
                    {/* Clients Table */}
                    <table className="table-auto w-full text-left border-collapse border border-gray-300">
                        <thead>
                        <tr>
                            <th className="border border-gray-300 px-4 py-2">Nome</th>
                            <th className="border border-gray-300 px-4 py-2">Email</th>
                            <th className="border border-gray-300 px-4 py-2">Telefone</th>
                            <th className="border border-gray-300 px-4 py-2">Localização</th>
                            <th className="border border-gray-300 px-4 py-2">Estado Email</th>
                            <th className="border border-gray-300 px-4 py-2">Rating</th>
                            <th className="border border-gray-300 px-4 py-2">Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        {clients.map((client) => (
                            <tr key={client.id} className="hover:bg-gray-100">
                                <td className="border border-gray-300 px-4 py-2">{client.name}</td>
                                <td className="border border-gray-300 px-4 py-2">{client.email}</td>
                                <td className="border border-gray-300 px-4 py-2">{client.phoneNumber}</td>
                                <td className="border border-gray-300 px-4 py-2">{client.location}</td>
                                <td className="border border-gray-300 px-4 py-2">
                                    {client.isEmailConfirmed ? "Confirmado" : "Não Confirmado"}
                                </td>
                                <td className="border border-gray-300 px-4 py-2">{client.rating.toFixed(1)}</td>
                                <td className="border border-gray-300 px-4 py-2">
                                    <button
                                        className="text-blue-500 hover:underline"
                                        onClick={() => setSelectedClient(client)}
                                    >
                                        Ver Detalhes
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    {selectedClient && (
                        <ClientDetails
                            client={selectedClient}
                            updateClient={updateClient}
                            onClose={() => setSelectedClient(null)
                            }// Pass refresh function
                        />
                    )}
                </div>
            )}

            {activeTab === "professionals" && (
                <div>
                    {/* Professionals Table */}
                    <table className="table-auto w-full text-left border-collapse border border-gray-300">
                        <thead>
                        <tr>
                            <th className="border border-gray-300 px-4 py-2">Nome</th>
                            <th className="border border-gray-300 px-4 py-2">Email</th>
                            <th className="border border-gray-300 px-4 py-2">Telefone</th>
                            <th className="border border-gray-300 px-4 py-2">Localização</th>
                            <th className="border border-gray-300 px-4 py-2">Estado Email</th>
                            <th className="border border-gray-300 px-4 py-2">Rating</th>
                            <th className="border border-gray-300 px-4 py-2">Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        {professionals.map((professional) => (
                            <tr key={professional.id} className="hover:bg-gray-100">
                                <td className="border border-gray-300 px-4 py-2">{professional.name}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.email}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.phoneNumber}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.location}</td>
                                <td className="border border-gray-300 px-4 py-2">
                                    {professional.isEmailConfirmed ? "Confirmado" : "Não Confirmado"}
                                </td>
                                <td className="border border-gray-300 px-4 py-2">{professional.rating.toFixed(1)}</td>
                                <td className="border border-gray-300 px-4 py-2">
                                    <button
                                        className="text-blue-500 hover:underline"
                                        onClick={() => setSelectedProfessional(professional)}
                                    >
                                        Ver Detalhes
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                    {selectedProfessional && (
                        <ProfessionalDetails
                            professional={selectedProfessional}
                            updateProfessional={updateProfessional}
                            onClose={() => setSelectedProfessional(null)}
                        />
                    )}
                </div>
            )}

        </div>
    );
};

export default UsersManagement;
