// Add Product Page JavaScript

let variantsData = [];
let galleryImagesData = [];

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeSelect2();
    initializeImageUpload();
    initializeFormHandlers();
    initializeSEOPreview();
    initializeCharacterCount();
    setupAutoSlug();
    setupStatusHandlers();
});

// Initialize Select2
function initializeSelect2() {
    $('#categories').select2({
        theme: 'bootstrap-5',
        placeholder: 'Chọn danh mục',
        allowClear: true
    });

    $('#tags').select2({
        theme: 'bootstrap-5',
        placeholder: 'Chọn tags',
        allowClear: true,
        tags: true
    });
}

// Initialize Image Upload
function initializeImageUpload() {
    const mainImageInput = document.getElementById('mainImage');
    const mainImageBox = document.getElementById('mainImageBox');
    const galleryInput = document.getElementById('galleryImages');

    // Main image upload
    if (mainImageInput) {
        mainImageInput.addEventListener('change', function(e) {
            handleMainImageUpload(e.target.files[0]);
        });

        // Drag and drop for main image
        mainImageBox.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.classList.add('dragover');
        });

        mainImageBox.addEventListener('dragleave', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');
        });

        mainImageBox.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');
            const file = e.dataTransfer.files[0];
            if (file && file.type.startsWith('image/')) {
                handleMainImageUpload(file);
            }
        });
    }

    // Gallery images upload
    if (galleryInput) {
        galleryInput.addEventListener('change', function(e) {
            handleGalleryImagesUpload(Array.from(e.target.files));
        });
    }
}

// Handle main image upload
function handleMainImageUpload(file) {
    if (!file) return;

    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
        showToast('Kích thước ảnh không được vượt quá 5MB', 'error');
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        const preview = document.getElementById('mainImagePreview');
        preview.innerHTML = `
            <img src="${e.target.result}" alt="Main Image">
            <button type="button" class="remove-image" onclick="removeMainImage()">
                <i class="bi bi-x-lg"></i>
            </button>
        `;
        preview.classList.add('active');
    };
    reader.readAsDataURL(file);
}

// Remove main image
function removeMainImage() {
    const preview = document.getElementById('mainImagePreview');
    const input = document.getElementById('mainImage');
    preview.innerHTML = '';
    preview.classList.remove('active');
    input.value = '';
}

// Handle gallery images upload
function handleGalleryImagesUpload(files) {
    const galleryGrid = document.getElementById('galleryGrid');
    const currentCount = galleryImagesData.length;

    if (currentCount + files.length > 10) {
        showToast('Tối đa 10 ảnh trong thư viện', 'warning');
        return;
    }

    files.forEach((file, index) => {
        if (file.size > 5 * 1024 * 1024) {
            showToast(`Ảnh ${file.name} vượt quá 5MB`, 'error');
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            const imageId = Date.now() + index;
            galleryImagesData.push({
                id: imageId,
                file: file,
                url: e.target.result
            });

            const galleryItem = document.createElement('div');
            galleryItem.className = 'gallery-item';
            galleryItem.dataset.imageId = imageId;
            galleryItem.innerHTML = `
                <img src="${e.target.result}" alt="Gallery Image">
                <button type="button" class="remove-gallery-image" onclick="removeGalleryImage(${imageId})">
                    <i class="bi bi-x-lg"></i>
                </button>
            `;

            // Insert before upload box
            const uploadBox = galleryGrid.querySelector('.gallery-upload-box');
            galleryGrid.insertBefore(galleryItem, uploadBox);
        };
        reader.readAsDataURL(file);
    });
}

// Remove gallery image
function removeGalleryImage(imageId) {
    galleryImagesData = galleryImagesData.filter(img => img.id !== imageId);
    const item = document.querySelector(`[data-image-id="${imageId}"]`);
    if (item) {
        item.remove();
    }
}

// Add Variant
function addVariant() {
    const modal = new bootstrap.Modal(document.getElementById('variantModal'));

    // Reset form
    document.getElementById('variantName').value = '';
    document.getElementById('variantSku').value = '';
    document.getElementById('variantRegularPrice').value = '';
    document.getElementById('variantSalePrice').value = '';
    document.getElementById('variantStock').value = '0';
    document.getElementById('variantImage').value = '';

    modal.show();
}

