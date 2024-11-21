
import React, { useEffect, useState } from 'react';
import Spinner from "../components/Spinner";
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
    const [selectedImage, setSelectedImage] = useState(null);
    const [paymentOptions, setPaymentOptions] = useState([]);
    const [fees, setFees] = useState([]);
    const [selectedFee, setSelectedFee] = useState(null); // Fee currently being paid
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('');
    const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
    const [isErrorModalOpen, setIsErrorModalOpen] = useState(false);
    const [modalMessage, setModalMessage] = useState('');
    const [modalTitle, setModalTitle] = useState('');
    const [loadingMessage, setLoadingMessage] = useState("");

    const [newCategoryData, setNewCategoryData] = useState({
        category: '',
        mediumPricePerService: '',
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
            .then((response) => {
                if (response.data.length > 0) {
                    const portfolioItem = response.data[0];
                    setPortfolio({
                        ...portfolioItem,
                        byteContent: portfolioItem.byteContent || [],
                    });
                } else {
                    setPortfolio(null);
                }
            })
            .catch((error) => {
                console.error('Error fetching portfolio:', error);
            });

        axiosInstance.get('/paymentMethods')
            .then((response) => {
                const options = response.data.map((method) => ({
                    value: method.id,
                    label: method.name,
                }));
                setPaymentOptions(options);
            })
            .catch((error) => console.error('Error fetching payment methods:', error));

        axiosInstance.get(`/categoryDescriptions/user/${id}`)
            .then((response) => {
                const associatedCategories = response.data.map((cat) => cat.category.name);
                setCategories(response.data);

                axiosInstance.get(`/categories`)
                    .then((allCategoriesResponse) => {
                        const allCategories = allCategoriesResponse.data;

                        const uniqueAvailableCategories = allCategories
                            .filter(
                                (category) => !associatedCategories.includes(category.name)
                            )
                            .map((category) => ({
                                value: category.id,
                                label: category.name,
                            }));

                        setAvailableCategories(uniqueAvailableCategories);
                    })
                    .catch((error) => {
                        console.error('Error fetching all categories:', error);
                    });
            })
            .catch((error) => {
                console.error('Error fetching professional categories:', error);
            });

        axiosInstance.get(`/professionalFees`)
            .then((response) => {
                // Filter fees for the current professional
                const filteredFees = response.data.filter(fee => fee.professional.id === id);
                setFees(filteredFees);
            })
            .catch((error) => {
                console.error('Error fetching professional fees:', error);
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

    //------------------------- CATEGORIES --------------------------------------------------//

    const handleCategorySelection = (selectedOption) => {
        setNewCategoryData({
            ...newCategoryData,
            category: {
                id: selectedOption.value,
                name: selectedOption.label,
            },
        });
    };

    const handleAddCategory = () => {
        if (newCategoryData.category && newCategoryData.mediumPricePerService) {
            const categoryPayload = {
                professionalId: id,
                category: newCategoryData.category,
                chargesTravels: newCategoryData.chargesTravels,
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
                        chargesTravels: false,
                    });

                    window.location.href = '/ProfessionalProfile'
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


    const handleDeleteCategory = (categoryId) => {
        setSelectedCategoryId(categoryId);
        setShowConfirmDeleteCategoryModal(true);
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

    const handleCancelNewCategory = () => {
        setNewCategoryData({ category: '', mediumPricePerService: '', chargesTravels: false });
        setShowNewCategory(false);
    };

    const handleEditCategory = (category) => {
        setEditMode(true)
        setCategoryEditData((prevData) => ({
            ...prevData,
            [category.id]: { ...category }
        }));
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


    //------------------------ TAB CHANGES --------------------------------------------------//

    const handleTabChange = (index) => {
        if (editMode) {
            handleCancelPersonalInformation();
            handleCancelCategory()
        }
        setSelectedIndex(index);
    };

    //------------------------ PORTFOLIO ----------------------------------------------------//

    const handlePortfolioImageUpload = (e) => {
        const files = Array.from(e.target.files);
        const imagePromises = files.map((file) => {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = () => resolve({ file, preview: reader.result });
                reader.onerror = (error) => reject(error);
            });
        });

        Promise.all(imagePromises)
            .then((images) => {
                setPortfolioImages((prev) => [...prev, ...images]);
            })
            .catch((error) => console.error('Error loading images:', error));
    };

    const handleUploadImage = (file) => {
        if (!file || !portfolio) return;

        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
            const base64Image = reader.result.replace(/^data:image\/\w+;base64,/, '');

            axiosInstance.put(`/portfolioItems/images/${portfolio.id}`, [base64Image])
                .then(() => {

                    axiosInstance.get(`/portfolioItems/${portfolio.id}`)
                        .then((response) => {
                            setPortfolio(response.data);
                            setSuccessMessage('Image uploaded successfully!');
                            setTimeout(() => setSuccessMessage(''), 3000);
                        })
                        .catch((error) => console.error('Error fetching updated portfolio:', error));
                })
                .catch((error) => {
                    console.error('Error uploading image:', error);
                    setErrorMessage('Failed to upload image. Please try again.');
                    setTimeout(() => setErrorMessage(''), 3000);
                });
        };
        reader.onerror = (error) => console.error('Error reading file:', error);
    };

    const handleRemovePreviewImage = (index) => {
        setPortfolioImages((prev) => prev.filter((_, i) => i !== index));
    };

    const handleDeletePortfolioImage = (imageIndex) => {
        const updatedImages = portfolio.byteContent.filter((_, i) => i !== imageIndex);

        axiosInstance.put(`/portfolioItems/${portfolio.id}`, {
            ...portfolio,
            byteContent: updatedImages,
        })
            .then((response) => {
                setPortfolio(response.data);
                setSuccessMessage('Image deleted successfully!');
                setTimeout(() => setSuccessMessage(''), 3000);
            })
            .catch((error) => {
                console.error('Error deleting image:', error);
                setErrorMessage('Failed to delete image. Please try again.');
                setTimeout(() => setErrorMessage(''), 3000);
            });
    };

    const handleSavePortfolio = () => {
        const newImages = portfolioImages.map((image) =>
            image.preview.replace(/^data:image\/\w+;base64,/, '')
        );

        const updatedPortfolio = {
            ...portfolio,
            byteContent: [...portfolio.byteContent, ...newImages],
        };

        axiosInstance.put(`/portfolioItems/${portfolio.id}`, updatedPortfolio)
            .then((response) => {
                setPortfolio(response.data);
                setPortfolioImages([]);
                setEditMode(false);
                setSuccessMessage('Portfolio updated successfully!');
                setTimeout(() => setSuccessMessage(''), 3000);
            })
            .catch((error) => {
                console.error('Error saving portfolio:', error);
                setErrorMessage('Failed to save portfolio. Please try again.');
                setTimeout(() => setErrorMessage(''), 3000);
            });
    };

    const handleDeleteEntirePortfolio = () => {
        axiosInstance.delete(`/portfolioItems/${portfolio.id}`)
            .then(() => {
                setPortfolio(null);
                setSuccessMessage('Portfolio deleted successfully!');
                setTimeout(() => setSuccessMessage(''), 3000);
            })
            .catch((error) => {
                console.error('Error deleting portfolio:', error);
                setErrorMessage('Failed to delete portfolio. Please try again.');
                setTimeout(() => setErrorMessage(''), 3000);
            });
    };

    const handleCreatePortfolio = () => {
        if (!portfolioDescription) {
            setErrorMessage('Description is mandatory!');
            setTimeout(() => setErrorMessage(''), 3000);
            return;
        }

        const portfolioPayload = {
            professionalId: id,
            byteContent: portfolioImages.map((image) =>
                image.preview.replace(/^data:image\/\w+;base64,/, '')
            ),
            description: portfolioDescription,
        };

        axiosInstance.post(`/portfolioItems`, portfolioPayload)
            .then((response) => {
                setPortfolio(response.data);
                setPortfolioDescription('');
                setPortfolioImages([]);
                setSuccessMessage('Portfolio created successfully!');
                setTimeout(() => setSuccessMessage(''), 3000);
            })
            .catch((error) => {
                console.error('Error creating portfolio:', error);
                setErrorMessage('Failed to create portfolio. Please try again.');
                setTimeout(() => setErrorMessage(''), 3000);
            });
    };

    const handleOpenImage = (image) => {
        setSelectedImage(image);
    };

    const handleCloseModal = () => {
        setSelectedImage(null);
    };


    //-------------------------PERSONAL INFORMATION ----------------------------------------//

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

    const handleSavePersonalInformation = async () => {

        const updatedPayments = formData.acceptedPayments?.map((payment) => ({
            id: payment.id,
            name: payment.name,
        }));
        const profileImageBase64 = formData.profileImage?.startsWith('data:image')
            ? formData.profileImage.replace(/^data:image\/\w+;base64,/, '')
            : formData.profileImage;

        const formDataToSend = {
            ...formData,
            profileImage: profileImageBase64,
            acceptedPayments: updatedPayments,
            userType: "PROFESSIONAL"
        };

        if (formData.password === '*****' || formData.password === '') {
            formDataToSend.password = profileData.password;
        }

        try {
            const response = await axiosInstance.put(`/professionals/${id}`, formDataToSend);
            const updatedProfileData = {
                ...response.data,
                profileImage: `data:image/jpeg;base64,${profileImageBase64}`
            };

            setProfileData(updatedProfileData);
            setFormData(updatedProfileData);
            if (!imagePreview) {
                setImagePreview(null);
            }

            setEditMode(false);
            setSuccessMessage("Personal information updated successfully!");
            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (error) {
            console.error('Error updating professional profile data:', error);
            setErrorMessage("Failed to update personal information. Please try again.");
            setTimeout(() => setErrorMessage(''), 3000);
        }
    };

    const handleDeleteAccount = () => {
        setShowConfirmDeleteModal(true);
    };

    const confirmDeleteAccount = () => {
        axiosInstance.delete(`/professionals/${id}`)
            .then(() => {
                setShowConfirmDeleteModal(false);
                setShowDeleteModal(true);
            })
            .catch(error => {
                console.error('Error deleting account:', error);
                setErrorMessage("Failed to delete account. Please try again. If the error persists, please contact support.");
                setTimeout(() => {
                    setErrorMessage('');
                }, 3000);
            });
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {

            setImagePreview(URL.createObjectURL(file));

            const reader = new FileReader();
            reader.onload = () => {
                const base64Image = reader.result.replace(/^data:image\/\w+;base64,/, '');
                setFormData({ ...formData, profileImage: base64Image });
            };
            reader.onerror = (error) => console.error("Error reading file:", error);
            reader.readAsDataURL(file);
        }
    };

    //----------------------------------- PAYMENT --------------------------------------------//
    const handleOpenPaymentModal = (fee) => {
        setSelectedFee(fee);
        setShowPaymentModal(true);
    };

    const handleClosePaymentModal = () => {
        setSelectedFee(null);
        setPaymentMethod('');
        setShowPaymentModal(false);
    };

    const handlePayment = async () => {
        setLoadingMessage("A processar o pagamento...");
        setLoading(true); // Show the loading indicator
        try {
            const response = await axiosInstance.put(`/professionalFees/${selectedFee.id}/pay`);
            console.log(response.data);

            setFees(fees.map(f => f.id === selectedFee.id ? { ...f, paymentStatus: "PAID" } : f));

            setModalTitle('Sucesso');
            setModalMessage(`Pagamento de €${selectedFee.value.toFixed(2)} realizado com sucesso!`);
            setIsSuccessModalOpen(true);
        } catch (error) {
            console.error('Error during payment:', error);

            setModalTitle('Erro');
            setModalMessage('Falha no pagamento. Tente novamente.');
            setIsErrorModalOpen(true);
        } finally {
            setLoading(false); // Hide the loading indicator
            setShowPaymentModal(false);
        }
    };

    const handleDownloadInvoice = async (feeId) => {
        try {
            const response = await axiosInstance.get(`/professionalFees/${feeId}/invoice`, {
                responseType: 'blob',
            });

            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;

            link.setAttribute("download", `invoice_${feeId}.pdf`);

            document.body.appendChild(link);
            link.click();

            document.body.removeChild(link);
        } catch (error) {
            console.error("Error downloading the invoice:", error);
            setModalTitle("Erro");
            setModalMessage("Falha ao descarregar a fatura. Tente novamente.");
            setIsErrorModalOpen(true);
        }
    };





    //---------------------------- GENERAL HANDLING ----------------------------------------//

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

    const handleEdit = () => {
        setEditMode(true);
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
        return <Spinner message={loadingMessage} spinnerColor="border-yellow-600" />;
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
                                onClick={() => setShowConfirmDeleteCategoryModal(false)}
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
            {/* General Success Modal */}
            {isSuccessModalOpen && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4 text-green-800">{modalTitle}</h2>
                        <p className="mb-4">{modalMessage}</p>
                        <div className="flex justify-end">
                            <button
                                onClick={() => setIsSuccessModalOpen(false)}
                                className="px-4 py-2 bg-green-500 text-white rounded-lg hover:opacity-80 transition"
                            >
                                Fechar
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* General Error Modal */}
            {isErrorModalOpen && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4 text-red-800">{modalTitle}</h2>
                        <p className="mb-4">{modalMessage}</p>
                        <div className="flex justify-end">
                            <button
                                onClick={() => setIsErrorModalOpen(false)}
                                className="px-4 py-2 bg-red-500 text-white rounded-lg hover:opacity-80 transition"
                            >
                                Fechar
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
                                onClick={() => setShowConfirmDeleteModal(false)}
                                className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmDeleteAccount}
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

            {/* Modal for viewing the image */}
            {selectedImage && (
                <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
                    <div className="relative bg-white rounded-lg p-4">
                        <button
                            onClick={handleCloseModal}
                            className="absolute top-2 right-2 text-white bg-red-600 rounded-full w-8 h-8 flex items-center justify-center"
                        >
                            &times;
                        </button>
                        <img
                            src={`data:image/jpeg;base64,${selectedImage}`}
                            alt="Enlarged Portfolio"
                            className="max-w-full max-h-screen"
                        />
                    </div>
                </div>
            )}

            {showPaymentModal && (
                <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Simular Pagamento</h2>
                        <p className="mb-4">Escolha o método de pagamento para pagar <strong>€{selectedFee?.value.toFixed(2)}</strong></p>
                        <div className="mb-4">
                            <select
                                value={paymentMethod}
                                onChange={(e) => setPaymentMethod(e.target.value)}
                                className="w-full p-2 border rounded"
                            >
                                <option value="" disabled>Escolha o método de pagamento</option>
                                <option value="Cartão de Crédito">Cartão de Crédito/Débito</option>
                                <option value="MBWay">MBWay</option>
                                <option value="Transferência Bancária">Transferência Bancária</option>
                            </select>
                        </div>
                        <div className="flex justify-end space-x-4">
                            <button
                                onClick={handleClosePaymentModal}
                                className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                                disabled={loading}
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={handlePayment}
                                className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                disabled={!paymentMethod || loading}
                            >
                                Pagar
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
                        <Tab key={category.category.id} className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                            {category.category.name}
                        </Tab>
                    ))}
                    <Tab className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                        Nova Categoria
                    </Tab>
                    <Tab className={({ selected }) => selected ? 'px-4 py-2 bg-yellow-600 text-white rounded-lg' : 'px-4 py-2 bg-gray-200 text-gray-600 rounded-lg'}>
                        Pagamentos
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
                                            onChange={(e) => setFormData({...formData, password: e.target.value})}
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
                                <div>
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Métodos de Pagamento
                                        Aceites</h3>
                                    {editMode ? (
                                        <Select
                                            isMulti
                                            options={paymentOptions} // This should be loaded from the GET /paymentMethods API
                                            value={formData.acceptedPayments?.map((payment) => ({
                                                value: payment.id,
                                                label: payment.name,
                                            }))}
                                            onChange={(selectedOptions) => {
                                                setFormData({
                                                    ...formData,
                                                    acceptedPayments: selectedOptions.map((option) => ({
                                                        id: option.value,
                                                        name: option.label,
                                                    })),
                                                });
                                            }}
                                            placeholder="Selecione métodos de pagamento"
                                            className="w-full"
                                        />
                                    ) : (
                                        <ul className="text-gray-600">
                                            {profileData?.acceptedPayments && profileData.acceptedPayments.length > 0 ? (
                                                profileData.acceptedPayments.map((payment) => (
                                                    <li key={payment.id}>{payment.name}</li>
                                                ))
                                            ) : (
                                                <p className="text-gray-600">Nenhum método de pagamento registrado.</p>
                                            )}
                                        </ul>
                                    )}
                                </div>

                                <div>
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Estado da Conta</h3>
                                    <p className="text-gray-600">
                                        {profileData?.suspended ? "Suspensa" : "Ativa"}
                                    </p>
                                </div>
                                <div>
                                    <h3 className="text-xl font-semibold text-gray-800 mb-2">Strikes</h3>
                                    <p className="text-gray-600">{profileData?.strikes || 0}</p>
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

                            {portfolio ? (
                                <div className="border p-4 rounded-lg">
                                    {editMode ? (
                                        <>
                                            {/* Editable Description */}
                                            <textarea
                                                className="w-full p-2 border rounded mb-2"
                                                value={portfolio.description}
                                                onChange={(e) => setPortfolio({ ...portfolio, description: e.target.value })}
                                            />

                                            {/* Editable Images */}
                                            {Array.isArray(portfolio.byteContent) && portfolio.byteContent.length > 0 && (
                                                <div className="grid grid-cols-3 gap-2 mb-4">
                                                    {portfolio.byteContent.map((media, index) => (
                                                        <div key={index} className="relative">
                                                            <img
                                                                src={`data:image/jpeg;base64,${media}`}
                                                                alt="Portfolio"
                                                                className="w-full h-24 object-cover rounded cursor-pointer"
                                                                onClick={() => handleOpenImage(media)}
                                                            />
                                                            <button
                                                                onClick={() => handleDeletePortfolioImage(index)}
                                                                className="absolute top-1 right-1 bg-red-600 text-white rounded-full w-6 h-6 text-sm"
                                                            >
                                                                &times;
                                                            </button>
                                                        </div>
                                                    ))}
                                                </div>
                                            )}

                                            {/* Add New Images */}
                                            <div className="space-y-2">
                                                <input
                                                    type="file"
                                                    accept="image/*"
                                                    onChange={(e) => handleUploadImage(e.target.files[0])}
                                                    className="w-full"
                                                />
                                                {portfolioImages.map((image, index) => (
                                                    <div key={index} className="relative">
                                                        <img
                                                            src={image.preview}
                                                            alt="Preview"
                                                            className="w-full h-24 object-cover rounded mb-2"
                                                        />
                                                        <button
                                                            onClick={() => handleRemovePreviewImage(index)}
                                                            className="absolute top-1 right-1 bg-red-600 text-white rounded-full w-6 h-6 text-sm"
                                                        >
                                                            &times;
                                                        </button>
                                                    </div>
                                                ))}
                                            </div>

                                            {/* Save and Cancel Buttons */}
                                            <div className="flex space-x-4 mt-4">
                                                <button
                                                    onClick={handleSavePortfolio}
                                                    className="px-4 py-2 bg-green-600 text-white rounded-lg"
                                                >
                                                    Save Changes
                                                </button>
                                                <button
                                                    onClick={() => setEditMode(false)}
                                                    className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                                                >
                                                    Cancel
                                                </button>
                                            </div>
                                        </>
                                    ) : (
                                        <>
                                            {/* Non-editable Description */}
                                            <p className="text-gray-600 mb-4">{portfolio.description}</p>

                                            {/* Non-editable Images */}
                                            {Array.isArray(portfolio.byteContent) && portfolio.byteContent.length > 0 && (
                                                <div className="grid grid-cols-3 gap-2 mb-4">
                                                    {portfolio.byteContent.map((media, index) => (
                                                        <img
                                                            key={index}
                                                            src={`data:image/jpeg;base64,${media}`}
                                                            className="w-full h-24 object-cover rounded cursor-pointer"
                                                            onClick={() => handleOpenImage(media)}
                                                        />
                                                    ))}
                                                </div>
                                            )}

                                            {/* Edit Button */}
                                            <button
                                                onClick={() => setEditMode(true)}
                                                className="px-4 py-2 bg-gray-800 text-white rounded-lg mr-4"
                                            >
                                                Editar
                                            </button>
                                        </>
                                    )}

                                    {/* Delete Entire Portfolio */}
                                    <button
                                        onClick={handleDeleteEntirePortfolio}
                                        className="mt-4 px-4 py-2 bg-red-700 text-white rounded-lg"
                                    >
                                        Apagar
                                    </button>
                                </div>
                            ) : (
                                <div className="space-y-4">
                                    {/* Create Portfolio Form */}
                                    <textarea
                                        placeholder="Enter a portfolio description"
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
                                    {portfolioImages.map((image, index) => (
                                        <div key={index} className="relative">
                                            <img
                                                src={image.preview}
                                                alt="Preview"
                                                className="w-full h-24 object-cover rounded mb-2"
                                            />
                                            <button
                                                onClick={() => handleRemovePreviewImage(index)}
                                                className="absolute top-1 right-1 bg-red-600 text-white rounded-full w-6 h-6 text-sm"
                                            >
                                                &times;
                                            </button>
                                        </div>
                                    ))}
                                    <div className="flex space-x-4 mt-4">
                                        <button
                                            onClick={handleCreatePortfolio}
                                            className="px-4 py-2 bg-gray-800 text-white rounded-lg"
                                        >
                                            Create Portfolio
                                        </button>
                                        <button
                                            onClick={() => {
                                                setPortfolioDescription('');
                                                setPortfolioImages([]);
                                            }}
                                            className="px-4 py-2 bg-gray-400 text-white rounded-lg"
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </Tab.Panel>


                    {categories.map((category) => (
                        <Tab.Panel key={category.category.id}>
                            <div className="space-y-6">
                                <h3 className="text-2xl font-bold text-gray-800 mb-4">{category.category.name}</h3>
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
                    <Tab.Panel>
                        <div className="space-y-6">
                            <h3 className="text-2xl font-bold text-gray-800 mb-4">Pagamentos Pendentes</h3>

                            {fees && fees.length > 0 ? (
                                <div className="border p-4 rounded-lg space-y-4">
                                    {fees.map((fee) => (
                                        <div key={fee.id} className="border-b pb-4 mb-4">
                                            <div className="flex justify-between items-center">
                                                <div>
                                                    <p className="text-gray-800"><strong>Mês:</strong> {fee.relatedMonthYear}</p>
                                                    <p className="text-gray-800"><strong>Serviços Concluídos:</strong> {fee.numberServices}</p>
                                                    <p className="text-gray-800"><strong>Valor:</strong> €{fee.value.toFixed(2)}</p>
                                                    <p className="text-gray-800"><strong>Status:</strong> {fee.paymentStatus === "PENDING" ? "Pendente" : "Concluído"}</p>
                                                </div>
                                                {fee.paymentStatus === "PENDING" && (
                                                    <button
                                                        onClick={() => handleOpenPaymentModal(fee)}
                                                        className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                                    >
                                                        Pagar
                                                    </button>
                                                )}
                                                {fee.paymentStatus === "COMPLETED" && fee.invoice && (
                                                    <button
                                                        onClick={() => handleDownloadInvoice(fee.id)}
                                                        className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition"
                                                    >
                                                        Download Recibo
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-gray-600">Sem pagamentos pendentes.</p>
                            )}
                        </div>
                    </Tab.Panel>



                </Tab.Panels>
            </Tab.Group>
        </div>
    );
}

export default ProfessionalProfile;