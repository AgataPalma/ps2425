import React from 'react';
import '../index.css';
import user from '../images/user.png';
import Footer from '../components/Footer';

const NewRequests = () => {
    return (
        <div className="h-screen text-black font-sans">
            <div className="bg-gray-800 bg-opacity-15 shadow-lg">
                <br/>
                <section className="mt-6 text-center">
                    <h2 className="text-yellow-600 text-3xl font-bold">Novos Pedidos</h2>
                </section>
                <br/><br/>
                <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-6">
                    {Array(4).fill(0).map((_, index) => (
                        <div key={index} className="rounded-xl shadow-lg bg-gray-50 p-4">
                            <div className="flex items-center space-x-4">
                                <img src={user} alt="Profile" className="w-20 h-20"/>
                                <div className="flex-1">
                                    <h3 className="text-xl font-semibold">Title</h3>
                                    <p className="text-sm">Category</p>
                                </div>
                            </div>
                            <div className="mt-4">
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
                                <textarea
                                    className="w-full bg-white mt-2 p-2 h-20 placeholder-black border border-black"
                                    placeholder="Description"
                                ></textarea>

                            </div>
                            <div className="mt-4 flex justify-center">
                                <button
                                    className="px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                                    Aceitar
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

export default NewRequests;
