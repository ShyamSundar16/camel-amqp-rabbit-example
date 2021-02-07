# camel-amqp-rabbit-example

##Camel RabbitMQ component example

####Preparing RabbitMQ
    This example requires that RabbitMQ Server is up and running.

####Pushing samples to queue

    java -classpath .:./lib/* MessagePublisherClient <RABBITMQ_HOST> <RABBITMQ_PORT> <SAMPLE_COUNT>
   
   *Example*,
   
    java -classpath .:./lib/* RabbitMQComponentExample localhost 5672 guest guest 5
   

##Camel AMQP component example - TPS Calculator 

####Run the command given below to start the tool:  

    java -classpath .:./lib/* RabbitMQAMQPExample <RABBITMQ_HOST> <QUEUE_TO_MONITOR> <COUNT_FOR_TPS> <LOCATION_TO_WRITE>

  *Example*,
  
    java -classpath .:./lib/* RabbitMQAMQPExample localhost:5672 OutCamel 5 /home/administrator

This command has to be executed in the same location where lib is located.
1. First argument is the hostName(localhost:5672).
2. Second argument is the queue name which has to be monitered (OutCamel)
3. Third argument is the count after which the time will be logged (5).
4. Fourth argument is the file location where the log file will be generated, which gives the information in more readable format.

**NOTE** : Copy the required jars in lib folder, and execute the corresponding tool.
   


