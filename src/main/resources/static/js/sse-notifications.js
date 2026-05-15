(function () {
    var evtSource = new EventSource('/notifications/stream');

    function updateBadge(count) {
        var bell = document.querySelector('.nav-bell');
        if (!bell) return;

        var badge = bell.querySelector('.bell-badge');

        if (count > 0) {
            if (!badge) {
                badge = document.createElement('span');
                badge.className = 'bell-badge';
                bell.appendChild(badge);
            }
            badge.textContent = count > 99 ? '99+' : String(count);
            badge.style.display = 'flex';
        } else {
            if (badge) badge.style.display = 'none';
        }
    }

    evtSource.addEventListener('notification-count', function (e) {
        updateBadge(parseInt(e.data, 10));
    });

    // Clean up the connection when the user leaves the page
    window.addEventListener('beforeunload', function () {
        evtSource.close();
    });
})();
