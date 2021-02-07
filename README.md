# camel-amqp-rabbit-example

## Camel RabbitMQ component example

#### Preparing RabbitMQ
  This example requires RabbitMQ Server which is up and running. Refer [Docker image for RabbitMQ](https://github.com/ShyamSundar16/camel-amqp-rabbit-example#docker-for-quick-run) .
  
#### Pushing samples to queue

    java -classpath .:./lib/* MessagePublisherClient <RABBITMQ_HOST> <RABBITMQ_PORT> <SAMPLE_COUNT>
   
   *Example*,
   
    java -classpath .:./lib/* RabbitMQComponentExample localhost 5672 guest guest 5
   

## Camel AMQP component example - TPS Calculator 

#### Run the command given below to start the tool:  

    java -classpath .:./lib/* RabbitMQAMQPExample <RABBITMQ_HOST> <QUEUE_TO_MONITOR> <COUNT_FOR_TPS> <LOCATION_TO_WRITE>

  *Example*,
  
    java -classpath .:./lib/* RabbitMQAMQPExample localhost:5672 OutCamel 5 /home/administrator

This command has to be executed in the same location where lib is located.
1. First argument is the hostName(localhost:5672).
2. Second argument is the queue name which has to be monitered (OutCamel)
3. Third argument is the count after which the time will be logged (5).
4. Fourth argument is the file location where the log file will be generated, which gives the information in more readable format.

**NOTE** : Copy the required jars in lib folder, and execute the corresponding tool.

#### Docker for quick run
```
docker run --name docker-rabbit -p 5672:5672 -p 5673:5673 -p 15672:15672 rabbitmq:3-management```
```

#### Help & Feedback
If you hit any problem or have some feedback let me know at,
1. [LinkedIn Profile](https://www.linkedin.com/in/shaam-sundar-1405/)
2. [Gmail Account](shaamsundar16@gmail.com)