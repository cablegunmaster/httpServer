//inspired by Diogo Souza: https://www.htmlgoodies.com/html5/getting-started-with-websockets.html
var form = document.getElementById('form-msg');
var txtMsg = document.getElementById('txtMsg');
var listMsgs = document.getElementById('messages');
var socketStatus = document.getElementById('status');
var btnDisconnect = document.getElementById('close');

let socket = new WebSocket("ws://localhost:8080/chat", "chat");

socket.onopen = function (event) {
    socketStatus.innerHTML = 'Connected to: /chat';
    socketStatus.className = 'open';
};

socket.onclose = function (event) {
    socketStatus.innerHTML = 'Disconnected from the WebSocket.';
    socketStatus.className = 'closed';
};

socket.onerror = function (error) {
    console.log('WebSocket error: ' + error);
};

socket.onmessage = function (event) {
    //TODO add handler for the Mancala game.
    if (socket.readyState === WebSocket.OPEN) {
        var msg = event.data;
        listMsgs.innerHTML += '<li class="received"><span>Received:</span>' + msg + '</li>';
    }
};

form.onsubmit = function (e) {
    e.preventDefault();

    if (socket.readyState === WebSocket.OPEN) {
        var msg = txtMsg.value;
        socket.send(msg);
        listMsgs.innerHTML += '<li class="sent"><span>Sent:</span>' + msg + '</li>';
    }
    txtMsg.value = '';
    return false;
};

btnDisconnect.onclick = function (e) {
    if (socket.readyState === WebSocket.OPEN) {
        socket.close();
    }else{
        socket = new WebSocket("ws://localhost:8080/chat", "chat");
    }
};

window.onclose = function () {
    socket.send("disconnect");
    socket.close();
};