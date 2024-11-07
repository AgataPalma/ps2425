import React, { useEffect, useState } from 'react';
import axiosInstance from '../components/axiosInstance';

function ProfessionalRequestsHistory({ id }) {
    const [requests, setRequests] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Fetch services for the professional
        axiosInstance.get(`/services/professional/${id}`)
            .then(response => {
                setRequests(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching requests:', error);
                setLoading(false);
            });

        // Fetch categories
        axiosInstance.get(`/categoryDescriptions/user/${id}`)
            .then(response => {
                setCategories(response.data);
            })
            .catch(error => {
                console.error('Error fetching categories:', error);
            });
    }, [id]);

    const handleCategoryChange = (e) => {
        setSelectedCategory(e.target.value);
    };

    // Filter requests by selected category
    const filteredRequests = selectedCategory
        ? requests.filter(request => request.category === selectedCategory)
        : requests;

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">A carregar...</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Histórico de Serviços</h1>
            <div className="mb-6">
                <label htmlFor="category" className="block text-lg font-medium text-gray-700 mb-2">Filtrar por categoria</label>
                <select
                    id="category"
                    value={selectedCategory}
                    onChange={handleCategoryChange}
                    className="w-full p-2 border rounded-lg"
                >
                    <option value="">All Categories</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.category}>{category.category}</option>
                    ))}
                </select>
            </div>
            <div className="space-y-6">
                {filteredRequests.length > 0 ? (
                    filteredRequests.map(request => (
                        <div key={request.id} className="p-4 bg-gray-100 rounded-lg shadow-md">
                            <h3 className="text-xl font-bold text-gray-800 mb-2">{request.title}</h3>
                            <p className="text-gray-600">Category: {request.category}</p>
                            <p className="text-gray-600">Date: {new Date(request.date).toLocaleDateString()}</p>
                            <p className="text-gray-600">Price: ${request.price}</p>
                            <p className="text-gray-600">Status: {request.state}</p>
                            <p className="text-gray-600">Address: {request.address}, {request.postalCode}</p>
                            {request.languages && (
                                <p className="text-gray-600">Languages: {request.languages.join(', ')}</p>
                            )}
                            <p className="text-gray-600">Location: {request.location || "Not specified"}</p>
                            <p className="text-gray-600">Urgent: {request.urgent ? "Yes" : "No"}</p>
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">No services found.</p>
                )}
            </div>
        </div>
    );
}

export default ProfessionalRequestsHistory;
