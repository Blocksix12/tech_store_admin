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
    };
    reader.readAsDataURL(file);
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

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Handle tab changes
    const tabButtons = document.querySelectorAll('[data-bs-toggle="tab"]');
    tabButtons.forEach(button => {
        button.addEventListener('shown.bs.tab', function(e) {
            const targetId = e.target.getAttribute('data-bs-target');
            console.log('Tab changed to:', targetId);
        });
    });
});

// Profile.js - Thêm vào cuối file

// ============ SMS 2FA Functions ============
let currentPhone = '';
let currentEmail = '';
let sms2faModal;
let email2faModal;

// Khởi tạo modal
document.addEventListener('DOMContentLoaded', function() {
    const smsModalElement = document.getElementById('enableSms2faModal');
    const emailModalElement = document.getElementById('enableEmail2faModal');

    if (smsModalElement) {
        sms2faModal = new bootstrap.Modal(smsModalElement);
    }

    if (emailModalElement) {
        email2faModal = new bootstrap.Modal(emailModalElement);
    }

    // Kiểm tra trạng thái 2FA khi load trang
    check2faStatus();
});

// Kiểm tra trạng thái SMS 2FA
function check2faStatus() {
    fetch('/admin/2fa/status')
        .then(res => res.json())
        .then(data => {
            // SMS 2FA
            if (data.smsEnabled) {
                showSms2faEnabled();
            }

            // Email 2FA
            if (data.emailEnabled) {
                showEmail2faEnabled();
            }
        })
        .catch(error => {
            console.error('Error checking 2FA status:', error);
        });
}

// Hiển thị modal kích hoạt SMS 2FA
function showEnableSms2faModal() {
    resetSms2faModal();
    sms2faModal.show();
}

// Reset modal về bước 1
function resetSms2faModal() {
    document.getElementById('step1').style.display = 'block';
    document.getElementById('step2').style.display = 'none';
    document.getElementById('sendSmsBtn').style.display = 'inline-block';
    document.getElementById('verifyBtn').style.display = 'none';
    document.getElementById('verificationCode').value = '';
    document.getElementById('sendSmsError').style.display = 'none';
    document.getElementById('verifyError').style.display = 'none';
}

// Gửi mã SMS
async function sendSmsCode() {
    const phoneInput = document.getElementById('phoneInput');
    const phone = phoneInput.value.trim();
    const errorDiv = document.getElementById('sendSmsError');

    // Validate số điện thoại
    if (!phone) {
        errorDiv.textContent = 'Vui lòng nhập số điện thoại';
        errorDiv.style.display = 'block';
        return;
    }

    // Validate format số điện thoại Việt Nam
    const phoneRegex = /^(\+84|0)[0-9]{9,10}$/;
    if (!phoneRegex.test(phone)) {
        errorDiv.textContent = 'Số điện thoại không hợp lệ. Vui lòng nhập đúng định dạng +84xxxxxxxxx hoặc 0xxxxxxxxx';
        errorDiv.style.display = 'block';
        return;
    }

    errorDiv.style.display = 'none';
    currentPhone = phone;

    // Hiển thị loading
    const sendBtn = document.getElementById('sendSmsBtn');
    const originalText = sendBtn.innerHTML;
    sendBtn.disabled = true;
    sendBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang gửi...';

    try {
        const response = await fetch('/admin/2fa/phone/send-sms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `phoneNumber=${encodeURIComponent(phone)}`
        });

        if (response.ok) {
            // Chuyển sang bước 2
            document.getElementById('step1').style.display = 'none';
            document.getElementById('step2').style.display = 'block';
            sendBtn.style.display = 'none';
            document.getElementById('verifyBtn').style.display = 'inline-block';

            // Focus vào ô nhập mã
            document.getElementById('verificationCode').focus();

            showToast('success', 'Mã xác thực đã được gửi đến số điện thoại của bạn');
        } else {
            const errorText = await response.text();
            errorDiv.textContent = errorText || 'Không thể gửi mã. Vui lòng thử lại sau.';
            errorDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        errorDiv.textContent = 'Đã xảy ra lỗi. Vui lòng thử lại sau.';
        errorDiv.style.display = 'block';
    } finally {
        sendBtn.disabled = false;
        sendBtn.innerHTML = originalText;
    }
}

// Gửi lại mã SMS
async function resendSmsCode() {
    if (!currentPhone) return;

    document.getElementById('verifyError').style.display = 'none';

    try {
        const response = await fetch('/admin/2fa/phone/send-sms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `phoneNumber=${encodeURIComponent(currentPhone)}`
        });

        if (response.ok) {
            showToast('success', 'Mã xác thực mới đã được gửi');
            document.getElementById('verificationCode').value = '';
            document.getElementById('verificationCode').focus();
        } else {
            showToast('error', 'Không thể gửi lại mã. Vui lòng thử lại sau.');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Đã xảy ra lỗi. Vui lòng thử lại sau.');
    }
}

