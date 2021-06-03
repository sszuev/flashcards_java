function findById(array, id) {
    return array.find(e => e.id.toString() === id.toString());
}

function rememberAnswer(item, stage, answer) {
    if (item.details == null) {
        item.details = {};
    }
    item.details[stage] = answer;
}

function hasStage(item, stage) {
    return item.details != null && item.details[stage] != null;
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