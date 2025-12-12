// Import/Export Page JavaScript

let uploadedFile = null;
let previewData = [];
let columnMapping = {};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeFileUpload();
    initializeExportFields();
});

// Initialize File Upload
function initializeFileUpload() {
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('importFile');

    if (!uploadArea || !fileInput) return;

    // Click to upload
    uploadArea.addEventListener('click', function() {
        fileInput.click();
    });

    // File input change
    fileInput.addEventListener('change', function(e) {
        handleFileSelect(e.target.files[0]);
    });

    // Drag and drop
    uploadArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        e.stopPropagation();
        this.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', function(e) {
        e.preventDefault();
        e.stopPropagation();
        this.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', function(e) {
        e.preventDefault();
        e.stopPropagation();
        this.classList.remove('dragover');

        const file = e.dataTransfer.files[0];
        if (file) {
            handleFileSelect(file);
        }
    });
}

// Handle File Selection
function handleFileSelect(file) {
    if (!file) return;

    // Validate file type
    const allowedTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // .xlsx
        'application/vnd.ms-excel', // .xls
        'text/csv' // .csv
    ];

    if (!allowedTypes.includes(file.type)) {
        showToast('Định dạng file không hỗ trợ! Vui lòng chọn file Excel hoặc CSV.', 'error');
        return;
    }

    // Validate file size (max 10MB)
    if (file.size > 10 * 1024 * 1024) {
        showToast('Kích thước file quá lớn! Tối đa 10MB.', 'error');
        return;
    }

    uploadedFile = file;

    // Show file info
    showFileInfo(file);

    // Read and parse file
    readFile(file);
}

// Show File Info
function showFileInfo(file) {
    const uploadArea = document.getElementById('uploadArea');
    const fileInfo = document.getElementById('fileInfo');
    const importSettings = document.getElementById('importSettings');

    uploadArea.style.display = 'none';
    fileInfo.style.display = 'block';
    importSettings.style.display = 'block';

    // Update file details
    document.getElementById('fileName').textContent = file.name;
    document.getElementById('fileSize').textContent = formatFileSize(file.size);

    // Simulate upload progress
    simulateUploadProgress();
}

// Simulate Upload Progress
function simulateUploadProgress() {
    const progressBar = document.getElementById('uploadProgress');
    let progress = 0;

    const interval = setInterval(() => {
        progress += 10;
        progressBar.style.width = progress + '%';

        if (progress >= 100) {
            clearInterval(interval);
        }
    }, 100);
}

// Read File
function readFile(file) {
    const reader = new FileReader();

    reader.onload = function(e) {
        const data = e.target.result;
        parseFile(data, file.type);
    };

    reader.onerror = function() {
        showToast('Lỗi khi đọc file!', 'error');
    };

    reader.readAsBinaryString(file);
}

// Parse File
function parseFile(data, fileType) {
    try {
        let workbook;

        if (fileType.includes('csv')) {
            // Parse CSV
            workbook = XLSX.read(data, { type: 'binary' });
        } else {
            // Parse Excel
            workbook = XLSX.read(data, { type: 'binary' });
        }

        const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(firstSheet, { header: 1 });

        previewData = jsonData;

        // Update file row count
        document.getElementById('fileRows').textContent = `${jsonData.length} dòng`;

        // Show column mapping
        showColumnMapping(jsonData[0]);

        // Show data preview
        showDataPreview(jsonData);

        // Validate data
        validateData(jsonData);

        // Show import actions
        document.getElementById('importActions').style.display = 'flex';

    } catch (error) {
        console.error('Error parsing file:', error);
        showToast('Lỗi khi đọc dữ liệu file!', 'error');
    }
}

// Show Column Mapping
function showColumnMapping(headers) {
    const section = document.getElementById('columnMappingSection');
    const list = document.getElementById('columnMappingList');

    section.style.display = 'block';
    list.innerHTML = '';

    const systemFields = [
        { value: '', label: '-- Không ánh xạ --' },
        { value: 'name', label: 'Tên sản phẩm' },
        { value: 'sku', label: 'SKU' },
        { value: 'price', label: 'Giá' },
        { value: 'sale_price', label: 'Giá khuyến mãi' },
        { value: 'stock', label: 'Tồn kho' },
        { value: 'category', label: 'Danh mục' },
        { value: 'brand', label: 'Thương hiệu' },
        { value: 'description', label: 'Mô tả' },
        { value: 'status', label: 'Trạng thái' }
    ];

    headers.forEach((header, index) => {
        const mappingItem = document.createElement('div');
        mappingItem.className = 'mapping-item';
        mappingItem.innerHTML = `
            <div class="mapping-source">
                <div class="mapping-label">Cột trong file</div>
                <div class="mapping-value">${header}</div>
            </div>
            <div class="mapping-arrow">
                <i class="bi bi-arrow-right"></i>
            </div>
            <div class="mapping-target">
                <div class="mapping-label">Trường hệ thống</div>
                <select class="form-select" data-column-index="${index}">
                    ${systemFields.map(field =>
            `<option value="${field.value}">${field.label}</option>`
        ).join('')}
                </select>
            </div>
        `;
        list.appendChild(mappingItem);

        // Save mapping
        const select = mappingItem.querySelector('select');
        select.addEventListener('change', function() {
            columnMapping[header] = this.value;
        });
    });
}