// Save Variant
function saveVariant() {
    const name = document.getElementById('variantName').value;
    const sku = document.getElementById('variantSku').value;
    const regularPrice = document.getElementById('variantRegularPrice').value;
    const salePrice = document.getElementById('variantSalePrice').value;
    const stock = document.getElementById('variantStock').value;
    const imageFile = document.getElementById('variantImage').files[0];

    if (!name) {
        showToast('Vui lòng nhập tên biến thể', 'error');
        return;
    }

    const variantId = Date.now();
    const variant = {
        id: variantId,
        name: name,
        sku: sku,
        regularPrice: regularPrice,
        salePrice: salePrice,
        stock: stock,
        image: null
    };

    if (imageFile) {
        const reader = new FileReader();
        reader.onload = function(e) {
            variant.image = e.target.result;
            addVariantToList(variant);
        };
        reader.readAsDataURL(imageFile);
    } else {
        addVariantToList(variant);
    }

    bootstrap.Modal.getInstance(document.getElementById('variantModal')).hide();
}

// Add variant to list
function addVariantToList(variant) {
    variantsData.push(variant);

    const container = document.getElementById('variantsContainer');

    // Remove empty state if exists
    const emptyState = container.querySelector('.empty-state');
    if (emptyState) {
        emptyState.remove();
    }

    const variantItem = document.createElement('div');
    variantItem.className = 'variant-item';
    variantItem.dataset.variantId = variant.id;
    variantItem.innerHTML = `
        <img src="${variant.image || 'https://via.placeholder.com/80'}" alt="${variant.name}" class="variant-image">
        <div class="variant-info">
            <div class="variant-name">${variant.name}</div>
            <div class="variant-details">
                ${variant.sku ? `<div class="variant-detail-item">SKU: <strong>${variant.sku}</strong></div>` : ''}
                <div class="variant-detail-item">Giá: <strong>${formatPrice(variant.regularPrice)}₫</strong></div>
                ${variant.salePrice ? `<div class="variant-detail-item">Giá KM: <strong>${formatPrice(variant.salePrice)}₫</strong></div>` : ''}
                <div class="variant-detail-item">Tồn kho: <strong>${variant.stock}</strong></div>
            </div>
        </div>
        <div class="variant-actions">
            <button type="button" class="btn btn-sm btn-outline-primary" onclick="editVariant(${variant.id})">
                <i class="bi bi-pencil"></i>
            </button>
            <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeVariant(${variant.id})">
                <i class="bi bi-trash"></i>
            </button>
        </div>
    `;

    container.appendChild(variantItem);
}

// Edit Variant
function editVariant(variantId) {
    const variant = variantsData.find(v => v.id === variantId);
    if (!variant) return;

    document.getElementById('variantName').value = variant.name;
    document.getElementById('variantSku').value = variant.sku;
    document.getElementById('variantRegularPrice').value = variant.regularPrice;
    document.getElementById('variantSalePrice').value = variant.salePrice;
    document.getElementById('variantStock').value = variant.stock;

    // Remove old variant
    removeVariant(variantId);

    const modal = new bootstrap.Modal(document.getElementById('variantModal'));
    modal.show();
}

