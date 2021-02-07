import com.rabbitmq.jms.admin.RMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.main.Main;
import org.springframework.jms.connection.CachingConnectionFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class RabbitMQAMQPExample {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        String queueName = args[1];
        int messageCount = Integer.parseInt(args[2]);
        String path = args[3];
        String userName = args[4];
        String password = args[5];
        int seconds = Integer.parseInt(args[6]);
        boolean append = true;

        System.out.println("Arguments: " + Arrays.asList(args));
        File file = new File(path + File.separator + "TPSInfo.txt-" + UUID.randomUUID());
        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);

        final int[] count = {1};
        count[0] = 0;

        final int[] tpsCount = {1};
        tpsCount[0] = 0;

        CamelContext defaultCamelContext = new DefaultCamelContext();
        AMQPComponent component = new AMQPComponent();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        RMQConnectionFactory targetConnectionFactory = new RMQConnectionFactory();
//        targetConnectionFactory.setHost(host);
        String hostList = "";
        if (!host.contains(",")) {
            hostList = "amqp://" + host;
        }else {
            String[] split = host.split(",");
            for (int i =0 ; i<split.length;i++){
                hostList+="amqp://"+split[i];
                if(i!=split.length-1){
                    hostList+=",";
                }
            }
        }
        targetConnectionFactory.setUris(Arrays.asList(hostList));
        targetConnectionFactory.setUsername(userName);
        targetConnectionFactory.setPassword(password);
        connectionFactory.setTargetConnectionFactory(targetConnectionFactory);
        component.setConnectionFactory(connectionFactory);
        defaultCamelContext.addComponent("amqp", component);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("amqp:queue:" + queueName).process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        int val = count[0];
                        count[0] = val + 1;

                        int tpsVal = tpsCount[0];
                        tpsCount[0] = tpsVal + 1;

                        if (count[0] == 1) {
                            pw.println("Start time : " + sdf.format(Calendar.getInstance().getTime()));
                        }
                        if (val != 0) {
                            if (val % messageCount == 0) {
                                pw.println("Completed next " + messageCount + " at " + sdf.format(Calendar.getInstance().getTime()));
                            }
                        }
                    }
                });
                from("timer://simpleTimer?period=" + seconds * 1000).process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        float tps = (float) tpsCount[0] / (float) seconds;
                        System.out.println("TPS at " + sdf.format(Calendar.getInstance().getTime()) + " : " + tps);
                        tpsCount[0] = 0;
                    }
                });
            }
        });
        Thread printingHook = new Thread(() -> {
            pw.println("The End : " + count[0]);
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Runtime.getRuntime().addShutdownHook(printingHook);
        defaultCamelContext.start();
    }
}
