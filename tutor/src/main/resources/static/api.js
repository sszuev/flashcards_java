/*!
 * A js-script library that contains client-api calls
 */

function getDictionaries(onDone) {
    $.get('/api/dictionaries').done(onDone)
}

function uploadDictionary(data, onDone, onFail) {
    $.ajax({
        type: 'POST',
        url: '/api/dictionaries/',
        contentType: "application/octet-stream",
        data: data,
        processData: false,
    }).done(onDone).fail(onFail);
}

function downloadDictionaryURL(id) {
    return '/api/dictionaries/' + id + '/download';
}

function deleteDictionary(id, onDone) {
    $.ajax({
        type: 'DELETE',
        url: '/api/dictionaries/' + id
    }).done(onDone)
}

function getCards(id, onDone) {
    $.get('/api/dictionaries/' + id + '/cards').done(onDone);
}

function getNextCardDeck(id, length, onDone) {
    if (length === null) {
        $.get('/api/dictionaries/' + id + '/cards/random').done(onDone);
        return
    }
    $.get('/api/dictionaries/' + id + "/cards/random?length=" + length + "&unknown=false").done(onDone)
}

function createCard(item, onDone) {
    $.ajax({
        type: 'POST',
        url: '/api/cards/',
        contentType: "application/json",
        data: JSON.stringify(item)
    }).done(onDone)
}


function updateCard(item, onDone) {
    $.ajax({
        type: 'PUT',
        url: '/api/cards/',
        contentType: "application/json",
        data: JSON.stringify(item)
    }).done(onDone)
}

function deleteCard(id, onDone) {
    $.ajax({
        type: 'DELETE',
        url: '/api/cards/' + id
    }).done(onDone)
}

function resetCard(id, onDone) {
    $.ajax({
        type: 'PATCH',
        url: '/api/cards/' + id
    }).done(onDone)
}

function patchCard(update, onDone) {
    $.ajax({
        type: 'PATCH',
        url: '/api/cards/',
        contentType: "application/json",
        data: update
    }).done(onDone);
}

function playAudio(resource, callback) {
    if (!callback) {
        callback = () => {};
    }
    new Audio('/api/sounds/' + resource).play().then(callback);
}