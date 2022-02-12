/*!
 * js-library to work with app data (card-resources).
 */

function findById(array, id) {
    return array.find(e => e.id.toString() === id.toString());
}

function rememberAnswer(item, stage, answer) {
    if (item.details == null) {
        item.details = {};
    }
    item.details[stage] = answer;
    if (answer) {
        item.answered++;
    }
}

function hasStage(item, stage) {
    return item.details != null && item.details[stage] != null;
}

/**
 * Determines if the card is fully answered.
 * If there is a wrong answer for any stage, then the method returns false.
 * @param item a data (card)
 * @returns {boolean|undefined}
 */
function isAnsweredRight(item) {
    const details = item.details;
    if (details == null || !Object.keys(details).length) {
        return undefined;
    }
    for (let key in details) {
        if (!details.hasOwnProperty(key)) {
            continue;
        }
        if (!details[key]) {
            return false;
        }
    }
    return true;
}

/**
 * Answers of array with non-answered items to process.
 * @param array input array
 * @param limit max length of returned array
 * @returns {*[]} array of items to process
 */
function selectNonAnswered(array, limit) {
    const res = [];
    for (let i = 0; i < array.length; i++) {
        let item = array[i];
        if (item.answered < numberOfRightAnswers) {
            res.push(item);
        }
        if (limit && res.length === limit) {
            return res;
        }
    }
    return res;
}

/**
 * Answers a resource for sending to server.
 * @param array a data array
 * @param stage i.e. 'self-test', 'mosaic'
 * @returns {string}
 */
function toResource(array, stage) {
    const res = array.map(function (d) {
        const item = {};
        item.id = d.id;
        if (stage) {
            item.details = {};
            item.details[stage] = d.details[stage];
        } else {
            item.details = d.details;
        }
        return item;
    });
    return JSON.stringify(res);
}

/**
 * Represents an array of card-resources as a string, containing only words.
 * @param array
 * @returns {string}
 */
function toWordString(array) {
    return array.map(d => d.word).sort().join(', ');
}

/**
 * Finds translation string from the item that starts with the specified substring ignoring case.
 * @param item - card resource
 * @param test string to test
 * @returns {string} or undefined
 */
function findTranslationStartsWith(item, test) {
    test = test.toLowerCase();
    return toTranslationArray(item).find((s) => s.toLowerCase().startsWith(test));
}

/**
 * Represents an item translations as a single string.
 * @param item - card resource
 * @returns {string}
 */
function toTranslationString(item) {
    return toTranslationArray(item).join(', ');
}

/**
 * Represents an item translations as a flat array.
 * @param item - card resource
 * @returns {array}
 */
function toTranslationArray(item) {
    return $.map(item.translations, function (n) {
        return n;
    });
}

/**
 * Returns learning percentage for card.
 * @param item - card resource
 * @returns {number} - int percentage
 */
function percentage(item) {
    if (item.answered > numberOfRightAnswers) {
        return 100;
    }
    return Math.round(100.0 * item.answered / numberOfRightAnswers);
}