// Xác thực mã SMS
async function verifySmsCode() {
    const code = document.getElementById('verificationCode').value.trim();
    const errorDiv = document.getElementById('verifyError');

    // Validate mã
    if (!code || code.length !== 6) {
        errorDiv.textContent = 'Vui lòng nhập đầy đủ 6 số';
        errorDiv.style.display = 'block';
        return;
    }

    if (!/^\d{6}$/.test(code)) {
        errorDiv.textContent = 'Mã xác thực chỉ bao gồm 6 chữ số';
        errorDiv.style.display = 'block';
        return;
    }

    errorDiv.style.display = 'none';

    // Hiển thị loading
    const verifyBtn = document.getElementById('verifyBtn');
    const originalText = verifyBtn.innerHTML;
    verifyBtn.disabled = true;
    verifyBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xác thực...';

    try {
        const response = await fetch('/admin/2fa/phone/verify-sms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `phoneNumber=${encodeURIComponent(currentPhone)}&code=${encodeURIComponent(code)}`
        });

        const result = await response.text();

        if (response.ok && result.includes('✅')) {
            // Thành công
            showToast('success', 'Xác thực SMS 2FA đã được kích hoạt thành công!');
            sms2faModal.hide();
            showSms2faEnabled();

            // Reload trang sau 1 giây
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } else {
            // Thất bại
            errorDiv.textContent = 'Mã xác thực không chính xác hoặc đã hết hạn';
            errorDiv.style.display = 'block';
            document.getElementById('verificationCode').value = '';
            document.getElementById('verificationCode').focus();
        }
    } catch (error) {
        console.error('Error:', error);
        errorDiv.textContent = 'Đã xảy ra lỗi. Vui lòng thử lại sau.';
        errorDiv.style.display = 'block';
    } finally {
        verifyBtn.disabled = false;
        verifyBtn.innerHTML = originalText;
    }
}

// Hiển thị trạng thái đã bật 2FA
function showSms2faEnabled() {
    document.getElementById('sms2faStatus').style.display = 'inline-block';
    document.getElementById('sms2faStatusOff').style.display = 'none';
    document.getElementById('enableSms2faBtn').style.display = 'none';
    document.getElementById('disableSms2faBtn').style.display = 'inline-block';
}

