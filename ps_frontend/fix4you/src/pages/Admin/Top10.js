import React, { useState, useEffect } from "react";
import axios from "axios";

const TopLists = () => {
    const [topClientsByActivities, setTopClientsByActivities] = useState([]);
    const [topProfessionalsByActivities, setTopProfessionalsByActivities] = useState([]);
    const [topProfessionalsByExpenses, setTopProfessionalsByExpenses] = useState([]);

    const [loadingStates, setLoadingStates] = useState({
        clientsActivities: false,
        professionalsActivities: false,
        professionalsExpenses: false,
    });
    const [loadingTexts, setLoadingTexts] = useState({
        clientsActivities: "",
        professionalsActivities: "",
        professionalsExpenses: "",
    });

    const fetchTopLists = async () => {
        try {
            const [clientsActivities, professionalsActivities, professionalsExpenses] = await Promise.all([
                axios.get("http://localhost:8080/services/topActivitiesClients"),
                axios.get("http://localhost:8080/services/topActivitiesProfessionals"),
                axios.get("http://localhost:8080/professionalFees/topExpensesProfessionals"),
            ]);

            setTopClientsByActivities(clientsActivities.data);
            setTopProfessionalsByActivities(professionalsActivities.data);
            setTopProfessionalsByExpenses(professionalsExpenses.data);
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
                {data.map((item, index) => (
                    <tr key={index} className="hover:bg-gray-100">
                        <td className="border border-gray-300 px-4 py-2">
                            {type === "client" ? item.clientId : item.professionalId}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                            {type === "client" ? item.clientName : item.professionalName}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                            {item.serviceCount || item.totalSpent}
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );

    const renderProfessionalsExpensesTable = (data) => (
        <table className="table-auto w-full text-left border-collapse border border-gray-300 mb-8">
            <thead>
                <tr>
                    <th className="border border-gray-300 px-4 py-2">ID</th>
                    <th className="border border-gray-300 px-4 py-2">Nome</th>
                    <th className="border border-gray-300 px-4 py-2">Despesas €</th>
                </tr>
            </thead>
            <tbody>
                {data.map((item, index) => (
                    <tr key={index} className="hover:bg-gray-100">
                        <td className="border border-gray-300 px-4 py-2">
                            {item.professionalId}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                            {item.professionalName || "Não disponível"}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                            {item.totalSpent}
                        </td>
                    </tr>
                ))}
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
                    ["ID", "Nome", "Nº Serviços"],
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
                    ["ID", "Nome", "Nº Serviços"],
                    "professional"
                )}
            </div>

            <div>
                <h2 className="text-xl font-semibold mb-4 text-gray-700">Top 10 Profissionais por Despesas</h2>
                <div className="flex items-center">
                    <button
                        onClick={() =>
                            handleSendEmail("http://localhost:8080/professionalFees/sendEmailTopExpensesProfessionals", "professionalsExpenses")
                        }
                        className="bg-gray-800 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded mr-2"
                    >
                        Enviar Emails
                    </button>
                    {loadingStates.professionalsExpenses && (
                        <span className="text-gray-700 text-sm">{loadingTexts.professionalsExpenses}</span>
                    )}
                </div>
                <br />
                {renderProfessionalsExpensesTable(topProfessionalsByExpenses)}
            </div>
        </div>
    );
};

export default TopLists;
