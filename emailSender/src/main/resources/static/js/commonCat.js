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
        location.href = "account"

    });
}

/*
========================================================================================
                                  delete-confirmation
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
            $("#modalBody").text("Are You Sure You want to delete The Category ID: " + id);
        } else {
            $("#modalBody").text("Are You Sure You want to delete the selected categories?");
        }


        $("#confirmModal").modal('show');
    });
});

/*
========================================================================================
                                 delete-confirmation
========================================================================================

*/


function deleteUsers(form) {
    var id = $("#id").val();

    $("#modalTitle").text("Warning");

    if (id !== null && id !== undefined) {
        $("#modalBody").text("Are You Sure You want to delete The Category ID: " + id);
        // form.submit();
    } else {
        $("#modalBody").text("Are You Sure You want to delete the selected categories?");
        // form.submit();
    }

    $("#confirmModal").modal('show');
}

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
        window.location ='/' + windowLocationValue;
    });

    $("#fileImage").change(function () {
        fileSize = this.files[0].size;


        if (fileSize > 1048576) {
            this.setCustomValidity("Maximum image size is 1Mb  ehab2");
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
                           check Unique Name
========================================================================================

*/
function checkUniqueName(form) {
    var id = $("#id").val();
    var name = $("#name").val();
    // var csrf = $("input[name='_csrf']").val();
    // var params = {id: id, name: name, _csrf: csrf};
    return sendAjaxRequest(url, params, form);
}

/*
========================================================================================
                           checkUnique
========================================================================================

*/

function checkUnique(form) {
    var id = $("#id").val();
    var alies = $("#alies").val();
    var name = $("#name").val();
    // var csrf = $("input[name='_csrf']").val();
    // var params = {id: id, name: name, alies: alies, _csrf: csrf};
    return sendAjaxRequest(url, params, form);
}


/*
========================================================================================
                               sendAjaxRequest
========================================================================================

*/
function sendAjaxRequest(url, params, form) {
    $.post(url, params, function (response) {
        if (response === "Ok") {

            // alert(response)
            // showmodalDialog("response");
            form.submit();
        } else if (response === "DuplicateName") {
            showmodalDialog("Warning", "There is another Category having the name: " + params.name);
        } else if (response === "DuplicateAlies") {
            showmodalDialog("Warning", "There is another Category having the Alies: " + params.alies);
        } else {
            showmodalDialog("Warning", "Unknown response from server: " + response);
        }
    }).fail(function () {
        console.error("Failed to connect to the server");
        console.log(params.id, params.name, params.alies, url);
        showmodalDialog("Failed to connect to the server", url);
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