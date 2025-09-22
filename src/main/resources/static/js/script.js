// Custom JavaScript for Spring Store Management System

document.addEventListener('DOMContentLoaded', function() {
    
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
    
    // Confirm delete actions
    const deleteButtons = document.querySelectorAll('a[href*="delete"]');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            const entityType = this.href.includes('categories') ? 'category' : 'product';
            if (!confirm(`Are you sure you want to delete this ${entityType}?`)) {
                e.preventDefault();
            }
        });
    });
    
    // Form validation enhancement
    const forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;
            
            requiredFields.forEach(function(field) {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                const firstInvalid = form.querySelector('.is-invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                }
            }
        });
    });
    
    // Real-time form validation
    const formInputs = document.querySelectorAll('input[required], select[required], textarea[required]');
    formInputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            if (!this.value.trim()) {
                this.classList.add('is-invalid');
            } else {
                this.classList.remove('is-invalid');
            }
        });
        
        input.addEventListener('input', function() {
            if (this.value.trim()) {
                this.classList.remove('is-invalid');
            }
        });
    });
    
    // Search form enhancement
    const searchForms = document.querySelectorAll('form[action*="search"], form input[name="search"]');
    searchForms.forEach(function(form) {
        const searchInput = form.querySelector('input[name="search"]');
        if (searchInput) {
            searchInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    form.submit();
                }
            });
        }
    });
    
    // Table row hover effects
    const tableRows = document.querySelectorAll('table tbody tr');
    tableRows.forEach(function(row) {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = 'rgba(0, 0, 0, 0.05)';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });
    
    // Loading states for buttons
    const submitButtons = document.querySelectorAll('button[type="submit"]');
    submitButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            const originalText = this.innerHTML;
            this.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Processing...';
            this.disabled = true;
            
            // Re-enable after 3 seconds if form doesn't submit
            setTimeout(function() {
                button.innerHTML = originalText;
                button.disabled = false;
            }, 3000);
        });
    });
    
    // Smooth scrolling for pagination
    const paginationLinks = document.querySelectorAll('.pagination a');
    paginationLinks.forEach(function(link) {
        link.addEventListener('click', function(e) {
            // Add loading state
            const pagination = this.closest('.pagination');
            if (pagination) {
                pagination.style.opacity = '0.6';
            }
        });
    });
    
    // Tooltip initialization
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Popover initialization
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Auto-focus on search inputs
    const searchInputs = document.querySelectorAll('input[name="search"]');
    searchInputs.forEach(function(input) {
        if (input.value === '') {
            input.focus();
        }
    });
    
    // Price input formatting
    const priceInputs = document.querySelectorAll('input[type="number"][step="0.01"]');
    priceInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            // Ensure minimum value
            if (this.value && parseFloat(this.value) < 0.01) {
                this.value = '0.01';
            }
        });
    });
    
    // Category selection enhancement
    const categorySelects = document.querySelectorAll('select[name*="category"]');
    categorySelects.forEach(function(select) {
        select.addEventListener('change', function() {
            if (this.value) {
                this.classList.remove('is-invalid');
            }
        });
    });
    
    // Responsive table handling
    const tables = document.querySelectorAll('.table-responsive table');
    tables.forEach(function(table) {
        const wrapper = table.closest('.table-responsive');
        if (wrapper && wrapper.scrollWidth > wrapper.clientWidth) {
            wrapper.style.border = '1px solid #dee2e6';
            wrapper.style.borderRadius = '0.375rem';
        }
    });
    
    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + N for new item
        if ((e.ctrlKey || e.metaKey) && e.key === 'n') {
            const newButtons = document.querySelectorAll('a[href*="/new"]');
            if (newButtons.length > 0) {
                e.preventDefault();
                newButtons[0].click();
            }
        }
        
        // Escape to close modals/alerts
        if (e.key === 'Escape') {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }
    });
    
    // Print functionality
    const printButtons = document.querySelectorAll('[data-action="print"]');
    printButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            window.print();
        });
    });
    
    // Copy to clipboard functionality
    const copyButtons = document.querySelectorAll('[data-action="copy"]');
    copyButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const text = this.getAttribute('data-copy-text');
            if (text) {
                navigator.clipboard.writeText(text).then(function() {
                    // Show success message
                    const toast = document.createElement('div');
                    toast.className = 'toast align-items-center text-white bg-success border-0';
                    toast.innerHTML = `
                        <div class="d-flex">
                            <div class="toast-body">
                                Copied to clipboard!
                            </div>
                            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                        </div>
                    `;
                    document.body.appendChild(toast);
                    const bsToast = new bootstrap.Toast(toast);
                    bsToast.show();
                    setTimeout(function() {
                        document.body.removeChild(toast);
                    }, 3000);
                });
            }
        });
    });
    
    // Initialize all Bootstrap components
    const dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'));
    dropdownElementList.map(function (dropdownToggleEl) {
        return new bootstrap.Dropdown(dropdownToggleEl);
    });
    
    // Performance optimization: Debounce search
    let searchTimeout;
    const searchInputs = document.querySelectorAll('input[name="search"]');
    searchInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(function() {
                // Auto-submit search after 1 second of no typing
                if (input.value.length >= 2 || input.value.length === 0) {
                    const form = input.closest('form');
                    if (form) {
                        form.submit();
                    }
                }
            }, 1000);
        });
    });
    
    console.log('Spring Store Management System initialized successfully!');
});
