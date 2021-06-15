// main script.

let dictionary;
let data;

// noinspection JSUnusedLocalSymbols
function renderPage() {
    drawDictionariesPage();
}

function displayPageCard(id) {
    $.each($('.page'), function (k, v) {
        let x = $(v);
        if (x.attr('id') === id) {
            return;
        }
        $(x).hide();
    });
    $('#' + id).show()
}