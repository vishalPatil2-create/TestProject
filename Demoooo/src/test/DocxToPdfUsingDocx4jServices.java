package com.lettergeneration.src;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.bind.JAXBElement;

import org.apache.fop.apps.FOUserAgent;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.DatabaseConnectivity.DatabaseConnectivity;


public class DocxToPdfUsingDocx4jServices
{
	static private Properties properties_NcbMailConfirmation = null;
	static private String dbStr_PolicyNumber;
	static private String dbStr_PreviousInsurerName;
	static private String dbStr_Address1;
	static private String dbStr_Address2;
	static private String dbStr_Address3;
	static private String dbStr_City;
	static private String dbStr_State;
	static private String dbStr_Pincode;
	static private String dbStr_InsuredName;
	static private String dbStr_VehicleNo;
	static private String dbStr_ChassioNo;
	static private String dbStr_EngineNo;
	static private String dbStr_YourPolicy_CovernoteNo;
	static private String dbStr_PreviousYearNcb;
	
	private DocxToPdfUsingDocx4jServices() throws Exception 
	{
		Map<String,String> _map_ReplaceKeyAndValue = new LinkedHashMap<>();
		_map_ReplaceKeyAndValue.put("$date$", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		_map_ReplaceKeyAndValue.put("$refNo$", "201250030118800200000000");
		
		_map_ReplaceKeyAndValue.put("$insurerName$", "NEW INDIA INS" );
		_map_ReplaceKeyAndValue.put("$address1$", "2nd Floor");
		_map_ReplaceKeyAndValue.put("$address2$", "4 Mangoe Lane" );
		_map_ReplaceKeyAndValue.put("$city$", "KOLKATA" );
		_map_ReplaceKeyAndValue.put("$pincode$", "700001" );
		_map_ReplaceKeyAndValue.put("$state$", "WEST BENGAL" );

		_map_ReplaceKeyAndValue.put("$insuredName$", "NITHIN ANTONY");
		_map_ReplaceKeyAndValue.put("$vehicleNo$", "KL07CK7383");
		_map_ReplaceKeyAndValue.put("$engineNo$", "U3S5C1HE044430");
		_map_ReplaceKeyAndValue.put("$chassisNo$", "ME3U3S5C1HE880502");
		_map_ReplaceKeyAndValue.put("$yourPolicy$", "71070031170160011488");
		_map_ReplaceKeyAndValue.put("$ncb$", "0");
		
		
		String str_SampleDocxFilepath = properties_NcbMailConfirmation.getProperty("SampleDocxFilepath");
		String str_ReplaceDocxFilepath = properties_NcbMailConfirmation.getProperty("ReplaceDocxFilepath");
		String str_ReplaceDocxFilename;
		String str_PdfFilepath = properties_NcbMailConfirmation.getProperty("PdfFilepath");
		String str_PdfFilename = properties_NcbMailConfirmation.getProperty("PdfFilename");
		
		WordprocessingMLPackage wMlPackage = null;
		wMlPackage = getWordprocessingMLPackageTemplate(str_SampleDocxFilepath);
		
		for(Entry<String,String> entry_ReplaceHolder : _map_ReplaceKeyAndValue.entrySet()) 
		{
			String str_PlaceHolder = entry_ReplaceHolder.getKey();
			String str_Value = entry_ReplaceHolder.getValue();
			replacePlaceholder(wMlPackage, str_Value, str_PlaceHolder);
		}
		str_ReplaceDocxFilename = _map_ReplaceKeyAndValue.get("$yourPolicy$");
		writeDocxToStream(wMlPackage, str_ReplaceDocxFilepath+"\\"+str_ReplaceDocxFilename+".docx");
		
		byte [] byteCodePdf = convertToPDF(wMlPackage);
		try (FileOutputStream fos = new FileOutputStream(new File(str_PdfFilepath, str_PdfFilename+".pdf"))) 
		{
			fos.write(byteCodePdf);
		}	
		
	}
		
	public static void getDocxToPdfUsingDocx4jServices(Properties properties) 
	{
		Connection conn = null;
		try 
		{
			
			properties_NcbMailConfirmation = properties;
			
			conn = DatabaseConnectivity.getDatabaseConnection();
			
			Integer[] ids = getRequestForLetterGeneration(conn);
			int req_Id = ids[0];
			int src_Id = ids[1];
			if(req_Id != 0 && src_Id != 0) 
			{
				callingMethodLetterGenerationForPSU(conn,req_Id,src_Id);
			
			}
			
		}catch(Exception ex) 
		{
			ex.printStackTrace();
		}
		finally 
		{
			if(conn != null) 
			{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void callingMethodLetterGenerationForPSU(Connection conn,int req_Id, int src_Id) {
		try 
		{	
			String str_SelectQueryForPSUEntry  = "SELECT * FROM ncbdataentry WHERE Sector=? AND Req_Id=? AND Src_Id=?";
			PreparedStatement preparedStatement_SelectQueryForPSUEntry = conn.prepareStatement(str_SelectQueryForPSUEntry);
			preparedStatement_SelectQueryForPSUEntry.setString(1, "PSU");
			preparedStatement_SelectQueryForPSUEntry.setInt(2, req_Id);
			preparedStatement_SelectQueryForPSUEntry.setInt(3, src_Id);
			ResultSet rs_SelectQueryForPSUEntry = preparedStatement_SelectQueryForPSUEntry.executeQuery();
			while(rs_SelectQueryForPSUEntry.next()) 
			{
				int db_dt_Id = rs_SelectQueryForPSUEntry.getInt("Id");
				
				dbStr_PolicyNumber = rs_SelectQueryForPSUEntry.getString("PolicyNumber");
				dbStr_PolicyNumber = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_PolicyNumber;
				
				dbStr_PreviousInsurerName = rs_SelectQueryForPSUEntry.getString("PreviousInsurerName");
				dbStr_PreviousInsurerName = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_PreviousInsurerName;
				
				dbStr_Address1 = rs_SelectQueryForPSUEntry.getString("AddressLine1");
				dbStr_Address1 = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_Address1;
				
				dbStr_Address2 = rs_SelectQueryForPSUEntry.getString("AddressLine2");
				dbStr_Address2 = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_Address2;
				
				dbStr_Address3 = rs_SelectQueryForPSUEntry.getString("AddressLine3");
				dbStr_Address3 = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_Address3;

				dbStr_City = rs_SelectQueryForPSUEntry.getString("City");
				dbStr_City = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_City;

				dbStr_State = rs_SelectQueryForPSUEntry.getString("State"); 
				dbStr_State = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_State;

				dbStr_Pincode = rs_SelectQueryForPSUEntry.getString("Pincode");
				dbStr_Pincode = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_Pincode;
				
				/* If address is not available, need to refer to column AN- Branch Name.
				 *  On the basis of the branch name, refer to the below excel and 
				 *  get the correct address pertaining to PSU and respective branch
				*/
				
				dbStr_InsuredName = rs_SelectQueryForPSUEntry.getString("InsuredName");
				dbStr_InsuredName = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_InsuredName;

				dbStr_VehicleNo = rs_SelectQueryForPSUEntry.getString("RegistrationNumber");
				dbStr_VehicleNo = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_VehicleNo;

				dbStr_ChassioNo = rs_SelectQueryForPSUEntry.getString("ChassioNo");
				dbStr_ChassioNo = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_ChassioNo;

				dbStr_EngineNo = rs_SelectQueryForPSUEntry.getString("EngineNo");
				dbStr_EngineNo = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_EngineNo;

				dbStr_YourPolicy_CovernoteNo = rs_SelectQueryForPSUEntry.getString("PreviousYearPolicyNumber");
				dbStr_YourPolicy_CovernoteNo = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_YourPolicy_CovernoteNo;

				dbStr_PreviousYearNcb = rs_SelectQueryForPSUEntry.getString("PreviousYearNcbDsc");
				dbStr_PreviousYearNcb = rs_SelectQueryForPSUEntry.wasNull()?"":dbStr_PreviousYearNcb;
				
				Map<String,String> map_PlaceHodlerKey_PlaceHolderValue = mappingPlaceHolderWithFiles();
				
				String[] letterFileDetails = letterGeneration(map_PlaceHodlerKey_PlaceHolderValue);
				insertLetterDetailsIntoTable(conn,letterFileDetails,req_Id,src_Id,db_dt_Id);
				
//				Need to add Update query for every record
//				
				
			}
		}catch(SQLException sqlEx) 
		{
			sqlEx.printStackTrace();
		}
	}

	private static void insertLetterDetailsIntoTable(Connection conn, String[] letterFileDetails, int req_Id, int src_Id, int db_dt_Id)
	{
		try 
		{
			
			String str_InsertQueryForLetterDetails = "INSERT INTO ncbletterlogsdetails ("
					+ "Req_Id,	Src_Id,	Dt_Id,	InsuranceCompanyName,	ReferenceNumber,	InsuredName,	VehicleName,	ChassisNo,	EngineNo,"
					+ "	YourPolicyCoverNote,	PreviousYearNCB,	LetterName,	LetterLocation,	Status,	Remark,	LetterGenerateDate)"
					+ "VALUES("
					+ "?,?,?,?,?,?,?,?,?,"
					+ "?,?,?,?,?,?,?"
					+ ")";
			java.sql.Timestamp setDateTime = null;
			setDateTime = new java.sql.Timestamp(new java.util.Date().getTime());
			String str_PDFFileName = null;
			String str_Filelocation = null;
			String str_Status = null;
			String str_Remark = null;
			if(letterFileDetails.length == 2) 
			{
				str_PDFFileName = letterFileDetails[0];
				str_Filelocation = letterFileDetails[1];
				str_Status = "Successfully Completed";
				str_Remark = "Letter Generated";
			}
			else if(letterFileDetails.length == 3)
			{
				str_PDFFileName = letterFileDetails[0];
				str_Filelocation = letterFileDetails[1];
				str_Status = "Successfully Completed";
				str_Remark = letterFileDetails[2];
			}
			PreparedStatement preparedStatement_InsertQueryForLetterDetails = conn.prepareStatement(str_InsertQueryForLetterDetails);
			preparedStatement_InsertQueryForLetterDetails.setInt(1, req_Id);
			preparedStatement_InsertQueryForLetterDetails.setInt(2, src_Id);
			preparedStatement_InsertQueryForLetterDetails.setInt(3, db_dt_Id);
			preparedStatement_InsertQueryForLetterDetails.setString(4, dbStr_PreviousInsurerName);
			preparedStatement_InsertQueryForLetterDetails.setString(5, dbStr_PolicyNumber);
			preparedStatement_InsertQueryForLetterDetails.setString(6, dbStr_InsuredName);
			preparedStatement_InsertQueryForLetterDetails.setString(7, dbStr_VehicleNo);
			preparedStatement_InsertQueryForLetterDetails.setString(8, dbStr_ChassioNo);
			preparedStatement_InsertQueryForLetterDetails.setString(9, dbStr_EngineNo);
			preparedStatement_InsertQueryForLetterDetails.setString(11, dbStr_YourPolicy_CovernoteNo);
			preparedStatement_InsertQueryForLetterDetails.setString(12, dbStr_PreviousYearNcb);
			preparedStatement_InsertQueryForLetterDetails.setString(13, str_PDFFileName);
			preparedStatement_InsertQueryForLetterDetails.setString(14, str_Filelocation);
			preparedStatement_InsertQueryForLetterDetails.setString(15, str_Status);
			preparedStatement_InsertQueryForLetterDetails.setString(16, str_Remark);
			preparedStatement_InsertQueryForLetterDetails.setTimestamp(17, setDateTime);
			preparedStatement_InsertQueryForLetterDetails.execute();
	
		}
		catch(SQLException ex) {ex.printStackTrace();}
	}

	private static String[] letterGeneration(Map<String, String> map_PlaceHodlerKey_PlaceHolderValue) 
	{
		Formatter monthNameFormatter = null;
		String[] letterFileDetails = null;
		String str_PDFFilePath = null;
		String str_Filename = null;
		try 
		{
			monthNameFormatter = new Formatter();
			Calendar cal = Calendar.getInstance();
			monthNameFormatter.format("%tB", cal);
			int year = cal.get(Calendar.YEAR);
			SimpleDateFormat sdFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date now = new Date();
			String day = sdFormat.format(now);
			System.out.println(day);
	
			String str_SampleDocxFilepath = properties_NcbMailConfirmation.getProperty("SampleDocumentPath");
			String str_ReplaceDocxFilepath = properties_NcbMailConfirmation.getProperty("ReplaceDocumentOutputpath");
			String str_LetterOutputpathForStage1 = properties_NcbMailConfirmation.getProperty("LetterOutputpathForStage1");
			
			
			str_PDFFilePath =  str_LetterOutputpathForStage1+"\\" + year + "\\" + monthNameFormatter + "\\" + day + "\\";
			File dir1 = new File(str_PDFFilePath);
			if (!dir1.exists())
				dir1.mkdirs();
			
			WordprocessingMLPackage wMlPackage = null;
			wMlPackage = getWordprocessingMLPackageTemplate(str_SampleDocxFilepath);
			
			for(Entry<String,String> entry_ReplaceHolder : map_PlaceHodlerKey_PlaceHolderValue.entrySet()) 
			{
				String str_PlaceHolder = entry_ReplaceHolder.getKey();
				String str_Value = entry_ReplaceHolder.getValue();
				replacePlaceholder(wMlPackage, str_Value, str_PlaceHolder);
			}
			 str_Filename = map_PlaceHodlerKey_PlaceHolderValue.get("$refNo$");
			
			str_ReplaceDocxFilepath = str_ReplaceDocxFilepath+"\\"+str_Filename+".docx";
			
			writeDocxToStream(wMlPackage, str_ReplaceDocxFilepath);
			
			byte [] byteCodePdf = convertToPDF(wMlPackage);
			
			str_PDFFilePath = str_PDFFilePath+"\\"+str_Filename+".pdf";
			FileOutputStream fos = new FileOutputStream(new File(str_PDFFilePath));
			fos.write(byteCodePdf);
			fos.close();
			
			letterFileDetails = new String[2];
			letterFileDetails[0] = str_Filename+".pdf";
			letterFileDetails[1] = str_PDFFilePath;
				
		}catch(Exception ex)
		{
			ex.printStackTrace();
			letterFileDetails = new String[3];
			letterFileDetails[0] = str_Filename+".pdf";
			letterFileDetails[1] = str_PDFFilePath;
			letterFileDetails[2] = ex.getLocalizedMessage();
		}
		finally 
		{
			if(monthNameFormatter != null) {monthNameFormatter.close();}
		}
		return letterFileDetails;
	}

	private static Map<String,String> mappingPlaceHolderWithFiles() 
	{
		Map<String,String> map_PlaceHodlerKey_PlaceHolderValue = new LinkedHashMap<String, String>();
		try 
		{
			String str_JsonPalceHolderpath = properties_NcbMailConfirmation.getProperty("JsonPalceHolderpath");
			JSONParser parser = new JSONParser(); 
	        Object obj = parser.parse(new FileReader(str_JsonPalceHolderpath));
	        JSONObject jsonObject = (JSONObject)obj;
	        
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("date"), new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("refNo"), dbStr_PolicyNumber);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("insurerName"), dbStr_PreviousInsurerName);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("address1"), dbStr_Address1);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("address2"), dbStr_Address2);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("city"), dbStr_City);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("pincode"), dbStr_Pincode);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("state"), dbStr_State);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("insuredName"), dbStr_InsuredName);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("vehicleNo"), dbStr_VehicleNo);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("engineNo"), dbStr_EngineNo);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("chassisNo"), dbStr_ChassioNo);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("yourPolicy"), dbStr_YourPolicy_CovernoteNo);
	        map_PlaceHodlerKey_PlaceHolderValue.put((String)jsonObject.get("ncb"), dbStr_PreviousYearNcb);

		}
		catch(Exception ex) 
		{
			ex.printStackTrace();
		}
		return map_PlaceHodlerKey_PlaceHolderValue;
	}

	private static Integer[] getRequestForLetterGeneration(Connection conn)
	{ 	
		Integer[] ids = null;
		try 
		{
			int req_Id = 0;
			int src_Id = 0;
			
			String str_SelectRequestQueryForLetterGeneration = 
					"select ncbsourceentry.* from ncbsourceentry inner Join ncbrequestentry On ncbrequestentry.Id = ncbsourceentry.Req_Id Where \r\n" + 
					"ncbsourceentry.Stage=? AND ncbsourceentry.Status=? AND ncbrequestentry.Status=?";
			PreparedStatement preparedStatement_SelectRequestQueryForLetterGeneration =  conn.prepareStatement(str_SelectRequestQueryForLetterGeneration);
			preparedStatement_SelectRequestQueryForLetterGeneration.setString(1, "Stage 1");
			preparedStatement_SelectRequestQueryForLetterGeneration.setString(2, "Successfully Completed");
			preparedStatement_SelectRequestQueryForLetterGeneration.setString(3, "unprocess");

			ResultSet rs_SelectRequestQueryForLetterGeneration = preparedStatement_SelectRequestQueryForLetterGeneration.executeQuery();
			rs_SelectRequestQueryForLetterGeneration.next();
			req_Id = rs_SelectRequestQueryForLetterGeneration.getInt("Req_Id");
			src_Id = rs_SelectRequestQueryForLetterGeneration.getInt("Id");
			
			ids = new Integer[2];
			ids[0] = req_Id;
			ids[1] = src_Id;
		}
		catch(SQLException ex) {ex.printStackTrace();}
		return ids;
	}

	private static WordprocessingMLPackage getWordprocessingMLPackageTemplate(String str_SampleDocxFilepath) throws FileNotFoundException, Docx4JException {
		 WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(str_SampleDocxFilepath)));
		  return template;
	}
	
	private static void replacePlaceholder(WordprocessingMLPackage wMlPackage, String str_Value, String str_PlaceHolder) 
	{
		List<Object> texts = getAllElementFromObject(wMlPackage.getMainDocumentPart(), Text.class);		
		  for (Object text : texts)
		  {
			   Text textElement = (Text) text;
			   
			  if (textElement.getValue().equals(str_PlaceHolder))
			   {
				   textElement.setValue(str_Value);
				   break;
			   }
		  }
	}

	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) 
	{
		  List<Object> result = new ArrayList<Object>();
		  if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();
		  
		  if (obj.getClass().equals(toSearch))
		  {
			  result.add(obj);
		  }
		  else if (obj instanceof ContentAccessor) 
		  {
			  List<?> children = ((ContentAccessor) obj).getContent();
			  for (Object child : children)
			  {
				  result.addAll(getAllElementFromObject(child, toSearch));
			  }
		  }
		  return result;
	 }
	
	private static void writeDocxToStream(WordprocessingMLPackage template, String target) throws IOException, Docx4JException 
	{
		 File f = new File(target);
		 template.save(f);
	}
	
	@SuppressWarnings("deprecation")
	public static byte[] convertToPDF(WordprocessingMLPackage wordMLPackage) throws Exception
	{

		//Commented old and added Bubai's new Remediation
//		log.info("convertToPDF started");
		 wordMLPackage.getDocumentModel().refresh();
		byte[] documentByteArray = null;
		FOSettings foSettings = null;
		ByteArrayOutputStream baos = null;
		try {
			// Bubai's New Remediation
//			log.info("Mapper started");
			Mapper fontMapper = new IdentityPlusMapper();
			wordMLPackage.setFontMapper(fontMapper);
//			fontMapper = null;
//			log.info("createFOSettings started");
			foSettings = Docx4J.createFOSettings();
			foSettings.setApacheFopMime(FOSettings.MIME_PDF);
			foSettings.setWmlPackage(wordMLPackage);
			
			// Bubai's New Remediation
//			log.info("FORendererApacheFOP.getFOUserAgent started");
			FOUserAgent userAgent = FORendererApacheFOP.getFOUserAgent(foSettings);
			userAgent.setAccessibility(true);
//			userAgent = null;
			baos = new ByteArrayOutputStream();
//			log.info("Docx4J.toFO(foSettings, baos, Docx4J.FLAG_EXPORT_PREFER_NONXSL)  started");
			Docx4J.toFO(foSettings, baos, Docx4J.FLAG_EXPORT_PREFER_XSL);			
//			log.info("wordMLPackage.getMainDocumentPart().getFontTablePart() started");
			if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null) {
//				 log.info("Delete embedded font temp files");
				wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
			}
//			log.info("documentByteArray started");
			wordMLPackage = null;
			documentByteArray = baos.toByteArray();
//			log.info("documentByteArray completed");
		} catch (Exception ex) {
			throw ex;
		} finally {
			//baos.flush();
			baos.close();
			foSettings = null;
		}
//		log.info("return back");
		return documentByteArray;
			
	}

	
	public static void main(String args[]) throws Exception 
	{
		new DocxToPdfUsingDocx4jServices();
	}
}
