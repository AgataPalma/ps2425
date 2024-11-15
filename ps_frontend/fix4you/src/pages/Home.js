import React from 'react';
import '../index.css';
import backgroundImage from '../images/background.png';
import Footer from '../components/Footer';

const Home = () => {
  return (
    <div style={{ backgroundImage: `url(${backgroundImage})` }}>
      <main className="bg-white bg-opacity-75 py-12">
        {/* Seção de Slogan */}
        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center mt-16">
          <h1 className="text-5xl font-bold text-yellow-500">Fix4You</h1>
          <br></br>
          <h2 className="text-4xl font-bold text-gray-900">Conectamos Clientes a Profissionais de Confiança</h2>
          <p className="mt-4 text-gray-600 text-lg">
            Encontre o especialista certo para qualquer serviço. Facilitamos o processo para si.
          </p>
          <br></br>
        </section>

        {/* Seção de Dados Estatísticos */}
        <section className="">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-8">
              <div className="border p-6 rounded-lg shadow-lg bg-gray-50">
                <h3 className="text-2xl font-bold text-gray-800">+10,000</h3>
                <p className="mt-4 text-gray-800">Clientes satisfeitos</p>
              </div>

              <div className="border p-6 rounded-lg shadow-lg bg-gray-50">
                <h3 className="text-2xl font-bold text-gray-800">+1,500</h3>
                <p className="mt-4 text-gray-800">Profissionais disponíveis</p>
              </div>

              <div className="border p-6 rounded-lg shadow-lg bg-gray-50">
                <h3 className="text-2xl font-bold text-gray-800">+20,000</h3>
                <p className="mt-4 text-gray-800">Serviçoes Realizados</p>
              </div>
            </div>
          </div>
        </section>

        {/* Seção de Áreas de Atuação */}
        <section className="bg-white py-12 bg-opacity-75 mt-20">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <h2 className="text-3xl font-bold text-gray-800 text-center">Áreas de Atuação</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-8">
              <div className="border p-6 rounded-lg shadow-lg bg-gray-50">
                <h3 className="text-xl font-bold text-gray-800">Reparações Domésticas</h3>
                <p className="mt-4 text-gray-800">Encontre profissionais para eletricidade, canalização, e mais.</p>
              </div>

              <div className="border p-6 rounded-lg shadow-lg bg-gray-50">
                <h3 className="text-xl font-bold text-gray-800">Eventos</h3>
                <p className="mt-4 text-gray-800">Organize o evento perfeito com serviços de catering, fotografia, e decoração.</p>
              </div>

              <div className="border p-6 rounded-lg shadow-lg bg-gray-50">
                <h3 className="text-xl font-bold text-gray-800">Mudanças e Limpezas</h3>
                <p className="mt-4 text-gray-800">Contrate empresas para mudanças e serviços de limpeza.</p>
              </div>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
};

export default Home;
