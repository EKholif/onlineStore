document.querySelectorAll('.quantity-control').forEach(control => {
    const input     = control.querySelector('.quantity-input');
    const btnMinus  = control.querySelector('.btn-minus');
    const btnPlus   = control.querySelector('.btn-plus');
    const rowNumber = control.dataset.rowNumber;   // ← هنا نحصل على status.count

    /* دالة التحقق وإظهار التحذير */
    function validateAndWarn(value) {
        const min = parseInt(input.min) || 1;
        const max = input.max ? parseInt(input.max) : Infinity;

        if (value < min) {
            showWarningModal(`Minimum quantity is 1 (Row ${rowNumber})`);
            return min;
        }
        if (value > max) {
            showWarningModal(`Maximum quantity is 5 (Row ${rowNumber})`);
            return max;
        }
        return value;
    }

    /* زر ‑ */
    btnMinus.addEventListener('click', () => {
        let value = parseInt(input.value) || 1;
        value = validateAndWarn(value - 1);
        input.value = value;
        // يمكنك هنا استدعاء Ajax لتحديث الكمية في السيرفر وتمرير rowNumber
    });

    /* زر + */
    btnPlus.addEventListener('click', () => {
        let value = parseInt(input.value) || 1;
        value = validateAndWarn(value + 1);
        input.value = value;
    });

    /* الكتابة اليدوية */
    input.addEventListener('input', () => {
        let value = parseInt(input.value) || 0;
        input.value = validateAndWarn(value);
    });
});
