import React, { useState } from "react";
import Footer from "../components/Footer";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import "../custom-datepicker.css";
import axios from "axios";
import {useLocation, useNavigate} from 'react-router-dom';

function ScheduleAppointments({ id }) {
    const navigate = useNavigate();
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [selectedTimeSlot, setSelectedTimeSlot] = useState(null);
    const location = useLocation();
    const state = 0;
    const queryParams = new URLSearchParams(location.search);
    const clientId = queryParams.get('clientId');
    const professionalId = queryParams.get('professionalId');
    const serviceId = queryParams.get('serviceId');

    const timeSlots = [
        "06:00 - 07:00",
        "07:00 - 08:00",
        "08:00 - 09:00",
        "09:00 - 10:00",
        "10:00 - 11:00",
        "11:00 - 12:00",
        "13:00 - 14:00",
        "14:00 - 15:00",
        "15:00 - 16:00",
        "16:00 - 17:00",
        "17:00 - 18:00",
        "18:00 - 19:00",
        "19:00 - 20:00",
        "21:00 - 22:00",
    ];

    const handleDateChange = (date) => {
        setSelectedDate(date);
        setSelectedTimeSlot("06:00 - 07:00");
    };

    const handleTimeSlotSelect = (slot) => {
        setSelectedTimeSlot(slot);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const [startHour, endHour] = selectedTimeSlot.split(" - ").map((time) => parseInt(time));

        const dateStart = new Date(selectedDate);
        dateStart.setHours(startHour, 0, 0);

        const dateFinish = new Date(selectedDate);
        dateFinish.setHours(endHour, 0, 0);

        const appointmentData = {
            clientId,
            professionalId,
            dateStart: dateStart.toISOString(),
            dateFinish: dateFinish.toISOString(),
            state,
            serviceId
        };

        try {
            const response = await axios.post("http://localhost:8080/scheduleAppointments", appointmentData, {
                headers: {
                    "Content-Type": "application/json",
                },
            });

            if (response.status === 200) {
                navigate(`/PrincipalPageClient`);
                console.log("Agendamento realizado com sucesso!");
            } else {
                console.log("Falha no agendamento.");
            }
        } catch (error) {
            console.error("Erro ao agendar:", error);
            alert("Erro ao agendar.");
        }
    };

    return (
        <div className="h-screen bg-gray-200 text-black font-sans flex flex-col">
            <main className="flex-grow bg-gray-800 bg-opacity-10 flex items-center justify-center">
                <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
                    <h2 className="text-2xl font-semibold text-center mb-6 text-gray-800">Agendar Serviço</h2>
                    <form onSubmit={handleSubmit}>

                        <div className="mb-6">
                            <label className="block text-gray-700 font-medium mb-2">Data</label>
                            <DatePicker
                                selected={selectedDate}
                                onChange={handleDateChange}
                                dateFormat="dd/MM/yyyy"
                                className="w-full p-2 border border-gray-300 rounded"
                                placeholderText="Selecione a data"
                            />
                        </div>

                        <div className="mb-6">
                            <label className="block text-gray-700 font-medium mb-2">Horário</label>
                            <div className="grid grid-cols-2 gap-2">
                                {timeSlots.map((slot, index) => (
                                    <button
                                        key={index}
                                        type="button"
                                        onClick={() => handleTimeSlotSelect(slot)}
                                        className={`py-2 px-4 rounded-lg border ${
                                            selectedTimeSlot === slot
                                                ? "bg-yellow-600  text-white"
                                                : "bg-gray-100 text-gray-800 hover:bg-gray-200"
                                        } transition duration-200`}
                                    >
                                        {slot}
                                    </button>
                                ))}
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="w-full px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                        >
                            Agendar
                        </button>
                    </form>
                </div>
            </main>
            <Footer />
        </div>
    );
}

export default ScheduleAppointments;


