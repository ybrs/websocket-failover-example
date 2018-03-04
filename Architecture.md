Clustering
====================

Cluster usually refers to a bunch of servers doing the same kind of work, eg: webserver cluster, socket server cluster, database cluster etc.

When we say, a database cluster, we usually mean, there are more than one database server that can handle clients.

Highly Available cluster - or high availability cluster - means that if one of the servers goes down, a client can connect to another server and cluster will still be available.

Cluster Configurations
----------------

# Active/Passive

When a server goes down, another server takes its role. Eg: you have a master/slave database configuration, your server goes down, 
than you promote the slave to be a master and clients can connect to it.

```
[client1] ->
[client2] -> [loadbalancer] -> [server1/primary] ... [server2/failover]
[client3] ->
```

In this case if server1 goes down, loadbalancer understands and starts sending requests to server2. Although this results in having
an idle server for most of the time, its useful in some scenarios. 

# Active/Active

In active/active we mean that the servers in the cluster are always available to handle requests. 


```
[client1] ->                    [server1]
[client2] -> [loadbalancer] ->  [server2]
[client3] ->                    [server3]
```

In this case, if server1 goes down, loadbalancer will stop sending requests to server1 and cluster will still be available.

Active/Active vs. Active/Passive
------------------

The main advantage of Active/Passive configuration is you don't need to deal with replication lag. 
Failover server will be idle until primary goes down, so it won't handle any requests. 

Lets give an example, you have a cluster of database servers. Client1 updates a record on a table on database. 
This takes some time until its replicated to the second database - typically less than a few miliseconds. 
But sometimes it can take longer - maybe load on the second server, or a big update locking the tables etc.
When this happens we say there is a replication lag.

So say client2 connects to database2 and fetches the same record, in an ideal world client2 should see the same value, 
but if there is a replication lag, client2 will see the old value.

Depending on the case this might not be very important, but think of booking a room from a hotel, adding some item to your shopping cart etc.
Other clients might fetch and old value and say the room is still available, so you can get a double book on the same room.

The hardest part is that its not easy to debug this, because you can't really understand when that happens.

So usually for high-availability of the databases we tend to use Active/Passive, so that there is only one single source of truth.

Another advantage is of course its easy to setup such a configuration.

The disadvantage is that you have an idle server and its a waste of resources. 
One strategy is to use slaves for other purposes - eg. running reports, which such a replication lag will be negligible.

Another disadvantage of Active/Passive is service disruption. Say you lost your primary database server, failover server is promoted to 
being master - might take at more than a few seconds. In that timeframe you can't keep servicing clients. Though of course this rarely happens
with a database server. 

Active/Active has many advantages, comparing to Active/Passive. Most important is you don't have service interruptions, thats why we tend to use Active/Active for webservers.
Say you have a web application, and when you deploy a new version, you want to take it offline, deploy a new version and put it back to cluster. In an active/active
configuration, you don't need to stop servicing the clients.

In this example project, we are using an Active/Active configuration, we'll get into more details about how to handle replication lags (eg. using locks)

Service Discovery / Load Balancing
===================================

When we say "service discovery" we usually mean list of available nodes. This can be on the database, a list in the memory of loadbalancer, a text file, an api server etc.

There are different strategies to register a service to a service catalog (or service registry).

Service Registry
--------------------------

A service registry is a database that keeps a record of service locations. By saying database we don't mean a traditional database though, 
it can be a redis server, etcd, zookeeper, consul etc or even a text file.


Service Discovery Patterns
---------------------------

# Client Side Service discovery
--------------------------------

In this pattern, client asks for a list os services registered to a central location (database) and then decides which one to connect.

# Server Side Service discovery
--------------------------------
Client asks for a central location - eg: loadbalancer - which service to connect, gets the location and connects or the loadbalancer proxies the request
so that client doesn't need to do a round trip - doesn't need to make 2 requests.


Service Registration Patterns
------------------------------

# Self registration
---------------------------

In self registration, a service decides is its available and registers itself to the service catalog. 

In this pattern we usually use a heartbeat mechanism, so that when the service is available, 
it sends a heartbeat to a central location in an interval. If it doesn't send a heart beat in a timely fashion, we assume its dead.


# 3rd party registration
---------------------------

In this pattern, a service scans/detects a well known list of service locations. 

Eg. Elastic Load Balancer in AWS, you give the endpoints, and it checks them in regular intervals, sending probes. If it can't get a reply,
it simply stops sending requests to that location.

You can always combine these two patterns, eg. when a server is up at ec2 you can register it to ELB via api - or better use autoscale configuration.
Then ELB starts checking if your server is live.

In this example we are using a self registration - registering our service to datagrid itself. We use a client side service discovery to simplify things - 
client connects to a random server.