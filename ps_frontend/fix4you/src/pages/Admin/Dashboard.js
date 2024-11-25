import React, { useEffect, useState } from "react";
import axiosInstance from "../../components/axiosInstance";

function Dashboard() {
    const [suspiciousUsers, setSuspiciousUsers] = useState([]);
    const [lowestPriceProfessionals, setLowestPriceProfessionals] = useState([]);
    const [pendingPayments, setPendingPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchPriceAlerts = async () => {
            try {
                setLoading(true);

                // Fetch categories
                const categoriesResponse = await axiosInstance.get("/categories");
                const categories = categoriesResponse.data;

                // Fetch professional prices
                const descriptionsResponse = await axiosInstance.get("/categoryDescriptions");
                const descriptions = descriptionsResponse.data;

                const suspicious = [];
                const lowestPrices = [];

                categories.forEach((category) => {
                    // Filter professionals in this category
                    const pricesInCategory = descriptions
                        .filter((desc) => desc.category.id === category.id)
                        .map((desc) => desc.mediumPricePerService);

                    if (pricesInCategory.length === 0) return;

                    // Calculate Median and IQR
                    const sortedPrices = [...pricesInCategory].sort((a, b) => a - b);
                    const Q1 = sortedPrices[Math.floor(sortedPrices.length / 4)];
                    const Q3 = sortedPrices[Math.floor((3 * sortedPrices.length) / 4)];
                    const IQR = Q3 - Q1;
                    const lowerBound = Q1 - 1.5 * IQR;

                    // Identify professionals whose prices are below the lower bound
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

                    // Find the professional with the lowest price in the category
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
                setError("Failed to load data. Please try again.");
                console.error(err);
                setLoading(false);
            }
        };
        const fetchPendingPayments = async () => {
            try {
                setLoading(true);

                // Fetch professional fees and filter for pending payments
                const response = await axiosInstance.get("/professionalFees");
                const fees = response.data.filter(
                    (fee) => fee.paymentStatus === "PENDING"
                );
                setPendingPayments(fees);

                setLoading(false);
            } catch (err) {
                setError("Failed to load pending payments. Please try again.");
                console.error(err);
                setLoading(false);
            }
        };
        fetchPriceAlerts();
        fetchPendingPayments();
    }, []);

    if (loading) {
        return <div className="text-center py-10">Loading...</div>;
    }

    if (error) {
        return <div className="text-center py-10 text-red-500">{error}</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-8">Admin Dashboard</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {/* Pending Tickets Section */}
                <div className="border p-4 rounded-lg bg-gray-100">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">Pending Tickets</h2>
                    <ul className="space-y-2">
                        <li className="flex justify-between items-center bg-white p-2 rounded shadow">
                            <span>Ticket #1234</span>
                            <span className="text-sm text-yellow-600">High Priority</span>
                        </li>
                        <li className="flex justify-between items-center bg-white p-2 rounded shadow">
                            <span>Ticket #1235</span>
                            <span className="text-sm text-gray-500">Low Priority</span>
                        </li>
                    </ul>
                    <button className="mt-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                        View All Tickets
                    </button>
                </div>

                {/* Suspicious Users Section */}
                <div className="border p-4 rounded-lg bg-gray-100">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">
                        Suspicious Price Alerts
                    </h2>
                    {suspiciousUsers.length > 0 ? (
                        <ul className="space-y-2">
                            {suspiciousUsers.map((user, index) => (
                                <li key={index} className="bg-white p-2 rounded shadow">
                                    <p>
                                        <strong>Professional ID:</strong> {user.userId}
                                    </p>
                                    <p>
                                        <strong>Category:</strong> {user.categoryName}
                                    </p>
                                    <p className="text-sm">
                                        User's Price:{" "}
                                        <span className="text-red-600">€{user.price}</span> | Lower Bound:{" "}
                                        <span className="text-green-600">€{user.lowerBound}</span>
                                    </p>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <>
                            <p className="text-gray-600">No suspicious users detected.</p>
                            <div className="mt-4">
                                <h3 className="text-lg font-semibold text-gray-800 mb-2">
                                    Professionals with the Lowest Price (By Category)
                                </h3>
                                <ul className="space-y-2">
                                    {lowestPriceProfessionals.map((user, index) => (
                                        <li key={index} className="bg-white p-2 rounded shadow">
                                            <p>
                                                <strong>Professional ID:</strong> {user.userId}
                                            </p>
                                            <p>
                                                <strong>Category:</strong> {user.categoryName}
                                            </p>
                                            <p className="text-sm">
                                                User's Price: <span className="text-blue-600">€{user.price}</span>
                                            </p>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        </>
                    )}
                </div>

                {/* Pending Payments Section */}
                <div className="border p-4 rounded-lg bg-gray-100">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">Pending Payments</h2>
                    <ul className="space-y-2">
                        {pendingPayments.map((payment) => (
                            <li
                                key={payment.id}
                                className="flex justify-between items-center bg-white p-2 rounded shadow"
                            >
                                <span>{payment.professional.name || "Unknown Professional"}</span>
                                <span>€{payment.value.toFixed(2)}</span>
                            </li>
                        ))}
                    </ul>
                    {pendingPayments.length === 0 && (
                        <p className="text-gray-600 mt-4">No pending payments.</p>
                    )}
                    <button className="mt-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                        View All Payments
                    </button>
                </div>

                {/* Platform Growth Section */}
                <div className="border p-4 rounded-lg bg-gray-100">
                    <h2 className="text-xl font-semibold text-gray-800 mb-4">Platform Growth</h2>
                    <div>
                        <p className="mb-2">New Users This Week: <strong>25</strong></p>
                        <p className="mb-2">Locations: <strong>Lisbon, Porto, Braga</strong></p>
                    </div>
                    <button className="mt-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                        View Analytics
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;
