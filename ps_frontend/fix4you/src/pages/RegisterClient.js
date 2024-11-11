import React, { useState, useEffect } from 'react';
import axios from 'axios'; // Import axios for API requests
import Select from 'react-select'; // Import react-select
import '../index.css';
import Footer from '../components/Footer';
import { useNavigate } from 'react-router-dom';

const RegisterClient = () => {
    const navigate = useNavigate();

    // State variables for form fields
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [dateOfBirth, setDateOfBirth] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // State for location dropdown and options
    const [locationOptions, setLocationOptions] = useState([]);
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [editMode, setEditMode] = useState(true); // For toggling between view and edit

    const profileImage = "";

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
                console.error('Error fetching location data:', error);
            }
        };

        fetchLocationData();
    }, []);

    const handleLocationChange = (selectedOption) => {
        setSelectedLocation(selectedOption.value);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        // Age validation
        const today = new Date();
        const birthDate = new Date(dateOfBirth);
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDifference = today.getMonth() - birthDate.getMonth();

        if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
            age--;
        }

        if (age < 18) {
            setErrorMessage('You must be at least 18 years old to register.');
            return;
        }

        // Construct the request body
        const requestBody = {
            name: name,
            email: email,
            password: password,
            phoneNumber: phoneNumber,
            location: selectedLocation,
            profileImage: profileImage,
            ageValidation: true,
            dateOfBirth: dateOfBirth,
            userType: "CLIENT",
            IsEmailConfirmed: true,
            dateCreation: new Date().toISOString(),
        };

        try {
            const response = await fetch('http://localhost:8080/clients', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody),
            });

            if (response.ok) {
                alert('Conta criada com sucesso!');
                navigate('/Login');
            } else {
                const errorData = await response.json();
                console.error('Error:', errorData);
                setErrorMessage('An error occurred during registration. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
            setErrorMessage('An error occurred during registration. Please try again.');
        }
    };

    return (
        <div className="bg-gray-200">
            <div className="sm:mx-auto sm:w-full sm:max-w-sm py-12">
                <h2 className="text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                    Registar para contactar profissional
                </h2>
            </div>

            <div className="p-8 w-1/3 bg-gray-100 shadow-lg rounded-lg bg-cover bg-center sm:mx-auto sm:w-full sm:max-w-sm mb-40">
                <form className="space-y-6" onSubmit={handleSubmit}>
                    {errorMessage && (
                        <div className="text-red-500 text-sm text-center">
                            {errorMessage}
                        </div>
                    )}
                    <div>
                        <label htmlFor="name" className="block text-sm font-medium leading-6 text-gray-900">
                            Nome
                        </label>
                        <div className="mt-2">
                            <input
                                id="name"
                                name="name"
                                type="text"
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

                    <div>
                        <label htmlFor="email" className="block text-sm font-medium leading-6 text-gray-900">
                            Email
                        </label>
                        <div className="mt-2">
                            <input
                                id="email"
                                name="email"
                                type="email"
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

                    <div>
                        <label htmlFor="password" className="block text-sm font-medium leading-6 text-gray-900">
                            Password
                        </label>
                        <div className="mt-2">
                            <input
                                id="password"
                                name="password"
                                type="password"
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

                    <div>
                        <label htmlFor="phoneNumber" className="block text-sm font-medium leading-6 text-gray-900">
                            Telefone
                        </label>
                        <div className="mt-2">
                            <input
                                id="phoneNumber"
                                name="phoneNumber"
                                type="tel"
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

                    <div>
                        <label htmlFor="dateOfBirth" className="block text-sm font-medium leading-6 text-gray-900">
                            Data de Nascimento
                        </label>
                        <div className="mt-2">
                            <input
                                id="dateOfBirth"
                                name="dateOfBirth"
                                type="date"
                                required
                                value={dateOfBirth}
                                onChange={(e) => setDateOfBirth(e.target.value)}
                                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset
                                           ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset
                                           focus:ring-indigo-600 sm:text-sm sm:leading-6 pl-3"
                            />
                        </div>
                    </div>

                    <div>
                        <h3 className="block text-sm font-medium leading-6 text-gray-900">Localização</h3>
                        {editMode ? (
                            <Select
                                options={locationOptions}
                                onChange={handleLocationChange}
                                placeholder="Seleccione a freguesia"
                                value={locationOptions
                                    .flatMap(option => option.options)
                                    .find(option => option.value === selectedLocation)
                                }
                                className="mt-2"
                            />
                        ) : (
                            <p className="text-gray-600">{selectedLocation || "Sem localização definida"}</p>
                        )}
                    </div>

                    <br />

                    <div className="mt-6">
                        <h2>Termos e Condições de Serviço</h2>
                        <br></br>
                        <div className="border p-4 rounded-md max-h-40 overflow-y-auto bg-gray-100">
                            <p className="text-sm text-gray-700">

                                Bem-vindo à Fix4You! Ao utilizar nossa plataforma, você concorda com os Termos e Condições abaixo. Por favor, leia atentamente antes de utilizar nossos serviços.

                                1. Aceitação dos Termos
                                Ao acessar ou utilizar a plataforma Fix4You você concorda com estes Termos e Condições e com nossa Política de Privacidade. Se você não concorda com qualquer parte dos Termos, não utilize nossos serviços.

                                2. Descrição do Serviço
                                A Fix4You conecta clientes a profissionais para a contratação de serviços específicos, como limpeza, eletricidade, pintura e outros. A plataforma serve apenas como intermediário entre profissionais e clientes e não assume qualquer responsabilidade pelos serviços prestados pelos profissionais.
                                3. Cadastro e Conta
                                3.1. Elegibilidade: Para se cadastrar, você deve ter pelo menos 18 anos de idade.
                                3.2. Informações de Cadastro: Você concorda em fornecer informações precisas e completas durante o processo de cadastro e manter essas informações atualizadas.  3.3. Segurança da Conta: Você é responsável por manter a segurança de sua conta e senha. A Fix4You não será responsável por qualquer perda ou dano resultante do uso não autorizado da sua conta.

                                4. Uso da Plataforma
                                4.1. Clientes: Os clientes podem utilizar a plataforma para solicitar serviços de profissionais. Ao contratar um profissional, o cliente concorda em pagar pelo serviço prestado e cumprir os termos acordados com o profissional. 4.2. Profissionais: Profissionais podem utilizar a plataforma para oferecer seus serviços e se comprometem a realizar os serviços com profissionalismo e qualidade.  4.3. Restrições de Uso: Você concorda em não utilizar a plataforma para fins ilícitos, incluindo, mas não se limitando a, falsificação de identidade, spam, fraude, ou qualquer outro uso que infrinja as leis aplicáveis.

                                5. Pagamentos e Taxas
                                A plataforma pode cobrar taxas pelos serviços de conexão entre clientes e profissionais. Todas as taxas e comissões, se aplicáveis, serão descritas no momento da contratação ou em outros materiais de comunicação. A Fix4You reserva-se o direito de modificar as taxas e métodos de pagamento a qualquer momento.

                                6. Cancelamento e Reembolso
                                O cancelamento e reembolso devem ser acordados entre o cliente e o profissional. A Fix4You não oferece reembolsos, pois atua apenas como intermediário e não é responsável pela execução do serviço.

                                7. Limitação de Responsabilidade
                                A Fix4You não é responsável pela qualidade, segurança, legalidade ou pontualidade dos serviços realizados pelos profissionais. A plataforma não será responsabilizada por quaisquer danos diretos, indiretos, incidentais ou consequentes relacionados ao uso dos serviços prestados pelos profissionais.

                                8. Privacidade
                                A Fix4You coleta e utiliza informações pessoais conforme descrito em nossa Política de Privacidade. Ao utilizar a plataforma, você concorda com a coleta e o uso de informações de acordo com esta política.

                                9. Direitos de Propriedade Intelectual
                                Todos os direitos, títulos e interesses relacionados à plataforma e seu conteúdo são de propriedade da Fix4You ou de seus licenciadores. É proibido reproduzir, modificar, distribuir ou criar trabalhos derivados sem autorização prévia.

                                10. Modificações aos Termos
                                A Fix4You reserva-se o direito de modificar estes Termos e Condições a qualquer momento. Quaisquer alterações serão comunicadas e entrarão em vigor imediatamente após a publicação. O uso contínuo da plataforma após tais mudanças constitui aceitação das modificações.

                                11. Rescisão
                                A Fix4You reserva-se o direito de suspender ou encerrar sua conta e o acesso à plataforma a qualquer momento, sem aviso prévio, caso você viole estes Termos.

                                12. Lei Aplicável e Foro Competente
                                Estes Termos e Condições são regidos pelas leis de Portual. Em caso de controvérsia, você concorda em se submeter à jurisdição dos tribunais de Coimbra.

                                13. Contato
                                Caso tenha dúvidas sobre estes Termos e Condições, entre em contato conosco

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

                    <div>
                        <button
                            type="submit"
                            className="flex w-full justify-center rounded-md bg-gray-800 px-3 py-1.5 text-sm font-semibold
                                       leading-6 text-white shadow-sm hover:bg-yellow-600 focus-visible:outline
                                       focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                        >
                            Registar
                        </button>
                    </div>
                </form>
            </div>

            <Footer />
        </div>
    );
};

export default RegisterClient;
