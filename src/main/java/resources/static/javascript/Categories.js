// Categories Page JavaScript

// Toggle Category
function toggleCategory(button) {
    button.classList.toggle('active');
    const categoryItem = button.closest('.category-item');
    const children = categoryItem.querySelector(':scope > .category-children');

    if (children) {
        children.classList.toggle('expanded');
    }
}

// Expand All Categories
function expandAll() {
    const allToggles = document.querySelectorAll('.category-toggle');
    const allChildren = document.querySelectorAll('.category-children');

    allToggles.forEach(toggle => {
        if (!toggle.classList.contains('invisible')) {
            toggle.classList.add('active');
        }
    });

    allChildren.forEach(children => {
        children.classList.add('expanded');
    });
}

// Collapse All Categories
function collapseAll() {
    const allToggles = document.querySelectorAll('.category-toggle');
    const allChildren = document.querySelectorAll('.category-children');

    allToggles.forEach(toggle => {
        toggle.classList.remove('active');
    });

    allChildren.forEach(children => {
        children.classList.remove('expanded');
    });
}

// Add Sub Category
function addSubCategory(parentId) {
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    const parentCategorySelect = document.getElementById('parentCategory');

    // Reset form
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryModalLabel').innerHTML = '<i class="bi bi-folder-plus"></i> Thêm danh mục con';

    // Set parent category
    if (parentCategorySelect) {
        parentCategorySelect.value = parentId;
    }

    modal.show();
}

// Edit Category
function editCategory(categoryId) {
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));

    // Change modal title
    document.getElementById('categoryModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa danh mục';

    // In production, fetch category data from API
    // Example: Load category data
    fetch(`/admin/api/categories/${categoryId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('categoryName').value = data.name || '';
            document.getElementById('categoryIcon').value = data.icon || 'bi-folder';
            document.getElementById('parentCategory').value = data.parentId || '';
            document.getElementById('categoryStatus').value = data.status || 'active';
            document.getElementById('categorySlug').value = data.slug || '';
            document.getElementById('categoryDescription').value = data.description || '';
            document.getElementById('categoryOrder').value = data.order || 0;
            document.getElementById('showOnHome').value = data.showOnHome || 'no';
            document.getElementById('metaTitle').value = data.metaTitle || '';
            document.getElementById('metaDescription').value = data.metaDescription || '';
            document.getElementById('metaKeywords').value = data.metaKeywords || '';
        })
        .catch(error => {
            console.error('Error loading category:', error);
        });

    modal.show();
}

// Delete Category
function deleteCategory(categoryId) {
    if (confirm('Bạn có chắc chắn muốn xóa danh mục này?\n\nLưu ý: Tất cả danh mục con cũng sẽ bị xóa hoặc chuyển về danh mục cha.')) {
        // Show loading
        const categoryItem = document.querySelector(`[data-category-id="${categoryId}"]`);
        if (categoryItem) {
            categoryItem.style.opacity = '0.5';
        }

        // In production, call API to delete
        fetch(`/admin/api/categories/${categoryId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Remove category from DOM with animation
                    if (categoryItem) {
                        categoryItem.style.transition = 'all 0.3s ease';
                        categoryItem.style.transform = 'translateX(-20px)';
                        categoryItem.style.opacity = '0';
                        setTimeout(() => {
                            categoryItem.remove();
                        }, 300);
                    }
                    showToast('Đã xóa danh mục thành công', 'success');
                } else {
                    if (categoryItem) {
                        categoryItem.style.opacity = '1';
                    }
                    showToast(data.message || 'Có lỗi xảy ra', 'error');
                }
            })
            .catch(error => {
                console.error('Error deleting category:', error);
                if (categoryItem) {
                    categoryItem.style.opacity = '1';
                }
                showToast('Có lỗi xảy ra khi xóa danh mục', 'error');
            });
    }
}

