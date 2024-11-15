import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Select from 'react-select';
import Footer from "../components/Footer";
import axiosInstance from "../components/axiosInstance";
import axios from 'axios';

function RequestServiceGeneric({ id }) {
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [location, setLocation] = useState('');
  const [category, setCategory] = useState('');
  const [description, setDescription] = useState('');
  const [locationOptions, setLocationOptions] = useState([]);
  const [languages, setLanguages] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchLocationData = async () => {
      try {
        const response = await axios.get('https://json.geoapi.pt/municipios/freguesias');

        const organizedData = response.data.map((municipio) => ({
          label: municipio.nome,
          options: municipio.freguesias.map((freguesia) => ({
            label: freguesia,
            value: `${municipio.nome}, ${freguesia}`
          }))
        }));

        setLocationOptions(organizedData);
      } catch (error) {
        console.error('Error fetching location data:', error);
      }
    };

    fetchLocationData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const requestBody = {
      clientId: id,
      professionalId: null,
      price: 0,
      address: "0",
      postalCode: "0000-000",
      category: category,
      description: description,
      title: title,
      location: location,
      languages: languages.map(language => language.value),
      state: 0
    };

    try {
      const response = await axiosInstance.post('/services', requestBody);

      if (response.status === 200) {
        const serviceId = response.data;
        navigate(`/ScheduleAppointments?clientId=${id}&professionalId=${null}&serviceId=${serviceId}`);

      } else {
        console.log("Failed to create service:",  error.response?.data?.message);
        setError(
            <>
              Falha ao criar o serviço. Por favor, tente novamente.<br />
              <br />
              Todos os campos são obrigatórios!
            </>
        );
      }
    } catch (error) {
      console.error("Error creating service:", error);
      setError(
          <>
            Falha ao criar o serviço. Por favor, tente novamente.<br />
            <br />
            Todos os campos são obrigatórios!
          </>
      );
    }
  };

  const languageOptions = [
    { value: 'PORTUGUESE', label: 'Português' },
    { value: 'FRENCH', label: 'Francês' },
    { value: 'ENGLISH', label: 'Inglês' },
    { value: 'SPANISH', label: 'Espanhol' }
  ];

  return (
      <div className="h-screen bg-gray-200 text-black font-sans">
        <main className="flex-grow bg-gray-800 bg-opacity-15 flex items-center justify-center">
          <div className="relative w-full h-full bg-cover bg-center">
            <div className="absolute inset-0"></div>
            <div className="relative z-10 flex justify-center items-center h-full m-8">
              <div className="bg-white bg-opacity-80 p-8 rounded-lg max-w-lg w-full">
                <h2 className="text-2xl text-yellow-600 font-bold text-center mb-6 underline">Pedir Um Serviço</h2>
                {error && (
                    <div className="mb-4 p-2 bg-red-200 text-red-800 text-center rounded">
                      {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                  <div className="mb-4">
                    <label className="block text-black font-semibold mb-2">Titulo *</label>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="eg. Preciso de um canalizador"
                        className="w-full p-2 border-b-2 border-black placeholder-black placeholder-opacity-80 bg-transparent focus:outline-none focus:border-black"
                    />
                  </div>

                  <div className="mb-4">
                    <label className="block text-black font-semibold mb-2">Localização *</label>
                    <Select
                        options={locationOptions}
                        onChange={(selectedOption) => setLocation(selectedOption.value)}
                        placeholder="Selecione a sua freguesia"
                        className="w-full p-2 bg-white bg-opacity-50 focus:outline-none focus:border-black"
                        styles={{
                          control: (provided) => ({
                            ...provided,
                            border: 'none',
                          }),
                        }}
                    />
                  </div>

                  <div className="mb-4">
                    <label className="block text-black font-semibold mb-2">Categoria *</label>
                    <select
                        value={category}
                        onChange={(e) => setCategory(parseInt(e.target.value))}
                        className="w-full p-2 placeholder-gray-600 border-b-2 border-black bg-white bg-opacity-50 focus:outline-none focus:border-black"
                    >
                      <option value="">Selecionar</option>
                      <option value="0">Limpeza</option>
                      <option value="1">Canalizador</option>
                      <option value="2">Eletricista</option>
                      <option value="3">Jardineiro</option>
                      <option value="4">Pintor</option>
                      <option value="5">Outro</option>
                    </select>
                  </div>

                  <div className="mb-4">
                    <label className="block text-black font-semibold mb-2">Idioma(s) *</label>
                    <Select
                        isMulti  // Permitir múltiplas seleções
                        options={languageOptions}
                        onChange={(selectedOptions) => setLanguages(selectedOptions)}  // Armazenar múltiplos idiomas
                        placeholder="Selecione o(s) idioma(s)"
                        className="w-full p-2 bg-white bg-opacity-50 focus:outline-none focus:border-black"
                        styles={{
                          control: (provided) => ({
                            ...provided,
                            border: 'none',
                          }),
                        }}
                    />
                  </div>

                  <div className="mb-6">
                    <label className="block text-black font-semibold mb-2">Descrição *</label>
                    <textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        className="w-full bg-white bg-opacity-50 mt-2 p-2 h-20 placeholder-black placeholder-opacity-80 border border-black"
                        placeholder="Descrição"
                    ></textarea>
                  </div>

                  <button
                      type="submit"
                      className="w-full px-6 py-3 bg-yellow-600 text-white rounded-lg hover:bg-yellow-500 transition"
                  >
                    Avançar
                  </button>
                </form>
              </div>
            </div>
          </div>
        </main>
        <Footer />
      </div>
  );
}

export default RequestServiceGeneric;
