$(document).ready(function () {
    $("#buttonPostQuestion").on("click", function (e) {
        e.preventDefault();
        postQuestion();
    });

    $("#buttonAskQuestionLogin").on("click", function (e) {
        e.preventDefault();
        showModalDialog("Warning", "You must login to ask a question.");
    });
});

function postQuestion() {
    url = contextPath + "post_question/" + productId;
    question = $("#question").val();

    if (question.length < 5) {
        showModalDialog("Warning", "Please write a question.");
        return;
    }

    jsonData = {questionContent: question};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: jsonData,
        success: function (response) {
            showModalDialog("Success", "Your question has been posted and is awaiting approval.");
            $("#question").val("");
        },
        error: function (xhr) {
            showModalDialog("Error", "Error posting question: " + xhr.responseText);
        }
    });
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#confirmModal").modal("show");
}
