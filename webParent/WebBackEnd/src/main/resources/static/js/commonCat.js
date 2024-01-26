/*
========================================================================================
                              logout
========================================================================================

*/
     $(document).ready( function (){
            $("#logout").on("click",function (e){
                e.preventDefault();
                document.logoutForm.submit();
            });
           customDropMenu()
        });
     /*
     ========================================================================================
                                      customDropMenu
     ========================================================================================

     */

     function customDropMenu() {

         $(".navbar .dropdown").hover(

             function (){

                    $(this).find('.dropdown-menu').first().stop(true, true).delay(350).slideDown();
             },

             function (){
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


         $(document).ready(function (){
         $(".delete-confirmation").on("click", function (e){
             e.preventDefault();
             link=$(this);
             id=link.attr("id");
             $("#yesbutton").attr("href", link.attr('href'));

             $("#modalTitle").text("Warning");
             $("#modalBody").text("Are You Sure You want to delete  Cat :   " + id);
             $("#confirmModal").modal('show');
         });
     });
     /*
     ========================================================================================
                                      clearFilter
     ========================================================================================

     */

         function clearFilter(){
         window.location="[[@{/categories}]]"
     }
         $(document).ready( function (){
         $("#logout").on("click",function (e){
             e.preventDefault();
             document.logoutForm.submit();
         });
     });

     /*
     ========================================================================================
                                     buttonCancel
     ========================================================================================

     */


         $(document).ready(function (){
         $("#buttonCancel").on("click", function (){
             window.location="categories";
         });

         $("#fileImage").change(function (){
         fileSize =this.files[0].size;
         alert("file size" + fileSize)

         if (fileSize> 1048576){
         this.setCustomValidity("Maximum image size is 1Mb ");
         this.reportValidity();

     }else {
         // this.setCustomValidity("cool")

         showImage(this);}
     });

     });
 /*
========================================================================================
                                  checkPasswordMatch
========================================================================================

*/
         function showImage (fileInput){
         var file = fileInput.files[0];
         var reader = new FileReader();
         reader.onload=function (e){
         $("#thumbnail").attr("src", e.target.result)
     };
         reader.readAsDataURL (file);
         customDropMenu()
     }

     /*
========================================================================================
                               showImage
========================================================================================

*/

     function showImage (fileInput){
         var file = fileInput.files[0];
         var reader = new FileReader();
         reader.onload=function (e){
             $("#thumbnail").attr("src", e.target.result)
         };
         reader.readAsDataURL (file);
         customDropMenu()
     }
     /*
========================================================================================
                                checkUnique
========================================================================================

*/
function checkUnique(form) {
    // Build the URL for the server-side endpoint.

     var url = "[[@'{check_unique}']]";

    // Get the cat Id
    var id =$("#id").val();

    var alies = $("#alies").val();

    var name = $("#name").val();




    // Get the CSRF token from the form.
    var csrf = $("input[name='_csrf']").val();



    // Create a JavaScript object with the email and CSRF token.
    var params = {id: id,name: name, alies: alies,  _csrf: csrf };

    $.post(url, params, function (response) {

        alert(response);
    }).fail(function (){
     alert('faild');


    //     if (response === "OK" ) {
    //         showmodalDialog("This Unique", alies);
    //         // form.submit();
    //
    //
    //     }else if (response === "DuplicateName") {
    //
    //         showmodalDialog("There is another Category having the name ", name);
    //
    //
    //     }else if (response === "DuplicateAlies") {
    //
    //         showmodalDialog("There is another Category having the Alies ", alies);
    //
    //
    //     } else {
    //         showmodalDialog("  else else Unknown response from server");
    //     }
    // }).fail(function () {
    //     console.error("Failed to connect to the server");
    //     console.log(id,name,alies,url)
    //     showmodalDialog("fail  Fail Could not connect to the server",   alies );
    });

    return false;
}
     /*
========================================================================================
                                showmodalDialog
========================================================================================

*/



     function  showmodalDialog (tital, message){
         $("#modalTitle").text(tital);
         $("#modalBody").text(message);
         $('#modalDialog').modal('show');
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

     function checkPasswordMatch(confirmPassword){

         if (confirmPassword.value !==  $("#Password").val()) {
             confirmPassword.setCustomValidity("Password Not Match")
         }else {
             confirmPassword.setCustomValidity("");

         }
     }