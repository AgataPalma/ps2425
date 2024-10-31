import React from 'react';
import '../index.css';
import user from '../images/user.png';
import { FaStar } from 'react-icons/fa';
import Footer from '../components/Footer';

const PrincipalPageClient = () => {
    return (
        <div className="h-screen text-black font-sans">


                <div className="bg-gray-800 bg-opacity-15 shadow-lg">
                    <br/>
                    <section className="mt-6 text-center">
                        <h2 className="text-yellow-600 text-3xl mb-4 font-bold">Pesquisar Por Serviço</h2>
                        <div className="flex justify-center items-center space-x-2">
                            <input
                                type="text"
                                placeholder="Procurar..."
                                className="bg-yellow-600 w-96 px-4 py-2 rounded-full focus:outline-none placeholder-black"
                            />
                            <button
                                    className="bg-yellow-600 p-2 rounded-full hover:bg-yellow-700">
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    className="h-6 w-6"
                                    fill="none"
                                    viewBox="0 0 24 24"
                                    stroke="currentColor"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth="2"
                                        d="M4 6h16M4 12h16m-7 6h7"
                                    />
                                </svg>
                            </button>
                        </div>
                    </section>
                    <br/><br/>
                    <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                        {Array(6).fill(0).map((_, index) => (
                            <div key={index}  className="rounded-xl shadow-lg bg-gray-50 p-4">
                                <div className="flex items-center space-x-4">
                                    <img src={user} alt="Profile" className="w-20 h-20"/>
                                    <div className="flex-1">
                                        <h2 className="text-2xl font-bold ">Title</h2>
                                    </div>
                                    <div className="flex space-x-1">
                                        <FaStar className="text-yellow-600 w-6 h-6"/>
                                        <FaStar className="text-yellow-600 w-6 h-6"/>
                                        <FaStar className="text-yellow-600 w-6 h-6"/>
                                        <FaStar className="text-yellow-600 w-6 h-6"/>
                                        <FaStar className="text-gray-800 w-6 h-6"/>
                                    </div>
                                </div>
                                <div className="flex justify-between items-center mb-4">
                                    <p className="text-sm font-semibold flex items-center">
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            className="h-4 w-4 mr-2"
                                            fill="none"
                                            viewBox="0 0 24 24"
                                            stroke="currentColor"
                                        >
                                            <path
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                                strokeWidth="2"
                                                d="M12 2C8.134 2 5 5.134 5 9c0 4 5 11 7 11s7-7 7-11c0-3.866-3.134-7-7-7zm0 7a2 2 0 11-4 0 2 2 0 014 0z"
                                            />
                                        </svg>
                                        Location
                                    </p>
                                    <p className="text-sm text-gray-800">Price €</p>
                                </div>

                                <textarea
                                    className="w-full p-2 border border-gray-300 rounded-lg mb-4"
                                    rows="3"
                                    placeholder="Description"
                                />

                                <div className="flex justify-center items-center mb-6">
                                    <label className="flex items-center mr-5 text-sm text-gray-800">
                                        <input
                                            type="checkbox"
                                            className="form-checkbox h-4 w-4 text-gray-600"
                                        />
                                        <span className="ml-2">Custo de viagem</span>
                                    </label>
                                    <label className="flex ml-5 items-center text-sm text-gray-800">
                                        <input
                                            type="checkbox"
                                            className="form-checkbox h-4 w-4 text-gray-600"
                                        />
                                        <span className="ml-2">Passa Fatura</span>
                                    </label>
                                </div>

                                <div className="flex justify-center">
                                    <button
                                        className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                                        Contactar
                                    </button>
                                </div>
                            </div>
                        ))}
                    </section>
                    <Footer/>
                </div>
            </div>
            );
            };

            export default PrincipalPageClient;
