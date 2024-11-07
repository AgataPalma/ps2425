
import React, { useEffect, useState } from 'react';
import axiosInstance from '../components/axiosInstance';
import axios from 'axios';
import { Tab } from '@headlessui/react';
import Select from 'react-select';

function ProfessionalProfile({ id }) {
    const [profileData, setProfileData] = useState(null);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [locationOptions, setLocationOptions] = useState([]);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showDeleteCategoryModal, setShowDeleteCategoryModal] = useState(false);
    const [showConfirmDeleteModal, setShowConfirmDeleteModal] = useState(false);
    const [showConfirmDeleteCategoryModal, setShowConfirmDeleteCategoryModal] = useState(false);
    const [categoryEditData, setCategoryEditData] = useState({});
    const [selectedCategoryId, setSelectedCategoryId] = useState(null);
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [availableCategories, setAvailableCategories] = useState([]);
    const [portfolio, setPortfolio] = useState([]);
    const [portfolioDescription, setPortfolioDescription] = useState('');
    const [portfolioImages, setPortfolioImages] = useState([]);
    const [showNewCategory, setShowNewCategory] = useState(false);
    const [newCategoryData, setNewCategoryData] = useState({
        category: '',
        mediumPricePerService: '',
        providesInvoices: false,
        chargesTravels: false
    });

    useEffect(() => {
        axiosInstance.get(`/professionals/${id}`)
            .then(response => {
                const data = response.data;

                const profileImageBase64 = data.profileImage
                    ? `data:image/jpeg;base64,${data.profileImage}`
                    : '';

                setProfileData({ ...data, profileImage: profileImageBase64 });
                setFormData({
                    name: data.name || '',
                    email: data.email || '',
                    address: data.address || '',
                    phoneNumber: data.phoneNumber || '',
                    profileImage: profileImageBase64,
                    description: data.description || '',
                    dateCreation: data.dateCreation || '',
                    location: data.location || '',
                    nif: data.nif || '',
                    acceptedPayments: data.acceptedPayments || '',
                    languages: data.languages || '',
                    strikes: data.strikes || '',
                    password: '*****',
                });
                setLoading(false);

            })
            .catch(error => {
                console.error('Error fetching professional profile data:', error);
                setLoading(false);
            });

        axiosInstance.get(`/portfolioItems/user/${id}`)
            .then(response => {
                if (response.data.length > 0) {
                    setPortfolio(response.data);
                }
            })
            .catch(error => {
                console.error('Error fetching portfolio:', error);
            });

        axiosInstance.get(`/categoryDescriptions/user/${id}`)
            .then(response => {
                const associatedCategories = response.data.map(cat => cat.category);
                setCategories(response.data);

                axiosInstance.get(`/categoryDescriptions`)
                    .then(allCategoriesResponse => {
                        const allCategories = allCategoriesResponse.data;

                        const uniqueAvailableCategories = Array.from(
                            new Set(allCategories.map(category => category.category))
                        )
                            .filter(category => !associatedCategories.includes(category))
                            .map(category => ({ value: category, label: category }));

                        setAvailableCategories(uniqueAvailableCategories);
                    })
                    .catch(error => {
                        console.error('Error fetching all categories:', error);
                    });
            })
            .catch(error => {
                console.error('Error fetching professional categories:', error);
            });


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

        fetchLocationData();
    }, [id]);

    const handleCategorySelection = (selectedOption) => {
        setNewCategoryData({
            ...newCategoryData,
            category: selectedOption.value,
        });
    };

    const handleAddCategory = () => {
        if (newCategoryData.category && newCategoryData.mediumPricePerService) {
            const categoryPayload = {
                professionalId: id,
                category: newCategoryData.category,
                chargesTravels: newCategoryData.chargesTravels,
                providesInvoices: newCategoryData.providesInvoices,
                mediumPricePerService: parseFloat(newCategoryData.mediumPricePerService),
            };

            axiosInstance.post(`/categoryDescriptions`, categoryPayload)
                .then(response => {
                    setCategories([...categories, response.data]);
                    setSuccessMessage("New category added successfully!");
                    setTimeout(() => setSuccessMessage(''), 3000);

                    setNewCategoryData({
                        category: '',
                        mediumPricePerService: '',
                        providesInvoices: false,
                        chargesTravels: false,
                    });

                    window.location.href = '/professional-profile'
                })
                .catch(error => {
                    console.error('Error adding new category:', error);
                    setErrorMessage("Failed to add new category. Please try again.");
                    setTimeout(() => setErrorMessage(''), 3000);
                });
        } else {
            setErrorMessage("Please fill in all required fields.");
            setTimeout(() => setErrorMessage(''), 3000);
        }
    };

    const handlePortfolioImageUpload = (e) => {
        const files = Array.from(e.target.files);
        const imagePromises = files.map((file) => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => resolve(reader.result.replace(/^data:image\/\w+;base64,/, ''));
                reader.onerror = (error) => reject(error);
            });
        });

        Promise.all(imagePromises)
            .then(images => setPortfolioImages(images))
            .catch(error => console.error('Error loading images:', error));
    };

    const handleSavePortfolio = () => {
        const portfolioPayload = {
            professionalId: id,
            mediaContent: portfolioImages,  // Array of base64 strings
            description: portfolioDescription,
        };

        axiosInstance.post(`http://localhost:8080/portfolioItems`, portfolioPayload, {
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response => {
                setPortfolio([...portfolio, response.data]);
                setPortfolioDescription('');
                setPortfolioImages([]);  // Reset images after successful upload
                setSuccessMessage("Portfolio created successfully!");
                setTimeout(() => setSuccessMessage(''), 3000);
            })
            .catch(error => {
                console.error('Error creating portfolio:', error);
                setErrorMessage("Failed to create portfolio. Please try again.");
                setTimeout(() => setErrorMessage(''), 3000);
            });
    };

    const handleCancelNewCategory = () => {
        setNewCategoryData({ category: '', mediumPricePerService: '', providesInvoices: false, chargesTravels: false });
        setShowNewCategory(false);
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (name in formData) {
            setFormData({
                ...formData,
                [name]: type === 'checkbox' ? checked : value,
            });
        }
        else if (name in newCategoryData) {
            setNewCategoryData({
                ...newCategoryData,
                [name]: type === 'checkbox' ? checked : value,
            });
        }
    };

    const handleTabChange = (index) => {
        if (editMode) {
            handleCancelPersonalInformation();
            handleCancelCategory()
        }
        setSelectedIndex(index);
    };

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            setImagePreview(URL.createObjectURL(file));

            const reader = new FileReader();
            reader.readAsArrayBuffer(file);
            reader.onload = () => {
                const byteArray = new Uint8Array(reader.result);
                setFormData({ ...formData, profileImage: Array.from(byteArray) });
            };
            reader.onerror = (error) => {
                console.error("Error reading file:", error);
            };
        }
    };

    const handleEdit = () => {
        setEditMode(true);
    };

    const handleEditCategory = (category) => {
        setEditMode(true)
        setCategoryEditData((prevData) => ({
            ...prevData,
            [category.id]: { ...category }
        }));
    };

    const handleCancelPersonalInformation = () => {
        setEditMode(false);
        setImagePreview(null);

        setFormData(prev => ({
            ...profileData,
            profileImage: profileData.profileImage,
            password: '*****',
        }));

    };

    const handleLocationChange = (selectedOption) => {
        setFormData({ ...formData, location: selectedOption.value });
    };

    const handleSaveCategory = async (categoryId) => {
        const categoryData = categoryEditData[categoryId];
        if (!categoryData) return;

        const originalCategoryData = categories.find((category) => category.id === categoryId);
        const updatedCategoryData = {
            ...originalCategoryData,
            ...categoryData,
        };

        try {
            await axiosInstance.put(`/categoryDescriptions/${categoryId}`, updatedCategoryData);

            setCategories((categories) =>
                categories.map((category) =>
                    category.id === categoryId ? updatedCategoryData : category
                )
            );

            setCategoryEditData((prevData) => {
                const newData = { ...prevData };
                delete newData[categoryId];
                return newData;
            });

            setEditMode(false);
            setSuccessMessage("Information updated successfully!");
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (error) {
            console.error('Error updating category information:', error);
            setErrorMessage("Failed to update category information. Please try again.");
            setTimeout(() => setErrorMessage(''), 3000);
        }
    };

    const handleCancelCategory = (categoryId) => {
        setEditMode(false);
        setImagePreview(null);
        setCategoryEditData((prevData) => {
            const newData = { ...prevData };
            delete newData[categoryId];
            return newData;
        });
        setErrorMessage("Changes canceled.");
        setTimeout(() => setErrorMessage(''), 3000);
    };

    const handleSavePersonalInformation = async () => {

        const profileImageBytes = (imagePreview && imagePreview !== profileData.profileImage)
            ? imagePreview.replace(/^data:image\/\w+;base64,/, '') // Remove base64 prefix for new image
            : profileData.profileImage.replace(/^data:image\/\w+;base64,/, ''); // Ensure existing image is raw base64

        const formDataToSend = {
            ...formData,
            profileImage: profileImageBytes,
            userType: "PROFESSIONAL"
        };

        if (formData.password === '*****' || formData.password === '') {
            formDataToSend.password = profileData.password;
        }

        axiosInstance.put(`/professionals/${id}`, formDataToSend, {
        })
            .then(response => {
                const updatedProfileData = {
                    ...response.data,
                    profileImage: profileData.profileImage,
                };
                setProfileData(updatedProfileData);
                setFormData(updatedProfileData);
                setEditMode(false);
                setImagePreview(null);
                setSuccessMessage("Personal information updated successfully!");
                setTimeout(() => setSuccessMessage(''), 3000);
            })
            .catch(error => {
                console.error('Error updating professional profile data:', error);
                setErrorMessage("Failed to update personal information. Please try again.");
                setTimeout(() => setErrorMessage(''), 3000);
            });
    };

    const handleDeleteCategory = (categoryId) => {
        setSelectedCategoryId(categoryId);
        setShowConfirmDeleteCategoryModal(true);
    };

    const handleDeleteAccount = () => {
        setShowConfirmDeleteModal(true);
    };

    const confirmDeleteCategory = (categoryId) => {
        axiosInstance.delete(`/categoryDescriptions/${selectedCategoryId}`)
            .then(() => {
                setShowConfirmDeleteCategoryModal(false);
                setShowDeleteCategoryModal(true);
                setCategories(categories.filter(category => category.id !== selectedCategoryId));
            })
            .catch(error => {
                console.error('Error deleting category:', error);
                setErrorMessage("Failed to delete category. Please try again. If the error persists, please contact support.");
                setTimeout(() => {
                    setErrorMessage('');
                }, 3000);
            });
    };

    const confirmDeleteAccount = () => {
        axiosInstance.delete(`/professionals/${id}`)
            .then(() => {
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

    const handleConfirmRedirectCategory = () => {
        setShowDeleteModal(false);
        window.location.href = '/professional-profile'

    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
    }

    return (
        <div key={formData.profileImage} className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            {successMessage && <div className="bg-green-100 text-green-700 p-4 mb-4 rounded">{successMessage}</div>}
            {errorMessage && <div className="bg-red-100 text-red-700 p-4 mb-4 rounded">{errorMessage}</div>}


            {/* Confirmation Modal for Category Deletion */}
            {showConfirmDeleteCategoryModal && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Confirmação da eliminação de categoria</h2>
                        <p>Tem a certeza de que pretende apagar esta categoria? Esta acção é irreversível e os dados desta categoria serão eliminados.</p>
                        <div className="mt-6 flex justify-end space-x-4">
                            <button
                                onClick={() => setShowConfirmDeleteCategoryModal(false)} // Close modal without deleting
                                className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmDeleteCategory}
                                className="px-4 py-2 bg-red-600 text-white rounded-lg"
                            >
                                Confirmar
                            </button>
                        </div>
                    </div>
                </div>
            )}

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

            {showDeleteCategoryModal && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Categoria Apagada</h2>
                        <p>{successMessage}</p>
                        <div className="mt-6 flex justify-end">
                            <button
                                onClick={handleConfirmRedirectCategory}
                                className="px-4 py-2 bg-yellow-600 text-white rounded-lg"
                            >
                                OK
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <Tab.Group selectedIndex={selectedIndex} onChange={handleTabChange}>
                <Tab.List className="flex space-x-4 mb-8">
                    <Tab className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                        Dados Pessoais
                    </Tab>
                    <Tab className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                        Portfolio
                    </Tab>
                    {categories.map((category) => (
                        <Tab key={category.id} className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                            {category.category}
                        </Tab>
                    ))}
                    <Tab className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                        Nova Categoria
                    </Tab>
                </Tab.List>
                <Tab.Panels>
                    <Tab.Panel>
                        <div className="space-y-6">
                            <div className="flex items-center mb-6">
                                <div
                                    className="w-24 h-24 bg-gray-300 rounded-full flex items-center justify-center overflow-hidden">
                                    <img
                                        src={imagePreview || formData.profileImage}
                                        alt="Professional Profile Picture"
                                        className="w-full h-full object-cover"
                                    />
                                </div>
                                {editMode && (
                                    <div className="ml-4">
                                        <input
                                            type="file"
                                            accept="image/*"
                                            onChange={handleFileChange}
                                            className="text-sm text-gray-500"
                                        />
                                    </div>
                                )}
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
                                        <h1 className="text-4xl font-bold text-gray-800">{profileData?.name}</h1>
                                    )}
                                    <p className="text-gray-600">{profileData?.email}</p>
                                </div>
                            </div>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                <div>
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Telemóvel</h3>
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
                                <div>
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Descrição</h3>
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
                                        <button onClick={handleSavePersonalInformation}
                                                className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Guardar
                                        </button>
                                        <button onClick={handleCancelPersonalInformation}
                                                className="px-6 py-3 bg-gray-400 text-white rounded-lg hover:bg-gray-300 transition">Cancelar
                                        </button>
                                    </div>
                                ) : (
                                    <div className="flex space-x-4">
                                        <button onClick={handleEdit}
                                                className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Editar
                                        </button>
                                        <button onClick={handleDeleteAccount}
                                                className="px-6 py-3 bg-red-700 text-white rounded-lg hover:bg-red-600 transition">Apagar
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </Tab.Panel>
                    {/* Portfolio Tab Panel */}
                    <Tab.Panel>
                        <div className="space-y-4">
                            <h3 className="text-2xl font-bold text-gray-800">Portfolio</h3>
                            {portfolio.length > 0 ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {portfolio.map(item => (
                                        <div key={item.id} className="border p-4 rounded-lg">
                                            <p className="mb-2 text-gray-600">{item.description}</p>
                                            {item.mediaContent.length > 0 && (
                                                <div className="grid grid-cols-3 gap-2">
                                                    {item.mediaContent.map((media, index) => (
                                                        <img key={index} src={`data:image/jpeg;base64,${media}`} alt="Portfolio" className="w-full h-24 object-cover rounded" />
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <div className="space-y-4">
                                    <textarea
                                        placeholder="Portfolio description"
                                        value={portfolioDescription}
                                        onChange={(e) => setPortfolioDescription(e.target.value)}
                                        className="w-full p-2 border rounded"
                                    />
                                    <input
                                        type="file"
                                        multiple
                                        accept="image/*"
                                        onChange={handlePortfolioImageUpload}
                                        className="w-full"
                                    />
                                    <div className="flex space-x-4 mt-4">
                                        <button onClick={handleSavePortfolio} className="px-4 py-2 bg-gray-800 text-white rounded-lg">Save Portfolio</button>
                                        <button onClick={() => { setPortfolioDescription(''); setPortfolioImages([]); }} className="px-4 py-2 bg-gray-400 text-white rounded-lg">Cancel</button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </Tab.Panel>
                    {categories.map((category) => (
                        <Tab.Panel key={category.id}>
                            <div className="space-y-6">
                                <h3 className="text-2xl font-bold text-gray-800 mb-4">{category.category}</h3>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Preço do serviço por hora</h3>
                                        {editMode ? (
                                            <input
                                                type="text"
                                                name="mediumPricePerService"
                                                value={categoryEditData[category.id]?.mediumPricePerService || category.mediumPricePerService || ''}
                                                onChange={(e) =>
                                                    setCategoryEditData((prevData) => ({
                                                        ...prevData,
                                                        [category.id]: {
                                                            ...prevData[category.id],
                                                            mediumPricePerService: e.target.value,
                                                        }
                                                    }))
                                                }
                                                className="w-full p-2 border rounded"
                                            />
                                        ) : (
                                            <p className="text-gray-600">{category.mediumPricePerService || "No price provided"}</p>
                                        )}
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Emissão de Recibos</h3>
                                        {editMode ? (
                                            <input
                                                type="checkbox"
                                                name="providesInvoices"
                                                checked={categoryEditData[category.id]?.providesInvoices ?? category.providesInvoices ?? false}
                                                onChange={(e) =>
                                                    setCategoryEditData((prevData) => ({
                                                        ...prevData,
                                                        [category.id]: {
                                                            ...prevData[category.id],
                                                            providesInvoices: e.target.checked,
                                                        }
                                                    }))
                                                }
                                                className="w-4 h-4"
                                            />
                                        ) : (
                                            <p className="text-gray-600">{category.providesInvoices ? 'Sim' : 'Não'}</p>
                                        )}
                                    </div>
                                    <div>
                                        <h3 className="text-xl font-semibold text-gray-800 mb-2">Cobra pela deslocação</h3>
                                        {editMode ? (
                                            <input
                                                type="checkbox"
                                                name="chargesTravels"
                                                checked={categoryEditData[category.id]?.chargesTravels ?? category.chargesTravels ?? false}
                                                onChange={(e) =>
                                                    setCategoryEditData((prevData) => ({
                                                        ...prevData,
                                                        [category.id]: {
                                                            ...prevData[category.id],
                                                            chargesTravels: e.target.checked,
                                                        }
                                                    }))
                                                }
                                                className="w-4 h-4"
                                            />
                                        ) : (
                                            <p className="text-gray-600">{category.chargesTravels ? 'Sim' : 'Não'}</p>
                                        )}
                                    </div>
                                </div>
                                <div className="mt-8">
                                    {editMode ? (
                                        <div className="flex space-x-4">
                                            <button onClick={() => handleSaveCategory(category.id)}
                                                    className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Guardar
                                            </button>
                                            <button onClick={() => handleCancelCategory(category.id)}
                                                    className="px-6 py-3 bg-gray-400 text-white rounded-lg hover:bg-gray-300 transition">Cancelar
                                            </button>
                                        </div>
                                    ) : (
                                        <div className="flex space-x-4">
                                            <button onClick={handleEditCategory}
                                                    className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Editar
                                            </button>
                                            <button onClick={() => handleDeleteCategory(category.id)}
                                                    className="px-6 py-3 bg-red-700 text-white rounded-lg hover:bg-red-600 transition">Apagar
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </Tab.Panel>
                    ))}
                    {/* New Category Tab */}
                    <Tab.Panel>
                        <h3 className="text-2xl font-bold text-gray-800 mb-4">Nova Categoria</h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                            <div>
                                <label className="text-xl font-semibold text-gray-800 mb-2">Categoria Existente</label>
                                <Select
                                    options={availableCategories}
                                    onChange={handleCategorySelection}
                                    placeholder="Seleccione uma categoria"
                                    className="w-full"
                                />
                            </div>
                            <div>
                                <label className="text-xl font-semibold text-gray-800 mb-2">Preço do serviço por hora</label>
                                <input
                                    type="text"
                                    name="mediumPricePerService"
                                    value={newCategoryData.mediumPricePerService}
                                    onChange={handleInputChange}
                                    className="w-full p-2 border rounded"
                                />
                            </div>
                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    name="providesInvoices"
                                    checked={newCategoryData.providesInvoices}
                                    onChange={handleInputChange}
                                    className="w-4 h-4 mr-2"
                                />
                                <label className="text-xl font-semibold text-gray-800">Emissão de Recibos</label>
                            </div>
                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    name="chargesTravels"
                                    checked={newCategoryData.chargesTravels}
                                    onChange={handleInputChange}
                                    className="w-4 h-4 mr-2"
                                />
                                <label className="text-xl font-semibold text-gray-800">Cobra pela deslocação</label>
                            </div>
                        </div>
                        <div className="mt-8 flex space-x-4">
                            <button onClick={handleAddCategory}
                                    className="px-6 py-3 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition">Adicionar
                            </button>
                            <button onClick={handleCancelNewCategory}
                                    className="px-6 py-3 bg-gray-400 text-white rounded-lg hover:bg-gray-300 transition">Cancelar
                            </button>
                        </div>
                    </Tab.Panel>
                </Tab.Panels>
            </Tab.Group>
        </div>
    );
}

export default ProfessionalProfile;