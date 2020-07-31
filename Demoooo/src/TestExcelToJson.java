import org.json.simple.JSONObject;

import com.jsonObject.JsonConversion;

public class TestExcelToJson {

	public static void main(String[] args) throws Exception {
		 
	JSONObject	jsonObject = JsonConversion.creteJSONFromExcel("E:\\10.139.186.169\\OpsShare\\CPC_Retail\\NCB\\222.xlsx",1,2);
			System.out.println("DataBase Dump Successful");
			
       PSUNCBDataDumpInDataBase.readJsonAndDumpIntoDB(jsonObject, 1, 2);

	}

}
