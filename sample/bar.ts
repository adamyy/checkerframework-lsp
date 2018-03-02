console.log("start");
const { spawn } = require('child_process');

const child_hello = spawn('java',['HelloWorld']);
const child_wc = spawn('wc');

child_hello.stdout.pipe(child_wc.stdin);

child_wc.stdout.on('data', (data)=> {
    console.log(`stdout: \n${data}`);
}); 
console.log("end");