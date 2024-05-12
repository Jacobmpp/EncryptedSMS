# Encrypted SMS
An encrypted messaging app that uses SMS.
### Original Team members
- Landon Moon
- Jacob Holz
- Gilbert Lavin
- Parker Steach
- Nam Huynh
## Why?
Internet messaging is structurally trust based. You have to trust that whatever system you are using to transmit your messages does not have a backdoor to decrypt the messages if asked or infiltrated. With SMS you know for a fact that any raw data sent over SMS is effectively public if requested legally or sniffed from the air. If we cannot trust any part of the transmission medium, we can only have end to end security. Creating a system to send and receive encrypted messages over SMS means that we as the developers of the program have no control over the way that the data is routed. This gives us no opportunity to leak unencrypted data or decrypt your messages even if asked.
## How?
Everything lives on your device and the only thing that ever leaves is securely encrypted text over SMS (assuming you select a secure encryption key and share it securely with the other member of a conversation and leave AES as the encryption algorithm). The application does not use the internet at all. If you would like to, it is relatively reasonable to check that the application simply runs the specified algorithm on the given text with the given key and initial vectors where applicable.
## Limitations
The only algorithms that are currently available are symmetric. These are far less computationally intensive and secure, but they require you to find somewhat to securely communicate the key to each other outside of the application. The reason for avoiding asymmetric methods lies in how they are made secure. All simple 2 system asymmetric encryption methods are vulnerable to man-in-the-middle attacks because the first step of exchanging public information could be interfered with at a certain level of sophistication especially over such an insecure medium as SMS. The way that secure encrypted communication systems usually get around this is the use of certificate services, but those are only available online, and this system explicitly does not use the internet as a security feature. If you trust that your line is not being tampered with at a given time, we welcome you to run the algorithm manually with the person on the other side over plain text, but there is no built-in system for this because we can not guarantee that it would be secure.
Note that it is still possible to see that you have been communicating with someone. The only thing that is protected is the contents of the messages.
## Bonus
Another attribute of the application is that all saved data is encrypted. If you set a secure password for the application, any encryption keys saved in the application will also be protected even if the device is accessed without your permission. Do be aware that the unencrypted data exists in memory while the app is running, so take any precautions you wish to avoid having it accessed against your wishes while it is still running.
