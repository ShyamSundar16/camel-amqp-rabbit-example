import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQComponent;
import org.apache.camel.impl.DefaultCamelContext;
import java.util.Arrays;
import java.util.Calendar;

public class RabbitMQComponentExample {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String userName = args[2];
        String password = args[3];
        int messageCount=Integer.parseInt(args[4]);
        String testRabbitMQMessage = "Test Message from  RabbitMQ " + Calendar.getInstance().getTime();
        final int[] count = {1};
        count[0] = 0;

        System.out.println("Arguments: " + Arrays.asList(args));

        CamelContext defaultCamelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost("/");
        RabbitMQComponent component = new RabbitMQComponent();
        component.setConnectionFactory(connectionFactory);
        defaultCamelContext.addComponent("rabbitcomponent", component);
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:rabbitStart").routeId("DirectToRabbit").process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        int val = count[0];
                        count[0] = val + 1;
                    }
                }).to("rabbitcomponent:cache-refresh?exchangeType=fanout&routingKey=*&autoDelete=false&queue=cache-refresh")
                        .to("rabbitcomponent:TestPush?exchangeType=direct&autoAck=false&autoDelete=false&routingKey=Test&queue=TestPush");

                from("rabbitcomponent:TestPush?exchangeType=direct&autoAck=false&autoDelete=false&routingKey=Test&queue=TestPush").routeId("AnotherQueue")
                        .to("rabbitcomponent:AnotherQueue?exchangeType=direct&autoAck=false&autoDelete=false&routingKey=Test&queue=AnotherQueue")
                        .to("rabbitcomponent:AnotherQueue1?exchangeType=direct&autoAck=false&autoDelete=false&routingKey=Test&queue=AnotherQueue1");
//
//                from("rabbitcomponent:cache-refresh?exchangeType=fanout&routingKey=*&autoDelete=false&queue=cache-refresh")
//                        .to("rabbitcomponent:TopicTest?exchangeType=direct&autoAck=false&autoDelete=false&routingKey=Test&queue=TopicTest");
            }
        });
        Thread printingHook = new Thread(() -> {
            System.out.println("The End : " + count[0]);

        });
        Runtime.getRuntime().addShutdownHook(printingHook);
        ProducerTemplate producerTemplate = defaultCamelContext.createProducerTemplate();
        defaultCamelContext.start();
        for (int i = 0; i < messageCount; i++) {
            producerTemplate.sendBody("direct:rabbitStart", testRabbitMQMessage);
        }
    }
}
