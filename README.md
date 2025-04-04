# Kafka demo application

## Description
This is a demo application for using Kafka in a Spring boot application. 
A producer and consumer is created. The producer will create messages at a fixed rate, which will be consumed by the consumer.

I also wanted to experiment with kubernetes. This makes it possible to easily scale components.

### Notes:
This project assumes Rancher desktop to be installed.

All commands are to be run from the project root.

## Building

### Build producer and consumer
Both the producer and consumer are modules under the main project. They can be built by running:
> mvn clean install

This will produce a runnable jar under `/target` for each module:
`producer-*-executable.jar` or `consumer-*-executable.jar`

## Create docker image
For each of these jars a docker image must be created. A dockerfile is provided inside each module:
> docker build -t localhost:5000/mariusdw.com/demo-kafka-consumer consumer

> docker build -t localhost:5000/mariusdw.com/demo-kafka-producer producer

## Installation

### Option 1: Use docker compose
This starts everything together as individual docker containers. Note this will run the spring apps under the `dev` profile.
> docker compose up

Note kafka-ui will be available on `localhost:18081`

### Option 2: Use a local kubernetes cluster

#### Install kafka with the bitnami helm chart (see https://github.com/bitnami/charts/tree/main/bitnami/kafka)
Add repo:
> helm repo add bitnami https://charts.bitnami.com/bitnami

Install kafka into kafka namespace:
> helm install kafka bitnami/kafka -f devops/k8s/bitnami/kafka/values.yaml -n kafka

#### Install kafka-ui (see https://ui.docs.kafbat.io/configuration/helm-charts/quick-start)
This installation will expect kafka to be installed in the kafka namespace.
However, note that for some reason kafka-ui itself must be installed in the default namespace.
(TODO figure out why)

Add repo: 
> helm repo add kafbat-ui https://kafbat.github.io/helm-charts

Create config for kafka-ui:
> kubectl apply -f devops/k8s/kafka-ui

Install chart:
> helm install kafbat-ui kafbat-ui/kafka-ui --set existingConfigMap="kafbat-ui-helm-values"

We need to port forward the kafka-ui port to access it locally (on port 18080). Note this can also be done through a nodeport service:
> export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=kafka-ui,app.kubernetes.io/instance=kafbat-ui" -o jsonpath="{.items[0].metadata.name}")

> kubectl --namespace default port-forward $POD_NAME 18080:8080

#### App installation: Run local docker registry (see https://hub.docker.com/_/registry)
Kubernetes requires a docker registry to pull artifacts from. To pull the java apps we need to push them to a local registry.

Run a local registry as follows:
> docker run -d -p 5000:5000 --restart always --name registry registry:2

Now push the images we built earlier into it:
> docker push localhost:5000/mariusdw.com/demo-kafka-consumer

> docker push localhost:5000/mariusdw.com/demo-kafka-producer 

#### Install the applications
The applications will be installed as deployments in the apps namespace.

Install everything in the application directory.
> kubectl apply -f devops/k8s/application

Check the logs of each application pod created. Messages should be produced and consumed.

## Troubleshooting
#### Kafka DNS (kafka.kafka.cluster.local) cannot be resolved
* Is the hostname correct? Is Kafka installed under namespace kafka and is the service called kafka?


* Try the kafka-client to check if a connection can be made (see instructions when bitnami kafka is launched):
>To create a pod that you can use as a Kafka client run the following commands:

    kubectl run kafka-client --restart='Never' --image docker.io/bitnami/kafka:4.0.0-debian-12-r0 --namespace kafka --command -- sleep infinity
    kubectl exec --tty -i kafka-client --namespace kafka -- bash

    PRODUCER:
        kafka-console-producer.sh \
            --bootstrap-server kafka.kafka.svc.cluster.local:9092 \
            --topic test

    CONSUMER:
        kafka-console-consumer.sh \
            --bootstrap-server kafka.kafka.svc.cluster.local:9092 \
            --topic test \
            --from-beginning

* Check if all the pods under kube-system is running. If Traefik is crashing, then you can disable traefik in Rancher preferences -> Kubernetes.

#### On windows when running docker commands: the docker client must be run with elevated privileges to connect
* Run docker commands through WSL. In Rancher, under preferences -> WSL, select `Expose Rancher Desktop's Kubernetes configuration and Docker socket to Windows Subsystem for Linux (WSL) distros`