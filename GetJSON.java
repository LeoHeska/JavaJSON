import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Map.Entry;

public class GetJSON {

   String auth = "esapi_S3cr3t!";
   String URLText = "";
   String result = "";
   String ResultFileSpec = "";
   
   private String getHTML(String urlToRead) {
      URLText = urlToRead;
      return getHTML();
   }

   protected String getHTML() {
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      
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

}
