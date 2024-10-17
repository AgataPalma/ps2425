import React from 'react';
import '../index.css'; 

const Footer = () => {

  return (
    <footer className="text-center bg-gray-800 text-white p-8">
    <p>© 2024 Fix4You, Inc. All rights reserved.</p>
    <div className="mt-4">
      <a href="#" className="text-yellow-500 mx-2 hover:underline">Contact Us</a> |
      <a href="#" className="text-yellow-500 mx-2 hover:underline">About</a> |
      <a href="#" className="text-yellow-500 mx-2 hover:underline">FAQs</a>
    </div>
  </footer>
  );
};

export default Footer;