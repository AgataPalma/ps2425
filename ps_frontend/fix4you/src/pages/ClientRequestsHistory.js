import React, { useEffect, useState } from 'react';
import axiosInstance from "../components/axiosInstance";

function ClientRequestsHistory({ id }) {
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axiosInstance.get(`/services/client/${id}`)
            .then(response => {
                const completedRequests = response.data.filter(request => request.state === "COMPLETED");
                setRequests(completedRequests);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching client requests:', error);
                setLoading(false);
            });
    }, [id]);

    const handleWriteReview = (requestId) => {
        console.log(`Review: ${requestId}`);
    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Serviços Concluídos</h1>
            <div className="space-y-6">
                {requests.length > 0 ? (
                    requests.map(request => (
                        <div key={request.id} className="p-4 bg-gray-100 rounded-lg shadow-md">
                            <h3 className="text-xl font-bold text-gray-800 mb-2">{request.title}</h3>
                            <p className="text-gray-600">Category: {request.category}</p>
                            <p className="text-gray-600">Date: {new Date(request.date).toLocaleDateString()}</p>
                            <p className="text-gray-600">Price: ${request.price}</p>
                            <p className="text-gray-600">Status: {request.state}</p>
                            <button
                                onClick={() => handleWriteReview(request.id)}
                                className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition"
                            >
                                Write Review
                            </button>
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">Ainda não tem nenhum serviço concluído.</p>
                )}
            </div>
        </div>
    );
}

export default ClientRequestsHistory;