// Remove Variant
function removeVariant(variantId) {
    if (!confirm('Bạn có chắc chắn muốn xóa biến thể này?')) return;

    variantsData = variantsData.filter(v => v.id !== variantId);
    const item = document.querySelector(`[data-variant-id="${variantId}"]`);
    if (item) {
        item.remove();
    }

    // Show empty state if no variants
    const container = document.getElementById('variantsContainer');
    if (variantsData.length === 0 && !container.querySelector('.empty-state')) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <p>Chưa có biến thể nào</p>
                <button type="button" class="btn btn-primary" onclick="addVariant()">
                    <i class="bi bi-plus-lg"></i> Thêm biến thể đầu tiên
                </button>
            </div>
        `;
    }
}

// Format price
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN').format(price);
}

// Initialize form handlers
function initializeFormHandlers() {
    // Price validation
    const salePriceInput = document.getElementById('salePrice');
    const regularPriceInput = document.getElementById('regularPrice');

    if (salePriceInput && regularPriceInput) {
        salePriceInput.addEventListener('blur', function() {
            const salePrice = parseFloat(this.value);
            const regularPrice = parseFloat(regularPriceInput.value);

            if (salePrice && regularPrice && salePrice >= regularPrice) {
                showToast('Giá khuyến mãi phải nhỏ hơn giá gốc', 'warning');
                this.value = '';
            }
        });
    }
}

// Initialize SEO Preview
function initializeSEOPreview() {
    const productNameInput = document.getElementById('productName');
    const metaTitleInput = document.getElementById('metaTitle');
    const metaDescInput = document.getElementById('metaDescription');
    const slugInput = document.getElementById('productSlug');

    const seoTitlePreview = document.getElementById('seoTitlePreview');
    const seoDescPreview = document.getElementById('seoDescPreview');
    const seoUrlPreview = document.getElementById('seoUrlPreview');

    if (productNameInput) {
        productNameInput.addEventListener('input', function() {
            if (!metaTitleInput.value) {
                seoTitlePreview.textContent = this.value || 'Tiêu đề sản phẩm của bạn';
            }
        });
    }

    if (metaTitleInput) {
        metaTitleInput.addEventListener('input', function() {
            seoTitlePreview.textContent = this.value || productNameInput.value || 'Tiêu đề sản phẩm của bạn';
        });
    }

    if (metaDescInput) {
        metaDescInput.addEventListener('input', function() {
            seoDescPreview.textContent = this.value || 'Mô tả sản phẩm của bạn sẽ hiển thị ở đây...';
        });
    }

    if (slugInput) {
        slugInput.addEventListener('input', function() {
            seoUrlPreview.textContent = 'https://example.com/san-pham/' + (this.value || '');
        });
    }
}

// Initialize character count
function initializeCharacterCount() {
    const metaTitleInput = document.getElementById('metaTitle');
    const metaDescInput = document.getElementById('metaDescription');
    const metaTitleCount = document.getElementById('metaTitleCount');
    const metaDescCount = document.getElementById('metaDescCount');

    if (metaTitleInput && metaTitleCount) {
        metaTitleInput.addEventListener('input', function() {
            metaTitleCount.textContent = this.value.length;
            if (this.value.length > 60) {
                metaTitleCount.style.color = '#dc3545';
            } else {
                metaTitleCount.style.color = '';
            }
        });
    }

    if (metaDescInput && metaDescCount) {
        metaDescInput.addEventListener('input', function() {
            metaDescCount.textContent = this.value.length;
            if (this.value.length > 160) {
                metaDescCount.style.color = '#dc3545';
            } else {
                metaDescCount.style.color = '';
            }
        });
    }
}

// Setup auto slug generation
function setupAutoSlug() {
    const productNameInput = document.getElementById('productName');
    const slugInput = document.getElementById('productSlug');

    if (productNameInput && slugInput) {
        productNameInput.addEventListener('input', function() {
            if (!slugInput.value || slugInput.dataset.autoGenerated === 'true') {
                slugInput.value = generateSlug(this.value);
                slugInput.dataset.autoGenerated = 'true';
            }
        });

        slugInput.addEventListener('input', function() {
            if (this.value) {
                this.dataset.autoGenerated = 'false';
            }
        });
    }
}

// Generate slug
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

// Setup status handlers
function setupStatusHandlers() {
    const statusSelect = document.getElementById('status');
    const scheduleGroup = document.getElementById('scheduleGroup');

    if (statusSelect && scheduleGroup) {
        statusSelect.addEventListener('change', function() {
            if (this.value === 'scheduled') {
                scheduleGroup.style.display = 'block';
            } else {
                scheduleGroup.style.display = 'none';
            }
        });
    }
}

// Save Product
function saveProduct(status) {
    const form = document.getElementById('productForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // Validate required fields
    const productName = document.getElementById('productName').value;
    const regularPrice = document.getElementById('regularPrice').value;
    const stockQuantity = document.getElementById('stockQuantity').value;

    if (!productName) {
        showToast('Vui lòng nhập tên sản phẩm', 'error');
        return;
    }

    if (!regularPrice) {
        showToast('Vui lòng nhập giá sản phẩm', 'error');
        return;
    }

    // Prepare form data
    const formData = new FormData(form);
    formData.append('status', status);
    formData.append('variants', JSON.stringify(variantsData));
    formData.append('galleryImages', JSON.stringify(galleryImagesData.map(img => img.url)));

    // Show loading
    const saveButton = event.target;
    const originalText = saveButton.innerHTML;
    saveButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';
    saveButton.disabled = true;

    // Send to server
    fetch('/admin/api/products', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã lưu sản phẩm thành công', 'success');
                setTimeout(() => {
                    window.location.href = '/admin/products';
                }, 1500);
            } else {
                showToast(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error saving product:', error);
            showToast('Có lỗi xảy ra khi lưu sản phẩm', 'error');
        })
        .finally(() => {
            saveButton.innerHTML = originalText;
            saveButton.disabled = false;
        });
}

// Preview Product
function previewProduct() {
    const productName = document.getElementById('productName').value;

    if (!productName) {
        showToast('Vui lòng nhập tên sản phẩm để xem trước', 'warning');
        return;
    }

    // Open preview in new tab
    const previewData = {
        name: productName,
        slug: document.getElementById('productSlug').value,
        shortDescription: document.getElementById('shortDescription').value,
        fullDescription: document.getElementById('fullDescription').value,
        regularPrice: document.getElementById('regularPrice').value,
        salePrice: document.getElementById('salePrice').value,
        sku: document.getElementById('sku').value,
        stockQuantity: document.getElementById('stockQuantity').value
    };

    // Store in session storage
    sessionStorage.setItem('productPreview', JSON.stringify(previewData));

    // Open preview window
    window.open('/admin/products/preview', '_blank');
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

    toastElement.addEventListener('hidden.bs.toast', function() {
        toastElement.remove();
    });
}

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + S to save draft
    if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        saveProduct('draft');
    }

    // Ctrl/Cmd + Shift + S to publish
    if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 's') {
        e.preventDefault();
        saveProduct('published');
    }

    // Ctrl/Cmd + P to preview
    if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
        e.preventDefault();
        previewProduct();
    }
});

// Auto-save draft
let autoSaveTimer;
function enableAutoSave() {
    const form = document.getElementById('productForm');

    form.addEventListener('input', function() {
        clearTimeout(autoSaveTimer);
        autoSaveTimer = setTimeout(() => {
            const productName = document.getElementById('productName').value;
            if (productName) {
                // Save to localStorage
                const draftData = {
                    name: productName,
                    slug: document.getElementById('productSlug').value,
                    shortDescription: document.getElementById('shortDescription').value,
                    timestamp: new Date().toISOString()
                };
                localStorage.setItem('productDraft', JSON.stringify(draftData));
                console.log('Auto-saved draft');
            }
        }, 5000); // Auto-save after 5 seconds of inactivity
    });
}

// Load draft on page load
function loadDraft() {
    const draft = localStorage.getItem('productDraft');
    if (draft) {
        const draftData = JSON.parse(draft);
        const message = `Có bản nháp được lưu lúc ${new Date(draftData.timestamp).toLocaleString('vi-VN')}. Bạn có muốn khôi phục?`;

        if (confirm(message)) {
            document.getElementById('productName').value = draftData.name;
            document.getElementById('productSlug').value = draftData.slug;
            document.getElementById('shortDescription').value = draftData.shortDescription;
            showToast('Đã khôi phục bản nháp', 'success');
        }
    }
}

// Enable auto-save
enableAutoSave();

// Load draft if exists
loadDraft();

// Export functions for use in HTML
window.addVariant = addVariant;
window.saveVariant = saveVariant;
window.editVariant = editVariant;
window.removeVariant = removeVariant;
window.removeMainImage = removeMainImage;
window.removeGalleryImage = removeGalleryImage;
window.saveProduct = saveProduct;
window.previewProduct = previewProduct;