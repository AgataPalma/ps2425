import React, { useEffect, useState } from 'react';
import axios from 'axios';

function ClientProfile() {
    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState(null);

    useEffect(() => {
        axios.get('/api/client/profiledata')
            .then(response => {
                setProfileData(response.data);
                setFormData({
                    name: response.data.name || '',
                    email: response.data.email || '',
                    address: response.data.address || '',
                    phoneNumber: response.data.phoneNumber || '',
                    avatar: response.data.avatar || ''
                });
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching client profile data:', error);
                setLoading(false);
            });
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleEdit = () => {
        console.log("Edit button clicked"); // Debug line
        if (!profileData) return;
        setEditMode(true);
        setFormData({
            name: profileData.name || '',
            email: profileData.email || '',
            address: profileData.address || '',
            phoneNumber: profileData.phoneNumber || '',
            avatar: profileData.avatar || ''
        });
    };


    const handleCancel = () => {
        setEditMode(false);
        setFormData(profileData);
    };

    const handleSave = () => {
        // Update profile data in the backend
        axios.put('/api/client/profiledata', formData)
            .then(response => {
                setProfileData(response.data);
                setEditMode(false);
            })
            .catch(error => {
                console.error('Error updating client profile data:', error);
            });
    };

    const handleDeleteAccount = () => {
        // Delete account logic here
        if (window.confirm('Are you sure you want to delete your account?')) {
            axios.delete('/api/client/profiledata')
                .then(() => {
                    // Redirect or logout after account deletion
                    window.location.href = '/Login';
                })
                .catch(error => {
                    console.error('Error deleting account:', error);
                });
        }
    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">A carregar..</div>;
    }

    return (
        <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Perfil</h1>
            <div className="flex items-center mb-6">
                <div className="w-24 h-24 bg-gray-300 rounded-full flex items-center justify-center overflow-hidden">
                    {editMode ? (
                        <select
                            name="avatar"
                            value={formData.avatar}
                            onChange={handleInputChange}
                            className="w-full h-full object-cover"
                        >
                            <option value="avatar1">Avatar 1</option>
                            <option value="avatar2">Avatar 2</option>
                            <option value="avatar3">Avatar 3</option>
                        </select>
                    ) : (
                        <img src={profileData?.avatar || "https://via.placeholder.com/150"} alt="Client Avatar"
                             className="w-full h-full object-cover"/>
                    )}
                </div>
                <div className="ml-6">
                    {editMode ? (
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleInputChange}
                            className="text-4xl font-bold text-yellow-600 border-b-2 border-gray-300 focus:outline-none"
                        />
                    ) : (
                        <h1 className="text-4xl font-bold text-yellow-600">{profileData?.name}</h1>
                    )}
                    <p className="text-gray-600">{profileData?.email}</p>
                </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Address</h3>
                    {editMode ? (
                        <input
                            type="text"
                            name="address"
                            value={formData.address}
                            onChange={handleInputChange}
                            className="w-full p-2 border rounded"
                        />
                    ) : (
                        <p className="text-gray-600">{profileData?.address || "No address provided"}</p>
                    )}
                </div>
                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Phone Number</h3>
                    {editMode ? (
                        <input
                            type="text"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={handleInputChange}
                            className="w-full p-2 border rounded"
                        />
                    ) : (
                        <p className="text-gray-600">{profileData?.phoneNumber || "No phone number provided"}</p>
                    )}
                </div>
                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Member Since</h3>
                    <p className="text-gray-600">{profileData?.memberSince ? new Date(profileData.memberSince).toLocaleDateString() : "N/A"}</p>
                </div>
            </div>
            <div className="mt-8">
                {editMode ? (
                    <div className="flex space-x-4">
                        <button onClick={handleSave}
                                className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">Save
                        </button>
                        <button onClick={handleCancel}
                                className="px-6 py-3 bg-gray-400 text-white rounded-lg hover:bg-gray-300 transition">Cancel
                        </button>
                    </div>
                ) : (
                    <div className="flex space-x-4">
                        <button onClick={handleEdit}
                                className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Edit
                            Profile
                        </button>
                        <button onClick={handleDeleteAccount}
                                className="px-6 py-3 bg-red-700 text-white rounded-lg hover:bg-red-600 transition">Delete
                            Account
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ClientProfile;