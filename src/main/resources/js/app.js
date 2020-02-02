//inspired by Diogo Souza: https://www.htmlgoodies.com/html5/getting-started-with-websockets.html

//TODO close socket properly from server when socket.close is called.
//TODO add board to display the game.

//aaanpak eerst backend
// test als het moeilijke stukken komt.
// frontend verder toen ik op backend vastliep qua communicatie.
//mooie echo server is het nu.

var form = document.getElementById('form-msg');
var txtMsg = document.getElementById('txtMsg');
var listMsgs = document.getElementById('messages');
var socketStatus = document.getElementById('status');
var btnDisconnect = document.getElementById('close');
var socket;

function init() {
    socket = new WebSocket("ws://localhost:8080/chat", "chat");

    socket.onopen = function (event) {
        displayOpenStatus();
    };

    socket.onclose = function (event) {
        console.log('Web Socket Connection Closed');
        displayCloseStatus();
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

    btnDisconnect.innerText = "Disconnect";
}

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
        listMsgs.innerHTML = "";
        displayCloseStatus();
        btnDisconnect.innerText = "Connect";
    } else {
        init();
    }
};

window.onbeforeunload = function () {
    if (websocket instanceof WebSocket) {
        websocket.onclose = function () {
        }; // disable onclose handler first
        websocket.close();
    }
};


function displayCloseStatus() {
    socketStatus.innerHTML = 'Disconnected from the WebSocket.';
    socketStatus.className = 'closed';
}

function displayOpenStatus() {
    socketStatus.innerHTML = 'Connected to: /chat';
    socketStatus.className = 'open';
}


init();