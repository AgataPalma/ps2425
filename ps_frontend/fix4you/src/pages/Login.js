import React, { useState } from 'react';
import '../index.css';
import Footer from '../components/Footer';
import { Link, useNavigate } from 'react-router-dom';
import Spinner from '../components/Spinner'; // Importa o Spinner

const Login = ({ onLogin }) => { 
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    // Estados para os modais e carregamento
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMessage, setModalMessage] = useState('');
    const [isSuccess, setIsSuccess] = useState(false);
    const [loading, setLoading] = useState(false);
    const [userType, setUserType] = useState(''); // Para armazenar o tipo de usuário

    const navigate = useNavigate(); // Para navegação após o login

    const handleModalClose = () => {
        setIsModalOpen(false);
        if (isSuccess) {
            // Navega para a página apropriada com base no userType
            if (userType === "PROFESSIONAL") {
                navigate('/PrincipalPageProfessional');
            } else if (userType === "CLIENT") {
                navigate('/PrincipalPageClient');
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);

                onLogin(data.userType, data.userId);
                setUserType(data.userType); // Armazena o tipo de usuário para uso posterior

                setLoading(false);
                setIsSuccess(true);
                setIsModalOpen(true);
            } else {
                const errorMessage = await response.text(); // lê a resposta como texto
                setLoading(false);
                setModalMessage(errorMessage);
                setIsSuccess(false);
                setIsModalOpen(true);
            }
        } catch (error) {
            console.error('Ocorreu um erro:', error);
            setLoading(false);
            setModalMessage('Ocorreu um erro. Por favor, tente novamente.');
            setIsSuccess(false);
            setIsModalOpen(true);
        }
    };

    const Modal = ({ message, isSuccess, onClose }) => (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
            <div className="bg-white rounded-lg shadow-lg p-6 max-w-md w-full">
                <h3 className={`text-lg font-bold ${isSuccess ? 'text-green-600' : 'text-red-600'}`}>
                    {isSuccess ? 'Sucesso' : 'Erro'}
                </h3>
                <p className="mt-2 text-gray-800">{message}</p>
                <div className="mt-4 flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 text-white bg-gray-800 rounded-md hover:bg-yellow-600"
                    >
                        Fechar
                    </button>
                </div>
            </div>
        </div>
    );

    if (loading) {
        return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
    }

    return (
        <div className="bg-gray-200">
            <div className="sm:mx-auto sm:w-full sm:max-w-sm py-12">
                <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                    Faça login na sua conta
                </h2>
            </div>

            {isModalOpen && (
                <Modal
                    message={modalMessage}
                    isSuccess={isSuccess}
                    onClose={handleModalClose}
                />
            )}

            <div className="p-8 w-1/3 bg-gray-100 shadow-lg rounded-lg bg-cover bg-center mt-10 sm:mx-auto sm:w-full sm:max-w-sm mb-40">
                <form className="space-y-6" onSubmit={handleSubmit}>
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium leading-6 text-gray-900">Email</label>
                        <div className="mt-2">
                            <input
                                id="email"
                                name="email"
                                type="email"
                                autoComplete="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                           ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                           focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                            />
                        </div>
                    </div>

                    <div>
                        <div className="flex items-center justify-between">
                            <label htmlFor="password" className="block text-sm font-medium leading-6 text-gray-900">Password</label>
                            <div className="text-sm">
                                <Link to="/Recover">
                                    <span className="font-semibold text-yellow-500 mx-2 hover:underline">Esqueceu-se da password?</span>
                                </Link>
                            </div>
                        </div>
                        <div className="mt-2 mb-10">
                            <input
                                id="password"
                                name="password"
                                type="password"
                                autoComplete="current-password"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                           ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                           focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                            />
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            className="flex w-full justify-center rounded-md bg-gray-800 px-3 py-1.5 text-sm font-semibold
                                       leading-6 text-white shadow-sm hover:bg-yellow-600 focus-visible:outline
                                       focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                        >
                            Log in
                        </button>
                    </div>
                </form>

                <p className="mt-10 text-center text-sm text-gray-800">
                    Não tem uma conta?<br />
                    <Link to="/RegisterChoice">
                        <span className="font-semibold leading-6 text-yellow-500 mx-2 hover:underline">Registar</span>
                    </Link>
                </p>
                <br />
            </div>
            <Footer />
        </div>
    );
};

export default Login;
