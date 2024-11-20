import React from "react";

const MessageModal = ({ isOpen, onClose, title, message, type }) => {
    if (!isOpen) return null;

    const bgColor = type === "success" ? "bg-green-600" : "bg-red-600";
    const textColor = type === "success" ? "text-green-800" : "text-red-800";

    return (
        <div className="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
                <h2 className={`text-2xl font-bold mb-4 ${textColor}`}>{title}</h2>
                <p className="mb-4">{message}</p>
                <div className="flex justify-end">
                    <button
                        onClick={onClose}
                        className={`px-4 py-2 ${bgColor} text-white rounded-lg hover:opacity-80 transition`}
                    >
                        Fechar
                    </button>
                </div>
            </div>
        </div>
    );
};

export default MessageModal;
