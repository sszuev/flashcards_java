let data;
let dictionary;

// noinspection JSUnusedLocalSymbols
function renderPage() {
    drawDictionariesPage();
}

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
                displayPageCard('show');
                $.get('/api/dictionaries/' + dictionary.id + '/deck').done(function (response) {
                    data = response;
                    drawShowCardPage(data, 0);
                });
            });
            tbody.append(row);
        });
    });
}

function drawShowCardPage(showData, index) {
    if (index >= showData.length) { // no more data => display next stage
        drawMosaicCardPage();
        return;
    }
    const page = $('#show');
    const current = showData[index];
    const next = index + 1;

    drawAndPlayAudio(page, current.sound);
    displayTitle(page, 'show');
    $('.word', page).html(current.word);
    $('.translations', page).html(current.translations);
    $('#show-next').unbind('click').on('click', function () {
        drawShowCardPage(showData, next);
    });
}

function drawSelfTestCardPage(selfTestData, index) {
    const stage = 'self-test';
    if (index >= selfTestData.length) {
        sendPatch(toResource(selfTestData, stage), function () {
            // data synchronized
            displayPageCard('result');
            drawResultPage();
        });
        return;
    }
    const page = $('#self-test');

    const translation = $('.translations', page);
    const curtain = $('#self-test-display-translation');
    const display = $('#self-test-display-translation button');
    const correct = $('#self-test-correct');
    const wrong = $('#self-test-wrong');

    const current = selfTestData[index];
    const next = index + 1;

    drawAndPlayAudio(page, current.sound);
    displayTitle(page, stage);
    $('.word', page).html(current.word);
    translation.html(current.translations);
    correct.prop('disabled', true);
    wrong.prop('disabled', true);
    translation.hide();
    curtain.show();

    display.unbind('click').on('click', function () {
        display.unbind('click');
        curtain.hide();
        translation.show();
        correct.prop('disabled', false);
        wrong.prop('disabled', false);
    });
    correct.unbind('click').on('click', function () {
        correct.unbind('click');
        rememberAnswer(current, stage, true);
        drawSelfTestCardPage(selfTestData, next);
    });
    wrong.unbind('click').on('click', function () {
        wrong.unbind('click');
        rememberAnswer(current, stage, false);
        drawSelfTestCardPage(selfTestData, next);
    });
}

function drawMosaicCardPage() {
    const stage = 'mosaic';
    const borderDefault = 'border-white';
    const borderSelected = 'border-primary';
    const borderSuccess = 'border-success';
    const borderError = 'border-danger';

    displayPageCard('mosaic');
    displayTitle($('#mosaic'), stage);

    const leftPane = $('#mosaic-left');
    const rightPane = $('#mosaic-right');
    const next = $('#mosaic-next');

    const dataLeft = randomArray(data, numberOfWordsPerStage);
    const dataRight = randomArray(data, data.length);

    next.unbind('click').on('click', function () {
        sendPatch(toResource(dataLeft, stage), function () {
            displayPageCard('self-test');
            drawSelfTestCardPage(randomArray(data, numberOfWordsPerStage), 0);
        });
    });

    leftPane.html('');
    dataLeft.forEach(function (value) {
        let left = $(`<div class="card ${borderDefault}" id="${value.id}-left">${value.word}</div>`);
        left.unbind('click').on('click', function () {
            $.each($('#mosaic .card'), (k, v) => setBorderClass(v, borderDefault));
            setBorderClass(left, borderSelected);
            const sound = value.sound;
            if (sound != null) {
                playAudio(sound);
            }
        });
        leftPane.append(left);
    });

    rightPane.html('');
    dataRight.forEach(function (value) {
        let right = $(`<div class="card ${borderDefault}" id="${value.id}-right">${value.translations}</div>`);
        right.unbind('click').on('click', function () {
            const selected = $(`#mosaic-left .${borderSelected}`);
            if (!selected.length || !selected.text().trim()) {
                // nothing selected or selected already processed item (with empty text)
                return;
            }
            const rightCards = $('#mosaic-right .card');
            const leftCards = $('#mosaic-left .card');
            $.each(rightCards, (k, v) => setBorderClass(v, borderDefault));
            const left = $(document.getElementById(right.attr('id').replace('-right', '-left')));
            if (left.length && !left.text().trim()) { // exists but empty
                return;
            }
            const success = left.is(selected);
            setBorderClass(right, success ? borderSuccess : borderError);
            if (success) {
                left.html('&nbsp;').unbind('click');
                right.html('&nbsp;').unbind('click');
            }
            const id = selected.attr('id').replace('-left', '');
            const data = findById(dataLeft, id);
            if (!hasStage(data, stage)) { // only for first
                rememberAnswer(data, stage, success);
            }
            if (!leftCards.filter((i, e) => $(e).text().trim()).length) {
                // no more options
                $.each(rightCards, (k, v) => setBorderClass(v, borderDefault));
                $.each(leftCards, (k, v) => setBorderClass(v, borderDefault));
                $('#mosaic-next').parent().show();
            }
        });
        rightPane.append(right);
    });
}

function drawResultPage() {
    const page = $('#result');
    const right = data
        .filter(d => isAnsweredRight(d))
        .map(d => d.word)
        .sort()
        .join(', ');
    const wrong = data
        .filter(d => !isAnsweredRight(d))
        .map(d => d.word)
        .sort()
        .join(', ');

    displayTitle(page, 'result');
    $('#result-correct').html(right);
    $('#result-wrong').html(wrong);
}

function sendPatch(update, onDoneCallback) {
    $.ajax({
        type: 'PATCH',
        url: '/api/cards/',
        contentType: "application/json",
        data: update
    }).done(onDoneCallback);
}

function displayTitle(page, stage) {
    $('.card-title', page).html(dictionary.name + ": " + stage);
}

function setBorderClass(item, border) {
    return $(item).attr('class', $(item).attr('class').replace(/\bborder-.+\b/g, border));
}

function drawAndPlayAudio(parent, sound) {
    let item = $('.sound', parent);
    if (sound != null) {
        playAudio(sound, p => item.html(`<audio controls><source src='${p}' type='audio/wav'/></audio>`));
    } else {
        item.html('');
    }
}

function playAudio(sound, callback) {
    const path = '/api/sounds/' + sound;
    const promise = new Audio(path).play();
    if (callback) {
        promise.then(() => callback(path));
    } else {
        promise.then(() => {});
    }
}

function displayPageCard(id) {
    $.each($('.page'), function (k, v) {
        let x = $(v);
        if (x.attr('id') === id) {
            return;
        }
        $(x).hide();
    });
    $('#' + id).show()
}