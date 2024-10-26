import React from 'react';
import '../index.css';
import backgroundImage from '../images/background.png';
import Footer from '../components/Footer';

const Home = () => {
  return (
    <div style={{ backgroundImage: `url(${backgroundImage})` }}>



      <main className="bg-white bg-opacity-75 py-12 ">

        <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center py-12 mt-16" >
          <h2 className="text-3xl font-bold text-gray-900">Como Funciona?</h2>
          <p className="mt-4 text-gray-600">Descreva a sua necessidade, receba propostas, e escolha o melhor profissional.</p>

          {/* Barra de Pesquisa */}
          <div className="mt-8 flex justify-center">
            <input
              type="text"
              placeholder="O que precisa fazer?"
              className="px-4 py-2 border rounded-l-lg w-full max-w-lg"
            />
            <button className="px-6 py-2 bg-gray-800 text-white rounded-r-lg hover:bg-blue-600 transition">
              Pesquisar
            </button>
          </div>
        </section>




        <section className="bg-white py-12 bg-opacity-75 py-12 mt-16" >
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
