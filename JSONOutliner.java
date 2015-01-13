import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Stack;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.fasterxml.jackson.core.*;

// import com.fasterxml.jackson.core.json.JsonReadContext;

public class JSONOutliner {
   // String json =
   // "{'ipinfo': {'ip_address': '131.208.128.15','ip_type': 'Mapped','Location': {'continent': 'north america','latitude': 30.1,'longitude': -81.714,'CountryData': {'country': 'united states','country_code': 'us'},'region': 'southeast','StateData': {'state': 'florida','state_code': 'fl'},'CityData': {'city': 'fleming island','postal_code': '32003','time_zone': -5}}}}";
   // String json =
   // "{\"ipinfo\": {\"ip_address\": \"131.208.128.15\",\"ip_type\": \"Mapped\",\"Location\": {\"continent\": \"north america\",\"latitude\": 30.1,\"longitude\": -81.714,\"CountryData\": {\"country\": \"united states\",\"country_code\": \"us\"},\"region\": \"southeast\",\"StateData\": {\"state\": \"florida\",\"state_code\": \"fl\"},\"CityData\": {\"city\": \"fleming island\",\"postal_code\": \"32003\",\"time_zone\": -5}}}}";
   // String json =
   // "{\"ipinfo\":  {\"junk\": [1, 2, 3, 4, 5], \"ip_address\": \"131.208.128.15\",\"ip_type\": \"Mapped\",\"Location\": {\"continent\": \"north america\",\"latitude\": 30.1,\"longitude\": -81.714,\"CountryData\": {\"country\": \"united states\",\"country_code\": \"us\"},\"region\": \"southeast\",\"StateData\": {\"state\": \"florida\",\"state_code\": \"fl\"},\"CityData\": {\"city\": \"fleming island\",\"postal_code\": \"32003\",\"time_zone\": -5}}}}";
   String json = "{\"ipinfo\":  {\"junk\": [1, 2, 3, 4, 5], \"junk1\": [], \"ip_address\": \"131.208.128.15\",\"ip_type\": \"Mapped\",\"Location\": {\"continent\": \"north america\",\"latitude\": 30.1,\"longitude\": -81.714,\"CountryData\": {\"country\": \"united states\",\"country_code\": \"us\"},\"region\": \"southeast\",\"StateData\": {\"state\": \"florida\",\"state_code\": \"fl\"},\"CityData\": {\"city\": \"fleming island\",\"postal_code\": \"32003\",\"time_zone\": -5}}}}";
   String outfilename;
   boolean d = false; // debug mode
   String CurrentName;
   String ValueAsStr;
   String CurrentType = "";
   String PreviousType = "";
   JsonStreamContext jrc;
   JsonToken jtoken;
   String TokenName;

   String tok1;
   String val1;
   int LastSectionDigit = 1; // The specific digit as in above.
   String OutParent = "";
   String outline = "";
   String outlineline = ""; // one line of the outline - sorry!
   ArrayList<String[]> lines;
   boolean wasField;
   boolean wasValue = false;
   boolean startedStruct = false; // Just started an array or object?

   Vector<Integer> CurrentSecNum = new Vector<Integer>();
   // This just holds the digits of the current section number.
   // So, if the outline looks like this so far/during processing:
   // 1
   // 1.1
   // 1.2
   // 1.3
   // 1.3.1
   // 1.3.2
   // 1.3.2.1
   // 1.4
   // 1.4.1
   // Then CurrentSecNum will hold:
   // [1, 4, 1]

   Stack<Integer> ObjStartPos = new Stack<Integer>();
   // This holds levels of beginnings of objects. When dealing with
   // JSON objects, when encountering START_OBJECT, push the current
   // level onto the stack. When reaching END_OBJECT, to find the
   // we should be at, just pop the level/value off the stack.
   // Convenient when dealing with complicated nested/embedded
   // JSON objects.

   Stack<Integer> ArrStartPos = new Stack<Integer>();
   Map<String, String> RToT; // Means "Return value To Type"
   JsonFactory f;
   JsonParser jp;

   public static void main(String[] args) {
      JSONOutliner j = new JSONOutliner();
      j.setJSON("{\"ipinfo\":  {\"junk\": [1, 2, 3, 4, 5], \"junk1\": [], \"ip_address\": \"131.208.128.15\",\"ip_type\": \"Mapped\",\"Location\": {\"continent\": \"north america\",\"latitude\": 30.1,\"longitude\": -81.714,\"CountryData\": {\"country\": \"united states\",\"country_code\": \"us\"},\"region\": \"southeast\",\"StateData\": {\"state\": \"florida\",\"state_code\": \"fl\"},\"CityData\": {\"city\": \"fleming island\",\"postal_code\": \"32003\",\"time_zone\": -5}}}}");
      j.init();
      j.doIt();
      j.writeOutline("c:\\temp\\outline.txt");
   }

