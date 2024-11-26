import React, { useState, useEffect } from "react";
import axios from "axios";

const AdminManagement = () => {
    const [activeTab, setActiveTab] = useState("viewAdmins"); // Controla a aba ativa
    const [admins, setAdmins] = useState([]); // Lista de admins
    const [email, setEmail] = useState(""); // Email do admin para adicionar/editar
    const [password, setPassword] = useState(""); // Senha do admin para adicionar/editar
    const [selectedAdmin, setSelectedAdmin] = useState(null); // Admin selecionado para edição
    const [message, setMessage] = useState(""); // Mensagem de feedback
    const [error, setError] = useState(""); // Mensagem de erro

    useEffect(() => {
        fetchAdmins();
    }, []);

    const fetchAdmins = async () => {
        try {
            const response = await axios.get("http://localhost:8080/users");
            const adminUsers = response.data.filter(user => user.userType === "ADMIN");
            setAdmins(adminUsers);
        } catch (error) {
            console.error("Erro ao buscar admins:", error);
            setError("Não foi possível carregar a lista de admins.");
        }
    };

    const handleAddOrEditAdmin = async (e) => {
        e.preventDefault();

        const adminData = {
            email,
            ...(password && { password }), // Adiciona `password` apenas se preenchido
        };

        try {
            if (selectedAdmin) {
                // Editar admin existente (usando PATCH)
                await axios.patch(`http://localhost:8080/users/${selectedAdmin.id}`, adminData);
                setMessage("Admin atualizado com sucesso!");
            } else {
                // Adicionar novo admin
                await axios.post("http://localhost:8080/users", { ...adminData, userType: "ADMIN" });
                setMessage("Admin adicionado com sucesso!");
            }
            fetchAdmins(); // Atualiza a lista
            resetForm();
        } catch (error) {
            console.error("Erro ao salvar admin:", error);
            setError("Erro ao salvar admin. Verifique os dados e tente novamente.");
        }
    };

    const handleDeleteAdmin = async (id) => {
        try {
            await axios.delete(`http://localhost:8080/users/${id}`);
            setMessage("Admin eliminado com sucesso!");
            fetchAdmins(); // Atualiza a lista
        } catch (error) {
            console.error("Erro ao eliminar admin:", error);
            setError("Erro ao eliminar o admin.");
        }
    };

    const handleEditClick = (admin) => {
        setSelectedAdmin(admin);
        setEmail(admin.email);
        setPassword(""); // Deixa o campo de senha vazio para evitar preenchimento desnecessário
        setActiveTab("addOrEditAdmin");
    };

    const resetForm = () => {
        setSelectedAdmin(null);
        setEmail("");
        setPassword("");
        setError("");
        setMessage("");
    };

    return (
        <div className="p-8 max-w-6xl mx-auto bg-white shadow-lg rounded-lg mt-8">
            <h1 className="text-3xl font-bold mb-6 text-yellow-600">Gestão de Admins</h1>

            {/* Tabs Navigation */}
            <div className="flex space-x-4 border-b-2 mb-4">
                <button
                    onClick={() => setActiveTab("viewAdmins")}
                    className={`px-4 py-2 text-lg ${
                        activeTab === "viewAdmins"
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                >
                    Ver Admins
                </button>
                <button
                    onClick={() => setActiveTab("addOrEditAdmin")}
                    className={`px-4 py-2 text-lg ${
                        activeTab === "addOrEditAdmin"
                            ? "border-b-4 border-yellow-600 font-bold"
                            : "text-gray-500"
                    }`}
                >
                    {selectedAdmin ? "Editar Admin" : "Adicionar Admin"}
                </button>
            </div>

            {/* Tab Content */}
            {activeTab === "viewAdmins" && (
                <div>
                    {/* Mensagens de feedback */}
                    {message && <div className="text-green-600 mb-4">{message}</div>}
                    {error && <div className="text-red-600 mb-4">{error}</div>}

                    <table className="table-auto w-full text-left border-collapse border border-gray-300">
                        <thead>
                            <tr>
                                <th className="border border-gray-300 px-4 py-2">Email</th>
                                <th className="border border-gray-300 px-4 py-2">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            {admins.map(admin => (
                                <tr key={admin.id} className="hover:bg-gray-100">
                                    <td className="border border-gray-300 px-4 py-2">{admin.email}</td>
                                    <td className="border border-gray-300 px-4 py-2 flex space-x-4">
                                        <button
                                            className="text-blue-500 hover:underline"
                                            onClick={() => handleEditClick(admin)}
                                        >
                                            Editar
                                        </button>
                                        <button
                                            className="text-red-500 hover:underline"
                                            onClick={() => handleDeleteAdmin(admin.id)}
                                        >
                                            Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === "addOrEditAdmin" && (
                <div className="bg-gray-100 shadow-md rounded-lg p-6">
                    <h2 className="text-xl font-semibold mb-4">
                        {selectedAdmin ? "Editar Admin" : "Adicionar Admin"}
                    </h2>
                    {/* Mensagens de feedback */}
                    {message && <div className="text-green-600 mb-4">{message}</div>}
                    {error && <div className="text-red-600 mb-4">{error}</div>}

                    <form onSubmit={handleAddOrEditAdmin} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Email</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                           ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                           focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700">Password</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                           ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                           focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                                placeholder={selectedAdmin ? "Deixe vazio para manter a senha atual" : ""}
                            />
                        </div>
                        <div className="flex items-center justify-end space-x-4">
                            <button
                                type="button"
                                onClick={resetForm}
                                className="text-gray-500 hover:text-gray-700"
                            >
                                Cancelar
                            </button>
                            <button
                                type="submit"
                                className="px-4 py-2 bg-gray-800 text-white rounded-md hover:bg-yellow-600"
                            >
                                {selectedAdmin ? "Atualizar" : "Adicionar"}
                            </button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
};

export default AdminManagement;
