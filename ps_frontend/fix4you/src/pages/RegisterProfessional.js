import React, { useState } from 'react';
import '../index.css';
import Header from '../components/HeaderNotLogged';
import Footer from '../components/Footer';

const RegisterProfessional = () => {
  const [selectedServices, setSelectedServices] = useState({
    limpeza: false,
    pintura: false,
    jardinagem: false,
  });

  const handleCheckboxChange = (event) => {
    setSelectedServices({
      ...selectedServices,
      [event.target.name]: event.target.checked,
    });
  };

  const [selectedImage, setSelectedImage] = useState(null);

  const handleImageChange = (event) => {
    if (event.target.files && event.target.files[0]) {
      setSelectedImage(URL.createObjectURL(event.target.files[0]));
    }
  };

  const triggerFileInput = () => {
    document.getElementById('profileimage').click();
  };

  return (
    <div class="bg-gray-200">
      <Header />
      <div class="sm:mx-auto sm:w-full sm:max-w-lg py-12">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
          Register to Contact a Professional
        </h2>
      </div>

      <div class="p-8 bg-gray-100 shadow-lg rounded-lg bg-cover bg-center sm:mx-auto sm:w-full sm:max-w-lg mb-40">
        <form>
          <div class="space-y-12">
            <div class="border-b border-gray-900/10 pb-12">
              <h2 class="text-base font-semibold leading-7 text-gray-900">Informação Pessoal</h2>

              <div class="mt-10 grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6">
                <div class="sm:col-span-3">
                  <label for="first-name" class="block text-sm font-medium leading-6 text-gray-900">
                    Primeiro Nome
                  </label>
                  <div class="mt-2">
                    <input
                      type="text"
                      name="first-name"
                      id="first-name"
                      autocomplete="given-name"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>

                <div class="sm:col-span-3">
                  <label for="last-name" class="block text-sm font-medium leading-6 text-gray-900">
                    Último Nome
                  </label>
                  <div class="mt-2">
                    <input
                      type="text"
                      name="last-name"
                      id="last-name"
                      autocomplete="family-name"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>

                <div class="col-span-full">
                  <label for="photo" class="block text-sm font-medium leading-6 text-gray-900">Foto</label>
                  <div class="mt-2 flex items-center gap-x-3">
                    {selectedImage ? (
                      <img src={selectedImage} alt="Profile" class="h-12 w-12 rounded-full" />
                    ) : (
                      <svg class="h-12 w-12 text-gray-300" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                        <path
                          fill-rule="evenodd"
                          d="M18.685 19.097A9.723 9.723 0 0 0 21.75 12c0-5.385-4.365-9.75-9.75-9.75S2.25 6.615 2.25 12a9.723 9.723 0 0 0 3.065 7.097A9.716 9.716 0 0 0 12 21.75a9.716 9.716 0 0 0 6.685-2.653Zm-12.54-1.285A7.486 7.486 0 0 1 12 15a7.486 7.486 0 0 1 5.855 2.812A8.224 8.224 0 0 1 12 20.25a8.224 8.224 0 0 1-5.855-2.438ZM15.75 9a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z"
                          clip-rule="evenodd"
                        />
                      </svg>
                    )}
                    <button
                      type="button"
                      onClick={triggerFileInput}
                      class="rounded-md bg-white px-2.5 py-1.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                    >
                      Change
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

                <div class="sm:col-span-4">
                  <label for="email" class="block text-sm font-medium leading-6 text-gray-900">Email</label>
                  <div class="mt-2">
                    <input
                      id="email"
                      name="email"
                      type="email"
                      autocomplete="email"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>

                <div class="sm:col-span-3">
                  <label for="country" class="block text-sm font-medium leading-6 text-gray-900">País</label>
                  <div class="mt-2">
                    <select
                      id="country"
                      name="country"
                      defaultValue="Portugal"
                      autocomplete="country-name"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:max-w-xs sm:text-sm sm:leading-6"
                    >
                      <option value="Portugal">Portugal</option>
                      {/* Other country options */}
                    </select>
                  </div>
                </div>

                <div class="col-span-full">
                  <label for="street-address" class="block text-sm font-medium leading-6 text-gray-900">Endereço</label>
                  <div class="mt-2">
                    <input
                      type="text"
                      name="street-address"
                      id="street-address"
                      autocomplete="street-address"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>

                <div class="sm:col-span-2 sm:col-start-1">
                  <label for="city" class="block text-sm font-medium leading-6 text-gray-900">Localidade</label>
                  <div class="mt-2">
                    <input
                      type="text"
                      name="city"
                      id="city"
                      autocomplete="address-level2"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>

                <div class="sm:col-span-2">
                  <label for="region" class="block text-sm font-medium leading-6 text-gray-900">Distrito</label>
                  <div class="mt-2">
                    <input
                      type="text"
                      name="region"
                      id="region"
                      autocomplete="address-level1"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>

                <div class="sm:col-span-2">
                  <label for="postal-code" class="block text-sm font-medium leading-6 text-gray-900">Código Postal</label>
                  <div class="mt-2">
                    <input
                      type="text"
                      name="postal-code"
                      id="postal-code"
                      autocomplete="postal-code"
                      class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="border-b border-gray-900/10 pb-12">
              <h2 class="text-base font-semibold leading-7 text-gray-900">Área de Trabalho</h2>
              <p class="mt-1 text-sm leading-6 text-gray-600">Escolha uma ou mais áreas de trabalho e complete a informação adicional.</p>

              <div class="mt-10 space-y-10">
                <fieldset>
                  <div class="mt-6 space-y-6">
                    <div class="relative flex gap-x-3">
                      <div class="flex h-6 items-center">
                        <input
                          id="limpeza"
                          name="limpeza"
                          type="checkbox"
                          class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-600"
                          onChange={handleCheckboxChange}
                        />
                      </div>
                      <div class="text-sm leading-6">
                        <label for="limpeza" class="font-medium text-gray-900">Limpeza</label>
                      </div>
                    </div>
                    {selectedServices.limpeza && (
                      <div class="ml-6 space-y-4">
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Passa Fatura?</label>
                          <input type="checkbox" name="limpeza-fatura" class="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Cobra por Deslocação?</label>
                          <input type="checkbox" name="limpeza-deslocacao" class="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Preço Médio</label>
                          <input
                            type="text"
                            name="pintura-preco"
                            class="block w-20 rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600"
                          />
                        </div>

                      </div>
                    )}

                    <div class="relative flex gap-x-3">
                      <div class="flex h-6 items-center">
                        <input
                          id="pintura"
                          name="pintura"
                          type="checkbox"
                          class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-600"
                          onChange={handleCheckboxChange}
                        />
                      </div>
                      <div class="text-sm leading-6">
                        <label for="pintura" class="font-medium text-gray-900">Pintura</label>
                      </div>
                    </div>
                    {selectedServices.pintura && (
                      <div class="ml-6 space-y-4">
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Passa Fatura?</label>
                          <input type="checkbox" name="pintura-fatura" class="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Cobra por Deslocação?</label>
                          <input type="checkbox" name="pintura-deslocacao" class="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Preço Médio</label>
                          <input
                            type="text"
                            name="pintura-preco"
                            class="block w-20 rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600"
                          />
                        </div>

                      </div>
                    )}

                    <div class="relative flex gap-x-3">
                      <div class="flex h-6 items-center">
                        <input
                          id="jardinagem"
                          name="jardinagem"
                          type="checkbox"
                          class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-600"
                          onChange={handleCheckboxChange}
                        />
                      </div>
                      <div class="text-sm leading-6">
                        <label for="jardinagem" class="font-medium text-gray-900">Jardinagem</label>
                      </div>
                    </div>
                    {selectedServices.jardinagem && (
                      <div class="ml-6 space-y-4">
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Passa Fatura?</label>
                          <input type="checkbox" name="jardinagem-fatura" class="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Cobra por Deslocação?</label>
                          <input type="checkbox" name="jardinagem-deslocacao" class="h-4 w-4 text-indigo-600" />
                        </div>
                        <div>
                          <label class="block text-sm font-medium text-gray-900">Preço Médio</label>
                          <input
                            type="text"
                            name="pintura-preco"
                            class="block w-20 rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-indigo-600"
                          />
                        </div>

                      </div>
                    )}
                  </div>
                </fieldset>
              </div>
            </div>

            <div class="mt-6 flex items-center justify-end gap-x-6">
              <button type="button" class="text-sm font-semibold leading-6 text-gray-900">Cancel</button>
              <button
                type="submit"
                class="rounded-md bg-gray-800 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-blue-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
              >
                Save
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
