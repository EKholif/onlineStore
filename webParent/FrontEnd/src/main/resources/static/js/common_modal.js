function showmodalDialog(tital, message) {
    $("#modalTitle").text(tital);
    $("#modalBody").text(message);
    $("#confirmModal").modal('show');
}

function showErrorModal(message) {
    showmodalDialog("Error", message);
}

function showWarningModal(message) {

    showmodalDialog("Warning", message);
}