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

// Add smooth scroll behavior
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Animate stats on page load
window.addEventListener('load', function() {
    const statValues = document.querySelectorAll('.stat-value');
    statValues.forEach((stat, index) => {
        setTimeout(() => {
            stat.style.opacity = '0';
            stat.style.transform = 'translateY(10px)';

            setTimeout(() => {
                stat.style.transition = 'all 0.6s ease';
                stat.style.opacity = '1';
                stat.style.transform = 'translateY(0)';
            }, 50);
        }, index * 100);
    });
});

// Handle dropdown menus
document.addEventListener('DOMContentLoaded', function() {
    const dropdowns = document.querySelectorAll('[data-bs-toggle="dropdown"]');
    dropdowns.forEach(dropdown => {
        dropdown.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    });
});