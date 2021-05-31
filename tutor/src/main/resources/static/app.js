let data;
let dictionary;

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
    if (index >= showData.length) {
        displayPageCard('self-test');
        drawSelfTestCardPage(randomArray(data, numberOfWordsPerStage), 0);
        return;
    }
    const page = $('#show');
    const current = showData[index];
    const next = index + 1;

    drawAndPlaySound(page, current.sound);
    $('.card-title', page).html(dictionary.name);
    $('.word', page).html(current.word);
    $('.translations', page).html(current.translations);
    $('#show-next').unbind('click').on('click', function () {
        drawShowCardPage(showData, next);
    });
}

function drawSelfTestCardPage(selfTestData, index) {
    if (index >= selfTestData.length) {
        const update = JSON.stringify(selfTestData
            .map(function (d) {
                const res = {};
                res.id = d.id;
                res.details = d.details;
                return res;
            }));
        $.ajax({
            type: 'PATCH',
            url: '/api/cards/',
            contentType: "application/json",
            data: update
        }).done(function () {
            // data synchronized
            displayPageCard('result');
            drawResultPage();
        })
        return;
    }
    const stage = 'self-test';
    const page = $('#self-test');

    const translation = $('.translations', page);
    const curtain = $('#self-test-display-translation');
    const display = $('#self-test-display-translation button');
    const correct = $('#self-test-correct');
    const wrong = $('#self-test-wrong');

    const current = selfTestData[index];
    const next = index + 1;

    drawAndPlaySound(page, current.sound);
    $('.card-title', page).html(dictionary.name);
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

function drawResultPage() {
    const page = $('#result');
    const stage = 'self-test';
    const right = data
        .filter(function (d) {
            return d.details[stage];
        })
        .map(function (d) {
            return d.word;
        })
        .sort()
        .join(', ');
    const wrong = data
        .filter(function (d) {
            return !d.details[stage];
        })
        .map(function (d) {
            return d.word;
        })
        .sort()
        .join(', ');

    $('.card-title', page).html(dictionary.name);
    $('#result-correct').html(right);
    $('#result-wrong').html(wrong);
}

function rememberAnswer(data, stage, answer) {
    if (data.details == null) {
        data.details = {};
    }
    data.details[stage] = answer;
}

function drawAndPlaySound(parent, sound) {
    let item = $('.sound', parent);
    if (sound != null) {
        const path = '/api/sounds/' + sound;
        new Audio(path).play().then(() => {
            item.html(`<audio controls><source src='${path}' type='audio/wav'/></audio>`);
        });
    } else {
        item.html('');
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

/**
 * Creates random array from the given one.
 * @param data an array
 * @param length a length of new array
 * @returns {*[]} a new array
 */
function randomArray(data, length) {
    if (length > data.length) {
        throw "Wrong input: " + length + " > " + data.length;
    }
    const res = [];
    let i = 0;
    const max = 42 * data.length;
    let j = data.length - 1;
    while (res.length < length) {
        if (i++ > max) {
            throw "Can't create random array in " + max + " iterations";
        }
        const item = data[Math.floor(Math.random() * (j + 1))];
        if (jQuery.inArray(item, res) !== -1) {
            // choose another one
            continue;
        }
        res.push(item);
        j--;
    }
    return res;
}