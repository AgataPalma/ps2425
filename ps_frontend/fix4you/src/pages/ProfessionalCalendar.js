import React, { useEffect, useState } from 'react';
import axiosInstance from '../components/axiosInstance';
import { saveAs } from 'file-saver';
import { createEvent } from 'ics';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import Spinner from "../components/Spinner";

function ProfessionalCalendar({ id }) {
    const [appointments, setAppointments] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [clientDetails, setClientDetails] = useState(null);
    const [confirmationModalVisible, setConfirmationModalVisible] = useState(false);
    const [refuseModalVisible, setRefuseModalVisible] = useState(false);
    const [modalMessage, setModalMessage] = useState(null);
    const [isErrorModal, setIsErrorModal] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchAppointments = async () => {
            try {
                const response = await axiosInstance.get(`/scheduleAppointments/professional/${id}`);

                const appointmentsWithDetails = await Promise.all(
                    response.data.map(async (appointment) => {
                        const serviceResponse = await axiosInstance.get(`/services/${appointment.serviceId}`);
                        return {
                            id: appointment.id,
                            title: serviceResponse.data.title,
                            start: new Date(appointment.dateStart),
                            end: new Date(appointment.dateFinish),
                            originalStart: new Date(appointment.dateStart),
                            originalEnd: new Date(appointment.dateFinish),
                            state: appointment.state,
                            description: serviceResponse.data.description,
                            color: appointment.state === 'PENDING' ? '#f9a825' : '#43a047',
                            clientId: appointment.clientId,
                            location: serviceResponse.data.location,
                            serviceId: appointment.serviceId,
                        };
                    })
                );

                setAppointments(appointmentsWithDetails);

            } catch (error) {
                console.error('Error fetching appointments:', error);
                setModalMessage('Ocorreu um erro na recuperação das suas marcações');
                setIsErrorModal(true);
            } finally {
                setLoading(false);
            }
        };

        fetchAppointments();

    }, [id]);


    const handleAccept = async (appointmentId, serviceId) => {
        try {

            await axiosInstance.patch(`/services/${serviceId}`, { state: 'ACCEPTED' });
            await axiosInstance.put(`/scheduleAppointments/approve/${appointmentId}`);

            //await axiosInstance.put(`/services/accept-service`, {
            //    params: {
             //       professionalId: id,
            //        serviceId,
            //    },
            //});



            setAppointments((prevAppointments) =>
                prevAppointments.map((app) =>
                    app.id === appointmentId
                        ? { ...app, state: 'CONFIRMED', color: '#43a047' }
                        : app
                )
            );

            setModalMessage('Serviço e horário aceites com sucesso!');
            setIsErrorModal(false);
            setConfirmationModalVisible(true);
        } catch (error) {
            console.error('Error accepting appointment and service:', error?.response?.data || error.message);
            setModalMessage('Ocorreu um erro ao aceitar o horário e o serviço.');
            setIsErrorModal(true);
        }
    };

    const handleCancel = (appointmentId) => {
        axiosInstance.put(`/scheduleAppointments/disapprove/${appointmentId}`)
            .then(() => {
                setAppointments(appointments.filter(app => app.id !== appointmentId));
                setRefuseModalVisible(true);
            })
            .catch(error => {
                console.error('Error canceling appointment:', error);
                setModalMessage('Ocorreu um erro ao recusar o horário');
                setIsErrorModal(true);
            });
    };

    const handleEventChange = (changeInfo) => {
        const { event } = changeInfo;

        setAppointments((prevAppointments) =>
            prevAppointments.map((app) =>
                app.id === event.id
                    ? {
                        ...app,
                        start: event.start,
                        end: event.end,
                    }
                    : app
            )
        );

        setSelectedEvent((prevSelected) =>
            prevSelected?.id === event.id
                ? {
                    ...prevSelected,
                    start: event.start,
                    end: event.end,

                }
                : prevSelected
        );
    };

    const handleReschedule = () => {
        axiosInstance.patch(`/scheduleAppointments/${selectedEvent.id}`, {
            dateStart: formatDateForAPI(selectedEvent.start),
            dateFinish: formatDateForAPI(selectedEvent.end),
        })
            .then(() => {
                return axiosInstance.put(`/scheduleAppointments/approve/${selectedEvent.id}`);
            })
            .then(() => {
                return axiosInstance.patch(`/services/${selectedEvent.serviceId}`, { state: 'ACCEPTED' });
            })
            .then(() => {
                setAppointments((prevAppointments) =>
                    prevAppointments.map((app) =>
                        app.id === selectedEvent.id
                            ? {
                                ...app,
                                start: selectedEvent.start,
                                end: selectedEvent.end,
                                state: 'CONFIRMED',
                                color: '#43a047',
                            }
                            : app
                    )
                );
                setModalMessage('Horário alterado e confirmado');
                setIsErrorModal(false);
                setSelectedEvent(null);
            })
            .catch((error) => {
                console.error('Error rescheduling appointment:', error);
                setModalMessage('Ocorreu um erro ao alterar o horário e aceitar o serviço');
                setIsErrorModal(true);
            });
    };


    const handleDownload = (appointment) => {
        const { title, start, end, description } = appointment;

        const event = {
            start: [start.getFullYear(), start.getMonth() + 1, start.getDate(), start.getHours(), start.getMinutes()],
            end: [end.getFullYear(), end.getMonth() + 1, end.getDate(), end.getHours(), end.getMinutes()],
            title,
            description,
            organizer: { name: 'Fix4You', email: 'info@fix4you.com' }
        };

        const { error, value } = createEvent(event);

        if (error) {
            console.error('Error creating ICS file:', error);
            setModalMessage('Ocorreu um erro ao criar o ficheiro para download');
            setIsErrorModal(true);
            return;
        }

        const blob = new Blob([value], { type: 'text/calendar;charset=utf-8' });
        saveAs(blob, `${title}.ics`);
    };

    const formatDateForAPI = (date) => {
        const pad = (n) => (n < 10 ? '0' + n : n);
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    };

    const handleCloseConfirmationModal = () => {
        setConfirmationModalVisible(false);
        setSelectedEvent(null);
        setClientDetails(null);
    };

    const handleCloseRefuseModal = () => {
        setRefuseModalVisible(false);
        setSelectedEvent(null);
        setClientDetails(null);
    };

    const handleEventClick = async (info) => {

        const clickedAppointment = appointments.find(app => app.id === info.event.id);

        if (!clickedAppointment) {
            console.error("Clicked appointment not found");
            return;
        }

        const clickedEvent = {
            ...info.event.extendedProps,
            id: info.event.id,
            title: info.event.title,
            location: info.event.extendedProps.location,
            state: info.event.extendedProps.state,
            start: info.event.start,
            end: info.event.end,
            originalStart: clickedAppointment.originalStart,
            originalEnd: clickedAppointment.originalEnd,
            serviceId: clickedAppointment.serviceId
        };

        setSelectedEvent(clickedEvent);
        setClientDetails(null);

        try {
            const clientResponse = await axiosInstance.get(`/clients/${clickedEvent.clientId}`);
            setClientDetails(clientResponse.data);
        } catch (error) {
            console.error('Error fetching client details:', error);
            setModalMessage('Ocorreu um erro ao recuperar os dados do cliente');
            setIsErrorModal(true);
        }
    };

    const resetChanges = () => {
        if (selectedEvent) {

            setSelectedEvent((prevEvent) => ({
                ...prevEvent,
                start: prevEvent.originalStart,
                end: prevEvent.originalEnd,
            }));


            setAppointments((prevAppointments) =>
                prevAppointments.map((app) =>
                    app.id === selectedEvent.id
                        ? {
                            ...app,
                            start: selectedEvent.originalStart,
                            end: selectedEvent.originalEnd,
                        }
                        : app
                )
            );

            // Show success message
            setModalMessage('Alterações removidas');
            setIsErrorModal(false);
        }
    };

    const closeModal = () => {
        setSelectedEvent(null);
        setClientDetails(null);

    };

    const renderStars = (rating) => {
        return Array.from({ length: 5 }, (_, index) => (
            <span
                key={index}
                className={`text-xl ${index < rating ? 'text-yellow-500' : 'text-gray-300'}`}
            >
            ★
        </span>
        ));
    };

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Calendário</h1>
            <FullCalendar
                plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                initialView="timeGridWeek"
                headerToolbar={{
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                }}
                events={appointments}
                eventClick={handleEventClick}
                allDaySlot={false}
                slotEventOverlap={false}
                eventOverlap={true}
                eventDisplay="block"
                nowIndicator={true}
                editable={true}
                eventAllow={(dropInfo, draggedEvent) => draggedEvent.extendedProps.state === 'PENDING'}
                eventDrop={handleEventChange} // Handle drag and drop
                eventResize={handleEventChange} // Handle resizing
                eventContent={(eventInfo) => (
                    <div
                        className="h-full w-full text-center flex flex-col justify-center items-center px-2 overflow-hidden">
                        <strong className="text-sm font-bold text-gray-800 truncate w-full">
                            {eventInfo.event.title}
                        </strong>
                        <span className="text-xs text-gray-600 truncate w-full">
                {eventInfo.event.extendedProps.state}
            </span>
                    </div>
                )}
                eventBackgroundColor={(info) =>
                    info.extendedProps.state === 'PENDING' ? '#f9a825' : '#43a047'
                }
                eventDidMount={(info) => {
                    info.el.classList.add('cursor-pointer');
                }}
            />
            {selectedEvent && (
                <div
                    key={selectedEvent.id}
                    className="fixed inset-0 bg-gray-800 bg-opacity-75 flex items-center justify-center z-50"
                >
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-lg w-full">
                        <h2 className="text-xl font-bold mb-4 text-yellow-600">
                            {selectedEvent.title}
                        </h2>
                        <p className="text-sm text-gray-600 mb-2">
                            <strong>Description: </strong>
                            {selectedEvent.description}
                        </p>
                        <p className="text-sm text-gray-600 mb-2">
                            <strong>Schedule: </strong>
                            {new Date(selectedEvent.start).toLocaleString()} - {new Date(selectedEvent.end).toLocaleString()}
                        </p>
                        <p className="text-sm text-gray-600 mb-2">
                            <strong>Localização: </strong>
                            {selectedEvent.location}
                        </p>
                        {clientDetails && (
                            <>
                                <p className="text-sm text-gray-600 mb-2">
                                    <strong>Nome: </strong>
                                    {clientDetails.name}
                                </p>
                                <p className="text-sm text-gray-600 mb-2">
                                    <strong>Rating: </strong>
                                    {renderStars(clientDetails.rating)}
                                </p>
                            </>
                        )}
                        <div className="mt-4 flex justify-between items-center">
                            <div className="flex space-x-2">
                                {selectedEvent?.state === 'PENDING' &&
                                    !(formatDateForAPI(selectedEvent.start) !== formatDateForAPI(selectedEvent.originalStart) ||
                                        formatDateForAPI(selectedEvent.end) !== formatDateForAPI(selectedEvent.originalEnd)) && (
                                        <>
                                            <button
                                                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                                                onClick={() => handleAccept(selectedEvent.id, selectedEvent.serviceId)}
                                            >
                                                Accept
                                            </button>
                                            <button
                                                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-500 transition"
                                                onClick={() => handleCancel(selectedEvent.id)}
                                            >
                                                Refuse
                                            </button>
                                        </>
                                    )}
                                {selectedEvent?.state === 'PENDING' &&
                                    (formatDateForAPI(selectedEvent.start) !== formatDateForAPI(selectedEvent.originalStart) ||
                                        formatDateForAPI(selectedEvent.end) !== formatDateForAPI(selectedEvent.originalEnd)) && (
                                        <>
                                            <button
                                                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition"
                                                onClick={handleReschedule}
                                            >
                                                Reschedule
                                            </button>
                                            <button
                                                className="px-4 py-2 bg-orange-600 text-white rounded-lg hover:bg-orange-500 transition"
                                                onClick={resetChanges}
                                            >
                                                Reset
                                            </button>
                                        </>
                                    )}
                                {selectedEvent?.state === 'CONFIRMED' && (
                                    <button
                                        className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                                        onClick={() => handleDownload(selectedEvent)}
                                    >
                                        Download .ics
                                    </button>
                                )}
                            </div>
                            <button
                                className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                                onClick={closeModal}
                            >
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {confirmationModalVisible && (
                <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full text-center">
                        <h2 className="text-xl font-bold mb-4 text-green-600">Aceite</h2>
                        <p className="text-sm text-gray-600 mb-4">
                            O horário proposto foi aceite!
                        </p>
                        <button
                            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-500 transition"
                            onClick={handleCloseConfirmationModal}
                        >
                            OK
                        </button>
                    </div>
                </div>
            )}
            {refuseModalVisible && (
                <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full text-center">
                        <h2 className="text-xl font-bold mb-4 text-red-600">Recusado</h2>
                        <p className="text-sm text-gray-600 mb-4">
                            O horário proposto foi recusado.
                        </p>
                        <button
                            className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-500 transition"
                            onClick={handleCloseRefuseModal}
                        >
                            OK
                        </button>
                    </div>
                </div>
            )}
            {modalMessage && (
                <div className="fixed inset-0 bg-gray-800 bg-opacity-75 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full text-center">
                        <h2
                            className={`text-xl font-bold mb-4 ${
                                isErrorModal ? 'text-red-600' : 'text-green-600'
                            }`}
                        >
                            {isErrorModal ? 'Erro' : 'Sucesso'}
                        </h2>
                        <p className="text-sm text-gray-600 mb-4">{modalMessage}</p>
                        <button
                            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition"
                            onClick={() => setModalMessage(null)}
                        >
                            OK
                        </button>
                    </div>
                </div>
            )}

        </div>
    );
}

export default ProfessionalCalendar;
