const newman = require("newman");
const open = require("open");
const {getFileWithExtensionName} = require("./utils");

module.exports = {
    runNewman: function (cb, collection, dataset) {
        console.log("generating newman report based on postman collection...");
        //const newman = "newman run roles.postman_collection.json -n 3 -r htmlextra -d data.json";
        console.log('test start-----');
        newman.run({
            collection: collection,
            timeoutRequest: 10000,
            iterationData: dataset,
            reporters: ['cli', 'htmlextra']
        }).on('start', function (err, args) { // on start of run, log to console
            console.log('running a collection...' + err + " " + args);
        }).on('beforeIteration', function (i, a) { //After an iteration completes
            console.log('iteration starts');
        }).on('iteration', function (i, a) { //After an iteration completes
            console.log('iteration complete');
        }).on('test', function (i, a) { //After a test completes
            console.log('test complete');
        }).on('exception', function (i, a) { //After an exception is thrown
            console.log('exception');
        }).on('done', function (err, summary) {
            if (err || summary.error) {
                console.error('collection run encountered an error.');
            } else {
                console.log('collection run completed.');
                const fileName = getFileWithExtensionName(__dirname + "/newman", ".html");
                console.log("report generated: " + fileName);
                open(__dirname + "/newman/" + fileName);
            }
            cb();
        });
    }
}