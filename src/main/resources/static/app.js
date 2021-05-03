const dic = "Weather"
function renderPage(index) {
    if (index == null || !isInteger(index) || index < 0) {
        index = 1;
    }
    $.get('/api/card/' + dic + '/' + index).done(function(response) {
        drawContent(response, index);
    });
}
function drawContent(data, index) {
    const txt = data.word;
    const translations = data.translations;
    const sound = data.sound;
    $('.word').html(txt);
    $('.translations').html(translations);
    $('.next').attr('onclick', `renderPage(${index + 1})`);
    if (sound != null) {
        $('.sound').html(`<audio controls><source src='/api/sound/${sound}' type='audio/wav'/></audio>`);
        new Audio('/api/sound/' + sound).play();
    } else {
        $('.sound').html('');
    }
}
function isInteger(val) {
    return $.isNumeric(val) && Math.floor(val) === val;
}