// Email validation
export const validateEmail = (email) => {
  const re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return re.test(String(email).toLowerCase());
};

// Password validation (at least 8 chars, 1 uppercase, 1 lowercase, 1 number)
export const validatePassword = (password) => {
  const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/;
  return re.test(password);
};

// Username validation (3-20 chars, alphanumeric and underscore only)
export const validateUsername = (username) => {
  const re = /^[a-zA-Z0-9_]{3,20}$/;
  return re.test(username);
};

// Form validation for login
export const validateLoginForm = (formData) => {
  const errors = {};

  if (!formData.username) {
    errors.username = 'Username is required';
  } else if (!validateUsername(formData.username)) {
    errors.username = 'Username must be 3-20 characters, alphanumeric and underscore only';
  }

  if (!formData.password) {
    errors.password = 'Password is required';
  } else if (formData.password.length < 6) {
    errors.password = 'Password must be at least 6 characters';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Form validation for registration
export const validateRegisterForm = (formData) => {
  const errors = {};

  if (!formData.username) {
    errors.username = 'Username is required';
  } else if (!validateUsername(formData.username)) {
    errors.username = 'Username must be 3-20 characters, alphanumeric and underscore only';
  }

  if (!formData.email) {
    errors.email = 'Email is required';
  } else if (!validateEmail(formData.email)) {
    errors.email = 'Please enter a valid email address';
  }

  if (!formData.password) {
    errors.password = 'Password is required';
  } else if (!validatePassword(formData.password)) {
    errors.password = 'Password must be at least 8 characters, include uppercase, lowercase and number';
  }

  if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
};

// Validate required fields
export const validateRequired = (value, fieldName) => {
  if (!value || (typeof value === 'string' && value.trim() === '')) {
    return `${fieldName} is required`;
  }
  return null;
};