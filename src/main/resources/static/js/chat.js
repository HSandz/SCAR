// Declare WebSocket variables
let stompClient = null;
let username = 'Anonymous';
let profilePictureUrl = 'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Ffree-icon%2Fsmile_747402&psig=AOvVaw0RZ_wrHy1l1y7vKIEmuvyO&ust=1731946566539000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCKj7uPHh44kDFQAAAAAdAAAAABAE';

// Connect to the WebSocket server
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Retrieve username and profilePictureUrl from handshake attributes
        const headers = frame.headers;
        if (headers['username']) {
            username = headers['username'];
        }
        if (headers['profilePictureUrl']) {
            profilePictureUrl = headers['profilePictureUrl'];
        }

        // Subscribe to the public topic
        stompClient.subscribe('/topic/public', function (messageOutput) {
            showMessage(JSON.parse(messageOutput.body));
        });

        // Notify server of a new user
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({}));
    });
}

// Send a chat message
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
        document.getElementById('userMessage').value = ''; // Clear the input field
    }
}

// Display a received message in the chat box
function showMessage(message) {
    const chatBox = document.getElementById('chat-box');
    const messageElement = document.createElement('div');

    messageElement.classList.add('message');
    messageElement.classList.add(message.sender === username ? 'user-message' : 'bot-message');

    messageElement.innerHTML = `
        <img src="${message.profilePictureUrl}" alt="${message.sender}'s profile picture" class="profile-picture">
        <strong>${message.sender}:</strong> <span>${message.content}</span>
    `;
    chatBox.appendChild(messageElement);

    // Automatically scroll to the latest message
    chatBox.scrollTop = chatBox.scrollHeight;
}

// Cleanup WebSocket on page unload
window.addEventListener('beforeunload', function () {
    if (stompClient) {
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({ type: 'LEAVE' }));
    }
});

// Form submission handler for sending messages
document.getElementById('chat-form').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent default form submission
    sendMessage();
});

// Connect to WebSocket when the page loads
connect();