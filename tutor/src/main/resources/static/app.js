function renderPage() {
    drawDictionariesPage();
}

let data;

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
            $.get('/api/dictionaries/' + dic + '/deck').done(function(response) {
                data = response;
                drawCardPage(0);
            });
        });
    });
}

function drawCardPage(index) {
    if (index >= data.length) {
        console.log("No more data")
        return;
    }
    const d = data[index];
    const txt = d.word;
    const translations = d.translations;
    const sound = d.sound;
    $('.word').html(txt);
    $('.translations').html(translations);
    $('.next').attr('onclick', `drawCardPage(${++index})`);
    if (sound != null) {
        let path = '/api/sounds/' + sound;
        $('.sound').html(`<audio controls><source src='${path}' type='audio/wav'/></audio>`);
        new Audio(path).play();
    } else {
        $('.sound').html('');
    }
}