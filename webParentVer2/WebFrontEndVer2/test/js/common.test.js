const fs = require('fs');
const path = require('path');

// Read the file content
// Note: We are navigating from ./test/js/ to ./src/...
const commonJsPath = path.resolve(__dirname, '../../src/main/resources/static/js/common.js');
const commonJsContent = fs.readFileSync(commonJsPath, 'utf8');

// Evaluate the code in the global scope
// This adds functions like checkPasswordMatch to the global scope
eval(commonJsContent);

describe('common.js Unit Tests', () => {

    describe('checkPasswordMatch', () => {
        beforeEach(() => {
            // Reset DOM
            document.body.innerHTML = '';
        });

        test('should set custom validity message when passwords do not match', () => {
            // Arrange
            document.body.innerHTML = `
                <input type="password" id="Password" value="secret123">
            `;
            const confirmInput = document.createElement('input');
            confirmInput.type = 'password';
            confirmInput.value = 'mismatch';
            confirmInput.setCustomValidity = jest.fn();

            // Act
            checkPasswordMatch(confirmInput);

            // Assert
            expect(confirmInput.setCustomValidity).toHaveBeenCalledWith("Password Not Match ");
        });

        test('should clear custom validity message when passwords match', () => {
            // Arrange
            document.body.innerHTML = `
                <input type="password" id="Password" value="secret123">
            `;
            const confirmInput = document.createElement('input');
            confirmInput.type = 'password';
            confirmInput.value = 'secret123';
            confirmInput.setCustomValidity = jest.fn();

            // Act
            checkPasswordMatch(confirmInput);

            // Assert
            expect(confirmInput.setCustomValidity).toHaveBeenCalledWith("");
        });
    });
});
