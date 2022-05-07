/*!
 * main script.
 */

// selected dictionary resource (todo: support selecting several dictionaries)
let dictionary;
// an array with card resources
let data;

// noinspection JSUnusedLocalSymbols
function renderPage() {
    drawDictionariesPage();
}

function displayPage(id) {
    if (id === 'dictionaries') {
        $('#go-home').addClass('disabled').off('click');
    } else {
        $('#go-home').removeClass('disabled').on('click', () => window.location.reload());
    }

    $.each($('.page'), function (k, v) {
        let x = $(v);
        if (x.attr('id') === id) {
            return;
        }
        $(x).hide();
    });
    $('#' + id).show()
}

