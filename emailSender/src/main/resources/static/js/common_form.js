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
            $(this).find('.dropdown-menu').first().stop(true, true).delay(150).slideUp();
        },
    )

    $("#account").click(function () {
        location.href = "account"

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
    window.location = '/' + windowLocationValue;
}


/*
========================================================================================
                                buttonCancel
========================================================================================

*/


$(document).ready(function () {
    $("#buttonCancel").on("click", function () {
        window.location = '/' + windowLocationValue;
    });

    $("#fileImage").change(function () {

        if (!checkFileSize(this)) {
            return;
        }
        showImage(this);

    });


    $("input[name='extraImage']").each(function (index) {
        extraImagesCount++;

        $(this).change(function () {

            showExtraImage(this, index);

        });
    });
});

/*
========================================================================================
                    class counter by name
========================================================================================

*/


/*
========================================================================================
                        Show  Extra   Image
========================================================================================

*/


function showExtraImage(fileInput, index) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#extraThumbnail" + index).attr("src", e.target.result)
    };
    reader.readAsDataURL(file);


    if (index >= extraImagesCount - 1) {

        addExtraImages(index + 1);

    }
}


/*


========================================================================================
                           show  Image
========================================================================================

*/

function showImage(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result)

    };
    reader.readAsDataURL(file);

}

/*
========================================================================================
                               add  Extra  Images
========================================================================================

*/


function addExtraImages(index) {


    let htmlExtraImage = `  <div class="col border m-3 p-2 " id="divExtraProductImage${index}">

            <div id ="extraImageHeader${index}"> <label >Extra Images #${index + 1} :</label></div>

            <div >
                <input  type="file" id="extraImage${index}" name="extraImage" accept="image/jpg, image/jpeg, image/png"
                      onchange="showExtraImage(this, ${index})">
            </div>

            <div class="mb-2">
                    <img class="img-thumbnail "  style="width: 200px"  id="extraThumbnail${index}" alt="Extra Image #${index + 1}"    src="${defultImage}" />
            </div>

        </div>`;


    let htmLinkRemove = ` <a class="btn fas fa-times-circle fa-1x float-end icon-red"
 
             href="javascript:removeExtraImage(${index - 1})"
  
            title="Remove this image" >        </a>  `;

    $("#divProductImages").append(htmlExtraImage);
    $("#extraImageHeader" + (index - 1)).append(htmLinkRemove);
    extraImagesCount++;

}


function removeExtraImage(index) {

    if (index == 0) $("#divExtraProductImage").remove();

    $("#divExtraProductImage" + index).remove();
}


/*
========================================================================================
                            check file size
========================================================================================

*/
function checkFileSize(fileInput) {

    let fileSize = fileInput.files[0].size;


    if (fileSize > Max_File_Size) {
        fileInput.setCustomValidity("You must choose an image less than " + Max_File_Size + " bytes!");
        fileInput.reportValidity();

        return false;
    } else {
        fileInput.setCustomValidity("");

        return true;
    }
}


/*
========================================================================================
                            Div  Details
========================================================================================

*/

// function addExtraDetails() {
//     // Select all elements whose ID starts with 'divDetail'
//     let allDivDetails = $("[id^='divDetail']");
//     let divDetailsCount = allDivDetails.length;
//
//
//     // HTML template for a new detail section
//     let htmlExtraDetails = `
//         <div class="flex flex-row align-items-center flex-wrap p-2" id="'divDetail'${divDetailsCount}">
//             <input type="hidden" name="detailIDs" />
//
//             <label class="col-sm-1 text-center">Name</label>
//             <div class="col-sm-4">
//                 <input type="text" class="form-control" name="detailNames" aria-required="false" required minlength="2"
//                     placeholder="Product name detail names" maxlength="255"/>
//             </div>
//
//             <label class="col-sm-1 text-center">Value</label>
//             <div class="col-sm-4">
//                 <input type="text" class="form-control" name="detailValues" aria-required="false" required minlength="2"
//                     placeholder="Product name detail Values" maxlength="255"/>
//             </div>
//               <a class="btn fas fa-times-circle fa-1x icon-red"
//                 href="javascript:removeDetailSectionById('divDetail${divDetailsCount}')"
//                 title="Remove this detail"></a>
//
//         </div>
//     `;
//
//     alert("eeeeeeeeee"+ divDetailsCount);
//     // Append the new detail section to the container
//     $("#divProductDetails").append(htmlExtraDetails);
//     divDetailsCount++;
//
//     // Select the last added detail section
//     let previousDivDetailSection = allDivDetails.last();
//     let previousDivdetailIDs = previousDivDetailSection.attr('id');
//
//     // HTML link for removing the detail section
//     let htmlLinkRemove = `
// 		<a class="btn fas fa-times-circle fa-1x icon-red"
// 			href="javascript:removeDetailSectionById('${previousDivdetailIDs}')"
// 			title="Remove this detail">  </a>  `;
//
//
//     // Append the remove link to the last added detail section
//     // previousDivDetailSection.append(htmlLinkRemove);
//
//     // Set focus on the last added detail section's 'Name' input field
//     $("input[name='detailNames']").last().focus();
// }


function addExtraDetails() {
    // Select all elements whose ID starts with 'divDetail'
    let allDivDetails = $("[id^='divDetail']");
    let divDetailsCount = allDivDetails.length;

    // HTML template for a new detail section
    let htmlExtraDetails = `
        <div class="flex flex-row align-items-center flex-wrap p-2" th:id="divDetail${divDetailsCount}">
            <input type="hidden" name="detailIDs"  value="${divDetailsCount}" />
            
            <label class="col-sm-1 text-center">Name</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" name="detailNames" aria-required="false" required minlength="2"
                    placeholder="Product name detail names" maxlength="255" />
            </div>

            <label class="col-sm-1 text-center">Value</label>
            <div class="col-sm-4">
                <input type="text" class="form-control" name="detailValues" aria-required="false" required minlength="2"
                    placeholder="Product name detail Values" maxlength="255"  />
            </div>

            <a class="btn fas fa-times-circle fa-1x icon-red"
                href="javascript:removeDetailSectionById('divDetail${divDetailsCount}')"
                title="Remove this detail"></a>
        </div>
    `;

    // Append the new detail section to the container
    $("#divProductDetails").append(htmlExtraDetails);

    // Update divDetailsCount
    divDetailsCount++;

    // Set focus on the last added detail section's 'Name' input field
    $("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id) {
    // Remove the element with the specified ID from the DOM
    $("#" + id).remove();
}




function removeDetailSectionById(id) {
    // Remove the element with the specified I
    // D from the DOM

    $("#" + id).remove();

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
    // var csrf = $("input[name='_csrf']").val();

    console.log(id, url, userEmail);

    // Create a JavaScript object with the email and CSRF token.
    // var params = {email: userEmail, _csrf: csrf, id: id};

    $.post(url, params, function (response) {

        // alert(response);

        if (response === "Ok") {
            showmodalDialog("This Unique", userEmail);
            form.submit();
        } else if (response === "Duplicated") {

            showmodalDialog("There is another user having the email ", userEmail);

        } else {
            showmodalDialog("  else else Unknown response from server ");
        }
    }).fail(function () {
        showmodalDialog("fail  Fail Could not connect to the server ");
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

/*
========================================================================================
                                  show modal
========================================================================================

*/
function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal('show');
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}
