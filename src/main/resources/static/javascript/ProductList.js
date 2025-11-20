// Product List JavaScript

// Toggle Select All
function toggleSelectAll(checkbox) {
    const checkboxes = document.querySelectorAll('.product-checkbox');
    checkboxes.forEach(cb => cb.checked = checkbox.checked);
    updateBulkActions();
}

// Update Bulk Actions
function updateBulkActions() {
    const checkedBoxes = document.querySelectorAll('.product-checkbox:checked');
    const bulkActions = document.getElementById('bulkActions');
    const selectAllCheckbox = document.querySelector('thead input[type="checkbox"]');

    if (checkedBoxes.length > 0) {
        bulkActions.classList.add('show');
        bulkActions.querySelector('span strong').textContent = checkedBoxes.length;
    } else {
        bulkActions.classList.remove('show');
        if (selectAllCheckbox) {
            selectAllCheckbox.checked = false;
        }
    }

    // Update select all checkbox state
    const allCheckboxes = document.querySelectorAll('.product-checkbox');
    if (selectAllCheckbox && allCheckboxes.length > 0) {
        selectAllCheckbox.checked = checkedBoxes.length === allCheckboxes.length;
        selectAllCheckbox.indeterminate = checkedBoxes.length > 0 && checkedBoxes.length < allCheckboxes.length;
    }
}

// Clear Selection
function clearSelection() {
    const checkboxes = document.querySelectorAll('.product-checkbox');
    checkboxes.forEach(cb => cb.checked = false);
    const selectAllCheckbox = document.querySelector('thead input[type="checkbox"]');
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = false;
        selectAllCheckbox.indeterminate = false;
    }
    updateBulkActions();
}

// Delete Product
function deleteProduct(productId) {
    if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
        // Add your delete logic here
        console.log('Deleting product:', productId);
        // Example: Call API to delete product
        // fetch(`/admin/products/delete/${productId}`, { method: 'DELETE' })
        //     .then(response => response.json())
        //     .then(data => {
        //         if (data.success) {
        //             location.reload();
        //         }
        //     });
    }
}

// Bulk Delete
function bulkDelete() {
    const checkedBoxes = document.querySelectorAll('.product-checkbox:checked');
    const productIds = Array.from(checkedBoxes).map(cb => cb.closest('tr').dataset.productId);

    if (confirm(`Bạn có chắc chắn muốn xóa ${productIds.length} sản phẩm đã chọn?`)) {
        // Add your bulk delete logic here
        console.log('Deleting products:', productIds);
    }
}

// Bulk Update Status
function bulkUpdateStatus(status) {
    const checkedBoxes = document.querySelectorAll('.product-checkbox:checked');
    const productIds = Array.from(checkedBoxes).map(cb => cb.closest('tr').dataset.productId);

    // Add your bulk update logic here
    console.log('Updating products status to:', status, productIds);
}

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    // Add event listeners to all product checkboxes
    const productCheckboxes = document.querySelectorAll('.product-checkbox');
    productCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateBulkActions);
    });

    // Delete button confirmation
    const deleteButtons = document.querySelectorAll('.action-btn-delete');
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const productId = this.dataset.id;
            deleteProduct(productId);
        });
    });

    // Bulk actions buttons
    const bulkDeleteBtn = document.querySelector('.bulk-actions .btn-danger');
    if (bulkDeleteBtn) {
        bulkDeleteBtn.addEventListener('click', bulkDelete);
    }

    const bulkActivateBtn = document.querySelector('.bulk-actions .btn-success');
    if (bulkActivateBtn) {
        bulkActivateBtn.addEventListener('click', () => bulkUpdateStatus('ACTIVE'));
    }

    const bulkDeactivateBtn = document.querySelector('.bulk-actions .btn-warning');
    if (bulkDeactivateBtn) {
        bulkDeactivateBtn.addEventListener('click', () => bulkUpdateStatus('INACTIVE'));
    }

    // Filter functionality
    const filterBtn = document.querySelector('.filters-bar .btn-outline-primary');
    if (filterBtn) {
        filterBtn.addEventListener('click', applyFilters);
    }

    const resetBtn = document.querySelector('.filters-bar .btn-outline-secondary');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetFilters);
    }
});

// Apply Filters
function applyFilters() {
    const category = document.querySelector('.filter-group select[label="Danh mục"]')?.value;
    const brand = document.querySelector('.filter-group select[label="Thương hiệu"]')?.value;
    const status = document.querySelector('.filter-group select[label="Trạng thái"]')?.value;
    const stock = document.querySelector('.filter-group select[label="Tồn kho"]')?.value;

    // Build query string
    const params = new URLSearchParams();
    if (category && category !== 'Tất cả danh mục') params.append('category', category);
    if (brand && brand !== 'Tất cả thương hiệu') params.append('brand', brand);
    if (status && status !== 'Tất cả trạng thái') params.append('status', status);
    if (stock && stock !== 'Tất cả') params.append('stock', stock);

    // Redirect with filters
    window.location.href = `/admin/products?${params.toString()}`;
}

// Reset Filters
function resetFilters() {
    window.location.href = '/admin/products';
}

// Search functionality
const searchInput = document.querySelector('.search-box input');
if (searchInput) {
    let searchTimeout;
    searchInput.addEventListener('input', function(e) {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            const searchTerm = e.target.value;
            if (searchTerm.length >= 3) {
                // Perform search
                console.log('Searching for:', searchTerm);
                // Add your search logic here
            }
        }, 500);
    });
}

// Export functions for use in HTML
window.toggleSelectAll = toggleSelectAll;
window.clearSelection = clearSelection;
window.deleteProduct = deleteProduct;