import React, { useEffect, useState } from "react";
import axiosInstance from "../../components/axiosInstance";
import Spinner from "../../components/Spinner";
import { useNavigate } from "react-router-dom";

function Dashboard() {
    const [suspiciousUsers, setSuspiciousUsers] = useState([]);
    const [lowestPriceProfessionals, setLowestPriceProfessionals] = useState([]);
    const [pendingPayments, setPendingPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [tickets, setTickets] = useState([]);
    const navigate = useNavigate();
    const [topTops, setTopTops] = useState({
        clientActivity: null,
        professionalActivity: null,
        professionalExpense: null,
    });

    useEffect(() => {
        const fetchPriceAlerts = async () => {
            try {
                setLoading(true);

                const categoriesResponse = await axiosInstance.get("/categories");
                const categories = categoriesResponse.data;
                const descriptionsResponse = await axiosInstance.get("/categoryDescriptions");
                const descriptions = descriptionsResponse.data;
                const suspicious = [];
                const lowestPrices = [];

                categories.forEach((category) => {
                    const pricesInCategory = descriptions
                        .filter((desc) => desc.category.id === category.id)
                        .map((desc) => desc.mediumPricePerService);

                    if (pricesInCategory.length === 0) return;

                    const sortedPrices = [...pricesInCategory].sort((a, b) => a - b);
                    const Q1 = sortedPrices[Math.floor(sortedPrices.length / 4)];
                    const Q3 = sortedPrices[Math.floor((3 * sortedPrices.length) / 4)];
                    const IQR = Q3 - Q1;
                    const lowerBound = Q1 - 1.5 * IQR;

                    descriptions
                        .filter((desc) => desc.category.id === category.id)
                        .forEach((desc) => {
                            if (desc.mediumPricePerService < lowerBound) {
                                suspicious.push({
                                    userId: desc.professionalId,
                                    categoryName: category.name,
                                    price: desc.mediumPricePerService.toFixed(2),
                                    lowerBound: lowerBound.toFixed(2),
                                });
                            }
                        });

                    const lowestPrice = Math.min(...pricesInCategory);
                    const lowestProfessional = descriptions.find(
                        (desc) =>
                            desc.category.id === category.id &&
                            desc.mediumPricePerService === lowestPrice
                    );

                    if (lowestProfessional) {
                        lowestPrices.push({
                            userId: lowestProfessional.professionalId,
                            categoryName: category.name,
                            price: lowestProfessional.mediumPricePerService.toFixed(2),
                        });
                    }
                });

                setSuspiciousUsers(suspicious);
                setLowestPriceProfessionals(lowestPrices);
                setLoading(false);
            } catch (err) {
                setError(err.message);
                console.error(err);
                setLoading(false);
            }
        };
        const fetchPendingPayments = async () => {
            try {
                setLoading(true);

                const response = await axiosInstance.get("/professionalFees");
                const fees = response.data.filter(
                    (fee) => fee.paymentStatus === "PENDING"
                );
                setPendingPayments(fees);

                setLoading(false);
            } catch (err) {
                setError(err.message);
                setLoading(false);
            }
        };
        const fetchTickets = async () => {
            try {
                setLoading(true);

                const response = await axiosInstance.get("/tickets");
                const allTickets = response.data;
                const openTickets = allTickets.filter(ticket => ticket.status === "NEW");
                const inProgressTickets = allTickets.filter(ticket => ticket.status === "IN_REVIEW");
                const displayedTickets = [
                    ...openTickets.slice(0, 5),
                    ...inProgressTickets.slice(0, 5 - openTickets.length),
                ];

                setTickets(displayedTickets);
                setLoading(false);
            } catch (err) {
                setError(err.message);
                setLoading(false);
            }
        };
        const fetchTops = async () => {
            try {
                setLoading(true);

                const [topClientsResponse, topProfessionalsResponse, topExpensesResponse] =
                    await Promise.all([
                        axiosInstance.get("/services/topActivitiesClients"),
                        axiosInstance.get("/services/topActivitiesProfessionals"),
                        axiosInstance.get("/professionalFees/topExpensesProfessionals"),
                    ]);

                const topClient = topClientsResponse.data[0] || {};
                const topProfessional = topProfessionalsResponse.data[0] || {};
                const topExpense = topExpensesResponse.data[0] || {};

                setTopTops({
                    clientActivity: topClient,
                    professionalActivity: topProfessional,
                    professionalExpense: topExpense,
                });

                setLoading(false);
            } catch (err) {
                setError(err.message);
                setLoading(false);
            }
        };

        fetchPriceAlerts();
        fetchPendingPayments();
        fetchTickets();
        fetchTops();
    }, []);

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    if (error) {
        return <div className="text-center py-10 text-red-500">{error}</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-8">Admin Dashboard </h1>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {/* Pending Tickets Section */}
                <div className="border p-4 rounded-lg bg-gray-100 relative">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">Tickets</h2>
                    {tickets.length > 0 ? (
                        <ul className="space-y-2 pb-12">
                            {tickets.map((ticket) => (
                                <li
                                    key={ticket.id}
                                    className="flex justify-between items-center bg-white p-2 rounded shadow"
                                >
                                    <div>
                                        <p className="font-bold">{ticket.title}</p>
                                        <p className="text-sm text-gray-600">
                                            Status: {ticket.status === "NEW" ? "Open" : "In Progress"}
                                        </p>
                                    </div>
                                    <span className="text-sm text-gray-600">
                                        Created On: {new Date(ticket.ticketStartDate).toLocaleDateString()}
                                    </span>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-600">No tickets to display.</p>
                    )}
                    <button
                        className="absolute bottom-4 left-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                        onClick={() => navigate("/tickets")}
                    >
                        Ver tickets
                    </button>
                </div>

                {/* Suspicious Users Section */}
                <div className="border p-4 rounded-lg bg-gray-100 relative">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">
                        Controlo de preços
                    </h2>
                    {suspiciousUsers.length > 0 ? (
                        <ul className="space-y-2 pb-12">
                            {suspiciousUsers.map((user, index) => (
                                <li key={index} className="bg-white p-2 rounded shadow">
                                    <p>
                                        <strong>Profissional:</strong> {user.userId}
                                    </p>
                                    <p>
                                        <strong>Categoria:</strong> {user.categoryName}
                                    </p>
                                    <p className="text-sm">
                                        Preço:{" "}
                                        <span className="text-red-600">€{user.price}</span> | Valor mínimo:{" "}
                                        <span className="text-green-600">€{user.lowerBound}</span>
                                    </p>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <>
                            <p className="text-gray-600">Não existem flutuações significativas de preço</p>
                            <div className="mt-4">
                                <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                    Profissionais com preços mais baixos (Por categoria)
                                </h3>
                                <ul className="space-y-2 pb-12">
                                    {lowestPriceProfessionals.map((user, index) => (
                                        <li key={index} className="bg-white p-2 rounded shadow">
                                            <p>
                                                <strong>Profissional:</strong> {user.userId}
                                            </p>
                                            <p>
                                                <strong>Categoria:</strong> {user.categoryName}
                                            </p>
                                            <p className="text-sm">
                                                Preço: <span className="text-blue-600">€{user.price}</span>
                                            </p>
                                        </li>
                                    ))}
                                </ul>
                                <button
                                    className="absolute bottom-4 left-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                    onClick={() => navigate("/UsersManagement")}
                                >
                                    Utilizadores
                                </button>
                            </div>

                        </>
                    )}

                </div>

                {/* Pending Payments Section */}
                <div className="border p-4 rounded-lg bg-gray-100 relative">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">Pagamentos pendentes</h2>
                    <div className="flex justify-between bg-gray-200 p-2 rounded-t shadow font-semibold text-gray-700">
                        <span>Nome</span>
                        <span>ID</span>
                        <span>Valor</span>
                    </div>
                    <ul className="space-y-2 pb-12">
                        {pendingPayments.slice(0, 8).map((payment) => (
                            <li
                                key={payment.id}
                                className="flex justify-between items-center bg-white p-2 rounded shadow"
                            >
                                <span>{payment.professional?.name || "Unknown Professional"}</span>
                                <span>{payment.professional?.id || "Unknown Professional"}</span>
                                <span>€{payment.value.toFixed(2)}</span>
                            </li>
                        ))}
                    </ul>
                    {pendingPayments.length === 0 && (
                        <p className="text-gray-600 mt-4">No pending payments.</p>
                    )}
                    <button
                        className="absolute bottom-4 left-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                        onClick={() => navigate("/payments")}
                    >
                        Ver Pagamentos
                    </button>
                </div>

                {/* Platform Tops Section */}
                <div className="border p-4 rounded-lg bg-gray-100 relative">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">Top Utilizadores</h2>
                    {topTops.clientActivity && topTops.professionalActivity  && topTops.professionalExpense ? (
                        <div className="space-y-2 pb-12">
                            <div className="bg-white border-l-4 border-yellow-700 p-4 rounded-lg shadow">
                                <h3 className="text-xl font-bold border-gray-800">Top Cliente</h3>
                                <p className="text-gray-800">Nome: {topTops.clientActivity.clientName || "N/A"}</p>
                                <p className="text-gray-800">Total de serviços: {topTops.clientActivity.serviceCount || "N/A"}</p>
                                <p className="text-gray-800">ID: {topTops.clientActivity.clientId || "N/A"}</p>
                            </div>
                            <div className="bg-white border-l-4 border-yellow-700 p-4 rounded-lg shadow">
                                <h3 className="text-xl font-bold border-gray-800">Top Profissional - Por serviços</h3>
                                <p className="text-gray-800">Nome: {topTops.professionalActivity.professionalName || "N/A"}</p>
                                <p className="text-gray-800">Total de serviços: {topTops.professionalActivity.serviceCount || "N/A"}</p>
                                <p className="text-gray-800">ID: {topTops.professionalActivity.professionalId || "N/A"}</p>

                            </div>
                            <div className="bg-white border-l-4 border-yellow-700 p-4 rounded-lg shadow">
                                <h3 className="text-xl font-bold text-gray-800">Top Profissional - Por faturação</h3>
                                <p className="text-gray-800">Nome: {topTops.professionalExpense.professionalName || "N/A"}</p>
                                <p className="text-gray-800">Total Pago:
                                    € {topTops.professionalExpense.totalSpent?.toFixed(2) || "N/A"}</p>
                                <p className="text-gray-800">ID: {topTops.professionalExpense.professionalId || "N/A"}</p>
                            </div>
                        </div>
                    ) : (
                        <p className="text-gray-600">Loading top data...</p>
                    )}
                    <button
                        className="absolute bottom-4 left-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                        onClick={() => navigate("/Top10")}
                    >
                        Ver Tops
                    </button>
                </div>

            </div>
        </div>
    );
}

export default Dashboard;
