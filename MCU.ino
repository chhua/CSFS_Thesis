#include <SoftwareSerial.h>
#include "Adafruit_HTU21DF.h"

#define SSID "####";
#define PASSWD "####"
  
// replace with your channel's thingspeak API key
String apiKey = "B4X3SHISS2FPVRM*";

// connect RX of Serial ~ USB
// connect TX of serial USB
SoftwareSerial ser(12,9); // TX, RX

Adafruit_HTU21DF htu = Adafruit_HTU21DF();

void setup() {                
  
  // enable debug serial
  Serial.begin(115200);
  htu.begin(); 
  
  // enable software serial
  ser.begin(115200);
  
  // reset ESP8266
  ser.println("AT+RST");
  
  // WiFi Info.
  String wifi="AT+CWJAP=\"";
  wifi+=SSID;
  wifi+="\",\"";
  wifi+=PASSWD;
  wifi+="\"";
  ser.println(wifi);
}


// the loop 
void loop() { 
  float t = htu.readTemperature();
  float h = htu.readHumidity();
  
  int P0Value = analogRead(A0);
  int P1Value = analogRead(A1);
  int P2Value = analogRead(A2);
  int P3Value = analogRead(A3);
  int P4Value = analogRead(A6);
  int P5Value = analogRead(A7);
  
  //sft r 1
  float P0voltage = P0Value * (5.0 / 1023.0);
  float P1voltage = P1Value * (5.0 / 1023.0);
  float P2voltage = P2Value * (5.0 / 1023.0);
  float P3voltage = P3Value * (5.0 / 1023.0);
  float P4voltage = P4Value * (5.0 / 1023.0);
  float P5voltage = P5Value * (5.0 / 1023.0);
  
  float P0Force;
  float P1Force;
  float P2Force;
  float P3Force;
  float P4Force;
  float P5Force;

  Serial.print("(");
  //p0  (312.5 > mmHg)
  if (P0voltage <= 0.16)
 {
    P0Force = (P0voltage * 259.862 );
    if(P0Force>7)
      Serial.print(P0Force);
    else{
      P0Force = 0.00;
      Serial.print(P0Force);
      }
 }
  else if (P0voltage>0.16 && P0voltage<5)
 {
    P0Force = (P0voltage * 331 ) ;
    Serial.print(P0Force);
 }
  else{
    Serial.print("999");
    }
     
Serial.print(",");

  //p1 
  if (P1voltage <= 0.16)
 {
    P1Force = (P1voltage * 259.862 ) ;
    if(P1Force>7)
      Serial.print(P1Force);
    else{
      P1Force = 0.00;
      Serial.print(P1Force);
      }
 }
  else if (P1voltage>0.16 && P1voltage<5)
 {
    P1Force = (P1voltage * 331 ) ;
    Serial.print(P1Force);
 }
  else{
    Serial.print("999");
    }
Serial.print(",");

  //p2 
    if (P2voltage <= 0.16)
 {
    P2Force = (P2voltage * 259.862 ) ;
    if(P2Force>7)
      Serial.print(P2Force);
    else{
      P2Force = 0.00;
      Serial.print(P2Force);
      }
 }
  else if (P2voltage>0.16 && P2voltage<5)
 {
    P2Force = (P2voltage * 331 ) ;
    Serial.print(P2Force);
 }
  else{
    Serial.print("999");
    }
Serial.print(",");

  //p3
    if (P3voltage <= 0.16)
 {
    P3Force = (P3voltage * 259.862 ) ;
    if(P3Force>7)
      Serial.print(P3Force);
    else{
      P3Force = 0.00;
      Serial.print(P3Force);
      }
 }
  else if (P3voltage>0.16 && P3voltage<5)
 {
    P3Force = (P3voltage * 331 ) ;
    Serial.print(P3Force);
 }
  else{
    Serial.print("999");
    }
Serial.print(",");

  //p4
    if (P4voltage <= 0.16)
 {
    P4Force = (P4voltage * 259.862 ) ;
    if(P4Force>7)
      Serial.print(P4Force);
    else{
      P4Force = 0.00;
      Serial.print(P4Force);
      }
 }
  else if (P4voltage>0.16 && P4voltage<5)
 {
    P4Force = (P4voltage * 331 ) ;
    Serial.print(P4Force);
 }
  else{
    Serial.print("999");
    }
Serial.print(",");

  //p5
   if (P5voltage <= 0.16)
 {
    P5Force = (P5voltage * 259.862 ) ;
    if(P5Force>7)
      Serial.print(P5Force);
    else{
      P5Force = 0.00;
      Serial.print(P5Force);
      }
 }
  else if (P5voltage>0.16 && P5voltage<5)
 {
    P5Force = (P5voltage * 331 ) ;
    Serial.print(P5Force);
 }
  else{
    Serial.print("999");
    }
  Serial.print(",");  Serial.print(t);
  Serial.print(",");  Serial.print(h);
  Serial.println(")");
  delay(200); 
  
  // convert to string
  char buf[32];
  // TCP connection
  String cmd = "AT+CIPSTART=\"TCP\",\"";
  cmd += "184.106.153.149"; // api.thingspeak.com
  cmd += "\",80";
  ser.println(cmd);
  // prepare GET string
  String getStr = "GET /update?api_key=";
  getStr += apiKey;
  getStr +="&field1=";
  getStr += String(P0Force);
  getStr +="&field2=";
  getStr += String(P1Force);
  getStr +="&field3=";
  getStr += String(P2Force);
  getStr +="&field4=";
  getStr += String(P3Force);
  getStr +="&field5=";
  getStr += String(P4Force);
  getStr +="&field6=";
  getStr += String(P5Force);
  getStr +="&field7=";
  getStr += String(t);
  getStr +="&field8=";
  getStr += String(h);
  getStr += "\r\n\r\n";

  // send data length
  cmd = "AT+CIPSEND=";
  cmd += String(getStr.length());
  ser.println(cmd);
  //ser.print(getStr);
  if(ser.find(">")){
    ser.print(getStr);
  }
  else{
    ser.println("AT+CIPCLOSE");
  }
}
 
