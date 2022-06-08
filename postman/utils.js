const ps = require("ps-node");
const execc = require('child_process').exec;
const {spawn} = require('child_process');
const fs = require('fs');
const path = require("path");
//
module.exports = {
    getFileWithExtensionName: function (dir, ext) {
        const files = fs.readdirSync(dir);
        const htmlFiles = files.filter(el => path.extname(el) === ext);
        return htmlFiles[0];
    },
    shutdownJava: function (javaprocess) {
        if (javaprocess) {
            ps.kill(javaprocess.pid, function (err) {
                if (err) {
                    console.log("couldn't validate if java has finished. please check your task manager");
                } else {
                    console.log('Process %s has been killed!', javaprocess.pid);
                }
                process.exit();
            });
        } else {
            process.exit();
        }
    },
    lookupProcess: function (cb) {
        console.log("looking for java...");
        ps.lookup({
            command: 'java',
            arguments: ['-jar']
        }, function (err, resultList) {
            if (err) {
                throw new Error(err);
            }
            resultList.forEach(function (process) {
                console.log("done");
                if (process) {
                    console.log('PID: %s, COMMAND: %s, ARGUMENTS: %s', process.pid, process.command, process.arguments);
                    cb(process);
                } else {
                    throw new Error("Couldn't find java process. please kill it in task manager :(")
                }
            });
        });
    },
    isRunning: function (query, cb) {
        let platform = process.platform;
        let cmd = '';
        switch (platform) {
            case 'win32' :
                cmd = `tasklist`;
                break;
            case 'darwin' :
                cmd = `ps -ax | grep ${query}`;
                break;
            case 'linux' :
                cmd = `ps -A`;
                break;
            default:
                break;
        }
        execc(cmd, (err, stdout, stderr) => {
            cb(stdout.toLowerCase().indexOf(query.toLowerCase()) > -1);
        });
    },
    executeCommand: function (logMessage, textToExecute) {
        return new Promise(resolve => {
            console.log(logMessage);
            const command = spawn(textToExecute, {shell: true})
            command.stdout.pipe(process.stdout);
            command.on('close', () => resolve())
        });
    }
}