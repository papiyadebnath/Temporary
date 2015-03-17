package com.deere.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.deere.data.ExcelData;
import com.deere.email.Email;
import com.deere.excel.ReadExcel;
import com.deere.pdf.manupulation.Watermark;

public class MyAssignment {
    public static final String BASE
            = "././";
    public static final String EXCEL
            = BASE + "PricingCommunicator.xlsm";
	/** Owner password. */
	public static byte[] OWNER = "Confidential".getBytes();

	public static void main(String arr[]) throws Exception {
		Long start = System.currentTimeMillis();
//		FileInputStream file = null;
		StringBuffer successfulRecords = new StringBuffer(), failureRecords=new StringBuffer(),
				errors = new StringBuffer();
		try{
			//Open Excel
//			file = new FileInputStream(new File(EXCEL));
			XSSFWorkbook workbook= new XSSFWorkbook(EXCEL);
			ReadExcel readExcel = new ReadExcel();
			//Read Configuration
			Map<String, String> columnNameMapping = readExcel.getColumnNameMapping(workbook);
			//Read Data
			List<ExcelData> excelData = readExcel.readExcel(workbook, columnNameMapping);
			Email email = new Email(getHost());
			new File(BASE+"logs").mkdir();
//		new File(BASE+"failureRecords.txt").mkdir();
			final CountDownLatch latch = new CountDownLatch(excelData.size());
			ExecutorService executor = Executors.newFixedThreadPool(Integer.valueOf(columnNameMapping.get("Threads")));
			for (ExcelData excel: excelData){
				ProcessPerThread processPerThread =
						new ProcessPerThread(email, successfulRecords, failureRecords, excel, latch, errors);
				executor.execute(processPerThread);
			}
			latch.await();
			writeLogTOFile(successfulRecords.toString(), BASE + "logs/" + "successFull.txt");
			writeLogTOFile(failureRecords.toString(), BASE + "logs/" + "failureRecords.txt");

			System.out.println("Time = "+ (System.currentTimeMillis() - start)/1000);
			executor.shutdown();
		}catch(Exception e){
			logException(e, errors);
		}
		writeLogTOFile(errors.toString(), BASE+ "logs/" + "ErrorLog.txt");

	}
	private static void logException(Exception e, StringBuffer errors) {
		errors.append(ExceptionUtils.getStackTrace(e));
	}
	public static class ProcessPerThread implements Runnable {
		private Email email;
		private StringBuffer successfulRecords;
		private StringBuffer errors;
		private StringBuffer failureRecords;
		private ExcelData excel;
		private CountDownLatch latch;
		public ProcessPerThread(Email email, StringBuffer successfulRecords, StringBuffer failureRecords,
				ExcelData excel, CountDownLatch latch, StringBuffer errors) {
			this.email = email;
			this.successfulRecords = successfulRecords;
			this.failureRecords = failureRecords;
			this.excel = excel;
			this.latch = latch;
			this.errors = errors;
		}

		@Override
		public void run() {
			try {
				process(email, successfulRecords, failureRecords, excel);
				successfulRecords.append(excel.AccountflexCode + ",");
				System.out.println("Email sent to Dealer: " + excel.AccountflexCode);
			}catch(Exception e){
				failureRecords.append(excel.AccountflexCode + ",");
				System.err.println(
						"Failed: AccountflexCode=" + excel.AccountflexCode
								+ " Dealer ID=" +excel.SAPCodePAG + System.getProperty("line.separator")
								+ " due to- " + e.getMessage());
				logException(e, errors);
			}finally {
				this.latch.countDown();
			}
		}
	}
	private static void process(Email email, StringBuffer successfulRecords, StringBuffer failureRecords,
			ExcelData excel) throws Exception {
		encryptAndSendEmail(email, excel);
	}

	private static void encryptAndSendEmail(Email email, ExcelData excel) throws Exception {
		new Watermark().setWatermarkToFile(getFilenameWithoutEncryption(excel), getDestination(excel), OWNER, excel);
		email.send(email.populateEmailData(EXCEL),
				getPathname(excel), excel.OutputFileName,
				excel.To,excel.CC);
	}

	public static void writeLogTOFile(String log, String file) {
		BufferedWriter writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(file));
			writer.write(log);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}
	private static String getHost() {
		return "mail.dx.deere.com";
	}

	private static String getDestination(ExcelData excel) {
		return excel.OutputFileFolderPath.replace("generated-without-encryption\\", "") + excel.OutputFileName;
	}

	private static String getFilenameWithoutEncryption(ExcelData excel) {
		return excel.OutputFileFolderPath+excel.OutputFileName;
	}

//	private static String getFrom() {
//		return "pophalegaurin@johndeere.com";
//	}

//	private static String getFilename() {
//		return "PriceReport.pdf";
//	}

	private static String getPathname(ExcelData excel) {
		return getDestination(excel);
	}

//	private static String getBody(Map<String, String> emailData) throws Exception {
//		return new Velocity().parseContent(emailData, getTemplateName());
//	}

//	private static String getTemplateName() {
//		return "/velocity-templates/email-body.vm";
//	}
//
//	private static String getSubject() {
//		return "Price List";
//	}

//	private static Map<String, String> createEMailData(ExcelData excel) {
//		Map<String, String> emailData= new HashMap<String, String>();
//		emailData.put("user", excel.Nameofdealership);
//		return emailData;
//	}
}
