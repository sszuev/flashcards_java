function renderPage() {
    drawDictionariesPage();
}

function drawDictionariesPage() {
    $.get('/api/dictionaries').done(function (response) {
        $('#card').hide();
        $('#dictionaries').show();
        let selector = $('#selector');
        $.each(response, function (key, value) {
            selector.append($("<option></option>")
                .attr("value", key)
                .text(value));
        });
        selector.on('change', function () {
            let v = this.value;
            if ('#' === v) {
                return;
            }
            let dic = response[v];
            $('#dictionaries').hide();
            $('#card').show();
            toNextCard(dic, null);
        });
    });
}

function toNextCard(dic, index) {
    if (index == null || !isInteger(index) || index < 0) {
        index = 1;
    }
    $.get('/api/cards/' + dic + '/' + index).done(function(response) {
        drawCardPage(response, dic, index);
    });
}

function drawCardPage(data, dic, index) {
    const txt = data.word;
    const translations = data.translations;
    const sound = data.sound;
    $('.word').html(txt);
    $('.translations').html(translations);
    $('.next').attr('onclick', `toNextCard('${dic}', ${index + 1})`);
    if (sound != null) {
        let path = '/api/sounds/' + sound;
        $('.sound').html(`<audio controls><source src='${path}' type='audio/wav'/></audio>`);
        new Audio(path).play();
    } else {
        $('.sound').html('');
    }
}

function isInteger(val) {
    return $.isNumeric(val) && Math.floor(val) === val;
}