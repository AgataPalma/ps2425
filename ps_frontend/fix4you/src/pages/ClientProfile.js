import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Select from 'react-select';
import axiosInstance from "../components/axiosInstance";

function ClientProfile({ id }) {
    const [profileData, setProfileData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState(null);
    const [avatarList, setAvatarList] = useState([]);
    const [showAvatarModal, setShowAvatarModal] = useState(false);
    const [selectedAvatar, setSelectedAvatar] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [locationOptions, setLocationOptions] = useState([]);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showConfirmDeleteModal, setShowConfirmDeleteModal] = useState(false);

    const DICEBEAR_BASE_URL = "https://api.dicebear.com/9.x/adventurer/svg?seed=";

    useEffect(() => {
        const fetchProfileData = async () => {
            try {
                const response = await axiosInstance.get(`/clients/${id}`);

                let profileImage = atob(response.data.profileImage);

                if (!profileImage) {
                    profileImage = DICEBEAR_BASE_URL + "avatar3.svg";
                }

                setProfileData(response.data);
                setFormData({
                    name: response.data.name || '',
                    email: response.data.email || '',
                    phoneNumber: response.data.phoneNumber || '',
                    profileImage: profileImage,
                    dateCreation: response.data.dateCreation || '',
                    location: response.data.location || '',
                    password: '*****',
                });
            } catch (error) {
                console.error('Error fetching client profile data:', error);
            } finally {
                setLoading(false);
            }
        };

        const fetchLocationData = async () => {
            try {
                const response = await axios.get('https://json.geoapi.pt/municipios/freguesias');
                const organizedData = response.data.map((municipio) => ({
                    label: municipio.nome,
                    options: municipio.freguesias.map((freguesia) => ({
                        label: freguesia,
                        value: `${municipio.nome}, ${freguesia}`
                    }))
                }));
                setLocationOptions(organizedData);
            } catch (error) {
                console.error('Error fetching location data:', error);
            }
        };

        const generateAvatarList = () => {
            const avatars = Array.from({ length: 21 }, (_, i) => `${DICEBEAR_BASE_URL}avatar${i + 1}`);
            setAvatarList(avatars);
        };

        fetchLocationData();
        fetchProfileData();
        generateAvatarList();

    }, [id]);

    const handleEdit = () => {
        setEditMode(true);
    };

    const handleCancel = () => {
        setEditMode(false);
        
        const profileImage = profileData.profileImage.includes('http')
            ? profileData.profileImage
            : atob(profileData.profileImage);

        setFormData({
            ...profileData,
            password: '*****',
            profileImage: profileImage
        });
    };

    const handleLocationChange = (selectedOption) => {
        setFormData({ ...formData, location: selectedOption.value });
    };

    const handleSave = () => {
        formData.profileImage = btoa(formData.profileImage);
        formData.userType = "CLIENT";

        const formDataToSend = { ...formData };
        if (formData.password === '*****' || formData.password === '') {
            formDataToSend.password = profileData.password;
        }

        axiosInstance.put(`/clients/${id}`, formDataToSend)
            .then(response => {
                const updatedProfileData = {
                    ...response.data,
                    profileImage: atob(response.data.profileImage)
                };
                setProfileData(updatedProfileData);
                setFormData(updatedProfileData);
                setEditMode(false);
                setSuccessMessage("Profile updated successfully!");
                // Clear the success message after 3 seconds
                setTimeout(() => {
                    setSuccessMessage('');
                }, 3000);
            })
            .catch(error => {
                console.error('Error updating client profile data:', error);
                setErrorMessage("Failed to update profile. Please try again.");
                // Clear the error message after 3 seconds
                setTimeout(() => {
                    setErrorMessage('');
                }, 3000);
            });
    };

    const handleDeleteAccount = () => {
        setShowConfirmDeleteModal(true); // Show confirmation modal
    };

    const confirmDeleteAccount = () => {
        axiosInstance.delete(`/clients/${id}`)
            .then(() => {
                //setSuccessMessage("Account successfully deleted!");
                setShowConfirmDeleteModal(false); // Close confirmation modal
                setShowDeleteModal(true); // Show success modal
            })
            .catch(error => {
                console.error('Error deleting account:', error);
                setErrorMessage("Failed to delete account. Please try again. If the error persists, please contact support.");
                setTimeout(() => {
                    setErrorMessage('');
                }, 3000);
            });
    };

    const handleConfirmRedirect = () => {
        setShowDeleteModal(false);
        window.location.href = '/Login';
    };

    const openAvatarModal = () => {
        setSelectedAvatar(formData.profileImage);
        setShowAvatarModal(true);
    };

    const confirmAvatarSelection = () => {
        setFormData({ ...formData, profileImage: selectedAvatar });
        setShowAvatarModal(false);
    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
    }

    return (
        <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            {successMessage && <div className="bg-green-100 text-green-700 p-4 mb-4 rounded">{successMessage}</div>}
            {errorMessage && <div className="bg-red-100 text-red-700 p-4 mb-4 rounded">{errorMessage}</div>}

            {/* Confirmation Modal for Deletion */}
            {showConfirmDeleteModal && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Confirmação da eliminação de conta</h2>
                        <p>Tem a certeza de que pretende apagar a conta? Esta acção é irreversível e os seus dados serão eliminados.</p>
                        <div className="mt-6 flex justify-end space-x-4">
                            <button
                                onClick={() => setShowConfirmDeleteModal(false)} // Close modal without deleting
                                className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmDeleteAccount} // Call actual delete function
                                className="px-4 py-2 bg-red-600 text-white rounded-lg"
                            >
                                Confirmar
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Success Modal After Deletion */}
            {showDeleteModal && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Conta Apagada</h2>
                        <p>{successMessage}</p>
                        <div className="mt-6 flex justify-end">
                            <button
                                onClick={handleConfirmRedirect}
                                className="px-4 py-2 bg-yellow-600 text-white rounded-lg"
                            >
                                OK
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <div className="flex items-center mb-6">
                <div
                    className={`w-24 h-24 bg-gray-300 rounded-full flex items-center justify-center overflow-hidden ${editMode ? 'cursor-pointer' : ''}`}
                    onClick={editMode ? openAvatarModal : null}
                >
                    <img src={formData.profileImage} alt="Client Avatar" className="w-full h-full object-cover" />
                </div>
                <div className="ml-6">
                    {editMode ? (
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            className="text-4xl font-bold text-yellow-600 border-b-2 border-gray-300 focus:outline-none"
                        />
                    ) : (
                        <h1 className="text-4xl font-bold text-yellow-600">{profileData?.name}</h1>
                    )}
                    <p className="text-gray-600">{profileData?.email}</p>
                </div>
            </div>

            {showAvatarModal && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Escolha um Avatar</h2>
                        <div className="grid grid-cols-3 gap-4 overflow-y-auto max-h-60">
                            {avatarList.map((avatarUrl, index) => (
                                <img
                                    key={index}
                                    src={avatarUrl}
                                    alt={`Avatar ${index + 1}`}
                                    onClick={() => setSelectedAvatar(avatarUrl)}
                                    className={`w-16 h-16 rounded-full cursor-pointer ${selectedAvatar === avatarUrl ? 'border-4 border-yellow-600' : 'border-2 border-gray-300'}`}
                                />
                            ))}
                        </div>
                        <div className="mt-6 flex justify-end space-x-4">
                            <button
                                onClick={() => setShowAvatarModal(false)}
                                className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={confirmAvatarSelection}
                                className="px-4 py-2 bg-yellow-600 text-white rounded-lg"
                            >
                                Confirm
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mt-6">
                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Telemóvel</h3>
                    {editMode ? (
                        <input
                            type="text"
                            name="phoneNumber"
                            value={formData.phoneNumber}
                            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                    ) : (
                        <p className="text-gray-600">{profileData?.phoneNumber || "No phone number provided"}</p>
                    )}
                </div>

                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Membro desde</h3>
                    <p className="text-gray-600">{profileData?.dateCreation ? new Date(profileData.dateCreation).toLocaleDateString() : "N/A"}</p>
                </div>

                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Localização</h3>
                    {editMode ? (
                        <Select
                            options={locationOptions}
                            onChange={handleLocationChange}
                            placeholder="Seleccione a freguesia"
                            value={locationOptions.find(option =>
                                option.options.some(subOption => subOption.value === formData.location)
                            )}
                            className="w-full p-2 border rounded"
                        />
                    ) : (
                        <p className="text-gray-600">{profileData?.location || "Sem localização definida"}</p>
                    )}
                </div>
                <div>
                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Password</h3>
                    {editMode ? (
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            placeholder="Enter new password"
                            className="w-full p-2 border rounded"
                        />
                    ) : (
                        <p className="text-gray-600">*****</p>
                    )}
                </div>
            </div>

            <div className="mt-8">
                {editMode ? (
                    <div className="flex space-x-4">
                        <button onClick={handleSave} className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Guardar</button>
                        <button onClick={handleCancel} className="px-6 py-3 bg-gray-400 text-white rounded-lg hover:bg-gray-300 transition">Cancelar</button>
                    </div>
                ) : (
                    <div className="flex space-x-4">
                        <button onClick={handleEdit} className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Editar</button>
                        <button onClick={handleDeleteAccount} className="px-6 py-3 bg-red-700 text-white rounded-lg hover:bg-red-600 transition">Apagar</button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ClientProfile;
