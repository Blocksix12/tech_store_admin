/* ============================================
   ADMIN LAYOUT - SHARED JAVASCRIPT
============================================ */

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
        sidebar && sidebar.classList.contains('show') &&
        !sidebar.contains(event.target) &&
        !menuBtn?.contains(event.target)) {
        sidebar.classList.remove('show');
    }
});

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
    if (submenu) {
        submenu.classList.toggle('show');
        element.classList.toggle('expanded');
    }
}

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

// Show loading spinner
function showLoading() {
    const loadingHtml = `
        <div id="loadingOverlay" style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
        ">
            <div class="spinner-border text-light" role="status" style="width: 3rem; height: 3rem;">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', loadingHtml);
}

// Hide loading spinner
function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

// Show toast notification
function showToast(message, type = 'success') {
    const toastHtml = `
        <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
            <div class="toast show align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', toastHtml);

    // Auto remove after 3 seconds
    setTimeout(() => {
        const toastContainer = document.querySelector('.toast-container');
        if (toastContainer) {
            toastContainer.remove();
        }
    }, 3000);
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    }).format(date);
}

// Confirm dialog
function confirmDialog(message, callback) {
    if (confirm(message)) {
        callback();
    }
}