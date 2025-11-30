import DOMPurify from 'dompurify';

/**
 * Frontend input sanitization utility using DOMPurify
 */
const SanitizationUtil = {
  /**
   * Sanitize HTML content to prevent XSS
   * @param {string} html - HTML string to sanitize
   * @returns {string} - Sanitized HTML
   */
  sanitizeHTML: (html) => {
    if (!html || typeof html !== 'string') {
      return html;
    }
    return DOMPurify.sanitize(html, {
      ALLOWED_TAGS: ['p', 'br', 'strong', 'em', 'u', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'li'],
      ALLOWED_ATTR: ['href', 'title', 'target'], // Only allow safe attributes
      FORBID_TAGS: ['script', 'object', 'embed', 'form', 'input', 'button'],
      FORBID_ATTR: ['onclick', 'onload', 'onerror', 'onmouseover', 'onfocus']
    });
  },

  /**
   * Sanitize text content
   * @param {string} text - Text to sanitize
   * @returns {string} - Sanitized text
   */
  sanitizeText: (text) => {
    if (!text || typeof text !== 'string') {
      return text;
    }
    // Remove potentially dangerous characters
    return text.replace(/[<>"'&]/g, (match) => {
      const escapeMap = {
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#x27;',
        '&': '&amp;'
      };
      return escapeMap[match];
    });
  },

  /**
   * Sanitize URL
   * @param {string} url - URL to sanitize
   * @returns {string} - Sanitized URL
   */
  sanitizeURL: (url) => {
    if (!url || typeof url !== 'string') {
      return url;
    }
    // Only allow http, https, and mailto protocols
    if (!/^https?:\/\/|^mailto:|^tel:|^\//i.test(url)) {
      return '#'; // Return safe fallback
    }
    return url;
  },

  /**
   * Sanitize filename to prevent path traversal
   * @param {string} filename - Filename to sanitize
   * @returns {string} - Sanitized filename
   */
  sanitizeFilename: (filename) => {
    if (!filename || typeof filename !== 'string') {
      return filename;
    }
    // Remove path traversal attempts and dangerous characters
    return filename
      .replace(/\.\.\//g, '') // Prevent directory traversal
      .replace(/\.\.\\/g, '') // Prevent Windows-style traversal
      .replace(/[<>:"/\\|?*]/g, ''); // Remove invalid filename characters
  }
};

export default SanitizationUtil;