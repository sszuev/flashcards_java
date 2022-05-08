/*!
 * page:dictionaries js-script library.
 */

function drawDictionariesPage() {
    getDictionaries(function (response) {
        displayPage('dictionaries');

        const tbody = $('#dictionaries tbody');
        const btnRun = $('#dictionaries-btn-run');
        const btnEdit = $('#dictionaries-btn-edit');

        tbody.html('');
        initTableListeners('dictionaries', resetDictionarySelection);

        btnRun.on('click', drawRunPage);
        btnEdit.on('click', drawDictionaryPage);
        $('#dictionaries-table-row').css('height', calcInitTableHeight());
        $('#dictionaries-btn-upload').on('click', () => {
            $('#dictionaries-btn-upload-label').removeClass('btn-outline-danger');
        }).on('change', (e) => {
            const file = e.target.files[0];
            if (file !== undefined) {
                uploadDictionaryFile(file);
            }
        });
        bootstrap.Modal.getOrCreateInstance(document.getElementById('delete-dictionary-prompt')).hide();
        initDictionaryDeletePrompt();

        $.each(response, function (key, value) {
            let row = $(`<tr id="${'d' + value.id}">
                            <td>${value.sourceLang}</td>
                            <td>${value.targetLang}</td>
                            <td>${value.name}</td>
                            <td>${value.total}</td>
                            <td>${value.learned}</td>
                          </tr>`);
            row.on('click', function () {
                dictionaryRowOnClick(row, value);
            });
            row.dblclick(drawRunPage);
            tbody.append(row);
        });
    });
}

function uploadDictionaryFile(file) {
    const btnUpload = $('#dictionaries-btn-upload-label');
    const reader = new FileReader()
    reader.onload = function (e) {
        const txt = e.target.result.toString();
        if (!isXML(txt)) {
            btnUpload.addClass('btn-outline-danger');
            return;
        }
        uploadDictionary(txt, drawDictionariesPage, () => btnUpload.addClass('btn-outline-danger'))
    }
    reader.readAsText(file, 'utf-8')
    $('#dictionaries-btn-upload').val('')
}

function initDictionaryDeletePrompt() {
    $('#delete-dictionary-prompt-confirm').off('click').on('click', function () {
        const body = $('#delete-dictionary-prompt-body');
        const id = body.attr('item-id');
        if (!id) {
            return;
        }
        deleteDictionary(id, drawDictionariesPage);
    });
}

function dictionaryRowOnClick(row, dict) {
    dictionary = dict;
    const tbody = $('#dictionaries tbody');
    const btnRun = $('#dictionaries-btn-run');
    const btnEdit = $('#dictionaries-btn-edit');
    const btnDelete = $('#dictionaries-btn-delete');
    resetRowSelection(tbody);
    markRowSelected(row);
    btnRun.prop('disabled', false);
    btnEdit.prop('disabled', false);
    btnDelete.prop('disabled', false);

    const body = $('#delete-dictionary-prompt-body');
    body.attr('item-id', dict.id);
    body.html(dict.name);
}

function resetDictionarySelection() {
    dictionary = null;
    $('#dictionaries-btn-group button').each(function (i, b) {
        $(b).prop('disabled', true);
    });
    $('#dictionaries-btn-upload-label').removeClass('btn-outline-danger');
}

function drawRunPage() {
    if (dictionary == null) {
        return;
    }
    resetRowSelection($('#dictionaries tbody'));
    getNextCardDeck(dictionary.id, null, function (array) {
        data = array;
        stageShow();
    });
}

