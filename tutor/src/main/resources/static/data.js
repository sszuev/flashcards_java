function findById(data, id) {
    return data.find(e => e.id.toString() === id.toString());
}

function rememberAnswer(data, stage, answer) {
    if (data.details == null) {
        data.details = {};
    }
    data.details[stage] = answer;
}

function hasStage(data, stage) {
    return data.details != null && data.details[stage] != null;
}