# High Available websocket example

This is a simple websocket failover/high availability example with jetty and hazelcast.

What it does is, you spawn 3 (or more) websocket servers, clients connect to any of them. 

We hold a simple integer and increment when client presses a button. Think that as a session/room/game data.

Clients can disconnect and reconnect to any of the available websockets, 
and you can see the counter still has the same value.


What ?
-------------------------

![screencast](screencast.gif)

You can see we have 4 clients, connecting to each websocket server. Incrementing the counter on any port is published almost realtime
to other websocket clients.

The 4th client connects to one of the servers randomly.

How it works
-------------------------
- checkout the project
- build with:
```    
    mvn package
```
- on the same terminal, run server 
```
java -jar target//nl.ybrs.eventserver-1.0-SNAPSHOT-jar-with-dependencies.jar 9081
```
- on another terminal
```
java -jar target//nl.ybrs.eventserver-1.0-SNAPSHOT-jar-with-dependencies.jar 9082
```
- on another terminal
```
java -jar target//nl.ybrs.eventserver-1.0-SNAPSHOT-jar-with-dependencies.jar 9083
```

- now you have 3 servers, leave them running

- open chrome and access http://localhost:9081/

- on chrome dev console you'll see a similar ui like the one on the image above

- you can try, disconnecting, reconnecting, killing any of the servers etc. The room/session data is in the grid. 
So unless you kill all servers and restart, your room data will be available.

- for building the frontend, you can 
```
cd public/frontend
yarn build
```

- or to experiment with the client side,
```
cd public/frontend
yarn start
```

What now
------------
This is simply a starting point to explore the possibilities using a datagrid with java websockets. 


How it works in detail:
------------------------

There are a few topics to be explained.

0- Background High Availability

Just a brief overview, about architecture what we use etc.

[Architecture](Architecture.md)

1- Loadbalancing, service discovery

This part is how a client will understand which server to connect etc. Keeping a list of available servers etc.

[LoadBalancing](LoadBalancing.md)

2- Session data

This is about how to access session data from any server. Keeping data on the grid.

[SessionData](SessionData.md)

3- Publishing and listening events in the cluster

This part is about how you can publish events - eg: user joined, data updated etc. and how you can react accordingly

[EventPublishing](EventPublishing.md)