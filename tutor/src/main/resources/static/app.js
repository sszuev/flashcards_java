let dictionary;
let data;

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
                $.get('/api/dictionaries/' + dictionary.id + '/deck').done(function (array) {
                    data = array;
                    drawStageShow();
                });
            });
            tbody.append(row);
        });
    });
}

/**
 * First stage: show.
 */
function drawStageShow() {
    const data = selectData();
    if (data.length >  0) {
        displayPageCard('show');
        drawShowCardPage(data, 0);
        return;
    }
    drawStageResults();
}

/**
 * Second stage: mosaic.
 */
function drawStageMosaic() {
    const data = selectData();
    if (data.length > 0) {
        displayPageCard('mosaic');
        drawMosaicCardPage(data);
        return;
    }
    drawStageResults();
}

/**
 * Third stage: self-test.
 */
function drawStageSelfTest() {
    const data = selectData();
    if (data.length >  0) {
        displayPageCard('self-test');
        drawSelfTestCardPage(randomArray(data, numberOfWordsPerStage), 0);
        return;
    }
    drawStageResults();
}

/**
 * Last stage: resutls.
 */
function drawStageResults() {
    displayPageCard('result');
    drawResultCardPage();
}

function selectData() {
    return selectNonAnswered(data);
}

function drawShowCardPage(data, index) {
    if (index >= data.length) { // no more data => display next stage
        drawStageMosaic();
        return;
    }
    const page = $('#show');
    const current = data[index];
    const next = index + 1;

    drawAndPlayAudio(page, current.sound);
    displayTitle(page, 'show');
    $('.word', page).html(current.word);
    $('.translations', page).html(current.translations);
    $('#show-next').unbind('click').on('click', function () {
        drawShowCardPage(data, next);
    });
}

function drawMosaicCardPage(data) {
    const stage = 'mosaic';
    const borderDefault = 'border-white';
    const borderSelected = 'border-primary';
    const borderSuccess = 'border-success';
    const borderError = 'border-danger';

    displayTitle($('#mosaic'), stage);

    const leftPane = $('#mosaic-left');
    const rightPane = $('#mosaic-right');
    const next = $('#mosaic-next');

    const dataLeft = randomArray(data, numberOfWordsPerStage);
    const dataRight = randomArray(data, data.length);

    next.unbind('click').on('click', function () {
        sendPatch(toResource(dataLeft, stage), () => drawStageSelfTest());
    });

    leftPane.html('');
    dataLeft.forEach(function (value) {
        let left = $(`<div class="card ${borderDefault}" id="${value.id}-left"><h4>${value.word}</h4></div>`);
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
        let right = $(`<div class="card ${borderDefault}" id="${value.id}-right"><h4>${value.translations}</h4></div>`);
        right.unbind('click').on('click', function () {
            const selected = $(`#mosaic-left .${borderSelected}`);
            if (!selected.length || !$('h4', selected).text().trim()) {
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
                left.html('<h4>&nbsp;</h4>').unbind('click');
                right.html('<h4>&nbsp;</h4>').unbind('click');
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

function drawSelfTestCardPage(selfTestData, index) {
    const stage = 'self-test';
    if (index >= selfTestData.length) {
        sendPatch(toResource(selfTestData, stage), function () {
            drawStageResults();
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

function drawResultCardPage() {
    const page = $('#result');
    const right = toString(data.filter(d => isAnsweredRight(d)));
    const wrong = toString(data.filter(function (d) {
        const res = isAnsweredRight(d);
        return res !== undefined && !res;
    }));
    const learned = toString(data.filter(d => d.answered >= numberOfRightAnswers));
    displayTitle(page, 'result');
    $('#result-correct').html(right);
    $('#result-wrong').html(wrong);
    $('#result-learned').html(learned);
}

function toString(data) {
    return data.map(d => d.word).sort().join(', ');
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
        promise.then(() => {
        });
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