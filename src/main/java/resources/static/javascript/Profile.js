// Profile Page JavaScript

// Handle Avatar Change
function handleAvatarChange(event) {
    const file = event.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
        showToast('Vui lòng chọn file ảnh!', 'error');
        return;
    }

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
        showToast('Kích thước ảnh không được vượt quá 5MB!', 'error');
        return;
    }

    // Preview image
    const reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('avatarPreview').src = e.target.result;

        // Upload to server
        uploadAvatar(file);
    };
    reader.readAsDataURL(file);
}

// Upload Avatar
function uploadAvatar(file) {
    const formData = new FormData();
    formData.append('avatar', file);

    // Show loading
    showToast('Đang tải ảnh lên...', 'info');

    fetch('/admin/api/profile/avatar', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Cập nhật ảnh đại diện thành công!', 'success');
            } else {
                showToast(data.message || 'Có lỗi xảy ra!', 'error');
            }
        })
        .catch(error => {
            console.error('Error uploading avatar:', error);
            showToast('Có lỗi xảy ra khi tải ảnh lên!', 'error');
        });
}

// Edit Section
function editSection(section) {
    // Switch to edit tab
    const editTab = document.getElementById('edit-tab');
    const tab = new bootstrap.Tab(editTab);
    tab.show();

    // Scroll to form
    setTimeout(() => {
        document.getElementById('editProfileForm').scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }, 300);
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

// Handle Edit Profile Form
document.addEventListener('DOMContentLoaded', function() {
    const editForm = document.getElementById('editProfileForm');

    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);
            const data = Object.fromEntries(formData.entries());

            // Show loading
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';
            submitBtn.disabled = true;

            fetch('/admin/api/profile/update', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showToast('Cập nhật thông tin thành công!', 'success');

                        // Switch to overview tab
                        setTimeout(() => {
                            const overviewTab = document.getElementById('overview-tab');
                            const tab = new bootstrap.Tab(overviewTab);
                            tab.show();
                        }, 1500);
                    } else {
                        showToast(data.message || 'Có lỗi xảy ra!', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error updating profile:', error);
                    showToast('Có lỗi xảy ra khi cập nhật thông tin!', 'error');
                })
                .finally(() => {
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                });
        });
    }
});

// Handle Change Password Form
document.addEventListener('DOMContentLoaded', function() {
    const passwordForm = document.getElementById('changePasswordForm');

    if (passwordForm) {
        passwordForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            // Validate passwords match
            if (newPassword !== confirmPassword) {
                showToast('Mật khẩu xác nhận không khớp!', 'error');
                return;
            }

            // Validate password strength
            if (newPassword.length < 8) {
                showToast('Mật khẩu phải có ít nhất 8 ký tự!', 'error');
                return;
            }

            const data = {
                currentPassword: currentPassword,
                newPassword: newPassword
            };

            // Show loading
            const submitBtn = this.querySelector('button[type="submit"]');
            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xử lý...';
            submitBtn.disabled = true;

            fetch('/admin/api/profile/change-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showToast('Đổi mật khẩu thành công!', 'success');
                        passwordForm.reset();
                    } else {
                        showToast(data.message || 'Có lỗi xảy ra!', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error changing password:', error);
                    showToast('Có lỗi xảy ra khi đổi mật khẩu!', 'error');
                })
                .finally(() => {
                    submitBtn.innerHTML = originalText;
                    submitBtn.disabled = false;
                });
        });
    }
});

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

// Auto-save form data
let autoSaveTimer;
function enableAutoSave() {
    const form = document.getElementById('editProfileForm');

    if (form) {
        const inputs = form.querySelectorAll('input, textarea, select');

        inputs.forEach(input => {
            input.addEventListener('input', function() {
                clearTimeout(autoSaveTimer);
                autoSaveTimer = setTimeout(() => {
                    saveFormDraft();
                }, 3000);
            });
        });
    }
}

// Save form draft
function saveFormDraft() {
    const form = document.getElementById('editProfileForm');
    if (!form) return;

    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    localStorage.setItem('profileDraft', JSON.stringify({
        data: data,
        timestamp: new Date().toISOString()
    }));

    console.log('Draft saved');
}

// Load form draft
function loadFormDraft() {
    const draft = localStorage.getItem('profileDraft');
    if (!draft) return;

    const { data, timestamp } = JSON.parse(draft);
    const draftAge = Date.now() - new Date(timestamp).getTime();

    // Only load draft if less than 24 hours old
    if (draftAge < 24 * 60 * 60 * 1000) {
        const message = `Có bản nháp được lưu lúc ${new Date(timestamp).toLocaleString('vi-VN')}. Bạn có muốn khôi phục?`;

        if (confirm(message)) {
            const form = document.getElementById('editProfileForm');
            Object.keys(data).forEach(key => {
                const input = form.querySelector(`[name="${key}"]`);
                if (input) {
                    input.value = data[key];
                }
            });
            showToast('Đã khôi phục bản nháp', 'success');
        }
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    enableAutoSave();
    loadFormDraft();

    // Handle tab changes
    const tabButtons = document.querySelectorAll('[data-bs-toggle="tab"]');
    tabButtons.forEach(button => {
        button.addEventListener('shown.bs.tab', function(e) {
            const targetId = e.target.getAttribute('data-bs-target');
            console.log('Tab changed to:', targetId);
        });
    });
});

// Export functions
window.handleAvatarChange = handleAvatarChange;
window.editSection = editSection;
window.togglePasswordVisibility = togglePasswordVisibility;