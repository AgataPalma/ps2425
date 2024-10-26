
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Tab } from '@headlessui/react';

function ProfessionalProfile() {
    const [profileData, setProfileData] = useState(null);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        address: '',
        phoneNumber: '',
        avatar: '',
        description: '',
        password: '*****',
    });

    useEffect(() => {
        axios.get('/api/professional/profile')
            .then(response => {
                setProfileData(response.data);
                setFormData({
                    name: response.data.name || '',
                    email: response.data.email || '',
                    address: response.data.address || '',
                    phoneNumber: response.data.phoneNumber || '',
                    avatar: response.data.avatar || '',
                    description: response.data.description || '',
                    password: '*****',
                });
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching professional profile data:', error);
                setLoading(false);
            });

        axios.get('/api/professional/categories')
            .then(response => {
                setCategories(response.data);
            })
            .catch(error => {
                console.error('Error fetching professional categories:', error);
            });
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleEdit = () => {
        setEditMode(true);
    };

    const handleCancel = () => {
        setEditMode(false);
        setFormData(profileData);
    };

    const handleSave = () => {
        axios.put('/api/professional/profile', formData)
            .then(response => {
                setProfileData(response.data);
                setEditMode(false);
            })
            .catch(error => {
                console.error('Error updating professional profile data:', error);
            });
    };

    const handleDeleteAccount = () => {
        if (window.confirm('Are you sure you want to delete your account?')) {
            axios.delete('/api/professional/profile')
                .then(() => {
                    window.location.href = '/Login';
                })
                .catch(error => {
                    console.error('Error deleting account:', error);
                });
        }
    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
    }

    return (
        <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <Tab.Group>
                <Tab.List className="flex space-x-4 mb-8">
                    <Tab className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                        Personal Information
                    </Tab>
                    {categories.map((category) => (
                        <Tab key={category.id} className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                            {category.name}
                        </Tab>
                    ))}
                </Tab.List>
                <Tab.Panels>
                    <Tab.Panel>
                        <div className="space-y-6">
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
                                        <img src={profileData?.avatar || "https://via.placeholder.com/150"} alt="Professional Avatar" className="w-full h-full object-cover" />
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
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Password</h3>
                                    <p className="text-gray-600">{formData.password}</p>
                                </div>
                                <div>
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Description</h3>
                                    {editMode ? (
                                        <textarea
                                            name="description"
                                            value={formData.description}
                                            onChange={handleInputChange}
                                            className="w-full p-2 border rounded"
                                        />
                                    ) : (
                                        <p className="text-gray-600">{profileData?.description || "No description provided"}</p>
                                    )}
                                </div>
                            </div>
                            <div className="mt-8">
                                {editMode ? (
                                    <div className="flex space-x-4">
                                        <button onClick={handleSave} className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">Save</button>
                                        <button onClick={handleCancel} className="px-6 py-3 bg-gray-400 text-white rounded-lg hover:bg-gray-300 transition">Cancel</button>
                                    </div>
                                ) : (
                                    <div className="flex space-x-4">
                                        <button onClick={handleEdit} className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">Edit Profile</button>
                                        <button onClick={handleDeleteAccount} className="px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-500 transition">Delete Account</button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </Tab.Panel>
                    {categories.map((category) => (
                        <Tab.Panel key={category.id}>
                            <div className="space-y-6">
                                <h3 className="text-2xl font-bold text-gray-800 mb-4">{category.name}</h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Portfolio</h3>
                                        {/* Portfolio photos and description would go here */}
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Hourly Rate</h3>
                                        {editMode ? (
                                            <input
                                                type="text"
                                                name={`hourlyRate-${category.id}`}
                                                value={category.hourlyRate || ''}
                                                onChange={handleInputChange}
                                                className="w-full p-2 border rounded"
                                            />
                                        ) : (
                                            <p className="text-gray-600">{category.hourlyRate || "No hourly rate provided"}</p>
                                        )}
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Issues Receipt</h3>
                                        <p className="text-gray-600">{category.issuesReceipt ? 'Yes' : 'No'}</p>
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Charges for Travel</h3>
                                        <p className="text-gray-600">{category.chargesTravel ? 'Yes' : 'No'}</p>
                                    </div>
                                </div>
                            </div>
                        </Tab.Panel>
                    ))}
                </Tab.Panels>
            </Tab.Group>
        </div>
    );
}

export default ProfessionalProfile;