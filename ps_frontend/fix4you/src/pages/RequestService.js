import React from 'react';
import { useNavigate } from 'react-router-dom';
import Footer from "../components/Footer";

function RequestService() {
  const navigate = useNavigate();
  return (

      <div className="h-screen bg-gray-200 text-black font-sans">
          <main className="flex-grow bg-gray-800 bg-opacity-15 flex items-center justify-center">
            <div
                className="relative w-full h-full bg-cover bg-center"
            >
              <div className="absolute inset-0"></div>
              <div className="relative z-10 flex justify-center items-center h-full m-8">
                <div className="bg-white bg-opacity-80 p-8 rounded-lg max-w-lg w-full">
                  <h2 className="text-2xl text-yellow-600 font-bold text-center mb-6 underline">Pedir Um Serviço</h2>
                  <form>
                    <div className="mb-4">
                      <label className="block text-black font-semibold mb-2">Titulo *</label>
                      <input
                          type="text"
                          placeholder="eg. Preciso de uma canalizador"
                          className="w-full p-2 border-b-2 border-black placeholder-black placeholder-opacity-80 bg-transparent focus:outline-none focus:border-black"
                      />
                    </div>
                    <div className="mb-4">
                      <label className="block text-black font-semibold mb-2">Localização *</label>
                      <input
                          type="text"
                          placeholder="eg Lisboa, Portugal"
                          className="w-full p-2 border-b-2 border-black bg-transparent placeholder-black placeholder-opacity-80 focus:outline-none focus:border-black"
                      />
                    </div>
                    <div className="mb-4">
                      <label className="block text-black font-semibold mb-2">Categoria *</label>
                      <select
                          className="w-full p-2 placeholder-gray-600 border-b-2 border-black bg-white bg-opacity-50 focus:outline-none focus:border-black">
                        <option>Selecionar</option>
                        <option>Canalizador</option>
                        <option>Eletricista</option>
                        <option>Limpeza</option>
                      </select>
                    </div>
                    <div className="mb-6">
                      <label className="block text-black font-semibold mb-2">Descrição *</label>
                      <textarea
                          className="w-full bg-white bg-opacity-50 mt-2 p-2 h-20 placeholder-black placeholder-opacity-80 border border-black"
                          placeholder="Descrição"
                      ></textarea>
                    </div>
                    <button
                        onClick={() => navigate('/PrincipalPageClient')}
                        className="w-full px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition">
                      Publicar
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </main>
        <Footer/>
      </div>
  );
}

export default RequestService;
