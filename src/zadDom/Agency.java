package zadDom;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import zadDom.utils.ExchangeListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Agency {
    public static void main(String[] args) throws Exception {
        System.out.println("Enter Agency name: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String name = bf.readLine();

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String SERVICE_EXCHANGE_NAME = "service";
        channel.exchangeDeclare(SERVICE_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // starting listeners
        ExchangeListener listener = new ExchangeListener("agency." + name, SERVICE_EXCHANGE_NAME);
        ExchangeListener adminListener = new ExchangeListener("agencies.#", "admin");
        listener.run();
        adminListener.run();

        while (true) {
            // read msg
            System.out.println("Enter message: (P - people, S - satellite, L - load) \";\" and message content");
            String line = bf.readLine();
            // break condition
            if ("exit".equals(line))
                break;

            // publish
            String type = line.split(";")[0].trim();
            if(!type.matches("[PSL]{2}")  || (type.charAt(0) == type.charAt(1)) || line.split(";").length < 2){
                System.out.println("Unknown service type (P - people, S - satellite, L - load) or empty message");
                continue;
            }
            String key = "services." + type;
            String text = line.split(";")[1];
            String serviceID = String.valueOf(new Random().nextInt(100000));
            String msg = name + ";" + serviceID + ";" + text;
            channel.basicPublish(SERVICE_EXCHANGE_NAME, key, null, msg.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent: " + msg);
        }
    }
}
