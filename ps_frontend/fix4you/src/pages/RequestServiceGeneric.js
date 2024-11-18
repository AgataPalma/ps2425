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
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [description, setDescription] = useState('');
  const [locationOptions, setLocationOptions] = useState([]);
  const [languages, setLanguages] = useState([]);
  const [error, setError] = useState('');
  const [selectedLanguages, setSelectedLanguages] = useState([]);

  const handleLanguagesMethodClick = (language) => {
    setSelectedLanguages((prev) =>
        prev.includes(language)
            ? prev.filter((lang) => lang !== language)
            : [...prev, language]
    );
  };

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

        const languagesResponse = await axiosInstance.get('/languages');
        const languagesData = languagesResponse.data.map((languages) => ({
          value: languages.id,
          label: languages.name,
        }));
        setLanguages(languagesData);

        const fetchCategories = async () => {
          try {
            const response = await axiosInstance.get('/categories');
            const categoryData = response.data.map((category) => ({
              value: category.id,
              label: category.name,
            }));
            setCategories(categoryData);
          } catch (error) {
            console.error('Erro ao buscar categorias:', error);
          }
        };

        setLocationOptions(organizedData);
        fetchCategories();
      } catch (error) {
        console.error('Erro ao buscar dados de localização:', error);
      }
    };

    fetchLocationData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedCategory) {
      setError("A categoria é obrigatória.");
      return;
    }

    const requestBody = {
      clientId: id,
      professionalId: null,
      price: 0,
      address: "0",
      postalCode: "0000-000",
      category: {
        id: selectedCategory.value,
        name: selectedCategory.label
      },
      description: description,
      title: title,
      location: location,
      languages: selectedLanguages.map(lang => ({
        id: lang.value,
        name: lang.label
      })),
      state: 0
    };

    try {
      const response = await axiosInstance.post('/services', requestBody);

      if (response.status === 200) {
        const serviceId = response.data;
        navigate(`/ScheduleAppointments?clientId=${id}&professionalId=${null}&serviceId=${serviceId}`);
      } else {
        console.log("Falha ao criar o serviço:", error.response?.data?.message);
        setError(
            <>
              Falha ao criar o serviço. Por favor, tente novamente.<br />
              Todos os campos são obrigatórios!
            </>
        );
      }
    } catch (error) {
      console.error("Erro ao criar o serviço:", error);
      setError(
          <>
            Falha ao criar o serviço. Por favor, tente novamente.<br />
            Todos os campos são obrigatórios!
          </>
      );
    }
  };


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
                    <Select
                        options={categories}
                        onChange={(selectedOption) => setSelectedCategory(selectedOption)}
                        placeholder="Selecione a categoria"
                        className="w-full p-2 bg-white bg-opacity-50 focus:outline-none focus:border-black"
                        styles={{
                          control: (provided) => ({
                            ...provided,
                            border: 'none',
                          }),
                        }}
                    />
                  </div>

                  <div className="mt-4">
                    <h4 className="font-medium">Linguagens</h4>
                    <div className="flex flex-wrap gap-2">
                      {languages.map((language) => (
                          <button
                              key={language.value}
                              onClick={() => handleLanguagesMethodClick(language.label)}
                              className={`px-4 py-2 rounded-full ${
                                  selectedLanguages.includes(language.label)
                                      ? 'bg-yellow-600 text-white'
                                      : 'bg-gray-300 text-gray-700'
                              }`}
                          >
                            {language.label}
                          </button>
                      ))}
                    </div>
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
        <Footer/>
      </div>
  );
}

export default RequestServiceGeneric;
