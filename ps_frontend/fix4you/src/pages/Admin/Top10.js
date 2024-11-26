import React, { useEffect, useState } from "react";
import axios from "axios";

const Top10 = () => {
    const [clientsInfo, setClientsInfo] = useState({});
    const [professionalsInfo, setProfessionalsInfo] = useState({});
    const [topClients, setTopClients] = useState([]);
    const [topPayingProfessionals, setTopPayingProfessionals] = useState([]);
    const [topServiceProfessionals, setTopServiceProfessionals] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [clientsResponse, professionalsResponse, servicesResponse, feesResponse] = await Promise.all([
                axios.get("http://localhost:8080/clients"),
                axios.get("http://localhost:8080/professionals"),
                axios.get("http://localhost:8080/services"),
                axios.get("http://localhost:8080/professionalFees"),
            ]);

            // Map clients and professionals for easy lookup
            const clientsMap = clientsResponse.data.reduce((acc, client) => {
                acc[client.id] = { name: client.name, email: client.email };
                return acc;
            }, {});

            const professionalsMap = professionalsResponse.data.reduce((acc, professional) => {
                acc[professional.id] = { name: professional.name, email: professional.email };
                return acc;
            }, {});

            setClientsInfo(clientsMap);
            setProfessionalsInfo(professionalsMap);

            calculateTopClients(servicesResponse.data, clientsMap);
            calculateTopPayingProfessionals(feesResponse.data, professionalsMap);
            calculateTopServiceProfessionals(servicesResponse.data, professionalsMap);
        } catch (err) {
            console.error("Error fetching data:", err);
            setError("Não foi possível carregar os dados.");
        }
    };

    const calculateTopClients = (servicesData, clientsMap) => {
        const clientServiceCount = servicesData
            .filter(service => service.state === "COMPLETED") // Only completed services
            .reduce((acc, service) => {
                acc[service.clientId] = (acc[service.clientId] || 0) + 1;
                return acc;
            }, {});

        const sortedClients = Object.entries(clientServiceCount)
            .sort(([, countA], [, countB]) => countB - countA)
            .slice(0, 10)
            .map(([clientId, count]) => ({
                ...clientsMap[clientId],
                totalServices: count,
            }));

        setTopClients(sortedClients);
    };

    const calculateTopPayingProfessionals = (feesData, professionalsMap) => {
        const professionalPayments = feesData
            .filter(fee => fee.paymentStatus === "COMPLETED") // Only completed payments
            .reduce((acc, fee) => {
                acc[fee.professional.id] = (acc[fee.professional.id] || 0) + fee.value;
                return acc;
            }, {});

        const sortedProfessionals = Object.entries(professionalPayments)
            .sort(([, totalA], [, totalB]) => totalB - totalA)
            .slice(0, 10)
            .map(([professionalId, total]) => ({
                ...professionalsMap[professionalId],
                totalPaid: total,
            }));

        setTopPayingProfessionals(sortedProfessionals);
    };

    const calculateTopServiceProfessionals = (servicesData, professionalsMap) => {
        const professionalServiceCount = servicesData
            .filter(service => service.state === "COMPLETED") // Only completed services
            .reduce((acc, service) => {
                acc[service.professionalId] = (acc[service.professionalId] || 0) + 1;
                return acc;
            }, {});

        const sortedProfessionals = Object.entries(professionalServiceCount)
            .sort(([, countA], [, countB]) => countB - countA)
            .slice(0, 10)
            .map(([professionalId, count]) => ({
                ...professionalsMap[professionalId],
                totalServices: count,
            }));

        setTopServiceProfessionals(sortedProfessionals);
    };

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Estatísticas de Top</h1>

            {error && <div className="text-red-600 mb-4">{error}</div>}

            <div className="mb-12">
                <h2 className="text-2xl font-semibold mb-4">Top 10 Profissionais Pagantes</h2>
                <table className="table-auto w-full text-left border-collapse border border-gray-300">
                    <thead>
                        <tr>
                            <th className="border border-gray-300 px-4 py-2">#</th>
                            <th className="border border-gray-300 px-4 py-2">Nome</th>
                            <th className="border border-gray-300 px-4 py-2">Email</th>
                            <th className="border border-gray-300 px-4 py-2">Total Pago (€)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {topPayingProfessionals.map((professional, index) => (
                            <tr key={professional.email || index} className="hover:bg-gray-100">
                                <td className="border border-gray-300 px-4 py-2">{index + 1}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.name || "Desconhecido"}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.email || "N/A"}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.totalPaid}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <div className="mb-12">
                <h2 className="text-2xl font-semibold mb-4">Top 10 Clientes</h2>
                <table className="table-auto w-full text-left border-collapse border border-gray-300">
                    <thead>
                        <tr>
                            <th className="border border-gray-300 px-4 py-2">#</th>
                            <th className="border border-gray-300 px-4 py-2">Nome</th>
                            <th className="border border-gray-300 px-4 py-2">Email</th>
                            <th className="border border-gray-300 px-4 py-2">Total de Serviços</th>
                        </tr>
                    </thead>
                    <tbody>
                        {topClients.map((client, index) => (
                            <tr key={client.email || index} className="hover:bg-gray-100">
                                <td className="border border-gray-300 px-4 py-2">{index + 1}</td>
                                <td className="border border-gray-300 px-4 py-2">{client.name || "Desconhecido"}</td>
                                <td className="border border-gray-300 px-4 py-2">{client.email || "N/A"}</td>
                                <td className="border border-gray-300 px-4 py-2">{client.totalServices}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <div>
                <h2 className="text-2xl font-semibold mb-4">Top 10 Profissionais</h2>
                <table className="table-auto w-full text-left border-collapse border border-gray-300">
                    <thead>
                        <tr>
                            <th className="border border-gray-300 px-4 py-2">#</th>
                            <th className="border border-gray-300 px-4 py-2">Nome</th>
                            <th className="border border-gray-300 px-4 py-2">Email</th>
                            <th className="border border-gray-300 px-4 py-2">Total de Serviços</th>
                        </tr>
                    </thead>
                    <tbody>
                        {topServiceProfessionals.map((professional, index) => (
                            <tr key={professional.email || index} className="hover:bg-gray-100">
                                <td className="border border-gray-300 px-4 py-2">{index + 1}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.name || "Desconhecido"}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.email || "N/A"}</td>
                                <td className="border border-gray-300 px-4 py-2">{professional.totalServices}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Top10;
