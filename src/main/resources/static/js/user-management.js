/* ============================================
   USER MANAGEMENT - SPECIFIC JAVASCRIPT
============================================ */

// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');

    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const tableRows = document.querySelectorAll('#userTableBody tr');

            tableRows.forEach(row => {
                const text = row.textContent.toLowerCase();
                if (text.includes(searchTerm)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        });
    }
});

// Filter users function
function filterUsers() {
    // You can implement advanced filtering here
    console.log('Filter users functionality');
    // Example: Show modal with filter options
}

// Edit user function
function editUser(userId) {
    showLoading();

    fetch(`/admin/users/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(user => {
            hideLoading();

            // Fill form with user data
            document.getElementById('userId').value = user.id;
            document.querySelector('#userModal input[name="username"]').value = user.username;
            document.querySelector('#userModal input[name="fullname"]').value = user.fullname;
            document.querySelector('#userModal input[name="email"]').value = user.email;
            document.querySelector('#userModal input[name="phone"]').value = user.phone;
            document.querySelector('#userModal select[name="status"]').value = user.status;

            // Change modal title
            document.querySelector('#userModal .modal-title').textContent = 'Cập nhật người dùng';

            // Show modal
            const modal = new bootstrap.Modal(document.getElementById('userModal'));
            modal.show();
        })
        .catch(error => {
            hideLoading();
            console.error('Error:', error);
            showToast('Không thể tải thông tin người dùng!', 'error');
        });
}

// Delete user function
function deleteUser(userId) {
    confirmDialog('Bạn có chắc muốn xóa người dùng này?', function() {
        showLoading();

        fetch(`/admin/users/delete/${userId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                hideLoading();
                showToast(data.message || 'Xóa người dùng thành công!', 'success');

                // Reload page after 1 second
                setTimeout(() => {
                    location.reload();
                }, 1000);
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showToast('Có lỗi xảy ra khi xóa người dùng!', 'error');
            });
    });
}

// Handle user form submission
document.addEventListener('DOMContentLoaded', function() {
    const userForm = document.getElementById('userForm');

    if (userForm) {
        userForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const userId = document.getElementById('userId').value;
            const formData = {
                username: document.querySelector('#userModal input[name="username"]').value,
                fullname: document.querySelector('#userModal input[name="fullname"]').value,
                email: document.querySelector('#userModal input[name="email"]').value,
                phone: document.querySelector('#userModal input[name="phone"]').value,
                status: document.querySelector('#userModal select[name="status"]').value
            };

            // Validate form
            if (!formData.username || !formData.fullname || !formData.email || !formData.phone) {
                showToast('Vui lòng điền đầy đủ thông tin!', 'error');
                return;
            }

            // Validate email
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(formData.email)) {
                showToast('Email không hợp lệ!', 'error');
                return;
            }

            // Validate phone
            const phoneRegex = /^[0-9]{10,11}$/;
            if (!phoneRegex.test(formData.phone)) {
                showToast('Số điện thoại không hợp lệ!', 'error');
                return;
            }

            const url = userId ? `/admin/users/update/${userId}` : '/admin/users/add';
            const method = userId ? 'PATCH' : 'POST';

            showLoading();

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    hideLoading();

                    // Close modal
                    const modal = bootstrap.Modal.getInstance(document.getElementById('userModal'));
                    if (modal) {
                        modal.hide();
                    }

                    showToast(data.message || 'Thao tác thành công!', 'success');

                    // Reload page after 1 second
                    setTimeout(() => {
                        location.reload();
                    }, 1000);
                })
                .catch(error => {
                    hideLoading();
                    console.error('Error:', error);
                    showToast('Có lỗi xảy ra: ' + error.message, 'error');
                });
        });
    }
});

// Reset form when modal is closed
document.addEventListener('DOMContentLoaded', function() {
    const modalElement = document.getElementById('userModal');

    if (modalElement) {
        modalElement.addEventListener('hidden.bs.modal', function () {
            const form = document.getElementById('userForm');
            if (form) {
                form.reset();
            }

            const userId = document.getElementById('userId');
            if (userId) {
                userId.value = '';
            }

            const modalTitle = document.querySelector('#userModal .modal-title');
            if (modalTitle) {
                modalTitle.textContent = 'Thêm người dùng mới';
            }
        });
    }
});

// Export users to CSV
function exportUsers() {
    showLoading();

    fetch('/admin/users/export')
        .then(response => response.blob())
        .then(blob => {
            hideLoading();

            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `users_${new Date().getTime()}.csv`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            showToast('Xuất dữ liệu thành công!', 'success');
        })
        .catch(error => {
            hideLoading();
            console.error('Error:', error);
            showToast('Có lỗi xảy ra khi xuất dữ liệu!', 'error');
        });
}

// Bulk actions
function bulkAction(action) {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]:checked');

    if (checkboxes.length === 0) {
        showToast('Vui lòng chọn ít nhất một người dùng!', 'error');
        return;
    }

    const userIds = Array.from(checkboxes).map(cb => cb.value);

    confirmDialog(`Bạn có chắc muốn ${action} ${userIds.length} người dùng?`, function() {
        showLoading();

        fetch(`/admin/users/bulk/${action}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userIds })
        })
            .then(response => response.json())
            .then(data => {
                hideLoading();
                showToast(data.message || 'Thao tác thành công!', 'success');

                setTimeout(() => {
                    location.reload();
                }, 1000);
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showToast('Có lỗi xảy ra!', 'error');
            });
    });
}