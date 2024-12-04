import React, { useState, useEffect } from 'react';
import axiosInstance from "../../components/axiosInstance";

const Tickets = ({ id }) => {
    const [activeTab, setActiveTab] = useState('open');
    const [tickets, setTickets] = useState({
        allTickets: [],
        open: [],
        inProgress: [],
        closed: [],
    });
    const [showModal, setShowModal] = useState(false);
    const [ticketToRemove, setTicketToRemove] = useState(null);

    useEffect(() => {
        fetchTickets();
    }, []);

    // Carregar todos os tickets da API
    const fetchTickets = async () => {
        try {
            const response = await axiosInstance.get('/tickets');
            const allTickets = response.data;

            // Atualiza o estado com todos os tickets
            setTickets(prevState => ({
                ...prevState,
                allTickets,
            }));
        } catch (error) {
            console.error('Erro ao carregar os tickets:', error);
        }
    };

      useEffect(() => {
        if (tickets.allTickets.length > 0) {
            const filteredTickets = tickets.allTickets.filter(ticket => {
                if (activeTab === 'open') return ticket.status === 'NEW';
                if (activeTab === 'inProgress') return ticket.status === 'IN_REVIEW';
                if (activeTab === 'closed') return ticket.status === 'RESOLVED';
                return true;
            });

            if (activeTab === 'open') {
                setTickets(prevState => ({ ...prevState, open: filteredTickets }));
            } else if (activeTab === 'inProgress') {
                setTickets(prevState => ({ ...prevState, inProgress: filteredTickets }));
            } else if (activeTab === 'closed') {
                setTickets(prevState => ({ ...prevState, closed: filteredTickets }));
            }
        }
    }, [activeTab, tickets.allTickets]);

    // Função para aceitar o ticket
    const handleAcceptTicket = async (ticketId) => {
        try {
            const response = await axiosInstance.patch(`/tickets/accept/${ticketId}`, {
                ticketId,
                adminId: id
            });

            if (response.status === 200) {
                fetchTickets();
            }
        } catch (error) {
            console.error('Erro ao aceitar o ticket:', error);
        }
    };

    // Função para concluir o ticket
    const handleCompleteTicket = async (ticketId) => {
        try {
            const response = await axiosInstance.patch(`/tickets/resolve/${ticketId}`, { id: ticketId});

            if (response.status === 200) {
                fetchTickets();
            }
        } catch (error) {
            console.error('Erro ao resolver o ticket:', error.response?.data || error.message);
        }
    };

    // Função para remover o ticket
    const handleRemoveTicket = async (ticketId) => {
        try {
            const response = await axiosInstance.delete(`/tickets/${ticketId}`, { data: { id: ticketId } });

            if (response.status === 200) {
                fetchTickets();
            }
        } catch (error) {
            console.error('Erro ao remover o ticket:', error);
        }
    };

    // Função para mostrar o modal
    const showDeleteModal = (ticketId) => {
        setTicketToRemove(ticketId);
        setShowModal(true);
    };

    // Função para esconder o modal
    const hideDeleteModal = () => {
        setShowModal(false);
        setTicketToRemove(null);
    };

    // Função para confirmar a remoção
    const confirmDelete = () => {
        if (ticketToRemove) {
            handleRemoveTicket(ticketToRemove);
            hideDeleteModal();
        }
    };

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h2 className="text-2xl font-bold mb-4 text-yellow-600">Tickets</h2>
            <div className="flex space-x-8 border-b border-gray-300 mt-4">
                <button
                    className={`px-4 py-2 text-lg ${activeTab === 'open' ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"}`}
                    onClick={() => setActiveTab('open')}
                >
                    Tickets em Aberto
                </button>
                <button
                    className={`px-4 py-2 text-lg ${activeTab === 'inProgress' ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"}`}
                    onClick={() => setActiveTab('inProgress')}
                >
                    Tickets em Resolução
                </button>
                <button
                    className={`px-4 py-2 text-lg ${activeTab === 'closed' ? "border-b-4 border-yellow-600 font-bold" : "text-gray-500"}`}
                    onClick={() => setActiveTab('closed')}
                >
                    Tickets Fechados
                </button>
            </div>

            {/* Exibindo os tickets filtrados */}
            {activeTab === 'open' && (
                <div className="p-6">
                    <h3 className="text-xl font-semibold mb-4">Tickets em Aberto</h3>
                    {tickets.open.length > 0 ? (
                        tickets.open.map(ticket => (
                            <div key={ticket.id} className="flex justify-between items-center border-b py-3">
                                <div>
                                    <h4 className="font-bold">{ticket.title}</h4>
                                    <p>{ticket.ticketStartDate}</p>
                                </div>
                                <button
                                    onClick={() => handleAcceptTicket(ticket.id)}
                                    className="bg-blue-600 text-white px-4 py-2 rounded-lg"
                                >
                                    Aceitar
                                </button>
                            </div>
                        ))
                    ) : (
                        <p>Não há tickets em aberto.</p>
                    )}
                </div>
            )}

            {activeTab === 'inProgress' && (
                <div className="p-6">
                    <h3 className="text-xl font-semibold mb-4">Tickets em Resolução</h3>
                    {tickets.inProgress.length > 0 ? (
                        tickets.inProgress.map(ticket => (
                            <div key={ticket.id} className="flex justify-between items-center border-b py-3">
                                <div>
                                    <h4 className="font-bold">{ticket.title}</h4>
                                    <p>{ticket.description}</p>
                                </div>
                                <button
                                    onClick={() => handleCompleteTicket(ticket.id)}
                                    className="bg-green-600 text-white px-4 py-2 rounded-lg"
                                >
                                    Concluir
                                </button>
                            </div>
                        ))
                    ) : (
                        <p>Não há tickets em progresso.</p>
                    )}
                </div>
            )}

            {activeTab === 'closed' && (
                <div className="p-6">
                    <h3 className="text-xl font-semibold mb-4">Tickets Fechados</h3>
                    {tickets.closed.length > 0 ? (
                        tickets.closed.map(ticket => (
                            <div key={ticket.id} className="flex justify-between items-center border-b py-3">
                                <div>
                                    <h4 className="font-bold">{ticket.title}</h4>
                                    <p>{ticket.description}</p>
                                </div>
                                <button
                                    onClick={() => showDeleteModal(ticket.id)}
                                    className="bg-red-600 text-white px-4 py-2 rounded-lg"
                                >
                                    Remover
                                </button>
                            </div>
                        ))
                    ) : (
                        <p>Não há tickets fechados.</p>
                    )}
                </div>
            )}

            {/* Modal de confirmação */}
            {showModal && (
                <div className="fixed inset-0 flex justify-center items-center bg-gray-600 bg-opacity-50 z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-96">
                        <h3 className="text-xl font-semibold mb-4">Confirmar remoção</h3>
                        <p>Tem certeza que deseja remover este ticket?</p>
                        <div className="mt-4 flex justify-end space-x-4">
                            <button
                                onClick={hideDeleteModal}
                                className="bg-gray-400 text-white px-4 py-2 rounded-lg"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmDelete}
                                className="bg-red-600 text-white px-4 py-2 rounded-lg"
                            >
                                Remover
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Tickets;