   public void setOutlineFileName(String fn) {
      outfilename = fn;
   }

   public void setDebug() {
      d = true;
   }

   
   public void setJSON(String ij) {
      json = ij;
   }

   
   void init() {
      try {
         RToT = new HashMap<String, String>();
         RToT.put("VALUE_TRUE", "boolean");
         RToT.put("VALUE_FALSE", "boolean");
         RToT.put("VALUE_NULL", "null");
         RToT.put("VALUE_NUMBER_FLOAT", "float");
         RToT.put("VALUE_NUMBER_INT", "int");
         RToT.put("VALUE_STRING", "string");

         f = new JsonFactory();
         jp = f.createJsonParser(json);
         ValueAsStr = jp.getValueAsString();
         CurrentName = jp.getCurrentName();
         System.out.println("Here at 1.");
         jrc = jp.getParsingContext();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void doIt() {
      try {
         
         init();
         
         if (d) System.out.println("At beginning.");

         if (json.length() > 1000000)
            throw new Exception("JSON too large");

         CurrentType = jrc.getTypeDesc();
         
         if (d) {
         System.out.println("Type: " + CurrentType);
         System.out.println("First CurrentName: " + CurrentName);
         System.out.println("First ValueAsStr: " + ValueAsStr);
         }

         int i = 0;
         jtoken = jp.nextToken();
         while (jtoken != null) {
            PreviousType = CurrentType;
            CurrentName = jp.getCurrentName();
            ValueAsStr = jp.getValueAsString();
            jrc = jp.getParsingContext();
            TokenName = jtoken.name();
            CurrentType = jrc.getTypeDesc();

            if (d) {
            System.out.println();
            System.out.println("Token name: " + TokenName);
            System.out.println("Start of element " + ++i + ".");
            System.out.println("CurrentName: " + CurrentName);
            System.out.println("ValueAsStr: " + ValueAsStr);
            System.out.println("Type: " + CurrentType);
            }

            buildAndWrite();

            jtoken = jp.nextToken(); // On to the next value, field,
            // or beginning or end of object or array.
         }

         jp.close();
         
         if (d) {
         System.out.println();
         System.out.println(outline);
         }
         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   void buildAndWrite() {
      try {
         outlineline = "";

         // TokenName can have only several values:
         //
         // START_ARRAY is returned when encountering '[' which signals 
         // starting of an Array value
         if (TokenName.equals("START_ARRAY")) {
            int arrstart = CurrentSecNum.size(); 
            ArrStartPos.push(arrstart);
            if (d) System.out.println("Array starts at level " + arrstart);
            wasField = false;
            wasValue = false;
            startedStruct = true;
            CurrentSecNum.add(1);

            for (int i = 0; i < CurrentSecNum.size(); i++)
               outlineline += CurrentSecNum.get(i) + ".";

            outlineline = outlineline.substring(0, outlineline.length() - 1);
            outlineline = outlineline + "|array|";

            outline += outlineline + "\n";
            if (d) System.out.println(outlineline);
         }
         //
         // END_ARRAY is returned when encountering ']' which signals 
         // ending of an Array value
         else if (TokenName.equals("END_ARRAY")) {
            wasField = false;
            wasValue = false;
            startedStruct = false;
            
            int gotolevel = ArrStartPos.pop();
            
            while (CurrentSecNum.size() > gotolevel)
               CurrentSecNum.remove(CurrentSecNum.size() - 1);

            incLastSecDigit();
         }

         // START_OBJECT is returned when encountering '{' which signals
         // starting of an Object value.
         else if (TokenName.equals("START_OBJECT")) {
            wasField = false;
            wasValue = false;
            startedStruct = true;
            
            int objstart = CurrentSecNum.size(); 
            ObjStartPos.push(objstart);
            if (d) System.out.println("Object starts at level " + objstart);
            
            CurrentSecNum.add(1);

            outlineline = "";

            for (int j = 0; j < CurrentSecNum.size(); j++)
               outlineline += CurrentSecNum.get(j) + ".";

            outlineline = outlineline.substring(0, outlineline.length() - 1);
            outlineline += "|object|";

            outline += outlineline + "\n";
            if (d) System.out.println(outlineline);
         }

         // END_OBJECT is returned when encountering '}' 
         // which signals ending of an Object value
         else if (TokenName.equals("END_OBJECT")) {
            wasField = false;
            wasValue = false;
            startedStruct = false;

            int gotolevel = ObjStartPos.pop();
            // System.out.println("gotolevel:" + gotolevel);            
            while (CurrentSecNum.size() > gotolevel)
               CurrentSecNum.remove(CurrentSecNum.size() - 1);
  
            if (CurrentSecNum.size() > 0) // Don't do this at end
               incLastSecDigit();         // when struct is empty.
         }

         // FIELD_NAME is returned when a String token is encountered as
         // a field name (same lexical value, different function)
         else if (TokenName.equals("FIELD_NAME")) {
            outlineline = "";

            if (wasValue) {
               CurrentSecNum.remove(CurrentSecNum.size() - 1);
               incLastSecDigit();
            }

            else if (startedStruct) CurrentSecNum.add(1);

            wasField = true;
            wasValue = false;
            startedStruct = false;

            for (int j = 0; j < CurrentSecNum.size(); j++)
               outlineline += CurrentSecNum.get(j) + ".";

            outlineline = outlineline.substring(0, outlineline.length() - 1);

            outlineline += "|field|" + CurrentName;
            outline += outlineline + "\n";
            if (d) System.out.println(outlineline);
         }

         //
         // NOT_AVAILABLE can be returned if JsonParser implementation can not
         // currently return the requested token (usually next one), or even if
         // any will be available, but that may be able to determine this in
         // future.
         // In this case, blow up.
         else if (TokenName.equals("NOT_AVAILABLE")) throw new Exception(
                  "JSON Parsing error.");
         //
         // VALUE_EMBEDDED_OBJECT Placeholder token returned when the input
         // source has a concept of embedded Object that are not accessible as
         // usual structure (of starting with START_OBJECT, having values,
         // ending with END_OBJECT), but as "raw" objects.
         // Note: this token is never returned by regular JSON readers, but only
         // by readers that expose other kinds of source (like Maps, Lists and
         // such).
         // So if this happens, blow up.
         else if (TokenName.equals("VALUE_EMBEDDED_OBJECT")) throw new Exception(
                  "Not JSON Reader.");

         // VALUE_TRUE is returned when encountering literal "true" in value
         // context
         // VALUE_FALSE is returned when encountering literal "false" in value
         // context
         // VALUE_NULL is returned when encountering literal "null" in value
         // context
         // VALUE_NUMBER_FLOAT is returned when a numeric token that is not an
         // integer is encountered: that is, a number that does have floating
         // point or exponent marker in it, in addition to one or more digits.
         // VALUE_NUMBER_INT is returned when an integer numeric token is
         // encountered in value context: that is, a number that does not have
         // floating point or exponent marker in it (consists only of an
         // optional sign, followed by one or more digits)
         // VALUE_STRING is returned when a String token is encountered in value
         // context (array element, field value, or root-level stand-alone
         // value)
         else if (TokenName.equals("VALUE_TRUE")
                  || TokenName.equals("VALUE_FALSE")
                  || TokenName.equals("VALUE_STRING")
                  || TokenName.equals("VALUE_NULL")
                  || TokenName.equals("VALUE_NUMBER_FLOAT")
                  || TokenName.equals("VALUE_NUMBER_INT")) {

            wasField = false;

            if (wasValue) incLastSecDigit();
            else CurrentSecNum.add(1);

            wasValue = true;


            outlineline = "";

            for (int j = 0; j < CurrentSecNum.size(); j++)
               outlineline += CurrentSecNum.get(j) + ".";

            outlineline = 
                     outlineline.substring(0, outlineline.length() - 1);
            
            outlineline += "|" + RToT.get(TokenName) + "|" + ValueAsStr;
            
            outline += outlineline + "\n";
            if (d) System.out.println(outlineline);
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   void incLastSecDigit() {
      int curval;
      int nxtval;
      int lastpos;

      // System.out.println("CSN size: " + CurrentSecNum.size());
      lastpos = CurrentSecNum.size() - 1;
      // System.out.println("lastpos: " + lastpos);
      curval = CurrentSecNum.get(CurrentSecNum.size() - 1);
      // System.out.println("curval: " + curval);
      nxtval = curval + 1;
      // System.out.println("nxtval: " + nxtval);

      CurrentSecNum.setElementAt(nxtval, lastpos);

   }
   
   void writeOutline(String fn) { try {
      File file = new File(fn);
      if (!file.exists()) file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(outline);
      bw.close();
   } catch (Exception e) {e.printStackTrace();}
   }

}
