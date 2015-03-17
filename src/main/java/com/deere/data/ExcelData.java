package com.deere.data;

import java.util.HashMap;
import java.util.Map;

public class ExcelData {

	public String AccountflexCode;
	public String SAPCodePAG;
	public String State;
	public String Nameofdealership;
	public String City;
	public String To;
	public String CC;
	public String PANNo;
	public String DateofBirth;
	public String OutputFileFolderPath;
	public String OutputFileName;
	public String TINNoasperregcertificate;

	public String getPassword() throws Exception {
        String pwd = "";
        try {
            String accFlexPart = AccountflexCode.substring(AccountflexCode.length()-4 , AccountflexCode.length());
            String panNumberPart = PANNo.substring(0,4);
            pwd =  accFlexPart + panNumberPart;
	        if(pwd==null || pwd.equals("")){
		        throw new Exception("Exception while generating password,please check Accflex and PAN number");
	        }
        } catch(Exception ex){
	        throw new Exception("Exception while generating password,please check Accflex and PAN number", ex);
        }
		return pwd;
	}



}
