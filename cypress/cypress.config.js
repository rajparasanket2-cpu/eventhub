const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    // Live Library API; the specs only use cy.request, so no page is ever visited
    baseUrl: 'http://216.10.245.166',
    specPattern: 'e2e/**/*.cy.js',
    supportFile: false,
    video: false,
    screenshotOnRunFailure: false,
  },
});
