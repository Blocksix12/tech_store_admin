// Auth Page JavaScript

// Toggle password visibility
function togglePassword(inputId = 'password') {
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

// Password strength checker
function checkPasswordStrength(password) {
    let strength = 0;
    const requirements = {
        length: password.length >= 8,
        uppercase: /[A-Z]/.test(password),
        lowercase: /[a-z]/.test(password),
        number: /[0-9]/.test(password),
        special: /[^A-Za-z0-9]/.test(password)
    };

    // Update requirements UI
    Object.keys(requirements).forEach(req => {
        const element = document.getElementById(`req-${req}`);
        if (element) {
            if (requirements[req]) {
                element.classList.add('met');
                strength++;
            } else {
                element.classList.remove('met');
            }
        }
    });

    // Update strength bar
    const strengthFill = document.getElementById('strengthFill');
    const strengthText = document.getElementById('strengthText');

    if (strengthFill && strengthText) {
        strengthFill.className = 'strength-fill';
        strengthText.className = 'strength-text';

        if (strength <= 2) {
            strengthFill.classList.add('weak');
            strengthText.classList.add('weak');
            strengthText.textContent = 'Yếu';
        } else if (strength <= 4) {
            strengthFill.classList.add('medium');
            strengthText.classList.add('medium');
            strengthText.textContent = 'Trung bình';
        } else {
            strengthFill.classList.add('strong');
            strengthText.classList.add('strong');
            strengthText.textContent = 'Mạnh';
        }
    }

    return strength;
}

// Multi-step form navigation
function nextStep(step) {
    // Validate current step
    const currentStep = document.querySelector('.form-step.active');
    const inputs = currentStep.querySelectorAll('input[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.checkValidity()) {
            input.reportValidity();
            isValid = false;
        }
    });

    if (!isValid) return;

    // Special validation for step 2 (password confirmation)
    if (step === 3) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            const feedback = document.getElementById('passwordMismatch');
            feedback.style.display = 'block';
            document.getElementById('confirmPassword').classList.add('is-invalid');
            return;
        }
    }

    // Update progress steps
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.querySelector(`.form-step[data-step="${step}"]`).classList.add('active');

    document.querySelectorAll('.step').forEach(s => {
        const stepNum = parseInt(s.dataset.step);
        s.classList.remove('active');
        if (stepNum < step) {
            s.classList.add('completed');
        }
    });

    document.querySelector(`.step[data-step="${step}"]`).classList.add('active');

    // Scroll to top
    document.querySelector('.auth-form-container').scrollIntoView({ behavior: 'smooth' });
}

function prevStep(step) {
    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
    document.querySelector(`.form-step[data-step="${step}"]`).classList.add('active');

    document.querySelectorAll('.step').forEach(s => {
        const stepNum = parseInt(s.dataset.step);
        s.classList.remove('active');
        if (stepNum < step) {
            s.classList.add('completed');
        } else if (stepNum > step) {
            s.classList.remove('completed');
        }
    });

    document.querySelector(`.step[data-step="${step}"]`).classList.add('active');
}

// Social login functions
function loginWithGoogle() {
    // Implement Google OAuth
    console.log('Login with Google');
    window.location.href = '/oauth2/authorization/google';
}

function loginWithFacebook() {
    // Implement Facebook OAuth
    console.log('Login with Facebook');
    window.location.href = '/oauth2/authorization/facebook';
}

function loginWithGithub() {
    // Implement GitHub OAuth
    console.log('Login with GitHub');
    window.location.href = '/oauth2/authorization/github';
}

// Language switcher
function switchLanguage(lang) {
    const buttons = document.querySelectorAll('.lang-btn');
    buttons.forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.lang === lang) {
            btn.classList.add('active');
        }
    });

    // Save preference
    localStorage.setItem('preferred-language', lang);

    // Implement i18n logic here
    console.log('Switched to language:', lang);
}

// Form submission with loading state
function handleFormSubmit(form, loadingButton) {
    form.addEventListener('submit', function(e) {
        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.classList.add('loading');
        submitBtn.disabled = true;
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Initialize login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        handleFormSubmit(loginForm);
    }

    // Initialize register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        handleFormSubmit(registerForm);

        // Password strength checker
        const passwordInput = document.getElementById('password');
        if (passwordInput) {
            passwordInput.addEventListener('input', function() {
                checkPasswordStrength(this.value);
            });
        }

        // Password confirmation checker
        const confirmPasswordInput = document.getElementById('confirmPassword');
        if (confirmPasswordInput) {
            confirmPasswordInput.addEventListener('input', function() {
                const password = document.getElementById('password').value;
                const feedback = document.getElementById('passwordMismatch');

                if (this.value !== password) {
                    this.classList.add('is-invalid');
                    feedback.style.display = 'block';
                } else {
                    this.classList.remove('is-invalid');
                    feedback.style.display = 'none';
                }
            });
        }
    }

    // Language selector
    const langButtons = document.querySelectorAll('.lang-btn');
    langButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            switchLanguage(this.dataset.lang);
        });
    });

    // Load saved language preference
    const savedLang = localStorage.getItem('preferred-language');
    if (savedLang) {
        switchLanguage(savedLang);
    }

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        if (alert.style.display !== 'none') {
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        }
    });

    // Add animation to form inputs
    const formInputs = document.querySelectorAll('.form-control');
    formInputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });

        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });
    });

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Press Enter to submit on last step
        if (e.key === 'Enter' && !e.shiftKey) {
            const activeStep = document.querySelector('.form-step.active');
            const isLastStep = activeStep && activeStep.dataset.step === '3';
            if (isLastStep) {
                e.preventDefault();
                registerForm?.submit();
            }
        }
    });
});

// Export functions for use in HTML
window.togglePassword = togglePassword;
window.nextStep = nextStep;
window.prevStep = prevStep;
window.loginWithGoogle = loginWithGoogle;
window.loginWithFacebook = loginWithFacebook;
window.loginWithGithub = loginWithGithub;