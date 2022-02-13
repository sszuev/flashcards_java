/*!
 * dictionary js-script library.
 */

const selectedRowClass = 'table-success';
const runRowClass = 'table-dark';

const tableHeightRation = 2. / 3;
const lgFrameHeightRation = 7. / 18;

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
                resetRowSelection(tbody);
                selectRow(row);
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
    runRow(dictionary.id);
    $.get('/api/cards/random/' + dictionary.id).done(function (array) {
        data = array;
        stageShow();
    });
}

function drawDictionaryPage() {
    if (dictionary == null) {
        return;
    }
    runRow(dictionary.id);

    const tableRow = $('#words-table-row');
    const tbody = $('#words tbody');
    const title = $('#words-title');
    prepareDialogLinks('add');
    prepareDialogLinks('edit');
    const search = $('#words-search');
    title.html(dictionary.name);
    tbody.html('');
    prepareTable('words', resetWordSelection);
    tableRow.css('height', calcInitTableHeight());

    $.get('/api/cards/' + dictionary.id).done(function (response) {
        displayPageCard('words');

        search.on('input', function () {
            resetWordSelection();
            const item = findItem(response, search.val());
            if (item == null) {
                selectCardItemForAdd(null, search.val());
                return;
            }
            const row = $('#w' + item.id);
            const position = row.offset().top - tableRow.offset().top + tableRow.scrollTop();
            tableRow.scrollTop(position);

            selectCardItemForEdit(row, item.word);
            selectCardItemForAdd(row, search.val());
        });

        $.each(response, function (key, item) {
            let row = $(`<tr id="${'w' + item.id}">
                            <td>${item.word}</td>
                            <td>${toTranslationString(item)}</td>
                            <td>${percentage(item)}</td>
                          </tr>`);
            row.on('click', function () {
                resetWordSelection();
                selectCardItemForEdit(row, item.word);
                selectCardItemForAdd(row, item.word);
            });
            tbody.append(row);
        });
    });
}

function selectCardItemForEdit(row, word) {
    collapseDialogLinks('edit');
    selectRow(row);
    $('#edit-card-dialog-word').val(word);
    $('#words-btn-edit').prop('disabled', false);
}

function selectCardItemForAdd(row, word) {
    collapseDialogLinks('add');
    if (row != null) {
        selectRow(row);
    }
    $('#add-card-dialog-word').val(word);
    $('#words-btn-add').prop('disabled', false);
}

function prepareTable(id, resetSelection) {
    const thead = $('#' + id + ' thead');
    const title = $('#' + id + ' .card-title');
    const tbody = $('#' + id + ' tbody');

    resetSelection();
    thead.on('click', function () {
        resetRowSelection(tbody);
        resetSelection();
    });
    title.on('click', function () {
        resetRowSelection(tbody);
        resetSelection();
    });
}

function resetDictionarySelection() {
    dictionary = null;
    $('#dictionaries-btn-group button').each(function (i, b) {
        $(b).prop('disabled', true);
    });
}

function resetWordSelection() {
    disableWordButton('add');
    disableWordButton('edit');
    collapseDialogLinks('add');
    collapseDialogLinks('edit');
    resetRowSelection($('#words tbody'));
}

function resetRowSelection(tbody) {
    $('tr', tbody).each(function (i, r) {
        $(r).removeClass();
    })
}

function disableWordButton(suffix) {
    $('#words-btn-' + suffix).prop('disabled', true);
}

function prepareDialogLinks(prefix) {
    const glDiv = $('#' + prefix + '-card-dialog-gl-collapse');
    const yaDiv = $('#' + prefix + '-card-dialog-ya-collapse');
    const lgDiv = $('#' + prefix + '-card-dialog-lg-collapse');

    lgDiv.unbind('show.bs.collapse').on('show.bs.collapse', function () {
        onCollapseShow(lgDiv, createLgFrame);
    });
    yaDiv.unbind('show.bs.collapse').on('show.bs.collapse', function () {
        onCollapseShow(yaDiv, createYaLink);
    });
    glDiv.unbind('show.bs.collapse').on('show.bs.collapse', function () {
        onCollapseShow(glDiv, createGlLink);
    });
}

function collapseDialogLinks(prefix) {
    $('#' + prefix + '-card-dialog-gl-collapse').removeClass('show');
    $('#' + prefix + '-card-dialog-yl-collapse').removeClass('show');
    $('#' + prefix + '-card-dialog-ll-collapse').removeClass('show');
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
    const height = calcInitLgFrameHeight();
    const frame = $(`<iframe noborder="0" width="1140" height="800">xxx</iframe>`);
    frame.attr('src', extUri);
    frame.attr('height', height);
    frameDiv.html('').append(frame);
}

function runRow(id) {
    $('#' + id).addClass(runRowClass);
}

function selectRow(row) {
    row.addClass(selectedRowClass);
}

function calcInitTableHeight() {
    return Math.round($(document).height() * tableHeightRation);
}

function calcInitLgFrameHeight() {
    return Math.round($(document).height() * lgFrameHeightRation);
}