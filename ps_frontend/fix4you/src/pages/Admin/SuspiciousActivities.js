import React, { useEffect, useState } from "react";
import axiosInstance from "../../components/axiosInstance";

const SuspiciousActivities = () => {
    const [activities, setActivities] = useState([]);

    useEffect(() => {
        const fetchSuspiciousActivities = async () => {
            try {
                const response = await axiosInstance.get("/");
                setActivities(response.data);
            } catch (error) {
                console.error("Error fetching suspicious activities:", error);
            }
        };

        fetchSuspiciousActivities();
    }, []);

    return (
        <div>
            <h2 className="text-2xl font-semibold mb-4">Atividades Suspeitas</h2>
            <ul className="list-disc pl-6">
                {activities.map((activity) => (
                    <li key={activity.id}>
                        Profissional: {activity.name} (ID: {activity.id}) - Preço Suspeito
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default SuspiciousActivities;
