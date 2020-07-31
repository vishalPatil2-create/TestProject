

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;





public class PSUNCBDataDumpInDataBase 
 {
	//-----------------------------------------------------Check If Policy Number Exist Or Not------------------------------------------------------------------------------
		public static int toCheckPolicyNumberExistOrNot(Connection connection, String policyNo)
		{
			int count = 0;
			try 
			{
				connection = PsuNcbDataBaseConnection.getConnection();
				
				Statement statementPolicyNumberExistOrNot =connection.createStatement();
				String str_PolicyNumberExistOrNot = "SELECT count(*) AS rowcount FROM psuncbdata WHERE PolicyNumber = "+"'"+policyNo+"'";
				ResultSet resultSetPolicyNumberExistOrNot=statementPolicyNumberExistOrNot.executeQuery(str_PolicyNumberExistOrNot);
				resultSetPolicyNumberExistOrNot.next();
				count=resultSetPolicyNumberExistOrNot.getInt("rowcount");
				if(0<count){
					System.out.println(count);
				}else {
					System.out.println(count);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return count;
			
		}
	
	
	//-----------------------------------------------------------------------PSUNCB DataBase Dump--------------------------------------------------------------------------------------------------
	
	public static void readJsonAndDumpIntoDB(JSONObject jsonObject,int int_RequestIdPass, int int_SourceIdPass)	throws Exception 
	{


		Connection connection_dataDump = PsuNcbDataBaseConnection.getConnection();
		PreparedStatement preparedStatementDataBaseDump = null;
		String sqlQeury_InsertPsuNcbData = "INSERT INTO psuncbdata(PolicyNumber,EndorsementEumber,DcnNo,InsuredName,PolicyIssueDate,PolicyIssueDate1,PolicyStartDate,PolicyEndDate,"
				+ "PreviousYearPolicyNumber,PreviousInsurerName,PreviousPolicyFromDate,PreviousPolicyToDate,PreviousYearNcbDsc,NcbCurrentYear,NcbAmountCurrentYear,TypeOfCover,"
				+ "InsurerAddress,PhoneNo, AddressLine1,AddressLine2,AddressLine3,	City,State,	PinCode,PolicyStatusDesc,ProductGroup,ProductDescription,ChassisNo,"
				+ "EngineNo,Make,Model,RegistrationNumber,IntermediaryCode,IntermediaryName,BscCode,BscName,SmCode,SmName,BranchCode,BranchName,SumInsured,Rate,"
				+ "TxtRoofOfDocument,DetariffDiscount,FuelType,AgeOfVehicle,RtoCode,YesNo, SortOrder,ClaimStatus,LetterStatus,ClaimType,NcbPercentOnRenewal,MailLetter,"
				+ "Dates,TodaysDate,Tat ,ReminderSentDate ,RevertRecdYN,RevertReceivedDate,LetterSentToInsured,LetterSentToInsuredDate,UpdationDate,DataExtractionDate,Req_Id,Src_id,FilterStatus)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		  
		  preparedStatementDataBaseDump = connection_dataDump.prepareStatement(sqlQeury_InsertPsuNcbData);
		  JSONArray array = (JSONArray) jsonObject.get("Row");
		  
		for (Object obj : array) 
		{
			JSONObject dataBaseJsonObject = (JSONObject) obj;

			String str_PolicyNumber = dataBaseJsonObject.get("Policy Number").toString();
            System.out.println(str_PolicyNumber);
			
			int intExistDataOrNot = toCheckPolicyNumberExistOrNot(connection_dataDump,str_PolicyNumber);
            System.out.println(intExistDataOrNot);
			
			if (intExistDataOrNot == 1) 
			{
				System.out.println("Already data exist ");
			} else 
			{
			
				preparedStatementDataBaseDump.setString(1, str_PolicyNumber);
				preparedStatementDataBaseDump.setString(2, dataBaseJsonObject.get("Endorsement Number").toString());
				preparedStatementDataBaseDump.setString(3, dataBaseJsonObject.get("DCN No.").toString());
				preparedStatementDataBaseDump.setString(4, dataBaseJsonObject.get("INSURED NAME").toString());
				preparedStatementDataBaseDump.setString(5, dataBaseJsonObject.get("Policy Issue Date").toString());
				preparedStatementDataBaseDump.setString(6, dataBaseJsonObject.get("Policy Issue Date1").toString());
				preparedStatementDataBaseDump.setString(7, dataBaseJsonObject.get("POLICY START DATE").toString());
				preparedStatementDataBaseDump.setString(8, dataBaseJsonObject.get("POLICY END DATE").toString());
				preparedStatementDataBaseDump.setString(9, dataBaseJsonObject.get("PREVIOUS YEAR POLICY NUMBER").toString());
				preparedStatementDataBaseDump.setString(10, dataBaseJsonObject.get("PREVIOUS INSURER NAME").toString());
				preparedStatementDataBaseDump.setString(11, dataBaseJsonObject.get("PREVIOUS POLICY FROM DATE").toString());
				preparedStatementDataBaseDump.setString(12, dataBaseJsonObject.get("PREVIOUS POLICY TO DATE").toString());
				preparedStatementDataBaseDump.setString(13, dataBaseJsonObject.get("PREVIOUS YEAR NCB DSC").toString());
				preparedStatementDataBaseDump.setString(14, dataBaseJsonObject.get("NCB Current year").toString());
				preparedStatementDataBaseDump.setString(15, dataBaseJsonObject.get("NCB Amount Current Year").toString());
				preparedStatementDataBaseDump.setString(16, dataBaseJsonObject.get("TYPE OF COVER").toString());
				preparedStatementDataBaseDump.setString(17, dataBaseJsonObject.get("INSURER ADDRESS").toString());
				preparedStatementDataBaseDump.setString(18, dataBaseJsonObject.get("Phone no").toString());
				preparedStatementDataBaseDump.setString(19, dataBaseJsonObject.get("ADDRESS Line1").toString());
				preparedStatementDataBaseDump.setString(20, dataBaseJsonObject.get("ADDRESS Line2").toString());
				preparedStatementDataBaseDump.setString(21, dataBaseJsonObject.get("ADDRESS Line3").toString());
				preparedStatementDataBaseDump.setString(22, dataBaseJsonObject.get("CITY").toString());
				preparedStatementDataBaseDump.setString(23, dataBaseJsonObject.get("STATE").toString());
				preparedStatementDataBaseDump.setString(24, dataBaseJsonObject.get("PIN CODE").toString());
				preparedStatementDataBaseDump.setString(25, dataBaseJsonObject.get("POLICY STATUS DESC").toString());
				preparedStatementDataBaseDump.setString(26, dataBaseJsonObject.get("Product Group").toString());
				preparedStatementDataBaseDump.setString(27, dataBaseJsonObject.get("PRODUCT DESCRIPTION").toString());
				preparedStatementDataBaseDump.setString(28, dataBaseJsonObject.get("Chassis No").toString());
				preparedStatementDataBaseDump.setString(29, dataBaseJsonObject.get("Engine No").toString());
				preparedStatementDataBaseDump.setString(30, dataBaseJsonObject.get("Make").toString());
				preparedStatementDataBaseDump.setString(31, dataBaseJsonObject.get("Model").toString());
				preparedStatementDataBaseDump.setString(32, dataBaseJsonObject.get("REGISTRATION NUMBER").toString());
				preparedStatementDataBaseDump.setString(33, dataBaseJsonObject.get("INTERMEDIARY CODE").toString());
				preparedStatementDataBaseDump.setString(34, dataBaseJsonObject.get("INTERMEDIARY NAME").toString());
				preparedStatementDataBaseDump.setString(35, dataBaseJsonObject.get("BSC CODE").toString());
				preparedStatementDataBaseDump.setString(36, dataBaseJsonObject.get("BSC NAME").toString());
				preparedStatementDataBaseDump.setString(37, dataBaseJsonObject.get("SM CODE").toString());
				preparedStatementDataBaseDump.setString(38, dataBaseJsonObject.get("SM NAME").toString());
				preparedStatementDataBaseDump.setString(39, dataBaseJsonObject.get("BRANCH CODE").toString());
				preparedStatementDataBaseDump.setString(40, dataBaseJsonObject.get("BRANCH NAME").toString());
				preparedStatementDataBaseDump.setString(41, dataBaseJsonObject.get("SUM INSURED").toString());
				
				preparedStatementDataBaseDump.setString(42, dataBaseJsonObject.get("RATE").toString());
				preparedStatementDataBaseDump.setString(43, dataBaseJsonObject.get("TXT_PROOF_OF_DOCUMENT").toString());
				preparedStatementDataBaseDump.setString(44, dataBaseJsonObject.get("Detariff Discount").toString());
				preparedStatementDataBaseDump.setString(45, dataBaseJsonObject.get("Fuel Type").toString());
				preparedStatementDataBaseDump.setString(46, dataBaseJsonObject.get("Age of Vehicle").toString());
				preparedStatementDataBaseDump.setString(47, dataBaseJsonObject.get("RTO Code").toString());
				preparedStatementDataBaseDump.setString(48, dataBaseJsonObject.get("Yes / No").toString());
				preparedStatementDataBaseDump.setString(49, dataBaseJsonObject.get("Sort Order").toString());
				preparedStatementDataBaseDump.setString(50, dataBaseJsonObject.get("Claim Status").toString());
				preparedStatementDataBaseDump.setString(51, dataBaseJsonObject.get("Letter Status").toString());
				preparedStatementDataBaseDump.setString(52, dataBaseJsonObject.get("Claim Type").toString());
				preparedStatementDataBaseDump.setString(53, dataBaseJsonObject.get("NCB Percent On Renewal").toString());
				preparedStatementDataBaseDump.setString(54, dataBaseJsonObject.get("Mail / Letter").toString());
				preparedStatementDataBaseDump.setString(55, dataBaseJsonObject.get("Date").toString());
				preparedStatementDataBaseDump.setString(56, dataBaseJsonObject.get("Today's DATe").toString());
				preparedStatementDataBaseDump.setString(57, dataBaseJsonObject.get("TAT").toString());
				preparedStatementDataBaseDump.setString(58, dataBaseJsonObject.get("Reminder sent date").toString());
				preparedStatementDataBaseDump.setString(59, dataBaseJsonObject.get("Revert Recd Y/N").toString());
				preparedStatementDataBaseDump.setString(60, dataBaseJsonObject.get("Revert received date").toString());
				preparedStatementDataBaseDump.setString(61, dataBaseJsonObject.get("Letter sent to insured").toString());
				preparedStatementDataBaseDump.setString(62, dataBaseJsonObject.get("Letter sent to insured Date").toString());
				preparedStatementDataBaseDump.setString(63, dataBaseJsonObject.get("Updation Date").toString());
				preparedStatementDataBaseDump.setString(64, dataBaseJsonObject.get("Data Extraction Date").toString());
				preparedStatementDataBaseDump.setInt(65, int_RequestIdPass);
				preparedStatementDataBaseDump.setInt(66, int_SourceIdPass);
				preparedStatementDataBaseDump.setString(67, "unfilter");
				preparedStatementDataBaseDump.executeUpdate();
			} // End IF
		} // End For Loop
	}// End Method
}// End Class
