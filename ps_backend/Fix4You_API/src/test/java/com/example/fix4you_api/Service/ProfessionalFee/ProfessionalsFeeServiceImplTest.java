package com.example.fix4you_api.Service.ProfessionalFee;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Service.ProfessionalsFee.ProfessionalsFeeServiceImpl;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ProfessionalsFeeServiceImplTest {

    @InjectMocks
    private ProfessionalsFeeServiceImpl professionalsFeeService;

    public ProfessionalsFeeServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateInvoice() throws Exception {
        ProfessionalsFee fee = new ProfessionalsFee();
        fee.setValue(100.0f);
        fee.setPaymentStatus(PaymentStatusEnum.COMPLETED);
        fee.setRelatedMonthYear("2024-11");
        fee.setNumberServices(5);

        Professional professional = new Professional();
        professional.setName("John Doe");
        professional.setNif("123456789");

        byte[] invoiceBytes = professionalsFeeService.generateInvoice(fee, professional);

        // Parse the PDF and extract its text
        ByteArrayInputStream inputStream = new ByteArrayInputStream(invoiceBytes);
        PdfReader reader = new PdfReader(inputStream);
        String pdfContent = PdfTextExtractor.getTextFromPage(reader, 1);
        reader.close();

        // Verify the PDF content matches the business data
        assertTrue(pdfContent.contains("Invoice"));
        assertTrue(pdfContent.contains("Amount paid: 100.0"));
        assertTrue(pdfContent.contains("Name: John Doe"));
        assertTrue(pdfContent.contains("NIF: 123456789"));
        assertTrue(pdfContent.contains("Invoice Date:"));
    }
}