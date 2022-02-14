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
    $.each($('.page'), function (k, v) {
        let x = $(v);
        if (x.attr('id') === id) {
            return;
        }
        $(x).hide();
    });
    $('#' + id).show()
}

/**
 * Plays the audio resource in browser.
 * @param resource {string} - path to resource, not null
 * @param callback - function to call on complete
 */
function playAudio(resource, callback) {
    const audio = new Audio('/api/sounds/' + resource);
    if (!callback) {
        callback = () => {};
    }
    new Audio('/api/sounds/' + resource).play().then(callback);
}