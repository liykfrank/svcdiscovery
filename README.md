This is a fork of https://github.com/liykfrank/spring-cloud-kubernetes/tree/master/spring-cloud-kubernetes-examples/kubernetes-circuitbreaker-ribbon-example
## Kubernetes Circuit Breaker & Load Balancer Example

This example demonstrates how to use [Hystrix circuit breaker](https://martinfowler.com/bliki/CircuitBreaker.html) and the [Ribbon Load Balancing](http://microservices.io/patterns/client-side-discovery.html). The circuit breaker which is backed with Ribbon will check regularly if the target service is still alive. If this is not loner the case, then a fall back process will be excuted. In our case, the REST `greeting service` which is calling the `name Service` responsible to generate the response message will reply a "fallback message" to the client if the `name service` is not longer replying.
As the Ribbon Kubernetes client is configured within this example, it will fetch from the Kubernetes API Server, the list of the endpoints available for the name service and loadbalance the request between the IP addresses available

![](images/svclb.png?raw=true)

### Build/Deploy using Minishift

This project example runs OpenShift environments,you may need to have a openshift environment in pace as a precondition.
#### Start agency-service
We will start by deploying a single agency service. This service have majorly provide 2 functions: 1. return the hostname of the hosting server 2. query agency with a given code
You could build and run this application follow below  steps:
- Go to svc-agency-service directory

```
cd svcdiscovery/svc-agency-service

```

- Compile with maven and deploy to openshift

```
mvn clean package fabric8:deploy
```


- Then you could login to openshift wiht below link to check the status of svc-agency-service:
https://127.0.0.1:8443

And you will get something like this:

![](images/svc-agency.png?raw=true)


- Next you could have a try with the name and query agency function:

![](images/svc-agency-name.png?raw=true)

![](images/svc-agency-query.png?raw=true)

#### Call risk-service
Next step is to deploy risk-service
This service depends on agency-service to obtain a agency by a given code.
You could build and run this application follow below  steps:
- Go to svc-agency-service directory

```
cd svcdiscovery/svc-risk-service

```

- Compile with maven and deploy to openshift

```
mvn clean package fabric8:deploy
```

![](images/risk-mvn.png?raw=true)


- Then you could login to openshift wiht below link to check the status of svc-risk-service:
https://127.0.0.1:8443

And you will get something like this:

![](images/risk-oc.png?raw=true)


- Next you could have a try with the name and query agency function:

![](images/risk1.png?raw=true)


### Verify the load balancing

First, scale the number of pods of the `agency-service` to 2

```
oc scale --replicas=2 dc agency-service
```
![](images/agency-pods.png?raw=true)
Wait a few minutes before to issue the curl request to call the Risk Service to let the platform to create the new pod.

```
oc get pods --selector=project=agency-service
NAME                   READY     STATUS    RESTARTS   AGE
name-service-1-0ss0r   1/1       Running   0          3m
name-service-1-fblp1   1/1       Running   0          36m
```

If you issue the curl request to access the greeting service, you should see that the message response
contains a different id end of the message which corresponds to the name of the pod.
![](images/loadbalance.gif?raw=true)
As Ribbon will question the Kubernetes API to get, base on the `name-service` name, the list of IP Addresses assigned to the service as endpoints,
you should see that you will get a different response from one of the 2 pods running

```
oc get endpoints/name-service
NAME           ENDPOINTS                         AGE
name-service   172.17.0.2:8080,172.17.0.3:8080   40m
```

Here is an example about what you will get

```
curl http://IP_OR_HOSTNAME/greeting
Hello from name-service-1-0ss0r!
curl http://IP_OR_HOSTNAME/greeting
Hello from name-service-1-fblp1!
...
```

### Test the fall back

In order to test the circuit breaker and the fallback option, you will scale the `name-service` to 0 pods as such

```
oc scale --replicas=0 dc name-service
```
![](images/agency-shutdown.png?raw=true)
and next issue a new curl request to get the response from the greeting service

```
Hello from Fallback!
```

![](images/risk-fallback.png?raw=true)


## Trouble Shooting
oc grant permission for default user, to let this user has proper permission to access kubernetes service in this project