// Tắt SMS 2FA
async function disableSms2fa() {
    if (!confirm('Bạn có chắc chắn muốn tắt xác thực SMS 2FA?')) {
        return;
    }

    try {
        // TODO: Gọi API để tắt 2FA
        const response = await fetch('/admin/2fa/phone/disable', {
            method: 'POST'
        });

        if (response.ok) {
            showToast('success', 'Đã tắt xác thực SMS 2FA');
            document.getElementById('sms2faStatus').style.display = 'none';
            document.getElementById('sms2faStatusOff').style.display = 'inline-block';
            document.getElementById('enableSms2faBtn').style.display = 'inline-block';
            document.getElementById('disableSms2faBtn').style.display = 'none';
        } else {
            showToast('error', 'Không thể tắt 2FA. Vui lòng thử lại sau.');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Đã xảy ra lỗi. Vui lòng thử lại sau.');
    }
}

// Hiển thị thông báo toast
function showToast(type, message) {
    // Nếu đã có Bootstrap toast component
    const toastHtml = `
        <div class="toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0" 
             role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-circle'}-fill me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" 
                        data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    // Tạo container nếu chưa có
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    // Thêm toast
    const toastElement = document.createElement('div');
    toastElement.innerHTML = toastHtml;
    toastContainer.appendChild(toastElement.firstElementChild);

    // Hiển thị toast
    const toast = new bootstrap.Toast(toastContainer.lastElementChild, {
        autohide: true,
        delay: 3000
    });
    toast.show();

    // Xóa toast sau khi ẩn
    toastContainer.lastElementChild.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Auto-format mã xác thực khi nhập
document.addEventListener('DOMContentLoaded', function() {
    const verificationInput = document.getElementById('verificationCode');
    if (verificationInput) {
        verificationInput.addEventListener('input', function(e) {
            // Chỉ cho phép nhập số
            this.value = this.value.replace(/[^0-9]/g, '');

            // Tự động verify khi nhập đủ 6 số
            if (this.value.length === 6) {
                // Delay một chút để user thấy số cuối cùng
                setTimeout(() => {
                    verifySmsCode();
                }, 300);
            }
        });

        // Cho phép paste
        verificationInput.addEventListener('paste', function(e) {
            setTimeout(() => {
                this.value = this.value.replace(/[^0-9]/g, '').substring(0, 6);
            }, 10);
        });
    }
});

function getCurrentUserEmail() {
    const el = document.getElementById('userEmail');
    return el ? el.dataset.email : null;
}

function resetEmail2faModal() {
    document.getElementById('emailStep1').style.display = 'block';
    document.getElementById('emailStep2').style.display = 'none';

    document.getElementById('sendEmailBtn').style.display = 'inline-block';
    document.getElementById('verifyEmailBtn').style.display = 'none';

    document.getElementById('emailVerificationCode').value = '';
    document.getElementById('sendEmailError').style.display = 'none';
    document.getElementById('verifyEmailError').style.display = 'none';
}
async function sendEmailCode() {
    const errorDiv = document.getElementById('sendEmailError');
    errorDiv.style.display = 'none';

    // ✅ LẤY EMAIL ĐÚNG TỪ INPUT
    const emailInput = document.getElementById('emailInput');
    if (!emailInput || !emailInput.value) {
        errorDiv.textContent = 'Không tìm thấy email người dùng';
        errorDiv.style.display = 'block';
        return;
    }

    const email = emailInput.value.trim();

    try {
        const response = await fetch('/admin/2fa/email/send', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `email=${encodeURIComponent(email)}`
        });

        if (response.ok) {
            document.getElementById('emailStep1').style.display = 'none';
            document.getElementById('emailStep2').style.display = 'block';

            document.getElementById('sendEmailBtn').style.display = 'none';
            document.getElementById('verifyEmailBtn').style.display = 'inline-block';

            showToast('success', 'Mã xác thực đã được gửi tới email');
        } else {
            errorDiv.textContent = await response.text();
            errorDiv.style.display = 'block';
        }
    } catch (e) {
        errorDiv.textContent = 'Lỗi hệ thống';
        errorDiv.style.display = 'block';
    }
}

async function verifyEmailCode() {
    const code = document.getElementById('emailVerificationCode').value.trim();
    const email = document.getElementById('emailInput').value.trim();
    const errorDiv = document.getElementById('verifyEmailError');

    if (!/^\d{6}$/.test(code)) {
        errorDiv.textContent = 'Mã phải gồm 6 chữ số';
        errorDiv.style.display = 'block';
        return;
    }

    try {
        const response = await fetch('/admin/2fa/email/verify', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `email=${encodeURIComponent(email)}&code=${encodeURIComponent(code)}`
        });

        if (response.ok) {
            showToast('success', 'Kích hoạt Email 2FA thành công');
            await fetch('/admin/2fa/email/enable', {
                method: 'POST'
            });
            email2faModal.hide();
            setTimeout(() => location.reload(), 1000);
        } else {
            errorDiv.textContent = 'Mã không đúng hoặc đã hết hạn';
            errorDiv.style.display = 'block';
        }
    } catch (e) {
        errorDiv.textContent = 'Lỗi hệ thống';
        errorDiv.style.display = 'block';
    }
}
async function resendEmailCode() {
    const email = document.getElementById('emailInput').value.trim();
    if (!email) return;

    await fetch('/admin/2fa/email/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `email=${encodeURIComponent(email)}`
    });

    showToast('success', 'Đã gửi lại mã xác thực');
}


// Hiển thị modal kích hoạt Email 2FA
function showEnableEmail2faModal() {
    resetEmail2faModal();
    email2faModal.show();
}


async function load2FAStatus() {
    try {
        const response = await fetch('/admin/2fa/status');
        if (!response.ok) return;

        const data = await response.json();

        // EMAIL
        if (data.emailEnabled) {
            document.getElementById('email2faStatus').style.display = 'inline-block';
            document.getElementById('email2faStatusOff').style.display = 'none';
            document.getElementById('enableEmail2faBtn').style.display = 'none';
            document.getElementById('disableEmail2faBtn').style.display = 'inline-block';
        } else {
            document.getElementById('email2faStatus').style.display = 'none';
            document.getElementById('email2faStatusOff').style.display = 'inline-block';
            document.getElementById('enableEmail2faBtn').style.display = 'inline-block';
            document.getElementById('disableEmail2faBtn').style.display = 'none';
        }

        // SMS (nếu cần)
        if (data.smsEnabled) {
            document.getElementById('sms2faStatus').style.display = 'inline-block';
            document.getElementById('sms2faStatusOff').style.display = 'none';
        }

    } catch (e) {
        console.error('Không tải được trạng thái 2FA');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    load2FAStatus();
});

document.getElementById('security-tab')
    .addEventListener('click', load2FAStatus);


// Export functions
window.handleAvatarChange = handleAvatarChange;
window.editSection = editSection;
window.togglePasswordVisibility = togglePasswordVisibility;