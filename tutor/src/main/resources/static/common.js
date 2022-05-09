/*!
 * A js-script library that contains common function, which are not related to app directly.
 */

/**
 * Creates a random array from the given one.
 * @param data an array
 * @param length a length of new array
 * @returns {*[]} a new array
 */
function randomArray(data, length) {
    if (length > data.length) {
        throw "Wrong input: " + length + " > " + data.length;
    }
    const res = data.slice();
    shuffleArray(res);
    if (length === data.length) {
        return res;
    }
    return res.slice(0, length);
}

/**
 * Randomly permutes the specified array.
 * All permutations occur with approximately equal likelihood.
 * @param array
 */
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}

/**
 * Removes duplicates.
 * @param array
 * @returns {*[]} - a new array
 */
function removeDuplicates(array) {
    const res = [];
    $.each(array, function (i, e) {
        if ($.inArray(e, res) === -1) res.push(e);
    });
    return res;
}

/**
 * Splits string using separator.
 * @param value {string}
 * @param separator {string}
 * @returns {*[]|*|string[]}
 */
function toArray(value, separator) {
    value = value.trim();
    if (value === '') {
        return [];
    }
    return value.split(separator).map(x => x.trim());
}

/**
 * Answers [true] if the string is xml
 * @param canBeXml
 * @returns {boolean}
 */
function isXML(canBeXml){
    try {
        $.parseXML(canBeXml);
        return true;
    } catch (err) {
        return false;
    }
}

/**
 * Prepares the filename to save.
 * @param string
 * @returns {string}
 */
function toFilename(string) {
    return string.replace(/[^a-z\d]/gi, '_').toLowerCase();
}