let stompClient = null;
let username = 'Anonymous';
let profilePictureUrl = 'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Ffree-icon%2Fsmile_747402&psig=AOvVaw0RZ_wrHy1l1y7vKIEmuvyO&ust=1731946566539000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCKj7uPHh44kDFQAAAAAdAAAAABAE';

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        const headers = frame.headers;
        if (headers['username']) {
            username = headers['username'];
        }
        if (headers['profilePictureUrl']) {
            profilePictureUrl = headers['profilePictureUrl'];
        }

        stompClient.subscribe('/topic/chat', function (messageOutput) {
            showMessage(JSON.parse(messageOutput.body));
        });

        stompClient.send("/app/chat.addUser", {}, JSON.stringify({}));
    });
}

function sendMessage() {
    const messageContent = document.getElementById('userMessage').value;

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT',
            profilePictureUrl: profilePictureUrl
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        document.getElementById('userMessage').value = '';
    }
}

function showMessage(message) {
    const chatBox = document.getElementById('chat-box');
    if (!chatBox) {
        console.error('Chat box element not found');
        return;
    }

    const messageElement = document.createElement('div');
    messageElement.classList.add('message');
    messageElement.classList.add(message.sender === username ? 'user-message' : 'bot-message');

    messageElement.innerHTML = `
        <img src="${message.profilePictureUrl}" alt="${message.sender}'s profile picture" class="profile-picture">
        <strong>${message.sender}:</strong> <span>${message.content}</span>
    `;
    chatBox.appendChild(messageElement);

    chatBox.scrollTop = chatBox.scrollHeight;
    console.log('Message appended to chat box:', message);
}

window.addEventListener('beforeunload', function () {
    if (stompClient) {
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({ type: 'LEAVE' }));
    }
});

document.getElementById('chat-form').addEventListener('submit', function (event) {
    event.preventDefault();
    sendMessage();
});

connect();