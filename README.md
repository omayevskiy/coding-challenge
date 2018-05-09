Coding Challenge Backend Engineering
======
  

Introduction
------------

This exercise is designed to assess backend development skills.
Its intention is to evaluate the implementation against the criteria explained below.

Context
------------

My team is asked to build a microservice that will calculate
real-time statistics of item sales on a marketplace platform. This microservice will feed data
to a dashboard installed in a business team’s room.
The microservice shall have a REST interface with two endpoints. The first endpoint will be
called by the checkout service whenever a new payment is received and the second
endpoint will provide statistics about the total order amount and average amount per order
for the last 60 seconds. (Orders between _t_ and _t_ - 60 sec, given _t_ = request time)
Specifications for the requested endpoints are as follows:

Transactions
------------

**URL**:​ /sales  
**Method**: ​POST  
**Content-Type**:​ application/x-www-form-urlencoded  

**Parameters**:

|Name|Required|Type|
|----|--------|----|
|sales_amount|Yes|Number String (e.g. “10.00”)|

**Return HTTP Code**:​ 202 Accepted  
**Return Body**: ​blank

Statistics
----------

**URL**:​ /statistics  
**Method**: ​GET  
**Parameters**: ​none  
**Return HTTP Code**:​ 200 OK  
**Return Body**:
  
```
{  
  total_sales_amount: “1000000.00”,  
  average_amount_per_order: “45.04”  
}
```
    
Facts
-----

* Business people are only interested in seeing the statistics for the last minute. They
are not interested in seeing historical data, since they use this service only to create
a real-time dashboard. The dashboard is updated once every second.
* Around 150.000 items are sold each minute. So, the
service must expect a high amount of transaction data per minute and on several
TCP connections in parallel.
* It's not required to run the service on several nodes. The data can be processed in main memory. It's not allowed
to use persistent storage due to internal security policies. If the service should be
restarted, losing this in-memory data is not a big problem since the risk is only to lose
sales statistics for a couple of seconds, then new orders will arrive and the service
will again show statistics.
* The SiteOps team allocated limited cloud resources for this project. The main
memory should be used wisely. A high quantity of sales numbers will be delivered to the
endpoint and the service must operate over this data with
minimum memory.
* The statistics endpoint must return results very fast.
Since the CPU will be mainly busy with handling transactions, it should be used also
 wisely while calculating the statistics. The expected result should be delivered with low time complexity.
 
Solution
--------

Tech stack
- java 1.8
- gradle
- spring boot 2
- webflux (netty)
- junit5

Decision

Use webflux (netty) to handle massive amount of requests. To save memory don't store every request for one minute. 
The service must handle 150.000 requests per minute, persisting each request would consume a lot of memory.
Instead use two queues, one for incoming sales amounts and one for aggregated sales amount per second.
The incoming sales requests are kept only for a very short amount of time, less then one second. On the other hand 
keep aggregated sales amounts per second for the last minute. If statistics endpoint is invoked, 
it calculates the result based on the last 60 aggregated seconds. This way calculation is fast and memory usage is low.
