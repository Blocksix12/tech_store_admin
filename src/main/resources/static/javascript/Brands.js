// Brands Page JavaScript

// Change View (Grid/List)
function changeView(viewType) {
    const gridView = document.getElementById('brandsGridView');
    const listView = document.getElementById('brandsListView');
    const gridBtn = document.getElementById('viewGrid');
    const listBtn = document.getElementById('viewList');

    if (viewType === 'grid') {
        gridView.style.display = 'grid';
        listView.style.display = 'none';
        gridBtn.classList.add('active');
        listBtn.classList.remove('active');
        localStorage.setItem('brandsView', 'grid');
    } else {
        gridView.style.display = 'none';
        listView.style.display = 'block';
        gridBtn.classList.remove('active');
        listBtn.classList.add('active');
        localStorage.setItem('brandsView', 'list');
    }
}

// Edit Brand
function editBrand(brandId) {
    const modal = new bootstrap.Modal(document.getElementById('brandModal'));

    // Change modal title
    document.getElementById('brandModalLabel').innerHTML = '<i class="bi bi-pencil"></i> Chỉnh sửa thương hiệu';

    // In production, fetch brand data from API
    fetch(`/admin/api/brands/${brandId}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('brandName').value = data.name || '';
            document.getElementById('brandStatus').value = data.status || 'active';
            document.getElementById('brandCountry').value = data.country || '';
            document.getElementById('brandYear').value = data.year || '';
            document.getElementById('brandSlug').value = data.slug || '';
            document.getElementById('brandDescription').value = data.description || '';
            document.getElementById('brandDetailDescription').value = data.detailDescription || '';
            document.getElementById('brandWebsite').value = data.website || '';
            document.getElementById('brandOrder').value = data.order || 0;
            document.getElementById('brandFeatured').checked = data.featured || false;
            document.getElementById('brandMetaTitle').value = data.metaTitle || '';
            document.getElementById('brandMetaDescription').value = data.metaDescription || '';
            document.getElementById('brandMetaKeywords').value = data.metaKeywords || '';
        })
        .catch(error => {
            console.error('Error loading brand:', error);
            showToast('Có lỗi xảy ra khi tải thông tin thương hiệu', 'error');
        });

    modal.show();
}

// Delete Brand
function deleteBrand(brandId) {
    if (confirm('Bạn có chắc chắn muốn xóa thương hiệu này?\n\nLưu ý: Tất cả sản phẩm thuộc thương hiệu này sẽ không còn liên kết.')) {
        // Show loading
        const brandCard = document.querySelector(`[data-brand-id="${brandId}"]`);
        if (brandCard) {
            brandCard.style.opacity = '0.5';
            brandCard.style.pointerEvents = 'none';
        }

        // In production, call API to delete
        fetch(`/admin/api/brands/${brandId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Remove brand from DOM with animation
                    if (brandCard) {
                        brandCard.style.transition = 'all 0.3s ease';
                        brandCard.style.transform = 'scale(0.8)';
                        brandCard.style.opacity = '0';
                        setTimeout(() => {
                            brandCard.remove();
                            updateBrandsCount();
                        }, 300);
                    }
                    showToast('Đã xóa thương hiệu thành công', 'success');
                } else {
                    if (brandCard) {
                        brandCard.style.opacity = '1';
                        brandCard.style.pointerEvents = 'auto';
                    }
                    showToast(data.message || 'Có lỗi xảy ra', 'error');
                }
            })
            .catch(error => {
                console.error('Error deleting brand:', error);
                if (brandCard) {
                    brandCard.style.opacity = '1';
                    brandCard.style.pointerEvents = 'auto';
                }
                showToast('Có lỗi xảy ra khi xóa thương hiệu', 'error');
            });
    }
}

// Save Brand
function saveBrand() {
    const form = document.getElementById('brandForm');

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    // Get form data
    const brandData = {
        name: document.getElementById('brandName').value,
        status: document.getElementById('brandStatus').value,
        country: document.getElementById('brandCountry').value,
        year: document.getElementById('brandYear').value,
        slug: document.getElementById('brandSlug').value,
        description: document.getElementById('brandDescription').value,
        detailDescription: document.getElementById('brandDetailDescription').value,
        website: document.getElementById('brandWebsite').value,
        order: parseInt(document.getElementById('brandOrder').value) || 0,
        featured: document.getElementById('brandFeatured').checked,
        metaTitle: document.getElementById('brandMetaTitle').value,
        metaDescription: document.getElementById('brandMetaDescription').value,
        metaKeywords: document.getElementById('brandMetaKeywords').value
    };

    // Handle file uploads
    const logoFile = document.getElementById('brandLogo').files[0];
    const bannerFile = document.getElementById('brandBanner').files[0];

    if (logoFile || bannerFile) {
        const formData = new FormData();
        if (logoFile) formData.append('logo', logoFile);
        if (bannerFile) formData.append('banner', bannerFile);

        Object.keys(brandData).forEach(key => {
            formData.append(key, brandData[key]);
        });

        uploadBrandWithFiles(formData);
    } else {
        saveBrandData(brandData);
    }
}

