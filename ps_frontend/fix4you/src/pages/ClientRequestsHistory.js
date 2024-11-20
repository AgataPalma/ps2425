import React, { useEffect, useState } from "react";
import axiosInstance from "../components/axiosInstance";
import Spinner from "../components/Spinner";

function ClientRequestsHistory({ id }) {
    const [requests, setRequests] = useState([]);
    const [reviews, setReviews] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedRequest, setSelectedRequest] = useState(null);
    const [selectedDetails, setSelectedDetails] = useState(null);
    const [activeTab, setActiveTab] = useState("details"); // "details", "reviews", or "writeReview"
    const [review, setReview] = useState({ classification: 0, reviewDescription: "" });
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        axiosInstance
            .get(`/services/client/${id}`)
            .then((response) => {
                const relevantRequests = response.data.filter((request) =>
                    ["COMPLETED", "REFUSED", "CANCELED"].includes(request.state)
                );
                setRequests(relevantRequests);

                axiosInstance
                    .get(`/reviews?reviewerId=${id}`)
                    .then((reviewResponse) => {
                        const reviewMap = {};
                        reviewResponse.data.forEach((review) => {

                            reviewMap[review.serviceId] = review;
                        });
                        setReviews(reviewMap);
                    })
                    .catch((err) => {
                        console.error("Error fetching reviews:", err);
                        setError("Erro ao carregar as revisões.");
                    })
                    .finally(() => setLoading(false));
            })
            .catch((err) => {
                console.error("Error fetching client requests:", err);
                setError("Não foi possível carregar os serviços concluídos.");
                setLoading(false);
            });
    }, [id]);

    const handleViewReviews = async (request) => {
        setSelectedRequest(request);
        setActiveTab("details");
        setLoading(true);

        try {
            const scheduleResponse = await axiosInstance.get(`/scheduleAppointments`);
            const matchingAppointment = scheduleResponse.data.find(
                (appointment) => appointment.serviceId === request.id
            );

            setSelectedDetails({
                service: request,
                scheduleAppointment: matchingAppointment || null,
            });
        } catch (error) {
            console.error("Error fetching schedule appointment:", error);
            setError("Não foi possível carregar os detalhes do serviço.");
        } finally {
            setLoading(false);
        }
    };

    const handleWriteReview = (request) => {
        setSelectedRequest(request);
        setActiveTab("writeReview");
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
                reviewedId: selectedRequest.professionalId,
            })
            .then((response) => {

                setReviews((prev) => ({
                    ...prev,
                    [selectedRequest.id]: response.data,
                }));
                alert("Revisão enviada com sucesso!");
                setSelectedRequest(null);
                setReview({ classification: 0, reviewDescription: "" });
            })
            .catch((err) => {
                console.error("Error submitting review:", err);
                alert("Erro ao enviar a revisão. Por favor, tente novamente.");
            })
            .finally(() => {
                setSubmitting(false);
            });
    };

    const handleCloseModal = () => {
        setSelectedRequest(null);
        setSelectedDetails(null);
        setReview({ classification: 0, reviewDescription: "" });
    };

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    if (error) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">{error}</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Serviços</h1>
            <div className="space-y-6">
                {requests.length > 0 ? (
                    requests.map((request) => (
                        <div
                            key={request.id}
                            className="p-4 bg-gray-100 rounded-lg shadow-md"
                        >
                            <h3 className="text-xl font-bold text-gray-800 mb-2">{request.title}</h3>
                            <p className="text-gray-600">Estado: {request.state}</p>
                            {request.state === "COMPLETED" && (
                                <>
                                    {!reviews[request.id] && (
                                        <button
                                            onClick={() => handleWriteReview(request)}
                                            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition"
                                        >
                                            Escrever Review
                                        </button>
                                    )}
                                    <button
                                        onClick={() => handleViewReviews(request)}
                                        className="mt-4 ml-2 px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600 transition"
                                    >
                                        Ver Reviews
                                    </button>
                                </>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">Ainda não tem nenhum serviço concluído.</p>
                )}
            </div>

            {selectedRequest && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-2xl w-full relative">
                        {/* Close Button */}
                        <button
                            onClick={handleCloseModal}
                            className="absolute top-4 right-4 text-gray-500 hover:text-gray-800 text-2xl font-bold z-50"
                            aria-label="Close Modal"
                        >
                            &times;
                        </button>

                        {/* Tabs */}
                        <div className="border-b mb-4 flex space-x-4">
                            <button
                                className={`px-4 py-2 ${
                                    activeTab === "details"
                                        ? "border-b-2 border-yellow-600 text-yellow-600"
                                        : "text-gray-500"
                                }`}
                                onClick={() => setActiveTab("details")}
                            >
                                Detalhes
                            </button>
                            <button
                                className={`px-4 py-2 ${
                                    activeTab === "reviews"
                                        ? "border-b-2 border-yellow-600 text-yellow-600"
                                        : "text-gray-500"
                                }`}
                                onClick={() => setActiveTab("reviews")}
                            >
                                Reviews
                            </button>
                            {reviews[selectedRequest.id] === undefined && (
                                <button
                                    className={`px-4 py-2 ${
                                        activeTab === "writeReview"
                                            ? "border-b-2 border-yellow-600 text-yellow-600"
                                            : "text-gray-500"
                                    }`}
                                    onClick={() => setActiveTab("writeReview")}
                                >
                                    Escrever Review
                                </button>
                            )}
                        </div>

                        {/* Modal Content */}
                        {activeTab === "details" && selectedDetails && (
                            <div>
                                <h3 className="text-lg font-bold text-gray-800">Serviço</h3>
                                <p className="text-gray-600">Título: {selectedDetails.service.title}</p>
                                <p className="text-gray-600">
                                    Descriçao: {selectedDetails.service.description}
                                </p>
                                <p className="text-gray-600">
                                    Preço por hora: ${selectedDetails.service.price}
                                </p>

                                {selectedDetails.scheduleAppointment && (
                                    <>
                                        <h3 className="text-lg font-bold mt-4 text-gray-800">Horário Agendado</h3>
                                        <p className="text-gray-600">
                                            Início:{" "}
                                            {new Date(
                                                selectedDetails.scheduleAppointment.dateStart
                                            ).toLocaleString()}
                                        </p>
                                        <p className="text-gray-600">
                                            Fim:{" "}
                                            {new Date(
                                                selectedDetails.scheduleAppointment.dateFinish
                                            ).toLocaleString()}
                                        </p>
                                    </>
                                )}
                            </div>
                        )}

                        {activeTab === "reviews" && (
                            <div>
                                <h3 className="text-lg font-bold"></h3>
                                {reviews[selectedDetails.service.id] ? (
                                    <div>
                                        <p className="text-yellow-500">
                                            {"★".repeat(reviews[selectedDetails.service.id].classification)}
                                        </p>
                                        <p className="text-gray-600">
                                            {reviews[selectedDetails.service.id].reviewDescription}
                                        </p>
                                    </div>
                                ) : (
                                    <p className="text-gray-600 italic">
                                        Ainda não escreveu uma review para este serviço.
                                    </p>
                                )}

                                <h3 className="text-lg font-bold mt-4 text-gray-800">Review do Profissional</h3>
                                {reviews[selectedDetails.service.id]?.reviewedByProfessional ? (
                                    <div>
                                        <p className="text-yellow-500">
                                            {"★".repeat(
                                                reviews[selectedDetails.service.id].reviewedByProfessional
                                                    .classification
                                            )}
                                        </p>
                                        <p className="text-gray-600">
                                            {
                                                reviews[selectedDetails.service.id].reviewedByProfessional
                                                    .reviewDescription
                                            }
                                        </p>
                                    </div>
                                ) : (
                                    <p className="text-gray-600 italic">
                                        O profissional ainda não escreveu nenhuma review.
                                    </p>
                                )}
                            </div>
                        )}

                        {activeTab === "writeReview" && (
                            <div>
                                <label className="block mb-2">
                                    <span className="text-gray-600">Rating (1-5 estrelas):</span>
                                    <div className="flex space-x-1 mt-2">
                                        {[1, 2, 3, 4, 5].map((star) => (
                                            <button
                                                key={star}
                                                type="button"
                                                className={`w-8 h-8 text-yellow-500 ${
                                                    review.classification >= star
                                                        ? "text-yellow-600"
                                                        : "text-gray-300"
                                                }`}
                                                onClick={() =>
                                                    setReview({ ...review, classification: star })
                                                }
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
                                        setReview({ ...review, reviewDescription: e.target.value })
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
                                            submitting
                                                ? "bg-blue-300"
                                                : "bg-blue-600 hover:bg-blue-500"
                                        } text-white rounded-lg`}
                                    >
                                        {submitting ? "Enviando..." : "Enviar"}
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

export default ClientRequestsHistory;
