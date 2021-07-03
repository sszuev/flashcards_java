/*!
 * dictionary js-script library.
 */

function drawDictionariesPage() {
    $.get('/api/dictionaries').done(function (response) {
        displayPageCard('dictionaries');

        const tbody = $('#dictionaries tbody');
        tbody.html('');
        $.each(response, function (key, value) {
            let row = $(`<tr id="${value.id}">
                            <td>${value.sourceLang}</td>
                            <td>${value.targetLang}</td>
                            <td>${value.name}</td>
                            <td>${value.total}</td>
                            <td>${value.learned}</td>
                          </tr>`);
            row.unbind('click').on('click', function () {
                dictionary = value;
                $.get('/api/cards/' + dictionary.id).done(function (array) {
                    data = array;
                    stageShow();
                });
            });
            tbody.append(row);
        });
    });
}