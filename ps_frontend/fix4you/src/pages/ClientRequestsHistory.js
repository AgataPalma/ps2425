import React, { useEffect, useState } from "react";
import axiosInstance from "../components/axiosInstance";

function ClientRequestsHistory({ id }) {
    const [requests, setRequests] = useState([]);
    const [reviews, setReviews] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedRequest, setSelectedRequest] = useState(null);
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

                // Fetch reviews
                axiosInstance
                    .get(`/reviews?reviewerId=${id}`)
                    .then((reviewResponse) => {
                        const reviewMap = {};
                        reviewResponse.data.forEach((review) => {
                            // Map reviews to their respective service IDs
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



    const handleWriteReview = (request) => {
        setSelectedRequest(request);
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
                // Add the new review to the reviews map
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

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
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
                            <p className="text-gray-600">Category: {request.category?.name}</p>
                            <p className="text-gray-600">Date: {new Date(request.date).toLocaleDateString()}</p>
                            <p className="text-gray-600">Price: ${request.price}</p>
                            <p className="text-gray-600">Status: {request.state}</p>
                            {request.state === "COMPLETED" ? (
                                reviews[request.id] ? (
                                    <div className="mt-4 bg-white p-4 border rounded-lg">
                                        <h4 className="text-lg font-bold">Your Review</h4>
                                        <p className="text-yellow-500">
                                            Rating: {"★".repeat(reviews[request.id].classification)}
                                        </p>
                                        <p className="text-gray-600">
                                            Comment: {reviews[request.id].reviewDescription}
                                        </p>
                                    </div>
                                ) : (
                                    <button
                                        onClick={() => handleWriteReview(request)}
                                        className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition"
                                        aria-label={`Write a review for ${request.title}`}
                                    >
                                        Write Review
                                    </button>
                                )
                            ) : (
                                <p className="text-gray-600 italic">No review required for this status.</p>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">Ainda não tem nenhum serviço concluído.</p>
                )}
            </div>

            {selectedRequest && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                        <h2 className="text-2xl font-bold mb-4">Write a Review</h2>
                        <p className="text-gray-800 mb-2">Service: {selectedRequest.title}</p>
                        <label className="block mb-2">
                            <span className="text-gray-600">Rating (1-5 stars):</span>
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
                                        onClick={() => setReview({ ...review, classification: star })}
                                    >
                                        ★
                                    </button>
                                ))}
                            </div>
                        </label>
                        <label className="block mb-4">
                            <span className="text-gray-600">Review:</span>
                            <textarea
                                className="mt-2 w-full p-2 border rounded-lg"
                                value={review.reviewDescription}
                                onChange={(e) =>
                                    setReview({ ...review, reviewDescription: e.target.value })
                                }
                                rows="4"
                                placeholder="Write your review here..."
                            ></textarea>
                        </label>
                        <div className="flex justify-end space-x-2">
                            <button
                                onClick={() => setSelectedRequest(null)}
                                className="px-4 py-2 bg-gray-300 rounded-lg"
                            >
                                Cancel
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
                                {submitting ? "Submitting..." : "Submit"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );

}

export default ClientRequestsHistory;
