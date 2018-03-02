console.log("start");
var spawn = require('child_process').spawn;
var child_hello = spawn('java', ['HelloWorld']);
var child_wc = spawn('wc');
// process.stdin.pipe(child_wc.stdin);
// console.log(`child_hello stdout: \n${child_hello.stderr}`);
// child_hello.stdout.pipe(process.process.stdin);
// child_hello.stdout.on('data', (data)=>{
// 	console.log(`stdout: \n${data}`);
// });
child_hello.stdout.pipe(child_wc.stdin);
child_wc.stdout.on('data', function (data) {
    console.log("stdout: \n" + data);
});
console.log("end");
