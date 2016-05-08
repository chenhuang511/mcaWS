# mcaWS
Digital signing webservice for Viettel mobile CA


#Signing Integration

*Step1: call Upload file webservice
Using Httppost to call post file webservice: $address/MobileCA-WS/file/upload/
Ex: https://github.com/chenhuang511/mcaWS/blob/master/WebContent/FileUpload.html
The result: name of file that is saved on server

*Step2: call sign service
Using Httpget: $address/MobileCA-WS/sign/{phoneNumber}/{fileName}
with:
  phoneNumber: is user's moblie CA phone number
  fileName: the result of step 1. Valid file extentions are: pdf, xlsx, docx, xml.
  
The result: base64 string of signed file
