var notificationCount = 0;
var notifications = [];

function connectNotifications() {
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/notifications', function (notification) {
            notificationCount++;
            document.getElementById('notificationCount').innerText = notificationCount;
            notifications.push(JSON.parse(notification.body));
        });
    });
}

function toggleNotifications() {
    var notificationList = document.getElementById('notificationList');
    if (notificationList.style.display === 'none') {
        notificationList.style.display = 'block';
        updateNotificationList();
    } else {
        notificationList.style.display = 'none';
    }
}

function updateNotificationList() {
    var notificationList = document.getElementById('notifications');
    notificationList.innerHTML = '';
    notifications.forEach(function (notification) {
        var li = document.createElement('li');
        li.innerText = notification.content;
        notificationList.appendChild(li);
    });
}

document.getElementById('bellIcon').addEventListener('click', toggleNotifications);

connectNotifications();