// Save Category
function saveCategory() {
    const form = document.getElementById('categoryForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // Get form data
    const categoryData = {
        name: document.getElementById('categoryName').value,
        icon: document.getElementById('categoryIcon').value,
        parentId: document.getElementById('parentCategory').value || null,
        status: document.getElementById('categoryStatus').value,
        slug: document.getElementById('categorySlug').value,
        description: document.getElementById('categoryDescription').value,
        order: parseInt(document.getElementById('categoryOrder').value) || 0,
        showOnHome: document.getElementById('showOnHome').value === 'yes',
        metaTitle: document.getElementById('metaTitle').value,
        metaDescription: document.getElementById('metaDescription').value,
        metaKeywords: document.getElementById('metaKeywords').value
    };

    // Handle image upload if present
    const imageFile = document.getElementById('categoryImage').files[0];
    if (imageFile) {
        const formData = new FormData();
        formData.append('image', imageFile);
        Object.keys(categoryData).forEach(key => {
            formData.append(key, categoryData[key]);
        });

        // Upload with image
        uploadCategoryWithImage(formData);
    } else {
        // Save without image
        saveCategoryData(categoryData);
    }
}

// Save category data to server
function saveCategoryData(categoryData) {
    const saveButton = document.querySelector('#categoryModal .btn-primary');
    const originalText = saveButton.innerHTML;
    saveButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';
    saveButton.disabled = true;

    fetch('/admin/api/categories', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(categoryData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã lưu danh mục thành công', 'success');
                bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
                setTimeout(() => {
                    location.reload(); // Reload to show new category
                }, 500);
            } else {
                showToast(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error saving category:', error);
            showToast('Có lỗi xảy ra khi lưu danh mục', 'error');
        })
        .finally(() => {
            saveButton.innerHTML = originalText;
            saveButton.disabled = false;
        });
}

// Upload category with image
function uploadCategoryWithImage(formData) {
    const saveButton = document.querySelector('#categoryModal .btn-primary');
    const originalText = saveButton.innerHTML;
    saveButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang tải lên...';
    saveButton.disabled = true;

    fetch('/admin/api/categories/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã lưu danh mục thành công', 'success');
                bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
                setTimeout(() => {
                    location.reload();
                }, 500);
            } else {
                showToast(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error uploading category:', error);
            showToast('Có lỗi xảy ra khi tải lên', 'error');
        })
        .finally(() => {
            saveButton.innerHTML = originalText;
            saveButton.disabled = false;
        });
}

// Auto-generate slug from category name
function generateSlug(text) {
    const from = "àáãảạăằắẳẵặâầấẩẫậèéẻẽẹêềếểễệđùúủũụưừứửữựòóỏõọôồốổỗộơờớởỡợìíỉĩịäëïîöüûñçýỳỹỵỷ";
    const to   = "aaaaaaaaaaaaaaaaaeeeeeeeeeeeduuuuuuuuuuuoooooooooooooooooiiiiiaeiiouuncyyyyy";

    for (let i = 0, l = from.length; i < l; i++) {
        text = text.replace(new RegExp(from[i], 'gi'), to[i]);
    }

    text = text.toLowerCase()
        .trim()
        .replace(/[^a-z0-9\s-]/g, '')
        .replace(/\s+/g, '-')
        .replace(/-+/g, '-');

    return text;
}

// Show Toast Notification
function showToast(message, type = 'info') {
    // Create toast container if not exists
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '9999';
        document.body.appendChild(toastContainer);
    }

    // Create toast element
    const toastId = 'toast-' + Date.now();
    const iconMap = {
        'success': 'bi-check-circle-fill text-success',
        'error': 'bi-x-circle-fill text-danger',
        'warning': 'bi-exclamation-triangle-fill text-warning',
        'info': 'bi-info-circle-fill text-info'
    };

    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi ${iconMap[type]} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHTML);

    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 3000
    });

    toast.show();

    // Remove toast after hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}

// Search Categories
function searchCategories(searchTerm) {
    const categoryItems = document.querySelectorAll('.category-item');
    const searchLower = searchTerm.toLowerCase();

    if (!searchTerm) {
        // Show all categories
        categoryItems.forEach(item => {
            item.style.display = '';
        });
        return;
    }

    categoryItems.forEach(item => {
        const categoryName = item.querySelector('.category-name')?.textContent.toLowerCase();
        if (categoryName && categoryName.includes(searchLower)) {
            item.style.display = '';
            // Expand parent categories
            let parent = item.parentElement;
            while (parent) {
                if (parent.classList.contains('category-children')) {
                    parent.classList.add('expanded');
                    const parentItem = parent.previousElementSibling;
                    if (parentItem) {
                        const toggle = parentItem.querySelector('.category-toggle');
                        if (toggle) {
                            toggle.classList.add('active');
                        }
                    }
                }
                parent = parent.parentElement;
            }
        } else {
            item.style.display = 'none';
        }
    });
}

