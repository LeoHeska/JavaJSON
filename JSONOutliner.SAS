options set=classpath 'g:\code\JARs\jackson-core-2.2.3.jar;g:\code\JARs\JSONOutliner.jar';
data _null_;
dcl javaobj j("JSONOutliner");
j.callVoidMethod("setJSON", "{""ipinfo"":  {""junk"": [1, 2, 3, 4, 5], ""junk1"": [], ""ip_address"": ""131.208.128.15"",""ip_type"": ""Mapped"",""Location"": {""continent"": ""north america"",""latitude"": 30.1,""longitude"": -81.714,""CountryData"": {""country"": ""united states"",""country_code"": ""us""},""region"": ""southeast"",""StateData"": {""state"": ""florida"",""state_code"": ""fl""},""CityData"": {""city"": ""fleming island"",""postal_code"": ""32003"",""time_zone"": -5}}}}");
j.callVoidMethod("init");
j.callVoidMethod("doIt");
j.callVoidMethod("writeOutline", "c:\\temp\\outline.txt");
run;
