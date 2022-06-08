const fs = require('fs');
const {spawn} = require('child_process');
const {runNewman} = require("./newmanrun.js");
const {shutdownJava, executeCommand, isRunning, lookupProcess} = require("./utils");

var javaprocess = null;

async function boot() {
    console.log("Deleting ./var directory");
    fs.rmSync(__dirname + "/var", {recursive: true, force: true});
    //
    console.log("Deleting ./newman directory");
    fs.rmSync(__dirname + "/newman", {recursive: true, force: true});
    await executeCommand("executing maven install...", "mvn install -DskipTests -f ../pom.xml");
    boostrapApp();
}

function boostrapApp() {
    console.log("booting up spring...");
    spawn('java', ["-jar", "-Dapplication.feature.toggle.crawler.enabled=false", "../tempo-cc-api-app/target/tempo-cc-api-app-0.0.1-SNAPSHOT.jar"], {
        detached: true,
        setsid: true
    });
    // encouragement...
    setTimeout(function () {
        console.log("please be patient :)");
        // check if java started
        isRunning('java', (status) => {
            console.log("Java is Running? ->" + status);
            // check if the process lived...
            lookupProcess(function (p) {
                javaprocess = p;
                console.log("app booted...giving it a few seconds...")
                setTimeout(runNewman, 12000, function () {
                        shutdownJava(javaprocess)
                    },
                    require(__dirname + '/collections/roles.postman_collection.json'),
                    require(__dirname + '/collections/data.json'));
            });
        });
    }, 1000);
}

function exitHandler(options, exitCode) {
    if (options.cleanup) console.log('clean');
    if (exitCode || exitCode === 0) console.log(exitCode);
    shutdownJava(javaprocess);
}

//do something when app is closing
process.on('exit', exitHandler.bind(null, {cleanup: true}));

boot();