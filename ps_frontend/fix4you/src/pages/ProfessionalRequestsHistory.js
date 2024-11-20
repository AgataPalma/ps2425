import React, { useEffect, useState } from 'react';
import axiosInstance from '../components/axiosInstance';
import Spinner from "../components/Spinner";

function ProfessionalRequestsHistory({ id }) {
    const [requests, setRequests] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('');
    const [activeTab, setActiveTab] = useState('open'); // Tracks the active tab
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

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        setSelectedCategory(''); // Reset the category filter when switching tabs
    };

    // Filter requests based on the active tab and selected category
    const filteredRequests = requests.filter(request => {
        const isCategoryMatch = selectedCategory
            ? request.category?.name === selectedCategory
            : true;

        if (activeTab === 'open') {
            return request.state === 'ACCEPTED' && isCategoryMatch;
        } else if (activeTab === 'concluded') {
            return ['COMPLETED', 'CANCELED', 'REFUSED'].includes(request.state) && isCategoryMatch;
        }
        return false;
    });

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Serviços</h1>

            {/* Tab Navigation */}
            <div className="flex space-x-4 mb-6">
                <button
                    onClick={() => handleTabChange('open')}
                    className={`p-2 rounded-lg ${activeTab === 'open' ? 'bg-yellow-500 text-white' : 'bg-gray-200 text-gray-800'}`}
                >
                    Open Services
                </button>
                <button
                    onClick={() => handleTabChange('concluded')}
                    className={`p-2 rounded-lg ${activeTab === 'concluded' ? 'bg-yellow-500 text-white' : 'bg-gray-200 text-gray-800'}`}
                >
                    Concluded Services
                </button>
            </div>

            {/* Tab Content */}
            <div>
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
                            <option key={category.id} value={category.category.name}>
                                {category.category.name}
                            </option>
                        ))}
                    </select>
                </div>
                <div className="space-y-6">
                    {filteredRequests.length > 0 ? (
                        filteredRequests.map(request => (
                            <div key={request.id} className="p-4 bg-gray-100 rounded-lg shadow-md">
                                <h3 className="text-xl font-bold text-gray-800 mb-2">{request.title}</h3>
                                <p className="text-gray-600">Category: {request.category?.name || "Unknown"}</p>
                                <p className="text-gray-600">Date: {new Date(request.date).toLocaleDateString()}</p>
                                <p className="text-gray-600">Price: ${request.price}</p>
                                <p className="text-gray-600">Status: {request.state}</p>
                                <p className="text-gray-600">Languages: {request.languages.map(lang => lang.name).join(', ')}</p>
                                <p className="text-gray-600">Location: {request.location || "Not specified"}</p>
                                <p className="text-gray-600">Urgent: {request.urgent ? "Yes" : "No"}</p>
                            </div>
                        ))
                    ) : (
                        <p className="text-gray-600">No services found.</p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProfessionalRequestsHistory;
