import React from "react";

const Spinner = ({ message = "A carregar...", spinnerSize = "h-12 w-12", spinnerColor = "border-yellow-600" }) => {
    return (
        <div className="fixed inset-0 flex items-center justify-center bg-opacity-10 z-50">
            <div className="flex flex-col items-center space-y-4">
                <div className={`animate-spin rounded-full ${spinnerSize} border-t-4 ${spinnerColor} border-opacity-50`}></div>
                <p className="text-gray-800 text-lg font-medium">{message}</p>
            </div>
        </div>
    );
};

export default Spinner;
