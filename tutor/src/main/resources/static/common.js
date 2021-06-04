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