# websocket-failover-example
this is a simple websocket failover example with jetty and hazelcast.

What it does is, you spawn 3 (or more) websocket servers, client connects to any of them. We hold a simple integer and increment
when the client connects. Client disconnects and connects to any of the available websockets, and you see the counter is still incrementing.

How it works
-------------------------
- checkout the project
- build with:
```    
    mvn package
```
- on the same terminal, run server 
```
java -jar target/javax.websocket-example-1.0-SNAPSHOT-jar-with-dependencies.jar 9081
```
- on another terminal
```
java -jar target/javax.websocket-example-1.0-SNAPSHOT-jar-with-dependencies.jar 9082
```
- on another terminal
```
java -jar target/javax.websocket-example-1.0-SNAPSHOT-jar-with-dependencies.jar 9083
```

- now you have 3 servers, client will connect to only 9082 or 9083
- open chrome and access http://localhost:9081/
- on chrome dev console you'll see the output like
```
connecting to ->  ws://localhost:9082/events/
current value:1
```
- refresh your browser and you'll see output like
```
connecting to ->  ws://localhost:9083/events/
current value:2
```
- now you can kill server on 9083, you'll see something like
```
onclose CloseEvent {isTrusted: true, wasClean: false, code: 1006, reason: "", type: "close", …}
retrying connect
-> server -> ws://localhost:9082/events/
connecting to ->  ws://localhost:9082/events/
current value:27
```

What now
------------
This is simply a starting point to explore the possibilities using a datagrid with java websockets. 




