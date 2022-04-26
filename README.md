# Simaster-Presensi
Simaster Presence is an application to make it easier for users, especially UGM students when they will do a Qr Code attendance, because when students are outside the room and there is only one device students cannot make attendance.

With this application, I hope it can make it easier for UGM students to make attendance.

# How to use
Steps:
1. Download Simaster Presensi from this repo's releases page.
2. Install application then login with `UGM Account`.
3. Scan Qr with camera or select image from gallery.
4. The attendance was successful.

# Installation

You must have auth basic qrcode to compile this application, you can get it by decompile simaster application from playstore
```
AUTH_BASIC_QRCODE=$AUTH_BASIC_QRCODE
```
Change $AUTH_BASIC_QRCODE with the key you have and put the above code in `local.properties`

# Warning
If you use this application and log in using a `UGM account`, your Simster will not be able to be used ( must login back ) because the existing session in the Simaster is invalid, then you must logout and login back if you want to use the Simaster as before.
