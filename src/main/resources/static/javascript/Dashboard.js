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

// Toggle Submenu
function toggleSubmenu(element) {
    const submenu = element.nextElementSibling;
    const icon = element.querySelector('.bi-chevron-down');

    if (submenu && submenu.classList.contains('submenu')) {
        submenu.classList.toggle('show');
        element.classList.toggle('expanded');

        if (icon) {
            icon.style.transform = submenu.classList.contains('show') ? 'rotate(180deg)' : 'rotate(0deg)';
        }
    }
}

// Toggle Sidebar cho mobile
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('show');
    }
}

// Auto-expand submenu nếu đang ở trang con
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;

    // Tìm tất cả submenu items
    const submenuItems = document.querySelectorAll('.submenu-item');

    submenuItems.forEach(item => {
        const href = item.getAttribute('href');

        // Nếu URL hiện tại khớp với href của submenu item
        if (href && currentPath.includes(href)) {
            // Thêm class active cho submenu item
            item.classList.add('active');

            // Tìm parent submenu và mở nó
            const parentSubmenu = item.closest('.submenu');
            if (parentSubmenu) {
                parentSubmenu.classList.add('show');

                // Tìm menu item cha và expand nó
                const parentMenuItem = parentSubmenu.previousElementSibling;
                if (parentMenuItem && parentMenuItem.classList.contains('menu-item')) {
                    parentMenuItem.classList.add('expanded');

                    // Rotate chevron icon
                    const icon = parentMenuItem.querySelector('.bi-chevron-down');
                    if (icon) {
                        icon.style.transform = 'rotate(180deg)';
                    }
                }
            }
        }
    });

    // Xử lý cho menu items không có submenu
    const menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath.includes(href) && !item.querySelector('.bi-chevron-down')) {
            item.classList.add('active');
        }
    });
});

// Close sidebar khi click outside trên mobile
document.addEventListener('click', function(event) {
    const sidebar = document.getElementById('sidebar');
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');

    if (sidebar && sidebar.classList.contains('show')) {
        if (!sidebar.contains(event.target) && event.target !== mobileMenuBtn) {
            sidebar.classList.remove('show');
        }
    }
});


document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    // Tìm tất cả submenu item
    document.querySelectorAll(".submenu-item").forEach(item => {
        if (item.getAttribute("href") === currentPath) {

            // active item
            item.classList.add("active");

            // mở submenu cha
            const submenu = item.closest(".submenu");
            if (submenu) {
                submenu.style.display = "block";

                const parentMenuItem = submenu.previousElementSibling;
                if (parentMenuItem) {
                    parentMenuItem.classList.add("open");
                }
            }
        }
    });
});
