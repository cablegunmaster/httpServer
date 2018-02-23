#HttpServer aanpak
Aanpak van de HttpServer. 
##Request

###Timeout
1. Wacht tot max 30 seconde op een verzoek ( hoe een thread max 30 sec leeft, nog uitzoeken)
https://docs.oracle.com/javase/8/docs/api/java/net/Socket.html#connect-java.net.SocketAddress-int-
Socket timeout mee geven, verbinding stopt na X aantal seconden.

2. Parse de Requestline deze mag: “Action” Spatie “Path” Spatie “HTTP/1.1” bevatten

Action (enum) 8 types.
Path MaxLength nog uitzoeken. ( http://httpd.apache.org/docs/2.2/mod/core.html#limitrequestline ) 4094 bytes.
Keyword:"HTTP/1.1" eindigt met /r/n Als dit niet zo is dan header mismatch Exception.  
Afvangen, stoppen met verder het request te lezen en code header mismatch terugsturen.

Headers parsen op "Key": "Value"  lijst met mogelijke Headers in een headerType.java enum zetten.
Als die niet voorkomen (unknown header code, header mismatch exception ) afvangen , en terugsturen met status code XXX.

###Max Length RequestHeader
als de header groter is dan X aantal (x nog opzoeken )dan krijg je een 413 
 "The server is refusing to process a request,
  because the request payload is larger 
  than the server is willing or able to process."
https://httpstatuses.com/413

Headers bevatten \r\n als einde van de regel.

Value length van een Header, als deze langer is dan X of ongeldige karakters bevat (uitzoeken welke kunnen) 
als langer dan toegestaan (Header value mismatch exception ) response terugsturen met status code XXX.

###re-usage of Threads.
ik zit nu met het probleem dat 1 thread, 1 socket verbinding aan kan.
Socket switched niet van verbinding, Buffereren? op een bepaalde manier.
pas uitlezen als het klaar is met schrijven, of timeout heeft bereikt.
Teveel weggeschreven in de buffer door 1 verbinding, 
(exception Buffer_overflow) status code teruggeven, en verbinding met dat socket sluiten.

###Maximum aantal verbindingen
Aantal verbindingen maximaal, Httpconnecties,
Als de connection pool limiet is overschreden dan krijg je een 503 terug van de Server.
Uitvoering: Accept socket, niet uitlezen (overslaan) en gelijk 503 status code terugsturen.

##response
Een header status met een response status code.
Content-length (als er content is)
Header regel eindigt met \r\n
Headers eindigen met \r\n\r\n

Bron:
https://tools.ietf.org/html/rfc7230
