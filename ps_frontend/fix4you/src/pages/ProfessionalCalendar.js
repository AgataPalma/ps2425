import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { saveAs } from 'file-saver';
import { createEvent } from 'ics';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';

function ProfessionalCalendar() {
    const [appointments, setAppointments] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get('/api/professional/appointments')
            .then(response => {
                setAppointments(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching appointments:', error);
                setLoading(false);
            });
    }, []);

    const handleAccept = (appointmentId) => {
        axios.put(`/api/professional/appointments/${appointmentId}/accept`)
            .then(() => {
                setAppointments(appointments.map(app => app.id === appointmentId ? { ...app, status: 'accepted' } : app));
            })
            .catch(error => {
                console.error('Error accepting appointment:', error);
            });
    };

    const handleCancel = (appointmentId) => {
        axios.put(`/api/professional/appointments/${appointmentId}/cancel`)
            .then(() => {
                setAppointments(appointments.map(app => app.id === appointmentId ? { ...app, status: 'canceled' } : app));
            })
            .catch(error => {
                console.error('Error canceling appointment:', error);
            });
    };

    const handleDownload = (appointment) => {
        const { title, description, location, date, startTime, endTime } = appointment;

        const event = {
            start: [date.year, date.month, date.day, startTime.hour, startTime.minute],
            end: [date.year, date.month, date.day, endTime.hour, endTime.minute],
            title,
            description,
            location,
            status: 'CONFIRMED',
            organizer: { name: 'Fix4You', email: 'info@fix4you.com' } // change for the admin mail.
        };

        const { error, value } = createEvent(event);

        if (error) {
            console.error('Error creating ICS file:', error);
            return;
        }

        const blob = new Blob([value], { type: 'text/calendar;charset=utf-8' });
        saveAs(blob, `${title}.ics`);
    };

    if (loading) {
        return <div className="p-8 max-w-4xl mx-auto bg-white shadow-lg rounded-lg">Loading...</div>;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Calendário</h1>
            {appointments.length === 0 && (
                <FullCalendar
                    plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                    initialView="dayGridMonth"
                    headerToolbar={{
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,timeGridWeek,timeGridDay'
                    }}
                />
            )}
            <div className="space-y-6">
                {appointments.length > 0 ? (
                    appointments.map(appointment => (
                        <div key={appointment.id} className="p-4 bg-gray-100 rounded-lg shadow-md">
                            <h3 className="text-xl font-bold text-gray-800 mb-2">{appointment.title}</h3>
                            <p className="text-gray-600">Date: {new Date(appointment.date).toLocaleDateString()}</p>
                            <p className="text-gray-600">Time: {appointment.startTime} - {appointment.endTime}</p>
                            <p className="text-gray-600">Location: {appointment.location}</p>
                            <p className="text-gray-600">Status: {appointment.status}</p>
                            {appointment.status === 'pending' && (
                                <div className="mt-4 space-x-4">
                                    <button
                                        onClick={() => handleAccept(appointment.id)}
                                        className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                                    >
                                        Accept
                                    </button>
                                    <button
                                        onClick={() => handleCancel(appointment.id)}
                                        className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-500 transition"
                                    >
                                        Cancel
                                    </button>
                                </div>
                            )}
                            {appointment.status === 'accepted' && (
                                <button
                                    onClick={() => handleDownload(appointment)}
                                    className="mt-4 px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                >
                                    Download Appointment
                                </button>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-gray-600">No appointments found.</p>
                )}
            </div>
        </div>
    );
}

export default ProfessionalCalendar;
