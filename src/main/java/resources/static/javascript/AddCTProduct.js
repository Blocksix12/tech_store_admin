// AddCTProduct.js - Product Variant Form Logic

// Set current date
document.getElementById('currentDate').textContent = new Date().toLocaleDateString('vi-VN');

// Format number to VND currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount || 0);
}

// Update preview when product is selected
document.getElementById('productId').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const productName = selectedOption.text;
    document.getElementById('previewProduct').textContent = productName !== '-- Chọn sản phẩm --' ? productName : '-';
    updatePriceDisplay();
});

// Update preview when color is selected
document.getElementById('colorId').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const colorName = selectedOption.text;
    document.getElementById('previewColor').textContent = colorName !== '-- Chọn màu sắc --' ? colorName : '-';
});

// Update preview when storage is selected
document.getElementById('storageId').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const storageName = selectedOption.text;
    document.getElementById('previewStorage').textContent = storageName !== '-- Chọn dung lượng --' ? storageName : '-';
});

// Update preview when size is selected
document.getElementById('sizeId').addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const sizeName = selectedOption.text;
    document.getElementById('previewSize').textContent = sizeName !== '-- Chọn kích thước --' ? sizeName : '-';
});

// Update price display
function updatePriceDisplay() {
    const price = parseFloat(document.getElementById('price').value) || 0;
    const salePrice = parseFloat(document.getElementById('salePrice').value) || 0;

    let displayPrice = price;
    let discount = 0;

    if (salePrice > 0 && salePrice < price) {
        displayPrice = salePrice;
        discount = price - salePrice;
    }

    document.getElementById('displayPrice').textContent = formatCurrency(displayPrice);
    document.getElementById('discountAmount').textContent = formatCurrency(discount);
    document.getElementById('previewPrice').textContent = formatCurrency(displayPrice);
}

// Update preview when price changes
document.getElementById('price').addEventListener('input', updatePriceDisplay);
document.getElementById('salePrice').addEventListener('input', updatePriceDisplay);

// Update quantity preview
document.getElementById('quantity').addEventListener('input', function() {
    const quantity = parseInt(this.value) || 0;
    document.getElementById('previewQuantity').textContent = quantity;
});

// Format price input on blur
document.getElementById('price').addEventListener('blur', function() {
    if (this.value) {
        this.value = parseFloat(this.value).toFixed(2);
        updatePriceDisplay();
    }
});

document.getElementById('salePrice').addEventListener('blur', function() {
    if (this.value) {
        this.value = parseFloat(this.value).toFixed(2);
        updatePriceDisplay();
    }
});

// Form validation
document.getElementById('ctProductForm').addEventListener('submit', function(e) {
    const productId = document.getElementById('productId').value;
    const colorId = document.getElementById('colorId').value;
    const price = parseFloat(document.getElementById('price').value);
    const salePrice = parseFloat(document.getElementById('salePrice').value);
    const quantity = parseInt(document.getElementById('quantity').value);

    // Validate product selection
    if (!productId) {
        e.preventDefault();
        alert('Vui lòng chọn sản phẩm!');
        document.getElementById('productId').focus();
        return false;
    }

    // Validate color selection
    if (!colorId) {
        e.preventDefault();
        alert('Vui lòng chọn màu sắc!');
        document.getElementById('colorId').focus();
        return false;
    }

    // Validate price
    if (!price || price <= 0) {
        e.preventDefault();
        alert('Giá gốc phải lớn hơn 0!');
        document.getElementById('price').focus();
        return false;
    }

    // Validate sale price
    if (salePrice > 0 && salePrice >= price) {
        e.preventDefault();
        alert('Giá khuyến mãi phải nhỏ hơn giá gốc!');
        document.getElementById('salePrice').focus();
        return false;
    }

    // Validate quantity
    if (quantity < 0) {
        e.preventDefault();
        alert('Số lượng không được âm!');
        document.getElementById('quantity').focus();
        return false;
    }

    // Show loading state
    const submitBtn = this.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Đang lưu...';

    return true;
});

// Reset form function
function resetForm() {
    if (confirm('Bạn có chắc muốn xóa tất cả dữ liệu đã nhập?')) {
        document.getElementById('ctProductForm').reset();

        // Reset preview
        document.getElementById('previewProduct').textContent = '-';
        document.getElementById('previewColor').textContent = '-';
        document.getElementById('previewStorage').textContent = '-';
        document.getElementById('previewSize').textContent = '-';
        document.getElementById('previewPrice').textContent = '0 ₫';
        document.getElementById('previewQuantity').textContent = '0';

        // Reset price display
        document.getElementById('displayPrice').textContent = '0 ₫';
        document.getElementById('discountAmount').textContent = '0 ₫';
    }
}

// Prevent negative numbers in quantity input
document.getElementById('quantity').addEventListener('input', function() {
    if (this.value < 0) {
        this.value = 0;
    }
});

// Prevent negative numbers in price inputs
document.getElementById('price').addEventListener('input', function() {
    if (this.value < 0) {
        this.value = 0;
    }
});

document.getElementById('salePrice').addEventListener('input', function() {
    if (this.value < 0) {
        this.value = 0;
    }
});

// Auto-calculate discount percentage (optional display)
function calculateDiscountPercentage() {
    const price = parseFloat(document.getElementById('price').value) || 0;
    const salePrice = parseFloat(document.getElementById('salePrice').value) || 0;

    if (salePrice > 0 && salePrice < price) {
        const percentage = ((price - salePrice) / price * 100).toFixed(0);
        return percentage;
    }
    return 0;
}

// Update discount display with percentage
document.getElementById('salePrice').addEventListener('input', function() {
    const percentage = calculateDiscountPercentage();
    if (percentage > 0) {
        document.getElementById('discountAmount').innerHTML =
            `${formatCurrency(parseFloat(document.getElementById('price').value) - parseFloat(this.value))} 
            <small class="text-success">(${percentage}%)</small>`;
    } else {
        document.getElementById('discountAmount').textContent = '0 ₫';
    }
});

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    updatePriceDisplay();
});