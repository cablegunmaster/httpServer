# httpServer
This project contains a HTTP server in beta state.
Its not meant for production but purely made to learn how the http protocol works in a hands on approach, 
by building unit tests seeing them fail and looking at the specs on how a HTTP Server should behave.

Its made for learning purposes on how to implement a protocol and become better at understanding Java/HTTP/Sockets.

Working:
- GET requests.
- Upgrade requests.
- Socket Echo requests.
- retrieving files on resource location.

Todo:
- POST request with form data.
- POST with binary data.
- check if the single frame DROP is implemented in a good way
- check multiframe Socket requests. If 1 message exist of multiple frames, does it go back to 1 message and handles on a right way?
