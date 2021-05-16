function renderPage() {
    drawDictionariesPage();
}

let data;
let dictionary;

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
                    drawShowCardPage(0);
                });
            });
            tbody.append(row);
        });
    });
}

function drawShowCardPage(index) {
    if (index >= data.length) {
        displayPageCard('self-test');
        drawSelfTestCardPage(0);
        return;
    }
    const page = $('#show');
    const current = data[index];
    const next = index + 1;

    drawAndPlaySound(page, current.sound);
    $('.card-title', page).html(dictionary.name);
    $('.word', page).html(current.word);
    $('.translations', page).html(current.translations);
    $('#show-next').unbind('click').on('click', function () {
        drawShowCardPage(next);
    });
}

function drawSelfTestCardPage(index) {
    if (index >= data.length) {
        const update = JSON.stringify(data
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

    const current = data[index];
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
        drawSelfTestCardPage(next);
    });
    wrong.unbind('click').on('click', function () {
        wrong.unbind('click');
        rememberAnswer(current, stage, false);
        drawSelfTestCardPage(next);
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
        let path = '/api/sounds/' + sound;
        item.html(`<audio controls><source src='${path}' type='audio/wav'/></audio>`);
        new Audio(path).play().then(() => {
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