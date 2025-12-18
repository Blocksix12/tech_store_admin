// Toggle submenu with smooth animation
function toggleSubmenu(element) {
    const submenu = element.nextElementSibling;
    const allSubmenus = document.querySelectorAll('.submenu');
    const allMenuItems = document.querySelectorAll('.menu-item[onclick]');

    // Close other submenus
    allSubmenus.forEach(sm => {
        if (sm !== submenu && sm.classList.contains('show')) {
            sm.classList.remove('show');
        }
    });

    // Remove expanded class from other menu items
    allMenuItems.forEach(mi => {
        if (mi !== element && mi.classList.contains('expanded')) {
            mi.classList.remove('expanded');
        }
    });

    // Toggle current submenu
    submenu.classList.toggle('show');
    element.classList.toggle('expanded');
}

// Toggle sidebar on mobile
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('show');
}

// Close sidebar when clicking outside on mobile
document.addEventListener('click', function(event) {
    const sidebar = document.getElementById('sidebar');
    const menuBtn = document.querySelector('.mobile-menu-btn');

    if (window.innerWidth <= 768 &&
        sidebar.classList.contains('show') &&
        !sidebar.contains(event.target) &&
        !menuBtn?.contains(event.target)) {
        sidebar.classList.remove('show');
    }
});