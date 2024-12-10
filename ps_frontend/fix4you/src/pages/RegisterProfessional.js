import React, { useState, useEffect } from 'react';
import axios from 'axios'; // Importa o axios para requisições à API
import Select from 'react-select'; // Importa o react-select para dropdowns
import '../index.css';
import Footer from '../components/Footer';
import { useNavigate } from 'react-router-dom';
import Spinner from "../components/Spinner";

const RegisterProfessional = () => {
  const navigate = useNavigate();

  // Variáveis de estado para os campos do formulário
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [nif, setNif] = useState('');
  const [description, setDescription] = useState('');
  const [dateOfBirth, setDateOfBirth] = useState(''); // Usado apenas para validação
  const [languages, setLanguages] = useState([]);
  const [locationsRange, setLocationsRange] = useState(0);
  const [acceptedPayments, setAcceptedPayments] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMessage, setModalMessage] = useState('');
  const [isSuccess, setIsSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  // Estados para opções dinâmicas
  const [languageOptions, setLanguageOptions] = useState([]);
  const [paymentOptions, setPaymentOptions] = useState([]);

  // Estado para a imagem de perfil
  const [selectedImage, setSelectedImage] = useState(null);
  const [profileImage, setProfileImage] = useState(''); // Armazena a imagem em Base64

  // Estado para as opções de localização
  const [locationOptions, setLocationOptions] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);

  // Estado para as categorias (áreas de trabalho)
  const [categories, setCategories] = useState([]);
  const [selectedServices, setSelectedServices] = useState({});

  // Estado para detalhes adicionais dos serviços selecionados
  const [serviceDetails, setServiceDetails] = useState({});

  const capitalizeFirstLetter = (string) => {
    if (!string) return '';
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
  };

  // Buscar categorias da API ao montar o componente
  useEffect(() => {
    fetch('http://localhost:8080/categories')
      .then((response) => response.json())
      .then((data) => {
        setCategories(data); // Onde data é um array de objetos { id, name }
      })
      .catch((error) => console.error('Erro ao buscar categorias:', error));
  }, []);

  // Buscar idiomas da API
  useEffect(() => {
    fetch('http://localhost:8080/languages')
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((language) => ({
          value: language.id,
          label: language.name,
        }));
        setLanguageOptions(options);
      })
      .catch((error) => console.error('Erro ao buscar idiomas:', error));
  }, []);

  // Buscar métodos de pagamento da API
  useEffect(() => {
    fetch('http://localhost:8080/paymentMethods')
      .then((response) => response.json())
      .then((data) => {
        const options = data.map((method) => ({
          value: method.id,
          label: method.name,
        }));
        setPaymentOptions(options);
      })
      .catch((error) => console.error('Erro ao buscar métodos de pagamento:', error));
  }, []);

  // Buscar dados de localização ao montar o componente
  useEffect(() => {
    const fetchLocationData = async () => {
      try {
        const response = await axios.get('https://json.geoapi.pt/municipios/freguesias');
        const organizedData = response.data.map((municipio) => ({
          label: municipio.nome,
          options: municipio.freguesias.map((freguesia) => ({
            label: freguesia,
            value: `${municipio.nome}, ${freguesia}`,
          })),
        }));
        setLocationOptions(organizedData);
      } catch (error) {
        console.error('Erro ao buscar dados de localização:', error);
      }
    };

    fetchLocationData();
  }, []);

  const handleLocationChange = (selectedOption) => {
    setSelectedLocation(selectedOption.value);
  };

  const handleCheckboxChange = (event) => {
    const { name, checked } = event.target;
    setSelectedServices((prevSelectedServices) => ({
      ...prevSelectedServices,
      [name]: checked,
    }));
  };

  const handleServiceDetailChange = (category, field, value) => {
    setServiceDetails((prevDetails) => ({
      ...prevDetails,
      [category]: {
        ...prevDetails[category],
        [field]: value,
      },
    }));
  };

  const handleImageChange = (event) => {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];

      // Exibir a pré-visualização da imagem
      setSelectedImage(URL.createObjectURL(file));

      // Ler o arquivo como data URL (string Base64)
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = reader.result.split(',')[1]; // Remove o prefixo data:image/...;base64,
        setProfileImage(base64String);
      };
      reader.readAsDataURL(file);
    }
  };

  const triggerFileInput = () => {
    document.getElementById('profileimage').click();
  };

  const handleNifChange = (e) => {
    const nifValue = e.target.value;
    setNif(nifValue);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    // Validação de idade
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDifference = today.getMonth() - birthDate.getMonth();

    if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    if (age < 18) {
      setModalMessage('Deve ter pelo menos 18 anos para se registar.');
      setIsSuccess(false);
      setIsModalOpen(true);
      return;
    }

    // Construir o objeto professional conforme esperado pelo backend
    const professional = {
      email: email,
      password: password,
      dateCreation: new Date().toISOString(),
      userType: 'PROFESSIONAL',
      name: name,
      phoneNumber: phoneNumber,
      location: selectedLocation || '',
      ageValidation: true,
      rating: 0,
      profileImage: profileImage || null,
      description: description,
      nif: nif,
      languages: languages.map((id) => {
        const languageOption = languageOptions.find((option) => option.value === id);
        return { id: id, name: languageOption ? languageOption.label : '' };
      }),
      acceptedPayments: acceptedPayments.map((id) => {
        const paymentOption = paymentOptions.find((option) => option.value === id);
        return { id: id, name: paymentOption ? paymentOption.label : '' };
      }),
      strikes: 0,
      supended: false, // Verifique se o campo correto é 'supended' ou 'suspended'
      isEmailConfirmed: false,
    };

    try {
      // Enviar a requisição para criar o profissional (sem encapsulamento)
      const response = await axios.post('http://localhost:8080/professionals', professional);

     // if (response.status === 201 || response.status === 200) {
        const createdProfessional = response.data;
        const professionalId = createdProfessional.id; // Capturar o ID do profissional criado

        // Chamar função para enviar categoryDescriptions com o professionalId
        await createCategoryDescriptions(professionalId);
        setLoading(false);
        setModalMessage('Conta criada com sucesso! Verifique o seu email para validar a sua conta.');
        setIsSuccess(true);
        setIsModalOpen(true);
      //} else {
      //  setLoading(false);
      //  setModalMessage('Erro ao criar o profissional. Tente novamente.');
      //  setIsSuccess(false);
      //  setIsModalOpen(true);
     // }
    } catch (error) {
      if (error.response && error.response.data) {
      const backendError = error.response.data;

      if (typeof backendError === 'object') {
        // Handle error when it's an object with fields like `nif` or `profileImage`
        const detailedErrors = Object.entries(backendError)
            .map(([field, message]) => `${field}: ${message}`)
            .join('\n');
        setModalMessage(detailedErrors);
      } else if (typeof backendError === 'string') {
        // Handle error when it's a string
        setModalMessage(backendError);
      } else {
        // Fallback for unexpected formats
        setModalMessage('Erro inesperado. Por favor, tente novamente.');
      }
    } else {
      setModalMessage('Erro ao criar o profissional. Por favor, tente novamente.');
    }
      setLoading(false);
      setIsSuccess(false);
      setIsModalOpen(true);
    }

  };

  const createCategoryDescriptions = async (professionalId) => {
    // Construir o array de categoryDescriptions
    const categoryDescriptionsArray = categories
      .filter((category) => selectedServices[category.name])
      .map((category) => {
        return {
          professionalId: professionalId,
          category: {
            id: category.id,
            name: category.name,
          },
          chargesTravels: serviceDetails[category.name]?.chargesTravels || false,
          mediumPricePerService: serviceDetails[category.name]?.mediumPricePerService || 0,
        };
      });

    // Enviar cada categoryDescription individualmente
    for (const categoryDescription of categoryDescriptionsArray) {
      try {
        await axios.post('http://localhost:8080/categoryDescriptions', categoryDescription, {
          headers: {
            'Content-Type': 'application/json',
          },
        });
      } catch (error) {
        console.error('Erro ao criar categoryDescription:', error);
        setErrorMessage('Erro ao criar as áreas de trabalho. Por favor, tente novamente.');
      }
    }
  };
  const handleModalClose = () => {
    setIsModalOpen(false);
    if (isSuccess) {
      navigate('/Login'); // Redirect to the login page
    }
  };

  const Modal = ({ message, isSuccess, onClose }) => (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-gray-800 bg-opacity-50">
        <div className="bg-white rounded-lg shadow-lg p-6 max-w-md w-full">
          <h3 className={`text-lg font-bold ${isSuccess ? 'text-green-600' : 'text-red-600'}`}>
            {isSuccess ? 'Success' : 'Error'}
          </h3>
          <p className="mt-2 text-gray-800">{message}</p>
          <div className="mt-4 flex justify-end">
            <button
                onClick={onClose}
                className="px-4 py-2 text-white bg-gray-800 rounded-md hover:bg-yellow-600"
            >
              Close
            </button>
          </div>
        </div>
      </div>
  );


  if (loading) {
    return <Spinner message="A carregar" spinnerColor="border-yellow-600" />;
  }

  return (

    <div className="bg-gray-200">
      <div className="sm:mx-auto sm:w-full sm:max-w-lg py-12">
        <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
          Registre-se como Profissional
        </h2>
      </div>

      {isModalOpen && (
          <Modal
              message={modalMessage}
              isSuccess={isSuccess}
              onClose={handleModalClose}
          />
      )}

      <div className="p-8 bg-gray-100 shadow-lg rounded-lg bg-cover bg-center sm:mx-auto sm:w-full sm:max-w-lg mb-40">
        <form onSubmit={handleSubmit}>
          {errorMessage && (
            <div className="text-red-500 text-sm text-center">
              {errorMessage}
            </div>
          )}
          <div className="space-y-12">
            {/* Informação Pessoal */}
            <div className="border-b border-gray-900/10 pb-12">
              <h2 className="text-base font-semibold leading-7 text-gray-900">Informação Pessoal</h2>

              <div className="mt-10 grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6">
                {/* Nome Completo */}
                <div className="sm:col-span-6">
                  <label htmlFor="name" className="block text-sm font-medium leading-6 text-gray-900">
                    Nome Completo
                  </label>
                  <div className="mt-2">
                    <input
                      type="text"
                      name="name"
                      id="name"
                      autoComplete="name"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                    />
                  </div>
                </div>

                {/* Email */}
                <div className="sm:col-span-6">
                  <label htmlFor="email" className="block text-sm font-medium leading-6 text-gray-900">
                    Email
                  </label>
                  <div className="mt-2">
                    <input
                      type="email"
                      name="email"
                      id="email"
                      autoComplete="email"
                      required
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                    />
                  </div>
                </div>

                {/* Password */}
                <div className="sm:col-span-6">
                  <label htmlFor="password" className="block text-sm font-medium leading-6 text-gray-900">
                    Password
                  </label>
                  <div className="mt-2">
                    <input
                      type="password"
                      name="password"
                      id="password"
                      autoComplete="new-password"
                      required
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                    />
                  </div>
                </div>

                {/* Número de Telefone */}
                <div className="sm:col-span-6">
                  <label htmlFor="phoneNumber" className="block text-sm font-medium leading-6 text-gray-900">
                    Número de Telefone
                  </label>
                  <div className="mt-2">
                    <input
                      type="tel"
                      name="phoneNumber"
                      id="phoneNumber"
                      autoComplete="tel"
                      required
                      value={phoneNumber}
                      onChange={(e) => setPhoneNumber(e.target.value)}
                      className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                    />
                  </div>
                </div>

                {/* NIF */}
                <div className="sm:col-span-6">
                  <label htmlFor="nif" className="block text-sm font-medium leading-6 text-gray-900">
                    NIF
                  </label>
                  <div className="mt-2">
                    <input
                      type="text"
                      name="nif"
                      id="nif"
                      required
                      value={nif}
                      onChange={handleNifChange}
                      className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                    />
                  </div>
                </div>

                {/* Data de Nascimento (usado apenas para validação) */}
                <div className="sm:col-span-6">
                  <label htmlFor="dateOfBirth" className="block text-sm font-medium leading-6 text-gray-900">
                    Data de Nascimento
                  </label>
                  <div className="mt-2">
                    <input
                      type="date"
                      name="dateOfBirth"
                      id="dateOfBirth"
                      required
                      value={dateOfBirth}
                      onChange={(e) => setDateOfBirth(e.target.value)}
                      className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                    />
                  </div>
                </div>

                {/* Idiomas */}
                <div className="sm:col-span-6">
                  <label className="block text-sm font-medium leading-6 text-gray-900">
                    Idiomas
                  </label>
                  <Select
                    isMulti
                    options={languageOptions}
                    value={languageOptions.filter((option) => languages.includes(option.value))}
                    onChange={(selectedOptions) => setLanguages(selectedOptions.map((option) => option.value))}
                    className="mt-2"
                  />
                </div>

                {/* Métodos de Pagamento Aceitos */}
                <div className="sm:col-span-6">
                  <label className="block text-sm font-medium leading-6 text-gray-900">
                    Métodos de Pagamento Aceitos
                  </label>
                  <Select
                    isMulti
                    options={paymentOptions}
                    value={paymentOptions.filter((option) => acceptedPayments.includes(option.value))}
                    onChange={(selectedOptions) => setAcceptedPayments(selectedOptions.map((option) => option.value))}
                    className="mt-2"
                  />
                </div>

                {/* Descrição */}
                <div className="col-span-full">
                  <label htmlFor="description" className="block text-sm font-medium leading-6 text-gray-900">
                    Descrição
                  </label>
                  <div className="mt-2">
                    <textarea
                      name="description"
                      id="description"
                      rows="3"
                      required
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      className="block w-full rounded-md border-0 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:py-1.5 sm:text-sm sm:leading-6 pl-3"
                    ></textarea>
                  </div>
                  <p className="mt-3 text-sm leading-6 text-gray-600">
                    Escreva uma breve descrição sobre você e os seus serviços.
                  </p>
                </div>

                {/* Imagem de Perfil */}
                <div className="col-span-full">
                  <label htmlFor="photo" className="block text-sm font-medium leading-6 text-gray-900">Foto</label>
                  <div className="mt-2 flex items-center gap-x-3">
                    {selectedImage ? (
                      <img src={selectedImage} alt="Profile" className="h-12 w-12 rounded-full" />
                    ) : (
                      <svg
                        className="h-12 w-12 text-gray-300"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                        aria-hidden="true"
                      >
                        <path
                          fillRule="evenodd"
                          d="M18.685 19.097A9.723 9.723 0 0 0 21.75 12c0-5.385-4.365-9.75-9.75-9.75S2.25 6.615
                          2.25 12a9.723 9.723 0 0 0 3.065 7.097A9.716 9.716 0 0 0 12 21.75a9.716 9.716 0 0 0
                          6.685-2.653Zm-12.54-1.285A7.486 7.486 0 0 1 12 15a7.486 7.486 0 0 1
                          5.855 2.812A8.224 8.224 0 0 1 12 20.25a8.224 8.224 0 0 1-5.855-2.438ZM15.75
                          9a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z"
                          clipRule="evenodd"
                        />
                      </svg>
                    )}
                    <button
                      type="button"
                      onClick={triggerFileInput}
                      className="rounded-md bg-white px-2.5 py-1.5 text-sm font-semibold text-gray-900 shadow-sm
                                 ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                    >
                      Alterar
                    </button>
                    <input
                      type="file"
                      id="profileimage"
                      name="profileimage"
                      accept="image/*"
                      onChange={handleImageChange}
                      style={{ display: 'none' }}
                    />
                  </div>
                </div>

                {/* Localização */}
                <div className="sm:col-span-6">
                  <h3 className="block text-sm font-medium leading-6 text-gray-900">Localização</h3>
                  <Select
                    options={locationOptions}
                    onChange={handleLocationChange}
                    placeholder="Selecione a freguesia"
                    value={locationOptions
                      .flatMap((option) => option.options)
                      .find((option) => option.value === selectedLocation)
                    }
                    className="mt-2"
                  />
                </div>

                {/* Distância de Atuação (locationsRange) 
                <div className="sm:col-span-6">
                  <label htmlFor="locationsRange" className="block text-sm font-medium leading-6 text-gray-900">
                    Distância de Atuação (km)
                  </label>
                  <div className="mt-2">
                    <input
                      type="number"
                      name="locationsRange"
                      id="locationsRange"
                      required
                      value={locationsRange}
                      onChange={(e) => setLocationsRange(parseInt(e.target.value))}
                      className="block w-24 rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                 ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                 focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3 pr-1"
                    />
                  </div>
                </div>
                */}

              </div>
            </div>

            {/* Área de Trabalho */}
            <div className="border-b border-gray-900/10 pb-12">
              <h2 className="text-base font-semibold leading-7 text-gray-900">Área de Trabalho</h2>
              <p className="mt-1 text-sm leading-6 text-gray-600">
                Escolha uma ou mais áreas de trabalho e complete a informação adicional.
              </p>

              <div className="space-y-10">
                <fieldset>
                  <div className="mt-6 space-y-6">
                    {categories.map((category) => (
                      <div key={category.id}>
                        <div className="relative flex gap-x-3">
                          <div className="flex h-6 items-center">
                            <input
                              id={category.id}
                              name={category.name}
                              type="checkbox"
                              className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-600"
                              onChange={handleCheckboxChange}
                            />
                          </div>
                          <div className="text-sm leading-6">
                            <label htmlFor={category.id} className="font-medium text-gray-900">
                              {capitalizeFirstLetter(category.name)}
                            </label>
                          </div>
                        </div>
                        {selectedServices[category.name] && (
                          <div className="ml-6">
                            {/* Detalhes adicionais */}
                            <div>
                              <label className="block text-sm font-medium text-gray-900">Cobra por Deslocação?</label>
                              <input
                                type="checkbox"
                                name={`${category.name}-deslocacao`}
                                className="h-4 w-4 text-indigo-600"
                                onChange={(e) =>
                                  handleServiceDetailChange(category.name, 'chargesTravels', e.target.checked)
                                }
                              />
                            </div>
                            <div>
                              <label className="block text-sm font-medium text-gray-900">Preço Médio</label>
                              <input
                                type="number"
                                name={`${category.name}-preco`}
                                className="block w-20 rounded-md border-0 py-1.5 text-gray-900 shadow-sm
                           ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600 pl-3 pr-1"
                                onChange={(e) =>
                                  handleServiceDetailChange(
                                    category.name,
                                    'mediumPricePerService',
                                    parseFloat(e.target.value)
                                  )
                                }
                              />
                            </div>
                          </div>
                        )}

                      </div>
                    ))}

                  </div>
                </fieldset>
              </div>
            </div>

            <div className="mt-6">
              <h2>Termos e Condições de Serviço</h2>
              <br></br>
              <div className="border p-4 rounded-md max-h-40 overflow-y-auto bg-gray-100">
                <p className="text-sm text-gray-700">

                  Bem-vindo à Fix4You! Ao utilizar a nossa plataforma, concorda com os Termos e Condições abaixo. Por favor, leia atentamente antes de utilizar os nossos serviços.

                  1. Aceitação dos Termos
                  Ao aceder ou utilizar a plataforma Fix4You, concorda com estes Termos e Condições e com a nossa Política de Privacidade. Se não concorda com alguma parte dos Termos, não utilize os nossos serviços.

                  2. Descrição do Serviço
                  A Fix4You conecta clientes a profissionais para a contratação de serviços específicos, como limpeza, eletricidade, pintura e outros. A plataforma serve apenas como intermediário entre profissionais e clientes e não assume qualquer responsabilidade pelos serviços prestados pelos profissionais.

                  3. Registo e Conta
                  3.1. Elegibilidade: Para se registar, deve ter pelo menos 18 anos de idade.
                  3.2. Informações de Registo: Concorda em fornecer informações precisas e completas durante o processo de registo e em manter essas informações atualizadas.
                  3.3. Segurança da Conta: É responsável por manter a segurança da sua conta e palavra-passe. A Fix4You não será responsável por qualquer perda ou dano resultante do uso não autorizado da sua conta.

                  4. Utilização da Plataforma
                  4.1. Clientes: Os clientes podem utilizar a plataforma para solicitar serviços de profissionais. Ao contratar um profissional, o cliente concorda em pagar pelo serviço prestado e cumprir os termos acordados com o profissional.
                  4.2. Profissionais: Os profissionais podem utilizar a plataforma para oferecer os seus serviços e comprometem-se a realizar os serviços com profissionalismo e qualidade.
                  4.3. Restrições de Uso: Concorda em não utilizar a plataforma para fins ilícitos, incluindo, mas não se limitando a, falsificação de identidade, spam, fraude ou qualquer outro uso que infrinja as leis aplicáveis.

                  5. Pagamentos e Taxas
                  A plataforma pode cobrar taxas pelos serviços de conexão entre clientes e profissionais. Todas as taxas e comissões, se aplicáveis, serão descritas no momento da contratação ou em outros materiais de comunicação. A Fix4You reserva-se o direito de modificar as taxas e métodos de pagamento a qualquer momento.

                  6. Cancelamento e Reembolso
                  O cancelamento e o reembolso devem ser acordados entre o cliente e o profissional. A Fix4You não oferece reembolsos, pois atua apenas como intermediário e não é responsável pela execução do serviço.

                  7. Limitação de Responsabilidade
                  A Fix4You não é responsável pela qualidade, segurança, legalidade ou pontualidade dos serviços realizados pelos profissionais. A plataforma não será responsabilizada por quaisquer danos diretos, indiretos, incidentais ou consequenciais relacionados com o uso dos serviços prestados pelos profissionais.

                  8. Privacidade
                  A Fix4You recolhe e utiliza informações pessoais conforme descrito na nossa Política de Privacidade. Ao utilizar a plataforma, concorda com a recolha e o uso de informações de acordo com esta política.

                  9. Direitos de Propriedade Intelectual
                  Todos os direitos, títulos e interesses relacionados com a plataforma e o seu conteúdo são propriedade da Fix4You ou dos seus licenciadores. É proibido reproduzir, modificar, distribuir ou criar trabalhos derivados sem autorização prévia.

                  10. Modificações aos Termos
                  A Fix4You reserva-se o direito de modificar estes Termos e Condições a qualquer momento. Quaisquer alterações serão comunicadas e entrarão em vigor imediatamente após a publicação. O uso contínuo da plataforma após tais mudanças constitui aceitação das modificações.

                  11. Rescisão
                  A Fix4You reserva-se o direito de suspender ou encerrar a sua conta e o acesso à plataforma a qualquer momento, sem aviso prévio, caso viole estes Termos.

                  12. Lei Aplicável e Foro Competente
                  Estes Termos e Condições são regidos pelas leis de Portugal. Em caso de controvérsia, concorda em submeter-se à jurisdição dos tribunais de Coimbra.

                  13. Contacto
                  Caso tenha dúvidas sobre estes Termos e Condições, entre em contacto connosco.

                </p>
              </div>
              <div className="mt-4 flex items-center">
                <input
                  type="checkbox"
                  id="terms"
                  required
                  className="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                />
                <label htmlFor="terms" className="ml-2 block text-sm text-gray-900">
                  Eu li e aceito os Termos e Condições
                </label>
              </div>
            </div>



            {/* Mensagem de Erro */}
            {errorMessage && (
              <div className="text-red-500 text-sm text-center">
                {errorMessage}
              </div>
            )}

            {/* Botões de Envio */}
            <div className="mt-6 flex items-center justify-end gap-x-6">
              <button type="button" className="text-sm font-semibold leading-6 text-gray-900">
                Cancelar
              </button>
              <button
                type="submit"
                className="rounded-md bg-gray-800 px-3 py-2 text-sm font-semibold text-white shadow-sm
                           hover:bg-yellow-600 focus-visible:outline focus-visible:outline-2
                           focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
              >
                Registar
              </button>
            </div>
          </div>
        </form>
      </div>

      <Footer />
    </div>
  );
};

export default RegisterProfessional;
