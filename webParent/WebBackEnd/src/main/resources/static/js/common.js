/*
========================================================================================
                              logout
========================================================================================

*/


/*
========================================================================================
                                 customDropMenu
========================================================================================

*/

function customDropMenu() {

    $(".navbar .dropdown").hover(
        function () {

            $(this).find('.dropdown-menu').first().stop(true, true).delay(350).slideDown();
        },

        function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(150).slideU();
        },
    )

    $("#account").click(function () {
        location.href = "/account"

    });
}

/*
========================================================================================
                                  // delete-confirmation
========================================================================================

*/


$(document).ready(function () {
    $(".delete-confirmation").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        id = link.attr("id");
        $("#yesbutton").attr("href", link.attr('href'));

        $("#modalTitle").text("Warning");

        if (id !== null && id !== undefined) {
            $("#modalBody").text("Are You Sure You want to delete User ID: " + id);
        } else {
            $("#modalBody").text("Are You Sure You want to delete this Users?");
        }

        $("#confirmModal").modal('show');
    });
});

/*
========================================================================================
                                 clearFilter
========================================================================================

*/
function clearFilter() {
    window.location =  '/' + windowLocationValue;
}


/*
========================================================================================
                                buttonCancel
========================================================================================

*/


$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        window.location =  '/' + windowLocationValue;
    });

    $("#fileImage").change(function () {
        fileSize = this.files[0].size;


        if (fileSize > 1048576) {
            this.setCustomValidity("Maximum image size is 1Mb ehab1");
            this.reportValidity();

        } else {
            // this.setCustomValidity("cool")

            showImage(this);
        }
    });

});

/*
========================================================================================
                                 checkPasswordMatch
========================================================================================

*/
function showImage(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result)
    };
    reader.readAsDataURL(file);
    customDropMenu()
}

/*
========================================================================================
                          showImage
========================================================================================

*/

function showImage(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result)
    };
    reader.readAsDataURL(file);
    customDropMenu()
}

/*
========================================================================================
                           checkDuplicateEmail
========================================================================================

*/
function checkDuplicateEmail(form) {
    // Build the URL for the server-side endpoint.


    // Get the user Id
    var id = $("#id").val();


    // Get the user's email input from the form.
    var userEmail = $("#email").val();

    // Get the CSRF token from the form.
    var csrf = $("input[name='_csrf']").val();

    console.log(id, url, userEmail);

    // Create a JavaScript object with the email and CSRF token.
    var params = {email: userEmail, _csrf: csrf, id: id};

    $.post(url, params, function (response) {

        if (response === "OK") {
            showmodalDialog("This Unique", userEmail);
            form.submit();
        } else if (response === "Duplicated") {

            showmodalDialog("There is another user having the email ", userEmail);

        } else {
            showmodalDialog("else Unknown response from server ");
            console.error("Failed to connect to the server ");
        }
    }).fail(function () {
        showmodalDialog("Fail Could not connect to the server ");
        console.error("Failed to connect to the server ");
        console.log(id, name)

    });


    return false;
}

/*
========================================================================================
                           showmodalDialog
========================================================================================

*/


function showmodalDialog(tital, message) {
    $("#modalTitle").text(tital);
    $("#modalBody").text(message);
    $('#confirmModal').modal('show');
}

/*
========================================================================================
                                  jQuery
========================================================================================

*/


$j = jQuery.noConflict();
var $ = jQuery;

/*
========================================================================================
                                  checkPasswordMatch
========================================================================================

*/

function checkPasswordMatch(confirmPassword) {

    if (confirmPassword.value !== $("#Password").val()) {
        confirmPassword.setCustomValidity("Password Not Match")
    } else {
        confirmPassword.setCustomValidity("");

    }
}


