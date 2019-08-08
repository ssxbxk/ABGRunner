@echo off
cp C:\Project\ABGRunner\app\build\outputs\apk\debug\app-debug.apk app-debug.apk
java -jar signapk.jar platform.x509.pem platform.pk8 app-debug.apk app-debug-sign.apk