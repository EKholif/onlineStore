$(document).ready(function () {

    var spinner = function () {
        setTimeout(function () {
            if ($('#spinner').length > 0) {
                $('#spinner').removeClass('show');
            }
        }, 1);
    };
    spinner(0);


    $("#logout").on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    });
    customDropMenu()
});


// $(document).ready(function () {
//     $("#logout").on("click", function (e) {
//         e.preventDefault();
//         document.logoutForm.submit();
//     });
//     customDropMenu()
// });