// Auto Map Columns
function autoMapColumns() {
    const selects = document.querySelectorAll('.mapping-target select');

    const autoMappings = {
        'name': ['tên', 'name', 'product name', 'product_name', 'ten san pham'],
        'sku': ['sku', 'mã', 'ma', 'code'],
        'price': ['giá', 'gia', 'price', 'regular price', 'gia goc'],
        'sale_price': ['giá km', 'gia km', 'sale price', 'gia khuyen mai'],
        'stock': ['tồn kho', 'ton kho', 'stock', 'quantity', 'so luong'],
        'category': ['danh mục', 'danh muc', 'category', 'loai'],
        'brand': ['thương hiệu', 'thuong hieu', 'brand', 'nhan hieu'],
        'description': ['mô tả', 'mo ta', 'description', 'desc'],
        'status': ['trạng thái', 'trang thai', 'status']
    };

    selects.forEach(select => {
        const sourceColumn = select.closest('.mapping-item')
            .querySelector('.mapping-value').textContent.toLowerCase();

        for (const [field, keywords] of Object.entries(autoMappings)) {
            if (keywords.some(keyword => sourceColumn.includes(keyword))) {
                select.value = field;
                columnMapping[sourceColumn] = field;
                break;
            }
        }
    });

    showToast('Đã tự động ánh xạ cột!', 'success');
}

