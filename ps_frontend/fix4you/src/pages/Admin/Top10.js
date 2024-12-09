import React, { useState, useEffect } from "react";
import axios from "axios";

const TopLists = () => {
    const [topClientsByActivities, setTopClientsByActivities] = useState([]);
    const [topProfessionalsByActivities, setTopProfessionalsByActivities] = useState([]);
    const [topClientsByExpenses, setTopClientsByExpenses] = useState([]);
    const [clientDetails, setClientDetails] = useState({});
    const [professionalDetails, setProfessionalDetails] = useState({});

    // Estados de carregamento para cada botão
    const [loadingStates, setLoadingStates] = useState({
        clientsActivities: false,
        professionalsActivities: false,
        clientsExpenses: false,
    });
    const [loadingTexts, setLoadingTexts] = useState({
        clientsActivities: "",
        professionalsActivities: "",
        clientsExpenses: "",
    });

    const fetchDetails = async (id, type) => {
        try {
            const response = await axios.get(`http://localhost:8080/${type}s/${id}`);
            return response.data;
        } catch (error) {
            console.error(`Erro ao buscar detalhes de ${type} com ID ${id}:`, error);
            return { name: "Não disponível", email: "Não disponível" };
        }
    };

    const fetchTopLists = async () => {
        try {
            const [clientsActivities, professionalsActivities, clientsExpenses] = await Promise.all([
                axios.get("http://localhost:8080/services/topActivitiesClients"),
                axios.get("http://localhost:8080/services/topActivitiesProfessionals"),
                axios.get("http://localhost:8080/services/topExpensesClients"),
            ]);

            const clientIds = [
                ...new Set([
                    ...clientsActivities.data.map(item => item.clientId),
                    ...clientsExpenses.data.map(item => item.clientId),
                ]),
            ];
            const professionalIds = [
                ...new Set(professionalsActivities.data.map(item => item.professionalId)),
            ];

            const clientDetailsMap = {};
            const professionalDetailsMap = {};

            for (const id of clientIds) {
                clientDetailsMap[id] = await fetchDetails(id, "client");
            }

            for (const id of professionalIds) {
                professionalDetailsMap[id] = await fetchDetails(id, "professional");
            }

            setClientDetails(clientDetailsMap);
            setProfessionalDetails(professionalDetailsMap);

            setTopClientsByActivities(clientsActivities.data);
            setTopProfessionalsByActivities(professionalsActivities.data);
            setTopClientsByExpenses(clientsExpenses.data);
        } catch (error) {
            console.error("Erro ao buscar dados dos top lists:", error);
        }
    };

    const handleSendEmail = async (endpoint, key) => {
        setLoadingStates(prev => ({ ...prev, [key]: true }));
        setLoadingTexts(prev => ({ ...prev, [key]: "A enviar e-mails" }));
        let dots = 0;

        const interval = setInterval(() => {
            dots = (dots + 1) % 4;
            setLoadingTexts(prev => ({
                ...prev,
                [key]: `A enviar e-mails${".".repeat(dots)}`,
            }));
        }, 500);

        try {
            await axios.get(endpoint);
            alert("Email enviado com sucesso!");
        } catch (error) {
            console.error("Erro ao enviar o email:", error);
            alert("Erro ao enviar o email.");
        } finally {
            clearInterval(interval);
            setLoadingStates(prev => ({ ...prev, [key]: false }));
            setLoadingTexts(prev => ({ ...prev, [key]: "" }));
        }
    };

    const renderTable = (data, headers, type) => (
        <table className="table-auto w-full text-left border-collapse border border-gray-300 mb-8">
            <thead>
                <tr>
                    {headers.map((header, index) => (
                        <th key={index} className="border border-gray-300 px-4 py-2">{header}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {data.map((item, index) => {
                    const details =
                        type === "client"
                            ? clientDetails[item.clientId]
                            : professionalDetails[item.professionalId];
                    return (
                        <tr key={index} className="hover:bg-gray-100">
                            <td className="border border-gray-300 px-4 py-2">
                                {details?.name || "Não disponível"}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {details?.email || "Não disponível"}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {type === "client" ? item.serviceCount || item.totalSpent : item.serviceCount}
                            </td>
                        </tr>
                    );
                })}
            </tbody>
        </table>
    );

    useEffect(() => {
        fetchTopLists();
    }, []);

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Top 10 Listas</h1>

            <div>
                <h2 className="text-xl font-semibold mb-4 text-gray-700">Top 10 Clientes por Serviços</h2>
                <div className="flex items-center">
                    <button
                        onClick={() =>
                            handleSendEmail("http://localhost:8080/services/sendEmailTopActivitiesClients", "clientsActivities")
                        }
                        className="bg-gray-800 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded mr-2"
                    >
                        Enviar Emails
                    </button>
                    {loadingStates.clientsActivities && (
                        <span className="text-gray-700 text-sm">{loadingTexts.clientsActivities}</span>
                    )}
                </div>
                <br />
                {renderTable(
                    topClientsByActivities,
                    ["Nome", "Email", "Serviços"],
                    "client"
                )}
            </div>

            <div>
                <h2 className="text-xl font-semibold mb-4 text-gray-700">Top 10 Profissionais por Serviços</h2>
                <div className="flex items-center">
                    <button
                        onClick={() =>
                            handleSendEmail("http://localhost:8080/services/sendEmailActivitiesProfessionals", "professionalsActivities")
                        }
                        className="bg-gray-800 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded mr-2"
                    >
                        Enviar Emails
                    </button>
                    {loadingStates.professionalsActivities && (
                        <span className="text-gray-700 text-sm">{loadingTexts.professionalsActivities}</span>
                    )}
                </div>
                <br />
                {renderTable(
                    topProfessionalsByActivities,
                    ["Nome", "Email", "Serviços"],
                    "professional"
                )}
            </div>

            <div>
                <h2 className="text-xl font-semibold mb-4 text-gray-700">Top 10 Clientes por Despesas</h2>
                <div className="flex items-center">
                    <button
                        onClick={() =>
                            handleSendEmail("http://localhost:8080/services/sendEmailTopExpensesClients", "clientsExpenses")
                        }
                        className="bg-gray-800 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded mr-2"
                    >
                        Enviar Emails
                    </button>
                    {loadingStates.clientsExpenses && (
                        <span className="text-gray-700 text-sm">{loadingTexts.clientsExpenses}</span>
                    )}
                </div>
                <br />
                {renderTable(
                    topClientsByExpenses,
                    ["Nome", "Email", "Despesas"],
                    "client"
                )}
            </div>
        </div>
    );
};

export default TopLists;
