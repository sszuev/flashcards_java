/*!
 * dictionary js-script library.
 */

const selectedRowClass = 'table-secondary';
const runRowClass = 'table-dark';

function drawDictionariesPage() {
    $.get('/api/dictionaries').done(function (response) {
        displayPageCard('dictionaries');

        const tbody = $('#dictionaries tbody');
        const btnRun = $('#dictionaries-btn-run');
        const btnEdit = $('#dictionaries-btn-edit');
        tbody.html('');
        prepareTable('dictionaries', resetDictionarySelection);

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
                btnRun.prop('disabled', false);
                btnEdit.prop('disabled', false);
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
    const tbody = $('#words tbody');
    const title = $('#words-title');
    const frameDiv = $('#edit-card-dialog-lingvo-collapse');
    title.html(dictionary.name);
    tbody.html('');
    prepareTable('words', resetWordSelection);

    $('#words-table-row').css('height', calcInitTableHeight());

    $.get('/api/cards/' + dictionary.id).done(function (response) {
        displayPageCard('words');
        $.each(response, function (key, item) {
            let row = $(`<tr id="${'w' + item.id}">
                            <td>${item.word}</td>
                            <td>${toTranslationString(item)}</td>
                            <td>${percentage(item)}</td>
                          </tr>`);

            row.on('click', function () {
                resetRowSelections(tbody);
                frameDiv.removeClass('show');
                row.addClass(selectedRowClass);
                $('#words-btn-edit').prop('disabled', false);

                const wordInput = $('#edit-card-dialog-word');
                wordInput.val(item.word);

                frameDiv.unbind('show.bs.collapse').on('show.bs.collapse', onFrameOpen);
            });

            tbody.append(row);
        });
    });
}

function onFrameOpen() {
    const wordInput = $('#edit-card-dialog-word');
    const frameDiv = $('#edit-card-dialog-lingvo-collapse div');

    const wordNext = wordInput.val();
    const wordPrev = frameDiv.attr('word-txt');
    if (wordNext === wordPrev) {
        return;
    }
    let extUri = toLingvoURI(wordNext, dictionary.sourceLang, dictionary.targetLang);
    let height = calcInitFrameHeight();
    let frame = $(`<iframe noborder="0" width="1140" height="800">xxx</iframe>`);
    frame.attr('src', extUri);
    frame.attr('height', height);
    frameDiv.html('').append(frame);
    frameDiv.attr('word-txt', wordNext);
}


function prepareTable(id, resetSelection) {
    const thead = $('#' + id + ' thead');
    const title = $('#' + id + ' .card-title');
    const tbody = $('#' + id +  ' tbody');

    resetSelection();
    thead.on('click', function () {
        resetRowSelections(tbody);
        resetSelection();
    });
    title.on('click', function () {
        resetRowSelections(tbody);
        resetSelection();
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

function resetWordSelection() {
    $('#words-btn-edit').prop('disabled', true);
    $('#edit-card-dialog-lingvo-collapse').removeClass('show');
}

function selectActiveRow(id) {
    $('#' + id).addClass(runRowClass);
}

function calcInitTableHeight() {
    return Math.round($(document).height() * 7 / 9);
}

function calcInitFrameHeight() {
    return Math.round($(document).height() * 5 / 9);
}