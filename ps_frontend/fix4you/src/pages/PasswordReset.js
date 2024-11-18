import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import '../index.css';
import Footer from '../components/Footer';

const ResetPassword = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    // Extrair o token da URL
    const [token, setToken] = useState('');
    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const tokenFromUrl = queryParams.get('token');
        if (tokenFromUrl) {
            setToken(tokenFromUrl);
        } else {
            setError('Token inválido ou em falta.');
        }
    }, [location]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError('Passwords não são idênticas');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/users/resetPassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password, token }), // Inclui email, token e senha
            });

            if (response.ok) {
                setMessage('Password restaurada com sucesso! A redirecionar para login...');
                setError('');
                setTimeout(() => navigate('/login'), 3000); // Redireciona após 3 segundos
            } else {
                const responseData = await response.json();
                setError(responseData.message || 'Erro ao restaurar password. Tente outra vez.');
                setMessage('');
            }
        } catch (error) {
            setError('Ocorreu um erro. Tente outra vez.');
            setMessage('');
        }
    };

    return (
        <div className="bg-gray-200 min-h-screen flex flex-col">
            <div className="sm:mx-auto sm:w-full sm:max-w-sm py-12">
                <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                    Restaurar password
                </h2>
            </div>

            <div className="p-8 w-1/3 bg-gray-100 shadow-lg rounded-lg bg-cover bg-center mt-10 sm:mx-auto sm:w-full sm:max-w-sm mb-40">
                {error && <p className="text-red-500 text-center mb-4">{error}</p>}
                {!error && (
                    <form className="space-y-6" onSubmit={handleSubmit}>
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium leading-6 text-gray-900">Email</label>
                            <div className="mt-2">
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="password" className="block text-sm font-medium leading-6 text-gray-900">Nova Password</label>
                            <div className="mt-2">
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="confirmPassword" className="block text-sm font-medium leading-6 text-gray-900">Confirmar Password</label>
                            <div className="mt-2">
                                <input
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    type="password"
                                    required
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                                />
                            </div>
                        </div>

                        {message && <p className="text-green-500 text-center mb-4">{message}</p>}

                        <div>
                            <button
                                type="submit"
                                className="flex w-full justify-center rounded-md bg-gray-800 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-yellow-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                            >
                                Restaurar Password
                            </button>
                        </div>
                    </form>
                )}

                <p className="mt-10 text-center text-sm text-gray-800">
                    Ir para log in? <br />
                    <a href="/login" className="font-semibold leading-6 text-yellow-500 mx-2 hover:underline">Log in</a>
                </p>
                <br />
            </div>
            <Footer />
        </div>
    );
};

export default ResetPassword;
