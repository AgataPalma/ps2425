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
                    Register to Contact a Professional
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
                            Name
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
                            Email address
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
                            Phone Number
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
                            Date of Birth
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
                    <div>
                        <button
                            type="submit"
                            className="flex w-full justify-center rounded-md bg-gray-800 px-3 py-1.5 text-sm font-semibold
                                       leading-6 text-white shadow-sm hover:bg-yellow-600 focus-visible:outline
                                       focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                        >
                            Register
                        </button>
                    </div>
                </form>
            </div>

            <Footer />
        </div>
    );
};

export default RegisterClient;
