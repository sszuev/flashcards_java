const dic = "Weather"
function renderPage(index) {
    console.log("Dictionary: " + dic)
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
    $('.word').html(txt);
    $('.translations').html(translations);
    $('.next').attr('onclick', `renderPage(${index + 1})`);
}
function isInteger(val) {
    return $.isNumeric(val) && Math.floor(val) === val;
}