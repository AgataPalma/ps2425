import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const NotificationsProfessional = () => {
    const [notificacoes, setNotificacoes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState(null);
    const [notificacaoExpandidaId, setNotificacaoExpandidaId] = useState(null); // ID da notificação expandida
    const navigate = useNavigate();

    useEffect(() => {
        const userId = localStorage.getItem('userId');
        if (!userId) {
            setErro('O ID do utilizador não foi encontrado no armazenamento local.');
            setLoading(false);
            return;
        }

        const buscarNotificacoes = async () => {
            setLoading(true);
            try {
                const resposta = await fetch(`http://localhost:8080/notifications/professional/${userId}`);

                if (!resposta.ok) {
                    throw new Error('Falha ao buscar notificações');
                }

                const dadosNotificacoes = await resposta.json();
                setNotificacoes(dadosNotificacoes);
            } catch (err) {
                setErro(err.message || 'Ocorreu um erro ao buscar notificações.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        buscarNotificacoes();

        // Conectar ao endpoint SSE com o professionalId
        const eventSource = new EventSource(`http://localhost:8080/professionalFees/sse?professionalId=${userId}`);

        eventSource.onmessage = (event) => {
            console.log('Mensagem SSE recebida:', event.data);
            // Recarregar notificações
            buscarNotificacoes();
        };

        eventSource.onerror = (event) => {
            console.error('EventSource falhou:', event);
            eventSource.close();
        };

        // Limpar a conexão quando o componente for desmontado
        return () => {
            eventSource.close();
        };
    }, []);

    const deletarNotificacao = async (id) => {
        try {
            const resposta = await fetch(`http://localhost:8080/notifications/${id}`, {
                method: 'DELETE',
            });

            if (!resposta.ok) {
                throw new Error('Falha ao apagar notificação');
            }

            // Remover a notificação do estado
            setNotificacoes((prev) => prev.filter((notificacao) => notificacao.id !== id));
        } catch (err) {
            console.error(err);
        }
    };

    const alternarNotificacao = async (id, read) => {
        // Se a notificação não está lida, marcar como lida
        if (!read) {
            try {
                const resposta = await fetch(`http://localhost:8080/notifications/${id}/read`, {
                    method: 'PUT',
                });

                if (!resposta.ok) {
                    throw new Error('Falha ao marcar notificação como lida');
                }

                // Atualizar o estado para refletir que a notificação foi lida
                setNotificacoes((prev) =>
                    prev.map((notificacao) =>
                        notificacao.id === id ? { ...notificacao, read: true } : notificacao
                    )
                );
            } catch (err) {
                console.error(err);
            }
        }

        // Alternar a notificação expandida
        setNotificacaoExpandidaId((prevId) => (prevId === id ? null : id));
    };

    if (loading) {
        return <div>A carregar notificações...</div>;
    }

    if (erro) {
        return <div className="text-red-500">{erro}</div>;
    }

    return (
        <div className="min-h-screen bg-gray-200 p-8">
            <div className="max-w-4xl mx-auto bg-white shadow-md rounded-lg p-6">
                <h1 className="text-2xl font-bold mb-6">Notificações</h1>

                {notificacoes.length === 0 ? (
                    <p>Não há notificações disponíveis.</p>
                ) : (
                    <ul className="space-y-4 mb-8">
                        {notificacoes.map((notificacao) => (
                            <li
                                key={notificacao.id}
                                className={`border rounded-lg shadow-sm p-4 ${
                                    notificacao.read ? 'bg-gray-100' : 'bg-yellow-100'
                                }`}
                            >
                                <div
                                    className="flex justify-between items-center cursor-pointer"
                                    onClick={() => alternarNotificacao(notificacao.id, notificacao.read)}
                                >
                                    <p className="font-semibold">{notificacao.message}</p>
                                    <div className="flex items-center">
                                        {!notificacao.read && (
                                            <span className="text-sm text-red-500 mr-2">Novo</span>
                                        )}
                                        <button
                                            onClick={(e) => {
                                                e.stopPropagation(); // Impedir que o clique expanda a notificação
                                                deletarNotificacao(notificacao.id);
                                            }}
                                            className="text-red-500 hover:text-red-700"
                                        >
                                            Apagar
                                        </button>
                                    </div>
                                </div>
                                <p className="text-sm text-gray-500">
                                    {new Date(notificacao.createdAt).toLocaleString()}
                                </p>

                                {notificacaoExpandidaId === notificacao.id && (
                                    <div className="mt-4">
                                        <p>
                                            <strong>Serviços:</strong> {notificacao.numberServices}
                                        </p>
                                        <p>
                                            <strong>Valor:</strong> €{notificacao.feeValue}
                                        </p>
                                        <p>
                                            <strong>Período:</strong> {notificacao.relatedMonthYear}
                                        </p>
                                        <p>
                                            <strong>Estado do Pagamento:</strong> {notificacao.paymentStatus}
                                        </p>
                                        <p>
                                            <strong>Data do Pagamento:</strong>{' '}
                                            {notificacao.paymentDate
                                                ? new Date(notificacao.paymentDate).toLocaleDateString()
                                                : 'N/A'}
                                        </p>
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default NotificationsProfessional;
