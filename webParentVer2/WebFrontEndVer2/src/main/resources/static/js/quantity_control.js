document.querySelectorAll('.quantity-control').forEach(control => {
    const input    = control.querySelector('.quantity-input');
    const btnMinus = control.querySelector('.btn-minus');
    const btnPlus  = control.querySelector('.btn-plus');




    // دالة مساعدة للتحقق وإظهار التحذير
    function validateAndWarn(value) {
        const min = parseInt(input.min) || 1;
        const max = input.max ? parseInt(input.max) : Infinity;

        if (value < min) {
            showWarningModal('Minimum quantity is 1');
            return min;
        }
        if (value > max) {
            showWarningModal('Maximum quantity is 5');
            return max;
        }
        return value;
    }

    btnMinus.addEventListener('click', () => {
        let value = parseInt(input.value) || 1;
        value     = validateAndWarn(value - 1);
        input.value = value;
    });

    btnPlus.addEventListener('click', () => {
        let value = parseInt(input.value) || 1;
        value     = validateAndWarn(value + 1);
        input.value = value;
    });

    // التحقق عند الكتابة يدويًا
    input.addEventListener('input', () => {
        let value = parseInt(input.value) || 0;
        input.value = validateAndWarn(value);
    });
});
