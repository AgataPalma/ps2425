import React, { useEffect, useState } from 'react';
import axiosInstance from '../components/axiosInstance';
import Spinner from "../components/Spinner";
import MessageModal from "../components/MessageModal";

function ProfessionalRequestsHistory({ id }) {
    const [requests, setRequests] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState('');
    const [activeTab, setActiveTab] = useState('open');
    const [loading, setLoading] = useState(true);
    const [scheduleAppointments, setScheduleAppointments] = useState([]);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [clientDetails, setClientDetails] = useState({}); // Fix: Add clientDetails state
    const [reviews, setReviews] = useState({});
    const [activeModalTab, setActiveModalTab] = useState('details');
    const [review, setReview] = useState({ classification: 0, reviewDescription: '' });
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            setLoading(true);

            const servicesResponse = await axiosInstance.get(`/services/professional/${id}`);
            setRequests(servicesResponse.data);

            const categoriesResponse = await axiosInstance.get(`/categoryDescriptions/user/${id}`);
            setCategories(categoriesResponse.data);

            const scheduleResponse = await axiosInstance.get(`/scheduleAppointments/professional/${id}`);
            const appointmentsMap = {};
            scheduleResponse.data.forEach((appointment) => {
                console.log('Mapping appointment:', appointment);
                appointmentsMap[appointment.serviceId] = appointment;
            });
            console.log('Mapped Appointments:', appointmentsMap);
            setScheduleAppointments(appointmentsMap);

            const reviewsResponse = await axiosInstance.get(`/reviews?reviewedId=${id}`);
            const reviewMap = {};
            reviewsResponse.data.forEach((review) => {
                if (!reviewMap[review.serviceId]) {
                    reviewMap[review.serviceId] = {};
                }
                if (review.reviewerId === id) {
                    reviewMap[review.serviceId].professionalToClient = review;
                } else {
                    reviewMap[review.serviceId].clientToProfessional = review;
                }
            });
            setReviews(reviewMap);

            const acceptedRequests = servicesResponse.data.filter(
                (request) => request.state === "ACCEPTED"
            );
            const clientDetailsMap = {};
            for (const request of acceptedRequests) {
                if (request.clientId) {
                    const clientResponse = await axiosInstance.get(`/clients/${request.clientId}`);
                    clientDetailsMap[request.clientId] = clientResponse.data;
                }
            }
            setClientDetails(clientDetailsMap);
        } catch (error) {
            console.error('Error fetching data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCategoryChange = (e) => {
        setSelectedCategory(e.target.value);
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        setSelectedCategory('');
    };

    const handleViewDetails = async (request) => {
        setSelectedRequest(request);
        setActiveModalTab('details');

    };

    const handleCloseModal = () => {
        setSelectedRequest(null);
        setReview({ classification: 0, reviewDescription: '' });
    };

    const handleSubmitReview = () => {
        if (!review.classification || review.classification < 1 || review.classification > 5) {
            alert("Por favor, escolha uma classificação válida (1-5 estrelas).");
            return;
        }

        setSubmitting(true);

        axiosInstance
            .post("/reviews", {
                classification: review.classification,
                reviewDescription: review.reviewDescription,
                serviceId: selectedRequest.id,
                reviewerId: id,
                reviewedId: selectedRequest.clientId,
            })
            .then(() => {
                alert("Avaliação submetida!");
                setReviews((prev) => ({
                    ...prev,
                    [selectedRequest.id]: { classification: review.classification, reviewDescription: review.reviewDescription },
                }));
                handleCloseModal();
            })
            .catch((err) => {
                console.error("Error submitting review:", err);
                alert("Erro ao enviar avaliação. Por favor, tente novamente.");
            })
            .finally(() => {
                setSubmitting(false);
            });
    };

    //const getAppointmentState = (serviceId) => {
    //    const appointment = scheduleAppointments.find(appointment => appointment.serviceId === serviceId);
   //     return appointment ? appointment.state : 'Unknown';
   // };

    const getAppointmentState = (serviceId) => {
        const appointment = scheduleAppointments[serviceId];
        console.log('Appointment: ', appointment);
        console.log('serviceId: ', serviceId)
        return appointment ? appointment.state : 'Unknown';
    };

    const filteredRequests = requests.filter(request => {
        const isCategoryMatch = selectedCategory
            ? request.category?.name === selectedCategory
            : true;

        if (activeTab === 'open') {
            return ['PENDING', 'ACCEPTED'].includes(request.state) && isCategoryMatch;
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
                    Serviços pendentes
                </button>
                <button
                    onClick={() => handleTabChange('concluded')}
                    className={`p-2 rounded-lg ${activeTab === 'concluded' ? 'bg-yellow-500 text-white' : 'bg-gray-200 text-gray-800'}`}
                >
                    Serviços concluídos
                </button>
            </div>

            {/* Filter by Category */}
            <div className="mb-6">
                <label htmlFor="category" className="block text-lg font-medium text-gray-700 mb-2">Filtrar por
                    categoria</label>
                <select
                    id="category"
                    value={selectedCategory}
                    onChange={handleCategoryChange}
                    className="w-full p-2 border rounded-lg"
                >
                    <option value="">Todas as categorias</option>
                    {categories.map(category => (
                        <option key={category.id} value={category.category.name}>
                            {category.category.name}
                        </option>
                    ))}
                </select>
            </div>

            {/* Filtered Requests */}
            <div className="space-y-6">
                {filteredRequests.length > 0 ? (
                    filteredRequests.map(request => (
                        <div key={request.id} className="p-4 bg-gray-100 rounded-lg shadow-md flex justify-between">
                            <div>
                                <h3 className="text-xl font-bold text-gray-800 mb-2">{request.title}</h3>
                                <p className="text-gray-600">Preço por hora: {request.price}€</p>
                                <p className="text-gray-600">Estado: {request.state}</p>
                                <p className="text-gray-600">Estado da marcação: {getAppointmentState(request.id)}</p>
                                {request.state === "ACCEPTED" && clientDetails[request.clientId] && (
                                    <p className="text-gray-600">Contacto: {clientDetails[request.clientId].phoneNumber}</p>
                                )}
                            </div>
                            <div className="flex flex-col items-end space-y-2">
                            {request.state === 'COMPLETED' && !reviews[request.id] && (
                                    <button
                                        onClick={() => handleViewDetails(request)}
                                        className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                                    >
                                        Submeter
                                    </button>
                                )}
                                <button
                                    onClick={() => handleViewDetails(request)}
                                    className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition"
                                >
                                    Ver Detalhes
                                </button>
                            </div>
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">Nenhum serviço encontrado.</p>
                )}
            </div>

            {/* Modal for Service Details and Reviews */}
            {selectedRequest && (
                <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex justify-center items-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-2xl w-full relative">
                        {/* Close Button */}
                        <button
                            onClick={handleCloseModal}
                            className="absolute top-4 right-4 text-gray-500 hover:text-gray-800 text-2xl font-bold"
                        >
                            &times;
                        </button>

                        {/* Tabs */}
                        <div className="border-b mb-4 flex space-x-4">
                            <button
                                className={`px-4 py-2 ${
                                    activeModalTab === 'details' ? 'border-b-2 border-yellow-600 text-yellow-600' : 'text-gray-500'
                                }`}
                                onClick={() => setActiveModalTab('details')}
                            >
                                Detalhes
                            </button>
                            <button
                                className={`px-4 py-2 ${
                                    activeModalTab === 'reviews' ? 'border-b-2 border-yellow-600 text-yellow-600' : 'text-gray-500'
                                }`}
                                onClick={() => setActiveModalTab('reviews')}
                            >
                                Reviews
                            </button>
                            {!reviews[selectedRequest.id] && selectedRequest.state === "COMPLETED" && (
                                <button
                                    className={`px-4 py-2 ${
                                        activeModalTab === 'writeReview' ? 'border-b-2 border-yellow-600 text-yellow-600' : 'text-gray-500'
                                    }`}
                                    onClick={() => setActiveModalTab('writeReview')}
                                >
                                    Dê a sua opinião
                                </button>
                            )}
                        </div>

                        {/* Modal Content */}
                        {activeModalTab === 'details' && (
                            <div>
                                <h3 className="text-lg font-bold text-gray-800">Serviço</h3>
                                <p className="text-gray-600">Título: {selectedRequest.title}</p>
                                <p className="text-gray-600">Descrição: {selectedRequest.description}</p>
                                <p className="text-gray-600">Preço: {selectedRequest.price}€</p>
                                <p className="text-gray-600">Categoria: {selectedRequest.category?.name}</p>
                            </div>
                        )}

                        {activeModalTab === 'reviews' && (
                            <div>
                                <h3 className="text-lg font-bold text-gray-800"></h3>
                                <div className="mt-4">
                                    <h4 className="text-md font-bold text-gray-700">A minha review</h4>
                                    {reviews[selectedRequest.id]?.professionalToClient ? (
                                        <div className="mt-2">
                                            <p className="text-yellow-500">
                                                {"★".repeat(reviews[selectedRequest.id].professionalToClient.classification)}
                                            </p>
                                            <p className="text-gray-600">
                                                {reviews[selectedRequest.id].professionalToClient.reviewDescription}
                                            </p>
                                        </div>
                                    ) : (
                                        <p className="text-gray-600 italic">Ainda não escreveu uma revisão para este
                                            cliente.</p>
                                    )}
                                </div>

                                <div className="mt-6">
                                    <h4 className="text-md font-bold text-gray-700">Review do cliente</h4>
                                    {reviews[selectedRequest.id]?.clientToProfessional ? (
                                        <div className="mt-2">
                                            <p className="text-yellow-500">
                                                {"★".repeat(reviews[selectedRequest.id].clientToProfessional.classification)}
                                            </p>
                                            <p className="text-gray-600">
                                                {reviews[selectedRequest.id].clientToProfessional.reviewDescription}
                                            </p>
                                        </div>
                                    ) : (
                                        <p className="text-gray-600 italic">O cliente ainda não escreveu uma revisão
                                            para este serviço.</p>
                                    )}
                                </div>
                            </div>
                        )}

                        {activeModalTab === 'writeReview' && (
                            <div>
                                <label className="block mb-2">
                                    <span className="text-gray-600">Rating (1-5 estrelas):</span>
                                    <div className="flex space-x-1 mt-2">
                                        {Array.from({length: 5}, (_, index) => (
                                            <button
                                                key={index}
                                                type="button"
                                                className={`w-8 h-8 text-xl ${
                                                    index < review.classification ? 'text-yellow-500' : 'text-gray-300'
                                                }`}
                                                onClick={() => setReview({...review, classification: index + 1})}
                                            >
                                                ★
                                            </button>
                                        ))}
                                    </div>
                                </label>
                                <textarea
                                    className="mt-4 w-full p-2 border rounded-lg"
                                    value={review.reviewDescription}
                                    onChange={(e) =>
                                        setReview({...review, reviewDescription: e.target.value})
                                    }
                                    rows="4"
                                    placeholder="Escreva sua review..."
                                ></textarea>
                                <div className="flex justify-end space-x-2 mt-4">
                                    <button
                                        onClick={handleCloseModal}
                                        className="px-4 py-2 bg-gray-300 rounded-lg"
                                    >
                                        Cancelar
                                    </button>
                                    <button
                                        onClick={handleSubmitReview}
                                        disabled={submitting}
                                        className={`px-4 py-2 ${
                                            submitting ? 'bg-blue-300' : 'bg-blue-600 hover:bg-blue-500'
                                        } text-white rounded-lg`}
                                    >
                                        {submitting ? 'Enviando...' : 'Enviar'}
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            )}

        </div>
    );
}

export default ProfessionalRequestsHistory;
