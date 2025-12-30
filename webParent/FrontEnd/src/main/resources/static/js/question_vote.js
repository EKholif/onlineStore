$(document).ready(function () {
    $(".linkVoteQuestion").on("click", function (e) {
        e.preventDefault();
        voteQuestion($(this));
    });
});

function voteQuestion(currentLink) {
    requestURL = currentLink.attr("href");

    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (voteResult) {
        if (voteResult.message.includes("You must login")) {
            showModalDialog("Vote Question", voteResult.message);
            return;
        }

        updateVoteCountAndIcons(currentLink, voteResult);
        showModalDialog("Vote Question", voteResult.message);

    }).fail(function (xhr) {
        showErrorModal("Error voting question: " + xhr.responseText);
    });
}

function updateVoteCountAndIcons(currentLink, voteResult) {
    questionId = currentLink.attr("questionId");
    voteUpLink = $("#linkVoteUp-question-" + questionId);
    voteDownLink = $("#linkVoteDown-question-" + questionId);

    $("#voteCount-question-" + questionId).text(voteResult.voteCount + " Votes");

    message = voteResult.message;

    if (message.includes("successfully voted up")) {
        highlightVoteUpIcon(voteUpLink, voteDownLink);
    } else if (message.includes("successfully voted down")) {
        highlightVoteDownIcon(voteDownLink, voteUpLink);
    } else if (message.includes("unvoted down")) {
        unhighlightVoteDownIcon(voteDownLink);
    } else if (message.includes("unvoted up")) {
        unhighlightVoteUpIcon(voteUpLink);
    } else if (message.includes("You have voted up")) {
        highlightVoteUpIcon(voteUpLink, voteDownLink);
    } else if (message.includes("You have voted down")) {
        highlightVoteDownIcon(voteDownLink, voteUpLink);
    }
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
    voteUpLink.removeClass("far").addClass("fas");
    voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
    voteDownLink.removeClass("far").addClass("fas");
    voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink) {
    voteDownLink.removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink) {
    voteUpLink.removeClass("fas").addClass("far");
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}
