import React, { useState } from 'react';
import '../index.css';
import backgroundImage from '../images/background.png';
import Footer from '../components/Footer';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const [selectedArea, setSelectedArea] = useState(null);
  const navigate = useNavigate();

  const areas = [
    { title: 'Reparações Domésticas', description: 'Encontre profissionais para eletricidade, canalização, e mais.' },
    { title: 'Eventos', description: 'Organize o evento perfeito com serviços de catering, fotografia, e decoração.' },
    { title: 'Mudanças e Limpezas', description: 'Contrate empresas para mudanças e serviços de limpeza.' },
    { title: 'Consultoria', description: 'Especialistas em finanças, tecnologia, e marketing ao seu dispor.' },
    { title: 'Beleza e Bem-estar', description: 'Encontre profissionais de estética, cabeleireiros e massagens.' },
    { title: 'Educação', description: 'Aulas particulares, reforço escolar e treinamento profissional.' },
    { title: 'Tecnologia', description: 'Técnicos de TI, desenvolvimento de websites e suporte técnico.' },
    { title: 'Saúde', description: 'Profissionais de saúde como fisioterapeutas e cuidadores.' },
  ];

  const handleSelectArea = (area) => {
    setSelectedArea(area);
  };

  return (
    <div
      style={{
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      }}
      className="min-h-screen flex flex-col justify-between"
    >
      <main className="bg-white bg-opacity-90 py-12">
        {/* Seção de Slogan */}
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center mt-16">
          <h1 className="text-5xl md:text-6xl font-extrabold text-yellow-500">Fix4You</h1>
          <h2 className="mt-4 text-2xl md:text-4xl font-semibold text-gray-800">
            Conectamos Clientes a Profissionais de Confiança
          </h2>
          <p className="mt-4 text-gray-600 text-lg">
            Encontre o especialista certo para qualquer serviço. Facilitamos o processo para si.
          </p>
        </section>

        {/* Seção de Dados Estatísticos */}
        <section className="mt-20">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-8">
              {[
                { label: '+10,000', description: 'Clientes satisfeitos' },
                { label: '+1,500', description: 'Profissionais disponíveis' },
                { label: '+20,000', description: 'Serviços Realizados' },
              ].map((stat, index) => (
                <div
                  key={index}
                  className="p-6 rounded-lg shadow-lg bg-gray-800 hover:shadow-xl transition"
                >
                  <h3 className="text-3xl font-bold text-white">{stat.label}</h3>
                  <p className="mt-4 text-white">{stat.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Seção de Áreas de Atuação */}
        <section className="bg-gray-100 bg-opacity-75 py-16 mt-20">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <h2 className="text-3xl font-bold text-gray-800 text-center">Áreas de Atuação</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-8">
              {areas.map((area, index) => (
                <div
                  key={index}
                  className={`p-6 rounded-lg shadow-lg bg-white hover:shadow-xl transition cursor-pointer ${
                    selectedArea === area ? 'border-2 border-yellow-500' : ''
                  }`}
                  onClick={() => handleSelectArea(area)}
                >
                  <h3 className="text-xl font-semibold text-gray-800">{area.title}</h3>
                  <p className="mt-4 text-gray-600">{area.description}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Seção Interativa */}
        {selectedArea && (
          <section className="bg-gray-200 py-16">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
              <h2 className="text-3xl font-bold text-gray-800">
                Serviços Disponíveis para {selectedArea.title}
              </h2>
              <p className="mt-4 text-gray-600">{selectedArea.description}</p>
              <div className="mt-8">
                <button
                  className="py-3 px-6 bg-gray-800 text-white rounded-lg shadow-md hover:bg-yellow-600 transition"
                  onClick={() => navigate('/registerClient')}
                >
                  Criar Conta
                </button>
              </div>
            </div>
          </section>
        )}
      </main>

      <Footer />
    </div>
  );
};

export default Home;
