import React, { useEffect, useState } from 'react';
import axios from 'axios';

function ProfessionalRequestsHistory() {
    const [requests, setRequests] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Fetch completed services
        axios.get('/api/professional/requests')
            .then(response => {
                setRequests(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching requests:', error);
                setLoading(false);
            });

        // Fetch categories
        axios.get('/api/professional/categories')
            .then(response => {
                setCategories(response.data);
            })
            .catch(error => {
                console.error('Error fetching categories:', error);
            });
    }, []);

    const handleCategoryChange = (e) => {
        setSelectedCategory(e.target.value);
    };

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
                        <option key={category.id} value={category.name}>{category.name}</option>
                    ))}
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
                            {request.review && (
                                <div className="mt-4">
                                    <h4 className="text-lg font-semibold text-gray-800">Review</h4>
                                    <p className="text-gray-600">Rating: {request.review.rating} / 5</p>
                                    <p className="text-gray-600">Comment: {request.review.comment}</p>
                                </div>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">No completed services found.</p>
                )}
            </div>
        </div>
    );
}

export default ProfessionalRequestsHistory;