// Drag and Drop functionality
let draggedElement = null;

function initDragAndDrop() {
    const categoryItems = document.querySelectorAll('.category-content');

    categoryItems.forEach(item => {
        item.setAttribute('draggable', 'true');

        item.addEventListener('dragstart', function(e) {
            draggedElement = this.closest('.category-item');
            draggedElement.classList.add('dragging');
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData('text/html', this.innerHTML);
        });

        item.addEventListener('dragend', function(e) {
            draggedElement.classList.remove('dragging');

            document.querySelectorAll('.category-item').forEach(item => {
                item.classList.remove('drag-over');
            });
        });

        item.addEventListener('dragover', function(e) {
            if (e.preventDefault) {
                e.preventDefault();
            }
            e.dataTransfer.dropEffect = 'move';

            const targetItem = this.closest('.category-item');
            if (targetItem !== draggedElement) {
                targetItem.classList.add('drag-over');
            }
            return false;
        });

        item.addEventListener('dragleave', function(e) {
            this.closest('.category-item').classList.remove('drag-over');
        });

        item.addEventListener('drop', function(e) {
            if (e.stopPropagation) {
                e.stopPropagation();
            }

            const targetItem = this.closest('.category-item');

            if (draggedElement !== targetItem) {
                // Get IDs
                const draggedId = draggedElement.getAttribute('data-category-id');
                const targetId = targetItem.getAttribute('data-category-id');

                // Call API to update order
                updateCategoryOrder(draggedId, targetId);
            }

            targetItem.classList.remove('drag-over');

            return false;
        });
    });
}

// Update category order
function updateCategoryOrder(draggedId, targetId) {
    fetch('/admin/api/categories/reorder', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            draggedId: draggedId,
            targetId: targetId
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã cập nhật thứ tự danh mục', 'success');
                location.reload();
            } else {
                showToast('Có lỗi xảy ra khi cập nhật', 'error');
            }
        })
        .catch(error => {
            console.error('Error updating order:', error);
            showToast('Có lỗi xảy ra khi cập nhật', 'error');
        });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Auto-generate slug from category name
    const categoryNameInput = document.getElementById('categoryName');
    const categorySlugInput = document.getElementById('categorySlug');

    if (categoryNameInput && categorySlugInput) {
        categoryNameInput.addEventListener('input', function() {
            if (!categorySlugInput.value || categorySlugInput.dataset.autoGenerated === 'true') {
                categorySlugInput.value = generateSlug(this.value);
                categorySlugInput.dataset.autoGenerated = 'true';
            }
        });

        categorySlugInput.addEventListener('input', function() {
            if (this.value) {
                this.dataset.autoGenerated = 'false';
            }
        });
    }

    // Search functionality
    const searchInput = document.querySelector('.search-box input');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function(e) {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                searchCategories(e.target.value);
            }, 300);
        });
    }

    // Initialize drag and drop
    initDragAndDrop();

    // Reset modal when closed
    const categoryModal = document.getElementById('categoryModal');
    if (categoryModal) {
        categoryModal.addEventListener('hidden.bs.modal', function() {
            document.getElementById('categoryForm').reset();
            document.getElementById('categoryModalLabel').innerHTML = '<i class="bi bi-folder-plus"></i> Thêm danh mục mới';
        });
    }

    // Image preview
    const imageInput = document.getElementById('categoryImage');
    if (imageInput) {
        imageInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    // You can add image preview here
                    console.log('Image selected:', file.name);
                };
                reader.readAsDataURL(file);
            }
        });
    }
});

// Export functions for use in HTML
window.toggleCategory = toggleCategory;
window.expandAll = expandAll;
window.collapseAll = collapseAll;
window.addSubCategory = addSubCategory;
window.editCategory = editCategory;
window.deleteCategory = deleteCategory;
window.saveCategory = saveCategory;