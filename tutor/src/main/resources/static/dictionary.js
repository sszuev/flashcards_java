/*!
 * dictionary js-script library.
 */

const selectedRowClass = 'table-secondary';
const runRowClass = 'table-dark';
const findRowClass = 'table-success';

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
    const tableRow = $('#words-table-row');
    const tbody = $('#words tbody');
    const title = $('#words-title');
    const glDiv = $('#edit-card-dialog-gl-collapse');
    const yaDiv = $('#edit-card-dialog-ya-collapse');
    const lgDiv = $('#edit-card-dialog-lg-collapse');
    const search = $('#words-search');
    title.html(dictionary.name);
    tbody.html('');
    prepareTable('words', resetWordSelection);

    tableRow.css('height', calcInitTableHeight());

    $.get('/api/cards/' + dictionary.id).done(function (response) {
        displayPageCard('words');

        search.on('input', function () {
            resetRowSelections(tbody);
            const item = findItem(response, search.val());
            if (item == null) {
                return;
            }
            const row = $('#w' + item.id);
            const position = row.offset().top - tableRow.offset().top + tableRow.scrollTop();
            tableRow.scrollTop(position);
            row.addClass(findRowClass);
        });

        $.each(response, function (key, item) {
            let row = $(`<tr id="${'w' + item.id}">
                            <td>${item.word}</td>
                            <td>${toTranslationString(item)}</td>
                            <td>${percentage(item)}</td>
                          </tr>`);

            row.on('click', function () {
                resetRowSelections(tbody);
                lgDiv.removeClass('show');
                glDiv.removeClass('show');
                yaDiv.removeClass('show');
                row.addClass(selectedRowClass);
                $('#words-btn-edit').prop('disabled', false);

                const wordInput = $('#edit-card-dialog-word');
                wordInput.val(item.word);

                lgDiv.unbind('show.bs.collapse').on('show.bs.collapse', function () {
                    onCollapseShow(lgDiv, createLgFrame);
                });
                yaDiv.unbind('show.bs.collapse').on('show.bs.collapse', function () {
                    onCollapseShow(yaDiv, createYaLink);
                });
                glDiv.unbind('show.bs.collapse').on('show.bs.collapse', function () {
                    onCollapseShow(glDiv, createGlLink);
                });
            });

            tbody.append(row);
        });
    });
}

function onCollapseShow(collapseDiv, printLink) {
    const wordInput = $('#edit-card-dialog-word');
    const innerDiv = $('div', collapseDiv);

    const wordNext = wordInput.val();
    const wordPrev = innerDiv.attr('word-txt');
    if (wordNext === wordPrev) {
        return;
    }
    printLink(innerDiv, wordNext, dictionary.sourceLang, dictionary.targetLang);
    innerDiv.attr('word-txt', wordNext);
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

function createGlLink(frameDiv, text, sourceLang, targetLang) {
    const extUri = toGlURI(text, sourceLang, targetLang);
    frameDiv.html(`<a href='${extUri}'>${extUri}</a>`);
}

function createYaLink(frameDiv, text, sourceLang, targetLang) {
    const extUri = toYaURI(text, sourceLang, targetLang);
    frameDiv.html(`<a href='${extUri}'>${extUri}</a>`);
}

function createLgFrame(frameDiv, text, sourceLang, targetLang) {
    const extUri = toLgURI(text, sourceLang, targetLang);
    const height = calcInitFrameHeight();
    const frame = $(`<iframe noborder="0" width="1140" height="800">xxx</iframe>`);
    frame.attr('src', extUri);
    frame.attr('height', height);
    frameDiv.html('').append(frame);
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
    $('#edit-card-dialog-lg-collapse').removeClass('show');
}

function selectActiveRow(id) {
    $('#' + id).addClass(runRowClass);
}

function calcInitTableHeight() {
    return Math.round($(document).height() * 7 / 9);
}

function calcInitFrameHeight() {
    return Math.round($(document).height() * 7 / 18);
}