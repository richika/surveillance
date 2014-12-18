var express = require('express');
var app = express();
var server = require('http').Server(app);
var io = require('socket.io').listen(server);
var querystring = require('querystring');

app.set('port', (process.env.PORT || 5001))
app.use(express.static(__dirname + '/public'))

io.on('connection', function (socket){
	console.log('connected');
});

app.get('/', function(request, response) {
	response.sendFile(__dirname + '/public/index.html');
})

function getIpAddress(req){
	var ipAddr = req.headers["x-forwarded-for"];
  	if (ipAddr){
    	var list = ipAddr.split(",");
    	ipAddr = list[list.length-1];
  	} 
  	else{
    	ipAddr = req.connection.remoteAddress;
  	}
  	return ipAddr;
}
app.post('/phone_coordinates', function (request, response){
	var string = '';
	request.on('data', function (data){
		string += data;
	});
	request.on('end', function (){
		response.send(string);
		var qString = querystring.parse(string);
		var ip = getIpAddress(request);
		console.log(ip);
		qString['ip'] = ip;
		io.sockets.emit('coordinates', qString);

	});
});
app.get('/fake_coordinates', function (request, response){
	response.send('<html><form method = "post" action = "/phone_coordinates"><input type = "text" name = "latitutde"><br><input type = "text" name = "longitude"><br><input type = "submit"></form></html>" ')
});
app.get('/data_received', function (request, response){
	response.send(string);
});
server.listen(app.get('port'), function() {
  console.log("Node app is running at localhost:" + app.get('port'))
});