// Save brand data to server
function saveBrandData(brandData) {
    const saveButton = document.querySelector('#brandModal .btn-primary');
    const originalText = saveButton.innerHTML;
    saveButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';
    saveButton.disabled = true;

    fetch('/admin/api/brands', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(brandData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã lưu thương hiệu thành công', 'success');
                bootstrap.Modal.getInstance(document.getElementById('brandModal')).hide();
                setTimeout(() => {
                    location.reload();
                }, 500);
            } else {
                showToast(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error saving brand:', error);
            showToast('Có lỗi xảy ra khi lưu thương hiệu', 'error');
        })
        .finally(() => {
            saveButton.innerHTML = originalText;
            saveButton.disabled = false;
        });
}

// Upload brand with files
function uploadBrandWithFiles(formData) {
    const saveButton = document.querySelector('#brandModal .btn-primary');
    const originalText = saveButton.innerHTML;
    saveButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang tải lên...';
    saveButton.disabled = true;

    fetch('/admin/api/brands/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('Đã lưu thương hiệu thành công', 'success');
                bootstrap.Modal.getInstance(document.getElementById('brandModal')).hide();
                setTimeout(() => {
                    location.reload();
                }, 500);
            } else {
                showToast(data.message || 'Có lỗi xảy ra', 'error');
            }
        })
        .catch(error => {
            console.error('Error uploading brand:', error);
            showToast('Có lỗi xảy ra khi tải lên', 'error');
        })
        .finally(() => {
            saveButton.innerHTML = originalText;
            saveButton.disabled = false;
        });
}

// Auto-generate slug from brand name
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

