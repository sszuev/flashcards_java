/*!
 * dictionary js-script library.
 */

const selectedRowClass = 'table-secondary';
const runRowClass = 'table-dark';

function drawDictionariesPage() {
    $.get('/api/dictionaries').done(function (response) {
        displayPageCard('dictionaries');

        const tbody = $('#dictionaries tbody');
        const thead = $('#dictionaries thead');
        const title = $('#dictionaries .card-title');
        const btnRun = $('#btn-run');
        tbody.html('');
        resetSelection();
        thead.on('click', function () {
            resetRowSelections(tbody);
            resetSelection();
        });
        title.on('click', function () {
            resetRowSelections(tbody);
            resetSelection();
        });
        btnRun.on('click', runStage);
        $.each(response, function (key, value) {
            let row = $(`<tr id="${value.id}">
                            <td>${value.sourceLang}</td>
                            <td>${value.targetLang}</td>
                            <td>${value.name}</td>
                            <td>${value.total}</td>
                            <td>${value.learned}</td>
                          </tr>`);
            row.on('click', function () {
                dictionary = value;
                resetRowSelections(tbody);
                row.addClass(selectedRowClass);
                btnRun.prop("disabled", false);
            });
            row.dblclick(runStage);
            tbody.append(row);
        });
    });
}

function resetRowSelections(tbody) {
    $('tr', tbody).each(function (i, r) {
        $(r).removeClass();
    })
}

function resetSelection() {
    dictionary = null;
    $('#btn-run').prop("disabled", true);
}

function runStage() {
    if (dictionary == null) {
        return;
    }
    $('#' + dictionary.id).addClass(runRowClass);
    $.get('/api/cards/random/' + dictionary.id).done(function (array) {
        data = array;
        stageShow();
    });
}