// Show Data Preview
function showDataPreview(data) {
    const section = document.getElementById('dataPreviewSection');
    const tbody = document.getElementById('previewTableBody');

    section.style.display = 'block';
    tbody.innerHTML = '';

    // Show first 5 rows (skip header)
    const previewRows = data.slice(1, 6);

    previewRows.forEach((row, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${index + 1}</td>
            ${row.slice(0, 5).map(cell => `<td>${cell || '-'}</td>`).join('')}
        `;
        tbody.appendChild(tr);
    });
}

// Validate Data
function validateData(data) {
    const section = document.getElementById('validationSection');
    section.style.display = 'block';

    let validCount = 0;
    let warningCount = 0;
    let errorCount = 0;
    const errors = [];

    // Skip header
    const rows = data.slice(1);

    rows.forEach((row, index) => {
        const rowNumber = index + 2; // +2 because header is row 1

        // Validate required fields
        if (!row[0] || !row[1]) {
            errorCount++;
            errors.push({
                type: 'error',
                row: rowNumber,
                message: 'Thiếu trường bắt buộc (Tên hoặc SKU)',
                detail: `Dòng ${rowNumber}: Cần có tên sản phẩm và SKU`
            });
        } else if (!row[2] || isNaN(parseFloat(row[2]))) {
            warningCount++;
            errors.push({
                type: 'warning',
                row: rowNumber,
                message: 'Giá không hợp lệ',
                detail: `Dòng ${rowNumber}: Giá phải là số`
            });
        } else {
            validCount++;
        }
    });

    // Update counts
    document.getElementById('validCount').textContent = validCount;
    document.getElementById('warningCount').textContent = warningCount;
    document.getElementById('errorCount').textContent = errorCount;

    // Show errors
    const errorList = document.querySelector('.error-list');
    errorList.innerHTML = '';

    errors.slice(0, 10).forEach(error => {
        const errorItem = document.createElement('div');
        errorItem.className = `error-item ${error.type}`;
        errorItem.innerHTML = `
            <i class="bi bi-${error.type === 'error' ? 'x-circle-fill' : 'exclamation-triangle-fill'}"></i>
            <div class="error-content">
                <strong>${error.message}</strong>
                <small>${error.detail}</small>
            </div>
        `;
        errorList.appendChild(errorItem);
    });

    if (errors.length > 10) {
        const moreErrors = document.createElement('div');
        moreErrors.className = 'text-center mt-3';
        moreErrors.innerHTML = `<small class="text-muted">Và ${errors.length - 10} lỗi khác...</small>`;
        errorList.appendChild(moreErrors);
    }
}

// Start Import
function startImport() {
    if (!uploadedFile) {
        showToast('Vui lòng chọn file để import!', 'error');
        return;
    }

    // Show progress modal
    const modal = new bootstrap.Modal(document.getElementById('progressModal'));
    modal.show();

    // Simulate import process
    simulateImport();
}

// Simulate Import
function simulateImport() {
    const totalCount = previewData.length - 1;
    let processedCount = 0;

    const interval = setInterval(() => {
        processedCount += Math.floor(Math.random() * 20) + 10;

        if (processedCount > totalCount) {
            processedCount = totalCount;
        }

        updateProgress(processedCount, totalCount);

        if (processedCount >= totalCount) {
            clearInterval(interval);
            setTimeout(() => {
                completeImport();
            }, 500);
        }
    }, 200);
}

// Update Progress
function updateProgress(processed, total) {
    const percent = Math.round((processed / total) * 100);

    document.getElementById('progressPercent').textContent = percent + '%';
    document.getElementById('progressBar').style.width = percent + '%';
    document.getElementById('processedCount').textContent = processed;
    document.getElementById('totalCount').textContent = total;
}

// Complete Import
function completeImport() {
    bootstrap.Modal.getInstance(document.getElementById('progressModal')).hide();

    showToast('Import thành công ' + previewData.length + ' sản phẩm!', 'success');

    setTimeout(() => {
        window.location.href = '/admin/products';
    }, 2000);
}

// Remove File
function removeFile() {
    uploadedFile = null;
    previewData = [];
    columnMapping = {};

    document.getElementById('uploadArea').style.display = 'block';
    document.getElementById('fileInfo').style.display = 'none';
    document.getElementById('importSettings').style.display = 'none';
    document.getElementById('columnMappingSection').style.display = 'none';
    document.getElementById('dataPreviewSection').style.display = 'none';
    document.getElementById('validationSection').style.display = 'none';
    document.getElementById('importActions').style.display = 'none';

    document.getElementById('importFile').value = '';
    document.getElementById('uploadProgress').style.width = '0%';
}

// Reset Import
function resetImport() {
    if (confirm('Bạn có chắc chắn muốn làm lại?')) {
        removeFile();
    }
}

// Initialize Export Fields
function initializeExportFields() {
    const selectAllCheckbox = document.getElementById('selectAllFields');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            toggleAllFields();
        });
    }
}

// Toggle All Fields
function toggleAllFields() {
    const selectAll = document.getElementById('selectAllFields').checked;
    const fieldCheckboxes = document.querySelectorAll('.export-field');

    fieldCheckboxes.forEach(checkbox => {
        checkbox.checked = selectAll;
    });
}

// Start Export
function startExport() {
    const format = document.getElementById('exportFormat').value;
    const selectedFields = [];

    document.querySelectorAll('.export-field:checked').forEach(checkbox => {
        selectedFields.push(checkbox.value);
    });

    if (selectedFields.length === 0) {
        showToast('Vui lòng chọn ít nhất một trường để export!', 'warning');
        return;
    }

    // Show progress modal
    const modal = new bootstrap.Modal(document.getElementById('progressModal'));
    document.getElementById('progressText').textContent = 'Đang export dữ liệu...';
    modal.show();

    // Simulate export
    simulateExport(format);
}

// Simulate Export
function simulateExport(format) {
    setTimeout(() => {
        bootstrap.Modal.getInstance(document.getElementById('progressModal')).hide();

        showToast('Export thành công!', 'success');

        // Trigger download
        downloadFile(format);
    }, 2000);
}

// Download File
function downloadFile(format) {
    const filename = `products_export_${Date.now()}.${format}`;

    // In production, this would download the actual file
    console.log('Downloading:', filename);

    showToast(`Đang tải file ${filename}...`, 'info');
}

// Quick Export
function quickExport(type) {
    const modal = new bootstrap.Modal(document.getElementById('progressModal'));
    document.getElementById('progressText').textContent = 'Đang export dữ liệu...';
    modal.show();

    setTimeout(() => {
        bootstrap.Modal.getInstance(document.getElementById('progressModal')).hide();
        showToast(`Export ${type} thành công!`, 'success');
        downloadFile('xlsx');
    }, 2000);
}

// Reset Export
function resetExport() {
    document.getElementById('exportFormat').value = 'xlsx';
    document.getElementById('exportCategory').value = '';
    document.getElementById('exportBrand').value = '';
    document.getElementById('exportStatus').value = '';
    document.getElementById('exportStock').value = '';
    document.getElementById('exportDateFrom').value = '';
    document.getElementById('exportDateTo').value = '';
    document.getElementById('selectAllFields').checked = true;
    toggleAllFields();
}

// Clear History
function clearHistory() {
    if (confirm('Bạn có chắc chắn muốn xóa toàn bộ lịch sử?')) {
        showToast('Đã xóa lịch sử!', 'success');
        // In production, call API to clear history
    }
}

// Utility Functions

// Format File Size
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// Show Toast
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

// Export functions
window.removeFile = removeFile;
window.resetImport = resetImport;
window.autoMapColumns = autoMapColumns;
window.startImport = startImport;
window.toggleAllFields = toggleAllFields;
window.startExport = startExport;
window.quickExport = quickExport;
window.resetExport = resetExport;
window.clearHistory = clearHistory;