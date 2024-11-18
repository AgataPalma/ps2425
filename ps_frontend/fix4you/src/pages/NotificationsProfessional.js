import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const NotificationsProfessional = () => {
    const [notificacoesTaxas, setNotificacoesTaxas] = useState([]);
    const [notificacoesServicos, setNotificacoesServicos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState(null);
    const [notificacaoExpandidaId, setNotificacaoExpandidaId] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const buscarNotificacoes = async () => {
            setLoading(true);

            const userId = localStorage.getItem('userId');
            if (!userId) {
                setErro('O ID do utilizador não foi encontrado no armazenamento local.');
                setLoading(false);
                return;
            }

            try {
                const respostas = await Promise.all([
                    fetch(`http://localhost:8080/professionalFees/user/${userId}`),
                    fetch(`http://localhost:8080/services/professional/${userId}`)
                ]);

                const [dadosTaxas, dadosServicos] = await Promise.all(
                    respostas.map((resposta) => {
                        if (!resposta.ok) {
                            throw new Error('Falha ao buscar notificações');
                        }
                        return resposta.json();
                    })
                );

                setNotificacoesTaxas(dadosTaxas);
                setNotificacoesServicos(dadosServicos);
            } catch (err) {
                setErro(err.message || 'Ocorreu um erro ao buscar notificações.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        buscarNotificacoes();
    }, []);

    const alternarNotificacao = (id) => {
        setNotificacaoExpandidaId((prevId) => (prevId === id ? null : id));
    };

    const fazerDownloadInvoice = (invoice, id) => {
        try {
            const blob = new Blob([atob(invoice)], { type: 'application/pdf' });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = `invoice_${id}.pdf`;
            link.click();
        } catch (error) {
            console.error('Erro ao fazer download do invoice:', error);
        }
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

                {/* Notificações de taxas */}
                <h2 className="text-xl font-semibold mb-4">Notificações de Taxas</h2>
                {notificacoesTaxas.length === 0 ? (
                    <p>Não há notificações de taxas disponíveis.</p>
                ) : (
                    <ul className="space-y-4 mb-8">
                        {notificacoesTaxas.map((notificacao) => (
                            <li
                                key={notificacao.id}
                                className="border rounded-lg shadow-sm p-4 bg-gray-100"
                            >
                                <div
                                    className="flex justify-between items-center cursor-pointer"
                                    onClick={() => alternarNotificacao(notificacao.id)}
                                >
                                    <p className="font-semibold">Taxa para {notificacao.relatedMonthYear}</p>
                                    <span className="text-gray-500">
                                        {notificacaoExpandidaId === notificacao.id ? '-' : '+'}
                                    </span>
                                </div>

                                {notificacaoExpandidaId === notificacao.id && (
                                    <div className="mt-4">
                                        <p><strong>Serviços:</strong> {notificacao.numberServices}</p>
                                        <p><strong>Valor:</strong> €{notificacao.value}</p>
                                        <p><strong>Estado do Pagamento:</strong> {notificacao.paymentStatus}</p>
                                        <p><strong>Data do Pagamento:</strong> {new Date(notificacao.paymentDate).toLocaleDateString()}</p>
                                        {notificacao.invoice && (
                                            <button
                                                onClick={() => fazerDownloadInvoice(notificacao.invoice, notificacao.id)}
                                                className="mt-2 px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600"
                                            >
                                                Download
                                            </button>
                                        )}
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                )}
                <br></br>
                {/* Notificações de serviços */}
                <h2 className="text-xl font-semibold mb-4">Notificações de Serviço</h2>
                {notificacoesServicos.length === 0 ? (
                    <p>Não há pedidos de serviço no momento.</p>
                ) : (
                    <ul className="space-y-4">
                        {notificacoesServicos.map((notificacao) => (
                            <li
                                key={notificacao.id}
                                className="border rounded-lg shadow-sm p-4 bg-gray-100"
                            >
                                <div
                                    className="flex justify-between items-center cursor-pointer"
                                    onClick={() => alternarNotificacao(notificacao.id)}
                                >
                                    <p className="font-semibold">{notificacao.title}</p>
                                    <span className="text-gray-500">
                                        {notificacaoExpandidaId === notificacao.id ? '-' : '+'}
                                    </span>
                                </div>

                                {notificacaoExpandidaId === notificacao.id && (
                                    <div className="mt-4">
                                        <p>Verifica a página de Novos Pedidos para mais detalhes.</p>
                                        <button
                                            onClick={() => navigate('/NewRequests')}
                                            className="mt-2 px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-yellow-600"
                                        >
                                            Ir para Novos Pedidos
                                        </button>
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
