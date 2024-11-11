import React from 'react';
import '../index.css';
import Footer from '../components/Footer';
import professionalImage from '../images/professional.png';
import clientImage from '../images/client.png';
import { Link } from 'react-router-dom';


const RegisterChoice = () => {
    return (
        <div className="h-screen bg-gray-200 text-black font-sans">


            <div className="flex justify-center space-x-8 my-16">
                <div
                    className="bg-yellow-100 p-8 w-1/3 shadow-lg rounded-lg bg-cover bg-center min-h-[641px]"
                    style={{ backgroundImage: `url(${professionalImage})` }}
                >
                    <h2 className="text-3xl font-bold mb-4">Registar como profissional</h2>
                    <p className="mb-4">
                        Procuras um lugar para promover os teus serviços? ⛏️<br />
                        Seja carpinteiro, canalizador, eletricista ou outro profissional qualificado, a Fix4You é a plataforma certa para ti! 🌟
                    </p>
                    <p className="mb-6">Junta-te hoje e começa a crescer o teu negócio!</p>
                    <Link to="/RegisterProfessional">
                    <button className="bg-gray-800 text-white py-2 px-4 rounded hover:bg-yellow-500">Registar</button>
                    </Link>
                </div>


                <div
                    className="bg-yellow-100 p-8 w-1/3 shadow-lg rounded-lg bg-cover bg-center min-h-[641px]"
                    style={{ backgroundImage: `url(${clientImage})` }}
                >
                    <h2 className="text-3xl font-bold mb-4">Contactar profissionais</h2>
                    <p className="mb-4">
                        Pronto para encontrar o profissional perfeito para a tua necessidade? 🤝<br />
                        Na Fix4You, conectamos-te com profissionais qualificados em carpintaria, canalização, trabalhos elétricos e muito mais.
                    </p>
                    <p className="mb-6">É simples, rápido e gratuito! 🔧</p>
                    <Link to="/RegisterClient">
                    <button className="bg-gray-800 text-white py-2 px-4 rounded hover:bg-yellow-500">Registar</button>
                    </Link>
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default RegisterChoice;
