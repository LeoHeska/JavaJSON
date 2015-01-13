import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallAndOutline {
   String ofs; 
   
   String auth = "esapi_S3cr3t!";
   String URLText = "";
   String result = "";
   String ResultFileSpec = "";
   String rootKey;
   
   public static void main(String args[]) {
      try {
         CallAndOutline j = new CallAndOutline();
         j.setURL("http://extscoringapi.prod.corp.acxiom.net:8080/esapi/modellayout/input/");
         j.appendToURL("101091");
         j.setAuth("esapi_S3cr3t!");
         j.setOutFileSpec("c:\\temp\\tryout.txt");
         j.setRootKey("inputLayout");
         j.doIt();
      } catch (Exception e) { e.printStackTrace(); }
   }

   
   public String HelloWorld() {return "Hello, World!";}
   
   private String getJSON() {
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      
      result = "";
      
      try {
         url = new URL(URLText);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Authorization", auth);
         rd = new BufferedReader(new InputStreamReader(
                  conn.getInputStream()));
         
         while ((line = rd.readLine()) != null) { result += line; }
         
         rd.close();
      } catch (Exception e) { e.printStackTrace(); }
      
      return result;
   }

   public void setAuth(String instr) { auth = instr; }

   public void setURL(String instr) { URLText = instr; }

   public void appendToURL(String instr) { URLText = URLText + instr; }

   public void setResultFileSpec(String instr) {
      ResultFileSpec = instr;
   }

   public void setOutFileSpec(String iofs) {ofs = iofs;}

   public void setRootKey(String irk) {rootKey = irk;}
   
   public void doIt() throws Exception {
      CallAndOutline cao = new CallAndOutline();
      JSONOutliner jo = new JSONOutliner();
      cao.setURL("http://extscoringapi.prod.corp.acxiom.net:8080/esapi/modelinfo/full/pageSize=0;pageIndex=0");
      result = getJSON();
      System.out.println(result);
      // jo.init();
      jo.setJSON(result);
      jo.setOutlineFileName("c:\\temp\\outline1.txt");
      jo.setDebug();
      jo.doIt();
      jo.writeOutline("c:\\temp\\outline1.txt");
      
      // cao.setJSONString(result);
      // cao.setRootKey(rootKey); // Have to set this before ripping.
      // cao.rip();
      cao.setOutFileSpec(ofs);
      // cao.writeTable();
   }
}
