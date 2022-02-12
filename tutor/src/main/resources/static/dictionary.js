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
        const btnRun = $('#dictionaries-btn-run');
        const btnEdit = $('#dictionaries-btn-edit');
        tbody.html('');
        resetDictionarySelection();
        thead.on('click', function () {
            resetRowSelections(tbody);
            resetDictionarySelection();
        });
        title.on('click', function () {
            resetRowSelections(tbody);
            resetDictionarySelection();
        });
        btnRun.on('click', drawRunPage);
        btnEdit.on('click', drawDictionaryPage);
        $('#dictionaries-table-row').css('height', calcInitTableHeight());

        $.each(response, function (key, value) {
            let row = $(`<tr id="${'d' + value.id}">
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
                btnEdit.prop("disabled", false);
            });
            row.dblclick(drawRunPage);
            tbody.append(row);
        });
    });
}

function drawRunPage() {
    if (dictionary == null) {
        return;
    }
    selectActiveRow(dictionary.id);
    $.get('/api/cards/random/' + dictionary.id).done(function (array) {
        data = array;
        stageShow();
    });
}

function drawDictionaryPage() {
    if (dictionary == null) {
        return;
    }
    selectActiveRow(dictionary.id);
    const table = $('#dictionary-table');
    const tbody = $('#dictionary tbody');
    const title = $('#dictionary-title');
    title.html(dictionary.name);
    $('#dictionary-table-row').css('height', calcInitTableHeight());

    $.get('/api/cards/' + dictionary.id).done(function (response) {
        displayPageCard('dictionary');
        $.each(response, function (key, item) {
            let row = $(`<tr id="${'w' + item.id}">
                            <td>${item.word}</td>
                            <td>${toTranslationString(item)}</td>
                            <td>${percentage(item)}</td>
                          </tr>`);
            tbody.append(row);
        });
    });
}

function resetRowSelections(tbody) {
    $('tr', tbody).each(function (i, r) {
        $(r).removeClass();
    })
}

function resetDictionarySelection() {
    dictionary = null;
    $('#dictionaries-btn-group button').each(function (i, b) {
        $(b).prop('disabled', true);
    });
}

function selectActiveRow(id) {
    $('#' + id).addClass(runRowClass);
}

function calcInitTableHeight() {
    return Math.round($(document).height() * 7 / 9);
}