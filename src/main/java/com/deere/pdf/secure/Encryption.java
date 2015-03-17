package com.deere.pdf.secure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;


public class Encryption {
	public static void main(String[] args) throws Exception {
		try {
			File file = new File("c://test.pdf");
			FileOutputStream pdfFileout = new FileOutputStream(file);
			com.lowagie.text.Document doc = new com.lowagie.text.Document();
			PdfWriter writer = PdfWriter.getInstance(doc, pdfFileout);

			String user="gauri",owner="ashwini";

			writer.setEncryption(user.getBytes(), owner.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);

			doc.addAuthor("Ashwini Vayase");
			doc.addTitle("This is title");
			doc.open();

			Paragraph para1 = new Paragraph();
			para1.add("John Deere Text 1");

			Paragraph para2 = new Paragraph();
			para2.add("John Deere Text 2");

			doc.add(para1);
			doc.add(para2);

			//PdfStamper

			doc.close();
			pdfFileout.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
