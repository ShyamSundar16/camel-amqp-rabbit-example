import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Created by Shyam Sundar on 2/11/21.
 */
public class DirectTest {
    public static void main(String[] args) throws Exception {
        CamelContext defaultCamelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        RabbitMQComponent component = new RabbitMQComponent();
        component.setConnectionFactory(connectionFactory);
        defaultCamelContext.addComponent("rabbitcomponent", component);
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:rabbitStart").routeId("DirectToRabbit")
                        .to("rabbitcomponent:TestPush?exchangeType=direct&routingKey=cache-refresh&autoDelete=false&queue=cache-refresh")
                        .to("rabbitcomponent:TestPush?exchangeType=direct&autoAck=false&autoDelete=false&routingKey=Test&queue=TestPush");

                from("rabbitcomponent:TestPush?exchangeType=direct&routingKey=cache-refresh&autoDelete=false&queue=cache-refresh1").routeId("cache-refresh")
                        .to("rabbitcomponent:TestPush?exchangeType=direct&routingKey=cache-refresh&autoDelete=false&queue=cache-refresh2");
            }
        });

        ProducerTemplate producerTemplate = defaultCamelContext.createProducerTemplate();
        defaultCamelContext.start();
        for (int i = 0; i < 5; i++) {
            producerTemplate.sendBody("direct:rabbitStart", "testRabbitMQMessage"+i);
        }
    }
}
