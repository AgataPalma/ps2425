import React, { useState } from 'react';
import '../index.css';
import Footer from '../components/Footer';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`http://localhost:8080/users/send-email-verification/${email}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok) {
                setMessage('E-mail de verificação enviado! Por favor, verifique a sua caixa de correio.');
                setError('');
            } else {
                setError('Falha no envio do e-mail de verificação. Por favor, tente novamente.');
                setMessage('');
            }
        } catch (error) {
            setError('Ocorreu um erro. Por favor, tente novamente.');
            setMessage('');
        }
    };

    return (
        <div className="bg-gray-200">
            <div className="sm:mx-auto sm:w-full sm:max-w-sm py-12">
                <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                    Recuperar Password
                </h2>
            </div>

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
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                            />
                        </div>
                    </div>

                    {message && <p className="text-green-500 text-sm">{message}</p>}
                    {error && <p className="text-red-500 text-sm">{error}</p>}

                    <div>
                        <button
                            type="submit"
                            className="flex w-full justify-center rounded-md bg-gray-800 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-yellow-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                        >
                            Enviar email de verificação
                        </button>
                    </div>
                </form>

                <p className="mt-10 text-center text-sm text-gray-800">
                    Lembra-se da sua password?<br />
                    <a href="/login" className="font-semibold leading-6 text-yellow-500 mx-2 hover:underline">Log in</a>
                </p>
                
            </div>
            <br />
            <br />
            <br />
            <br />
            <br />
            
            <Footer />
        </div>
    );
};

export default ForgotPassword;