// Search Brands
function searchBrands(searchTerm) {
    const brandCards = document.querySelectorAll('.brand-card');
    const searchLower = searchTerm.toLowerCase();

    let visibleCount = 0;

    brandCards.forEach(card => {
        const brandName = card.querySelector('.brand-name')?.textContent.toLowerCase();
        const brandDescription = card.querySelector('.brand-description')?.textContent.toLowerCase();
        const brandOrigin = card.querySelector('.brand-origin')?.textContent.toLowerCase();

        if (!searchTerm ||
            (brandName && brandName.includes(searchLower)) ||
            (brandDescription && brandDescription.includes(searchLower)) ||
            (brandOrigin && brandOrigin.includes(searchLower))) {
            card.style.display = '';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });

    // Show empty state if no results
    showEmptyState(visibleCount === 0);
}

// Filter Brands
function filterBrands() {
    const status = document.getElementById('filterStatus').value;
    const country = document.getElementById('filterCountry').value;
    const brandCards = document.querySelectorAll('.brand-card');

    let visibleCount = 0;

    brandCards.forEach(card => {
        let shouldShow = true;

        // Filter by status
        if (status) {
            const statusBadge = card.querySelector('.badge');
            const cardStatus = statusBadge?.textContent.includes('Đang hoạt động') ? 'active' : 'inactive';
            if (cardStatus !== status) {
                shouldShow = false;
            }
        }

        // Filter by country
        if (country && shouldShow) {
            const origin = card.querySelector('.brand-origin')?.textContent.toLowerCase();
            // Map country codes to Vietnamese names
            const countryMap = {
                'us': 'mỹ',
                'kr': 'hàn quốc',
                'cn': 'trung quốc',
                'jp': 'nhật bản',
                'tw': 'đài loan',
                'vn': 'việt nam'
            };
            if (origin && !origin.includes(countryMap[country])) {
                shouldShow = false;
            }
        }

        card.style.display = shouldShow ? '' : 'none';
        if (shouldShow) visibleCount++;
    });

    showEmptyState(visibleCount === 0);
}

// Sort Brands
function sortBrands(sortBy) {
    const grid = document.getElementById('brandsGridView');
    const cards = Array.from(grid.querySelectorAll('.brand-card'));

    cards.sort((a, b) => {
        switch(sortBy) {
            case 'name':
                return a.querySelector('.brand-name').textContent.localeCompare(
                    b.querySelector('.brand-name').textContent
                );
            case 'name_desc':
                return b.querySelector('.brand-name').textContent.localeCompare(
                    a.querySelector('.brand-name').textContent
                );
            case 'products':
                const aProducts = parseInt(a.querySelector('.brand-stat-item .stat-value').textContent);
                const bProducts = parseInt(b.querySelector('.brand-stat-item .stat-value').textContent);
                return bProducts - aProducts;
            case 'revenue':
                const aRevenue = a.querySelectorAll('.brand-stat-item .stat-value')[1].textContent;
                const bRevenue = b.querySelectorAll('.brand-stat-item .stat-value')[1].textContent;
                return parseFloat(bRevenue) - parseFloat(aRevenue);
            default:
                return 0;
        }
    });

    cards.forEach(card => grid.appendChild(card));
}

// Show Empty State
function showEmptyState(show) {
    let emptyState = document.getElementById('brandsEmptyState');

    if (show) {
        if (!emptyState) {
            const grid = document.getElementById('brandsGridView');
            emptyState = document.createElement('div');
            emptyState.id = 'brandsEmptyState';
            emptyState.className = 'brands-empty';
            emptyState.innerHTML = `
                <i class="bi bi-award"></i>
                <h5>Không tìm thấy thương hiệu</h5>
                <p>Vui lòng thử lại với từ khóa hoặc bộ lọc khác</p>
                <button class="btn btn-primary" onclick="resetFilters()">
                    <i class="bi bi-arrow-clockwise"></i> Đặt lại bộ lọc
                </button>
            `;
            grid.parentElement.appendChild(emptyState);
        }
        emptyState.style.display = 'block';
        document.getElementById('brandsGridView').style.display = 'none';
    } else {
        if (emptyState) {
            emptyState.style.display = 'none';
        }
        document.getElementById('brandsGridView').style.display = 'grid';
    }
}

// Reset Filters
function resetFilters() {
    document.getElementById('filterStatus').value = '';
    document.getElementById('filterCountry').value = '';
    document.getElementById('sortBy').value = 'name';
    document.getElementById('searchBrands').value = '';

    const brandCards = document.querySelectorAll('.brand-card');
    brandCards.forEach(card => {
        card.style.display = '';
    });

    showEmptyState(false);
}

// Update Brands Count
function updateBrandsCount() {
    const visibleCards = document.querySelectorAll('.brand-card[style*="display: none"]').length;
    const totalCards = document.querySelectorAll('.brand-card').length;
    const activeCount = totalCards - visibleCards;

    // Update stats if needed
    console.log(`Showing ${activeCount} of ${totalCards} brands`);
}

// Image Preview
function previewImage(input, previewId) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            let preview = document.getElementById(previewId);
            if (!preview) {
                preview = document.createElement('img');
                preview.id = previewId;
                preview.className = 'image-preview';
                input.parentElement.appendChild(preview);
            }
            preview.src = e.target.result;
            preview.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Restore saved view preference
    const savedView = localStorage.getItem('brandsView') || 'grid';
    changeView(savedView);

    // Auto-generate slug from brand name
    const brandNameInput = document.getElementById('brandName');
    const brandSlugInput = document.getElementById('brandSlug');

    if (brandNameInput && brandSlugInput) {
        brandNameInput.addEventListener('input', function() {
            if (!brandSlugInput.value || brandSlugInput.dataset.autoGenerated === 'true') {
                brandSlugInput.value = generateSlug(this.value);
                brandSlugInput.dataset.autoGenerated = 'true';
            }
        });

        brandSlugInput.addEventListener('input', function() {
            if (this.value) {
                this.dataset.autoGenerated = 'false';
            }
        });
    }

    // Search functionality
    const searchInput = document.getElementById('searchBrands');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function(e) {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                searchBrands(e.target.value);
            }, 300);
        });
    }

    // Filter functionality
    const filterStatus = document.getElementById('filterStatus');
    const filterCountry = document.getElementById('filterCountry');

    if (filterStatus) {
        filterStatus.addEventListener('change', filterBrands);
    }

    if (filterCountry) {
        filterCountry.addEventListener('change', filterBrands);
    }

    // Sort functionality
    const sortBy = document.getElementById('sortBy');
    if (sortBy) {
        sortBy.addEventListener('change', function() {
            sortBrands(this.value);
        });
    }

    // Reset modal when closed
    const brandModal = document.getElementById('brandModal');
    if (brandModal) {
        brandModal.addEventListener('hidden.bs.modal', function() {
            document.getElementById('brandForm').reset();
            document.getElementById('brandModalLabel').innerHTML = '<i class="bi bi-award-fill"></i> Thêm thương hiệu mới';

            // Remove image previews
            const previews = document.querySelectorAll('.image-preview');
            previews.forEach(preview => preview.remove());
        });
    }

    // Image preview for logo
    const logoInput = document.getElementById('brandLogo');
    if (logoInput) {
        logoInput.addEventListener('change', function() {
            previewImage(this, 'logoPreview');
        });
    }

    // Image preview for banner
    const bannerInput = document.getElementById('brandBanner');
    if (bannerInput) {
        bannerInput.addEventListener('change', function() {
            previewImage(this, 'bannerPreview');
        });
    }

    // Add keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + K to focus search
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            searchInput?.focus();
        }

        // Ctrl/Cmd + N to add new brand
        if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
            e.preventDefault();
            const addButton = document.querySelector('[data-bs-target="#brandModal"]');
            addButton?.click();
        }
    });
});

// Export functions for use in HTML
window.changeView = changeView;
window.editBrand = editBrand;
window.deleteBrand = deleteBrand;
window.saveBrand = saveBrand;
window.resetFilters = resetFilters;