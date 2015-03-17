package com.deere.email;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.deere.data.ExcelData;
import com.deere.excel.ReadExcel;

public class Email {
	public Email(String host) throws Exception {
		this.host = host;
	}

	private String host;

	public EmailProperties populateEmailData(String pathname) throws Exception {
		try {
//			FileInputStream file = new FileInputStream(new File(pathname));

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook= new XSSFWorkbook(pathname);
			EmailProperties emailProperties = new EmailProperties();
			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheet("eMail Content");
			emailProperties.from = ReadExcel.getValueInCell(sheet.getRow(0).getCell(1));
			emailProperties.subject = ReadExcel.getValueInCell(sheet.getRow(1).getCell(1));
			emailProperties.body = ReadExcel.getValueInCell(sheet.getRow(2).getCell(1));
			return emailProperties;
		} catch (Exception e) {
			System.out.println("Error while reading the excel sheet.../n Please check if all column name are correct.");
			e.printStackTrace();
			throw e;
		}
	}

	private class EmailProperties{
		String from;
		String subject;
		String body;
	}
	public void send(EmailProperties emailProperties, String pathname, String filename, String to, String CC) throws
			Exception {
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(emailProperties.from));

		// Set To: header field of the header.
		message.addRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to==null?"":to));

		message.addRecipients(Message.RecipientType.CC,
				InternetAddress.parse(CC==null?"":CC));

		// Set Subject: header field
		message.setSubject(emailProperties.subject);

		// Create the message part
		BodyPart messageBodyPart = new MimeBodyPart();

		// Fill the message
		messageBodyPart.setText(emailProperties.body);

		// Create a multipar message
		Multipart multipart = new MimeMultipart();

		// Set text message part
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(new File(pathname));
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		multipart.addBodyPart(messageBodyPart);

		// Send the complete message parts
		message.setContent(multipart);

		// Send message
		Transport.send(message);
	}
}