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
        initTableListeners('dictionaries', resetDictionarySelection);

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
                markRowSelected(row);
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
    markRowRun(dictionary.id);
    $.get('/api/cards/random/' + dictionary.id).done(function (array) {
        data = array;
        stageShow();
    });
}

function drawDictionaryPage() {
    if (dictionary == null) {
        return;
    }
    markRowRun(dictionary.id);

    const tableRow = $('#words-table-row');
    const tbody = $('#words tbody');
    const search = $('#words-search');

    $('#words-title').html(dictionary.name);
    tbody.html('');
    initDialog('add');
    initDialog('edit');
    initEditDialog();
    initTableListeners('words', resetWordSelection);
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

            selectCardItemForEdit(row, item);
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
                selectCardItemForEdit(row, item);
                selectCardItemForAdd(row, item.word);
            });
            tbody.append(row);
        });
    });
}

function selectCardItemForEdit(row, item) {
    cleanDialogLinks('edit');
    markRowSelected(row);
    $('#edit-card-dialog-word').val(item.word);
    insertDialogLinks('edit');
    $('#words-btn-edit').prop('disabled', false);

    const btn = $('#edit-card-dialog-sound');
    btn.attr('word-txt', item.word);
    btn.attr('word-sound', item.sound);

    $('#edit-card-dialog-translation').val(toTranslationArray(item).join("; "));
}

function selectCardItemForAdd(row, word) {
    cleanDialogLinks('add');
    if (row != null) {
        markRowSelected(row);
    }
    $('#add-card-dialog-word').val(word);
    insertDialogLinks('add');
    $('#words-btn-add').prop('disabled', false);
}

function initTableListeners(id, resetSelection) {
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
    cleanDialogLinks('add');
    cleanDialogLinks('edit');
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

function initDialog(prefix) {
    $('#' + prefix + '-card-dialog-lg-collapse').unbind('show.bs.collapse').on('show.bs.collapse', function () {
        onCollapseLgFrame(prefix);
    });
    $('#' + prefix + '-card-dialog-word').on('input', function () {
        insertDialogLinks(prefix);
    });
}

function initEditDialog() {
    const btn = $('#edit-card-dialog-sound');
    btn.prop('disabled', false);
    btn.on('click', function () {
        const audio = btn.attr('word-sound');
        if (!audio) {
            return;
        }
        btn.prop('disabled', true);
        playAudio(audio, function () {
            btn.prop('disabled', false);
        });
    });
}

function cleanDialogLinks(prefix) {
    $('#' + prefix + '-card-dialog-gl-link').html('');
    $('#' + prefix + '-card-dialog-yq-link').html('');
    $('#' + prefix + '-card-dialog-lg-link').html('');
    $('#' + prefix + '-card-dialog-lg-collapse').removeClass('show');
}

function insertDialogLinks(prefix) {
    const input = $('#' + prefix + '-card-dialog-word');
    const sl = dictionary.sourceLang;
    const tl = dictionary.targetLang;
    const text = input.val();
    if (!text) {
        return;
    }
    createLink($('#' + prefix + '-card-dialog-gl-link'), toGlURI(text, sl, tl));
    createLink($('#' + prefix + '-card-dialog-ya-link'), toYaURI(text, sl, tl));
    createLink($('#' + prefix + '-card-dialog-lg-link'), toLgURI(text, sl, tl));
}

function createLink(parent, uri) {
    parent.html(`<a class='btn btn-link' href='${uri}'>${uri}</a>`);
}

function onCollapseLgFrame(prefix) {
    const sl = dictionary.sourceLang;
    const tl = dictionary.targetLang;

    const lgDiv = $('#' + prefix + '-card-dialog-lg-collapse div');
    const dialogLinksDiv = $('#' + prefix + '-card-dialog-links');
    const wordInput = $('#' + prefix + '-card-dialog-word');
    const text = wordInput.val();
    const prev = dialogLinksDiv.attr('word-txt');
    if (text === prev) {
        return;
    }
    const extUri = toLgURI(text, sl, tl);
    const height = calcInitLgFrameHeight();
    const frame = $(`<iframe noborder="0" width="1140" height="800">LgFrame</iframe>`);
    frame.attr('src', extUri);
    frame.attr('height', height);
    lgDiv.html('').append(frame);
    dialogLinksDiv.attr('word-txt', text);
}

function markRowRun(id) {
    $('#' + id).addClass(runRowClass);
}

function markRowSelected(row) {
    row.addClass(selectedRowClass);
}

function calcInitTableHeight() {
    return Math.round($(document).height() * tableHeightRation);
}

function calcInitLgFrameHeight() {
    return Math.round($(document).height() * lgFrameHeightRation);
}