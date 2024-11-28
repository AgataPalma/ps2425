import React, { useState, useEffect } from 'react';
import axiosInstance from "../../components/axiosInstance";

const CategoriesTab = () => {
    const [categories, setCategories] = useState([]);
    const [modalOpen, setModalOpen] = useState(false);
    const [modalType, setModalType] = useState('add'); // 'add' ou 'edit'
    const [currentCategory, setCurrentCategory] = useState({
        name: '',
        minValue: '',
        maxValue: '',
    });

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            const response = await axiosInstance.get('/categories');
            setCategories(response.data);
        } catch (error) {
            console.error('Erro ao buscar categorias:', error);
        }
    };

    const handleSaveCategory = async () => {
        const { name, minValue, maxValue } = currentCategory;

        if (!name || minValue === '' || maxValue === '') {
            alert('Preencha todos os campos!');
            return;
        }

        const minValueNumber = parseFloat(minValue);
        const maxValueNumber = parseFloat(maxValue);

        if (isNaN(minValueNumber) || isNaN(maxValueNumber)) {
            alert('Os valores mínimo e máximo devem ser números válidos!');
            return;
        }

        if (minValueNumber < 0 || maxValueNumber < 0) {
            alert('Os valores mínimo e máximo não podem ser negativos!');
            return;
        }

        if (minValueNumber > maxValueNumber) {
            alert('O valor mínimo não pode ser maior que o valor máximo!');
            return;
        }

        try {
            if (modalType === 'add') {
                // Criar nova categoria
                const response = await axiosInstance.post('/categories', {
                    name,
                    minValue: minValueNumber,
                    maxValue: maxValueNumber,
                });
                setCategories([...categories, response.data]);
            } else if (modalType === 'edit') {
                // Atualizar categoria existente
                const response = await axiosInstance.put(`/categories/${currentCategory.id}`, {
                    name,
                    minValue: minValueNumber,
                    maxValue: maxValueNumber,
                });
                fetchCategories(); // Atualiza a lista
            }
            setModalOpen(false);
            setCurrentCategory({ name: '', minValue: '', maxValue: '' });
        } catch (error) {
            console.error('Erro ao salvar categoria:', error);
        }
    };

    const handleDeleteCategory = async (id) => {
        try {
            await axiosInstance.delete(`/categories/${id}`);
            setCategories(categories.filter((category) => category.id !== id)); // Remove localmente
        } catch (error) {
            console.error('Erro ao apagar categoria:', error);
        }
    };

    const openModal = (type, category = null) => {
        setModalType(type);
        setCurrentCategory(
            type === 'edit'
                ? { ...category }
                : { name: '', minValue: '', maxValue: '' } // Limpa para adicionar
        );
        setModalOpen(true);
    };

    const closeModal = () => {
        setModalOpen(false);
        setCurrentCategory({ name: '', minValue: '', maxValue: '' });
    };

    return (
        <div className="p-6">
            <h2 className="text-2xl font-bold mb-4 text-yellow-600">Gestão de Categorias</h2>
            <button
                onClick={() => openModal('add')}
                className="bg-yellow-600 text-white px-4 py-2 rounded-lg mb-6"
            >
                Adicionar Nova Categoria
            </button>
            <table className="min-w-full bg-white shadow-lg rounded-lg">
                <thead>
                <tr className="text-yellow-600 border-b border-black">
                    <th className="py-3 px-6 text-left font-semibold">Nome</th>
                    <th className="py-3 px-6 text-left font-semibold">Valor Mínimo</th>
                    <th className="py-3 px-6 text-left font-semibold">Valor Máximo</th>
                    <th className="py-3 px-6 text-left font-semibold"></th>
                </tr>
                </thead>
                <tbody>
                {categories.map((category) => (
                    <tr key={category.id} className="border-t hover:bg-yellow-50">
                        <td className="py-3 px-6">{category.name}</td>
                        <td className="py-3 px-6">{category.minValue}</td>
                        <td className="py-3 px-6">{category.maxValue}</td>
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
                                placeholder="Nome da Categoria"
                                value={currentCategory.name}
                                onChange={(e) =>
                                    setCurrentCategory({ ...currentCategory, name: e.target.value })
                                }
                                className="border p-2 rounded-lg w-full"
                            />
                            <input
                                type="text"
                                placeholder="Valor Mínimo"
                                value={currentCategory.minValue}
                                onChange={(e) =>
                                    setCurrentCategory({ ...currentCategory, minValue: e.target.value })
                                }
                                className="border p-2 rounded-lg w-full"
                            />
                            <input
                                type="text"
                                placeholder="Valor Máximo"
                                value={currentCategory.maxValue}
                                onChange={(e) =>
                                    setCurrentCategory({ ...currentCategory, maxValue: e.target.value })
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

export default CategoriesTab;
