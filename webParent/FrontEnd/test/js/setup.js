const $ = require('jquery');
global.$ = global.jQuery = $;

// Mock window properties if needed
delete window.location;
window.location = {
    href: '',
    reload: jest.fn(),
    assign: jest.fn()
};

window.alert = jest.fn();
window.confirm = jest.fn();
