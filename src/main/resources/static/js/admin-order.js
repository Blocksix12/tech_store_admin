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
        menuBtn && !menuBtn.contains(event.target)) {
        sidebar.classList.remove('show');
    }
});

// ============ FILTER AND SEARCH ============
function filterByStatus() {
    const status = document.getElementById('statusFilter').value;
    const timestamp = new Date().getTime();
    const url = status
        ? `/admin/quanlidonhang?status=${status}&_t=${timestamp}`
        : `/admin/quanlidonhang?_t=${timestamp}`;
    window.location.href = url;
}

// Search orders
const searchInput = document.getElementById('searchInput');
if (searchInput) {
    searchInput.addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            const searchTerm = this.value.trim();
            if (searchTerm) {
                const timestamp = new Date().getTime();
                window.location.href = `/admin/quanlidonhang?search=${encodeURIComponent(searchTerm)}&_t=${timestamp}`;
            }
        }
    });
}

// ============ MODAL FUNCTIONS ============
function updateStatus(orderId) {
    document.getElementById('orderIdInput').value = orderId;
    const modal = new bootstrap.Modal(document.getElementById('updateStatusModal'));
    modal.show();
}

function submitStatusUpdate() {
    const orderId = document.getElementById('orderIdInput').value;
    const newStatus = document.getElementById('newStatusSelect').value;

    if (!newStatus) {
        alert('Vui lòng chọn trạng thái!');
        return;
    }

    // Call API to update status
    fetch(`/admin/api/orders/${orderId}/status?status=${newStatus}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (response.ok) {
                showToast('success', 'Cập nhật trạng thái thành công!');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                return response.text().then(text => {
                    throw new Error(text || 'Có lỗi xảy ra khi cập nhật!');
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('error', error.message || 'Có lỗi xảy ra!');
        });
}

// ============ ORDER ACTIONS ============
function deleteOrder(orderId) {
    if (confirm('Bạn có chắc chắn muốn xóa đơn hàng này?')) {
        fetch(`/admin/api/orders/${orderId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (response.ok) {
                    showToast('success', 'Xóa đơn hàng thành công!');
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                } else {
                    return response.text().then(text => {
                        throw new Error(text || 'Có lỗi xảy ra khi xóa!');
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('error', error.message || 'Có lỗi xảy ra!');
            });
    }
}

// ============ PDF EXPORT FUNCTIONS ============
async function exportAllOrdersPdf() {
    try {
        const btn = event.target.closest('button');
        const originalHTML = btn.innerHTML;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang xuất...';
        btn.disabled = true;

        const response = await fetch('/admin/orders/export/pdf');

        if (!response.ok) {
            throw new Error('Export failed');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `danh-sach-don-hang-${new Date().getTime()}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        btn.innerHTML = originalHTML;
        btn.disabled = false;

        showToast('success', 'Xuất PDF thành công!');
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Không thể xuất PDF. Vui lòng thử lại!');

        const btn = event.target.closest('button');
        btn.innerHTML = '<i class="bi bi-file-earmark-pdf"></i> Xuất PDF';
        btn.disabled = false;
    }
}

async function exportOrderPdf(orderId) {
    try {
        const btn = event.target.closest('button');
        const originalHTML = btn.innerHTML;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';
        btn.disabled = true;

        const response = await fetch(`/admin/orders/${orderId}/export/pdf`);

        if (!response.ok) {
            throw new Error('Export failed');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `don-hang-${orderId}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        btn.innerHTML = originalHTML;
        btn.disabled = false;

        showToast('success', 'Xuất PDF thành công!');
    } catch (error) {
        console.error('Error:', error);
        showToast('error', 'Không thể xuất PDF!');

        const btn = event.target.closest('button');
        btn.innerHTML = '<i class="bi bi-file-earmark-pdf"></i>';
        btn.disabled = false;
    }
}

// ============ TOAST NOTIFICATION ============
function showToast(type, message) {
    // Create toast container if it doesn't exist
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
        `;
        document.body.appendChild(toastContainer);
    }

    // Create toast
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
    toast.style.cssText = `
        min-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        animation: slideIn 0.3s ease;
    `;
    toast.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : 'x-circle'}-fill me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    // Add animation CSS if not exists
    if (!document.getElementById('toastAnimation')) {
        const style = document.createElement('style');
        style.id = 'toastAnimation';
        style.textContent = `
            @keyframes slideIn {
                from {
                    transform: translateX(400px);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
        `;
        document.head.appendChild(style);
    }

    toastContainer.appendChild(toast);

    // Auto remove after 3s
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ============ INITIALIZE ON PAGE LOAD ============
document.addEventListener('DOMContentLoaded', function() {
    // Set active status filter if exists in URL
    const urlParams = new URLSearchParams(window.location.search);
    const statusParam = urlParams.get('status');
    if (statusParam) {
        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter) {
            statusFilter.value = statusParam;
        }
    }

    // Set search input value if exists in URL
    const searchParam = urlParams.get('search');
    if (searchParam) {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.value = searchParam;
        }
    }
});