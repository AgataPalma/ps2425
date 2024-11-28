import React, { useState, useEffect } from 'react';
import axiosInstance from "../../components/axiosInstance";

const PaymentMethodsTab = () => {
    const [categories, setCategories] = useState([]);
    const [modalOpen, setModalOpen] = useState(false);
    const [modalType, setModalType] = useState('add'); // 'add' ou 'edit'
    const [currentCategory, setCurrentCategory] = useState({
        name: '',
    });

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            const response = await axiosInstance.get('/paymentMethods');
            setCategories(response.data);
        } catch (error) {
            console.error('Erro ao buscar PaymentMethods:', error);
        }
    };

    const handleSaveCategory = async () => {
        const { name } = currentCategory;

        if (!name) {
            alert('Preencha todos os campos!');
            return;
        }

        try {
            if (modalType === 'add') {
                // Criar nova categoria
                const response = await axiosInstance.post('/paymentMethods', {
                    name,
                });
                setCategories([...categories, response.data]);
            } else if (modalType === 'edit') {
                const response = await axiosInstance.put(`/paymentMethods/${currentCategory.id}`, {
                    name,
                });
                fetchCategories();
            }
            setModalOpen(false);
            setCurrentCategory({ name: '' });
        } catch (error) {
            console.error('Erro ao salvar categoria:', error);
        }
    };

    const handleDeleteCategory = async (id) => {
        try {
            await axiosInstance.delete(`/paymentMethods/${id}`);
            setCategories(categories.filter((category) => category.id !== id));
        } catch (error) {
            console.error('Erro ao apagar categoria:', error);
        }
    };

    const openModal = (type, category = null) => {
        setModalType(type);
        setCurrentCategory(
            type === 'edit'
                ? { ...category }
                : { name: '' }
        );
        setModalOpen(true);
    };

    const closeModal = () => {
        setModalOpen(false);
        setCurrentCategory({ name: '' });
    };

    return (
        <div className="p-6">
            <h2 className="text-2xl font-bold mb-4 text-yellow-600">Gestão de Metodos de Pagamento</h2>
            <button
                onClick={() => openModal('add')}
                className="bg-yellow-600 text-white px-4 py-2 rounded-lg mb-6"
            >
                Adicionar Novo Metodo de pagamento
            </button>
            <table className="min-w-full bg-white shadow-lg rounded-lg">
                <thead>
                <tr className="text-yellow-600 border-b border-black">
                    <th className="py-3 px-6 text-left font-semibold">Nome</th>
                    <th className="py-3 px-6 text-left font-semibold"></th>
                </tr>
                </thead>
                <tbody>
                {categories.map((category) => (
                    <tr key={category.id} className="border-t hover:bg-yellow-50">
                        <td className="py-3 px-6">{category.name}</td>
                        <td className="py-3 px-6 text-blue-600">
                            <button
                                onClick={() => openModal('edit', category)}
                                className="bg-yellow-600 text-white px-4 py-2 rounded-lg mb-6"
                            >
                                Editar
                            </button>{' '}
                            {' '}
                            <button
                                onClick={() => handleDeleteCategory(category.id)}
                                className="bg-red-700 text-white px-4 py-2 rounded-lg mb-6"
                            >
                                Apagar
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {modalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-1/3">
                        <h3 className="text-lg font-semibold mb-4">
                            {modalType === 'add' ? 'Adicionar Nova Categoria' : 'Editar Categoria'}
                        </h3>
                        <div className="flex flex-col space-y-3">
                            <input
                                type="text"
                                placeholder="Nome do método de pagamento"
                                value={currentCategory.name}
                                onChange={(e) =>
                                    setCurrentCategory({ ...currentCategory, name: e.target.value })
                                }
                                className="border p-2 rounded-lg w-full"
                            />
                            <div className="flex justify-end space-x-2">
                                <button
                                    onClick={closeModal}
                                    className="bg-gray-400 text-white px-4 py-2 rounded-lg"
                                >
                                    Cancelar
                                </button>
                                <button
                                    onClick={handleSaveCategory}
                                    className="bg-yellow-600 text-white px-4 py-2 rounded-lg"
                                >
                                    Salvar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PaymentMethodsTab;
