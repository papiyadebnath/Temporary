package com.deere.pdf.manupulation;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

import com.deere.data.ExcelData;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

public class Watermark {

	public void setWatermarkToFile(String source, String destination, /*String watermarkText, byte[] USER,*/ byte[] OWNER,
			ExcelData excelData) throws Exception {
		// Read the existing PDF document
		PdfReader pdfReader = new PdfReader(source);
		// Get the PdfStamper object
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(
				destination));
		pdfStamper.setEncryption(excelData.getPassword().getBytes(), OWNER, PdfWriter.ALLOW_PRINTING,
				PdfWriter.ENCRYPTION_AES_128);
		for(int pageNumber = 0; pageNumber< pdfReader.getNumberOfPages(); pageNumber++){
			setWatermarkToPage(pdfStamper, pageNumber + 1, excelData.Nameofdealership);
		}
		pdfStamper.setFullCompression();

		pdfStamper.close();
		pdfReader.close();

//		pdfStamper.createXmpMetadata();
	}

	public void setWatermarkToPage(PdfStamper pdfStamper, int pageNumber, String watermarkText) throws DocumentException, IOException {
		// Get the PdfContentByte type by pdfStamper.
		PdfContentByte underContent = pdfStamper.getUnderContent(pageNumber);

		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		PdfGState gs = new PdfGState();
		gs.setFillOpacity(0.4f);
		underContent.setGState(gs);
		underContent.beginText();
		underContent.setFontAndSize(bf, 30);
		underContent.setColorFill(Color.LIGHT_GRAY);
		int horizontal=175,vertical=900;
		for (int j = 0; j < 2; j++) {
			watermarkText +=watermarkText+"         ";
		}
		for (int i = 0; i < 15; i++) {
			underContent.showTextAligned(Element.ALIGN_CENTER, watermarkText,
					horizontal, vertical, 45);
			vertical-=100;
			horizontal+=15;
		}
		underContent.endText();
	}
}
 