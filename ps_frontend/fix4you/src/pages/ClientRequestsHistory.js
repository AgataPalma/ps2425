// src/pages/ClientRequestsHistory.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';

function ClientRequestsHistory() {
    const [requests, setRequests] = useState([]);
    const [filteredRequests, setFilteredRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [statusFilter, setStatusFilter] = useState('');

    useEffect(() => {
        axios.get('/api/client/requests')
            .then(response => {
                setRequests(response.data);
                setFilteredRequests(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching client requests:', error);
                setLoading(false);
            });
    }, []);

    const handleStatusFilterChange = (e) => {
        const status = e.target.value;
        setStatusFilter(status);
        if (status === '') {
            setFilteredRequests(requests);
        } else {
            setFilteredRequests(requests.filter(request => request.status === status));
        }
    };

    const handleWriteReview = (requestId) => {
        // Handle writing a review for the completed request
        console.log(`Review: ${requestId}`);
    };

    const handleConfirmCompletion = (requestId) => {
        // Handle confirming completion of an accepted request
        console.log(`Completado: ${requestId}`);
    };

    const handleContactSupport = (requestId) => {
        // Handle contacting support for an accepted request
        console.log(`Suporte: ${requestId}`);
    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Client Request History</h1>
            <div className="mb-6">
                <label htmlFor="statusFilter" className="block text-lg font-medium text-gray-700 mb-2">Filter by Status</label>
                <select
                    id="statusFilter"
                    value={statusFilter}
                    onChange={handleStatusFilterChange}
                    className="w-full p-2 border rounded-lg"
                >
                    <option value="">All Statuses</option>
                    <option value="completed">Completed</option>
                    <option value="pending">Pending</option>
                    <option value="accepted">Accepted</option>
                    <option value="canceled">Canceled</option>
                    <option value="refused">Refused</option>
                </select>
            </div>
            <div className="space-y-6">
                {filteredRequests.length > 0 ? (
                    filteredRequests.map(request => (
                        <div key={request.id} className="p-4 bg-gray-100 rounded-lg shadow-md">
                            <h3 className="text-xl font-bold text-gray-800 mb-2">{request.serviceName}</h3>
                            <p className="text-gray-600">Category: {request.category}</p>
                            <p className="text-gray-600">Date: {new Date(request.date).toLocaleDateString()}</p>
                            <p className="text-gray-600">Price: ${request.price}</p>
                            <p className="text-gray-600">Status: {request.status}</p>
                            {request.status === 'completed' && (
                                <button
                                    onClick={() => handleWriteReview(request.id)}
                                    className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition"
                                >
                                    Write Review
                                </button>
                            )}
                            {request.status === 'accepted' && (
                                <div className="mt-4 space-x-4">
                                    <button
                                        onClick={() => handleConfirmCompletion(request.id)}
                                        className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                                    >
                                        Confirm Completion
                                    </button>
                                    <button
                                        onClick={() => handleContactSupport(request.id)}
                                        className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-500 transition"
                                    >
                                        Contact Support
                                    </button>
                                </div>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">No requests found.</p>
                )}
            </div>
        </div>
    );
}

export default ClientRequestsHistory;
