import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Created by Shyam Sundar on 2/9/21.
 */
public class CSVFormatTest {
    public static void main(String[] args) throws Exception {
        CamelContext defaultCamelContext = new DefaultCamelContext();
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("kafka:localhost:9092?brokers=localhost:9092&groupId=Latency17&topic=DEMOTENANT_Latency&autoCommitEnable=false&allowManualCommit=true&maxPollRecords=5&consumersCount=10&consumerStreams=10&autoOffsetReset=earliest")
                    .unmarshal().csv()
                    .marshal().csv()
                    .to("file:../Latency?fileName=LatencyReport.csv&fileExist=Append");
            }
        });
        defaultCamelContext.start();
        Thread.sleep(6000);
    }

}