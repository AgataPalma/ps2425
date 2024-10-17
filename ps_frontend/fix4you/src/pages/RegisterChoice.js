import React from 'react';
import '../index.css';
import Header from '../components/HeaderNotLogged';
import Footer from '../components/Footer';
import professionalImage from '../images/professional.png';
import clientImage from '../images/client.png';
import { Link } from 'react-router-dom';


const RegisterChoice = () => {
    return (
        <div className="h-screen bg-gray-200 text-black font-sans">
            <Header />

            <div className="flex justify-center space-x-8 my-16">
                <div
                    className="bg-yellow-100 p-8 w-1/3 shadow-lg rounded-lg bg-cover bg-center min-h-[641px]"
                    style={{ backgroundImage: `url(${professionalImage})` }}
                >
                    <h2 className="text-3xl font-bold mb-4">Register as a professional</h2>
                    <p className="mb-4">
                        Looking for a place to promote your services? ⛏️<br />
                        Whether you're a carpenter, plumber, electrician, or any other skilled professional, Fix4You is the right platform for you! 🌟
                    </p>
                    <p className="mb-6">Join today and start growing your business!</p>
                    <Link to="/RegisterProfessional">
                    <button className="bg-yellow-600 text-white py-2 px-4 rounded hover:bg-yellow-500">Register</button>
                    </Link>
                </div>


                <div
                    className="bg-yellow-100 p-8 w-1/3 shadow-lg rounded-lg bg-cover bg-center min-h-[641px]"
                    style={{ backgroundImage: `url(${clientImage})` }}
                >
                    <h2 className="text-3xl font-bold mb-4">Contact professionals</h2>
                    <p className="mb-4">
                        Ready to find the perfect professional for your request? 🤝<br />
                        At Fix4You, we connect you with skilled professionals in carpentry, plumbing, electrical work, and much more.
                    </p>
                    <p className="mb-6">It's simple, fast, and free! 🔧</p>
                    <Link to="/RegisterClient">
                    <button className="bg-yellow-600 text-white py-2 px-4 rounded hover:bg-yellow-500">Register</button>
                    </Link>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default RegisterChoice;
