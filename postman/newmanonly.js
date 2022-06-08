const {runNewman} = require("./newmanrun");
runNewman(function () {
    console.log("done");
},
    require(__dirname + '/collections/roles.postman_collection.json'),
    require(__dirname + '/collections/data.json'));