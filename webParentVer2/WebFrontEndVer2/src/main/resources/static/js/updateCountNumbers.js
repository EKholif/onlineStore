function updateCountNumbers() {
    $(".divCount").each(function(index, element) {
        element.innerHTML = "" + (index + 1);
    });
}