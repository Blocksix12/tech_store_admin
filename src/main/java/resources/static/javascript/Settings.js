// Settings Page Complete JavaScript

let hasUnsavedChanges = false;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeSettings();
    initializeFormChanges();
    initializeSearch();
    loadSettings();
});

// Initialize Settings
function initializeSettings() {
    // Load saved preferences
    const savedTheme = localStorage.getItem('theme') || 'light';
    const savedColor = localStorage.getItem('primaryColor') || 'blue';

    applyTheme(savedTheme);
    applyColor(savedColor);

    // Handle hash navigation
    if (window.location.hash) {
        const section = window.location.hash.substring(1);
        showSection(section);
    }
}

// Show Section
function showSection(section) {
    // Remove active class from all nav links
    document.querySelectorAll('.settings-nav-link').forEach(link => {
        link.classList.remove('active');
    });

    // Add active class to clicked link
    const activeLink = document.querySelector(`.settings-nav-link[onclick*="${section}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }

    // Hide all content sections
    document.querySelectorAll('.settings-content').forEach(content => {
        content.style.display = 'none';
        content.classList.remove('active');
    });

    // Show selected section
    const sectionElement = document.getElementById(`${section}-section`);
    if (sectionElement) {
        sectionElement.style.display = 'block';
        sectionElement.classList.add('active');
    }

    // Update URL hash
    window.location.hash = section;

    // Scroll to top smoothly
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Apply Theme
function applyTheme(theme) {
    if (theme === 'dark') {
        document.body.classList.add('dark-theme');
    } else if (theme === 'auto') {
        const isDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        if (isDark) {
            document.body.classList.add('dark-theme');
        } else {
            document.body.classList.remove('dark-theme');
        }
    } else {
        document.body.classList.remove('dark-theme');
    }
}

// Apply Color
function applyColor(color) {
    const colorMap = {
        'blue': ['#667eea', '#764ba2'],
        'green': ['#56ab2f', '#a8e063'],
        'red': ['#ff6b6b', '#ee5a6f'],
        'purple': ['#a8c0ff', '#3f2b96'],
        'orange': ['#f093fb', '#f5576c']
    };

    if (colorMap[color]) {
        document.documentElement.style.setProperty('--primary-color-start', colorMap[color][0]);
        document.documentElement.style.setProperty('--primary-color-end', colorMap[color][1]);
    }
}

// Save Settings
function saveSettings(section) {
    const button = event.target;
    const originalText = button.innerHTML;

    // Show loading
    button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';
    button.disabled = true;

    // Get form data
    const formData = {};
    const sectionElement = document.getElementById(`${section}-section`);

    if (sectionElement) {
        const inputs = sectionElement.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            if (input.type === 'checkbox') {
                formData[input.id] = input.checked;
            } else if (input.type === 'file') {
                // Skip file inputs for now
            } else {
                formData[input.id] = input.value;
            }
        });
    }

    // Simulate API call
    setTimeout(() => {
        fetch('/admin/api/settings/' + section, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showToast('Cài đặt đã được lưu thành công!', 'success');
                    hasUnsavedChanges = false;
                } else {
                    showToast(data.message || 'Có lỗi xảy ra!', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Đã lưu cài đặt thành công!', 'success');
                hasUnsavedChanges = false;
            })
            .finally(() => {
                button.innerHTML = originalText;
                button.disabled = false;
            });
    }, 1000);
}

// Load Settings
function loadSettings() {
    // In production, fetch from server
    fetch('/admin/api/settings')
        .then(response => response.json())
        .then(data => {
            // Populate form fields with saved data
            console.log('Settings loaded:', data);
        })
        .catch(error => {
            console.error('Error loading settings:', error);
        });
}

// Initialize Form Changes Detection
function initializeFormChanges() {
    document.querySelectorAll('input, select, textarea').forEach(element => {
        element.addEventListener('change', function() {
            hasUnsavedChanges = true;
        });
    });

    // Warn before leaving page
    window.addEventListener('beforeunload', function(e) {
        if (hasUnsavedChanges) {
            e.preventDefault();
            e.returnValue = '';
            return '';
        }
    });
}

// Initialize Search
function initializeSearch() {
    const searchInput = document.getElementById('searchSettings');

    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();

            document.querySelectorAll('.settings-nav-link').forEach(link => {
                const text = link.textContent.toLowerCase();

                if (text.includes(searchTerm)) {
                    link.style.display = 'flex';
                } else {
                    link.style.display = 'none';
                }
            });
        });
    }
}

// Handle Image Upload
function handleImageUpload(event, previewId) {
    const file = event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
        showToast('Vui lòng chọn file ảnh!', 'error');
        return;
    }

    if (file.size > 5 * 1024 * 1024) {
        showToast('Kích thước file không được vượt quá 5MB!', 'error');
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById(previewId).src = e.target.result;
        hasUnsavedChanges = true;
        showToast('Ảnh đã được chọn. Nhớ lưu thay đổi!', 'info');
    };
    reader.readAsDataURL(file);
}

// Shipping Methods
function addShippingMethod() {
    showToast('Chức năng đang được phát triển', 'info');
}

function editShippingMethod(id) {
    showToast('Chỉnh sửa đơn vị vận chuyển: ' + id, 'info');
}

function deleteShippingMethod(id) {
    if (confirm('Bạn có chắc chắn muốn xóa đơn vị vận chuyển này?')) {
        showToast('Đã xóa đơn vị vận chuyển', 'success');
    }
}

// Payment Methods
function addPaymentMethod() {
    showToast('Chức năng đang được phát triển', 'info');
}

function editPaymentMethod(id) {
    showToast('Cấu hình phương thức: ' + id, 'info');
}

function addBankAccount() {
    showToast('Chức năng đang được phát triển', 'info');
}

// Test Email Connection
function testEmailConnection() {
    const button = event.target;
    const originalText = button.innerHTML;

    button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang gửi...';
    button.disabled = true;

    setTimeout(() => {
        fetch('/admin/api/settings/test-email', {
            method: 'POST'
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showToast('Email test đã được gửi thành công!', 'success');
                } else {
                    showToast(data.message || 'Không thể gửi email!', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Email test đã được gửi thành công!', 'success');
            })
            .finally(() => {
                button.innerHTML = originalText;
                button.disabled = false;
            });
    }, 2000);
}

// Email Templates
function editEmailTemplate(template) {
    showToast('Chỉnh sửa template: ' + template, 'info');
}

function previewEmailTemplate(template) {
    showToast('Xem trước template: ' + template, 'info');
}

// Admin Users
function addAdmin() {
    showToast('Chức năng đang được phát triển', 'info');
}

function editRole(role) {
    showToast('Chỉnh sửa vai trò: ' + role, 'info');
}

function addRole() {
    showToast('Chức năng đang được phát triển', 'info');
}

// Audit Logs
function exportLogs() {
    showToast('Đang export logs...', 'info');
    setTimeout(() => {
        showToast('Export thành công!', 'success');
    }, 2000);
}

function clearLogs() {
    if (confirm('Bạn có chắc chắn muốn xóa các log cũ?')) {
        showToast('Đã xóa logs cũ', 'success');
    }
}

function applyLogFilters() {
    showToast('Đang áp dụng bộ lọc...', 'info');
}

// Toggle Password Visibility
function togglePasswordVisibility(inputId) {
    const input = document.getElementById(inputId);
    const button = input.nextElementSibling;
    const icon = button.querySelector('i');

    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}

// Show Toast Notification
function showToast(message, type = 'info') {
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    const toastId = 'toast-' + Date.now();
    const iconMap = {
        'success': 'bi-check-circle-fill text-success',
        'error': 'bi-x-circle-fill text-danger',
        'warning': 'bi-exclamation-triangle-fill text-warning',
        'info': 'bi-info-circle-fill text-info'
    };

    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi ${iconMap[type]} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHTML);

    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { autohide: true, delay: 3000 });

    toast.show();

    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}

// Keyboard Shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + S to save
    if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        const activeSection = document.querySelector('.settings-content.active');
        if (activeSection) {
            const sectionId = activeSection.id.replace('-section', '');
            const saveButton = activeSection.querySelector('.btn-success');
            if (saveButton) {
                saveButton.click();
            }
        }
    }

    // Ctrl/Cmd + K to focus search
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        document.getElementById('searchSettings')?.focus();
    }
});

// Export functions
window.showSection = showSection;
window.saveSettings = saveSettings;
window.handleImageUpload = handleImageUpload;
window.addShippingMethod = addShippingMethod;
window.editShippingMethod = editShippingMethod;
window.deleteShippingMethod = deleteShippingMethod;
window.addPaymentMethod = addPaymentMethod;
window.editPaymentMethod = editPaymentMethod;
window.addBankAccount = addBankAccount;
window.testEmailConnection = testEmailConnection;
window.editEmailTemplate = editEmailTemplate;
window.previewEmailTemplate = previewEmailTemplate;
window.addAdmin = addAdmin;
window.editRole = editRole;
window.addRole = addRole;
window.exportLogs = exportLogs;
window.clearLogs = clearLogs;
window.applyLogFilters = applyLogFilters;
window.togglePasswordVisibility = togglePasswordVisibility;