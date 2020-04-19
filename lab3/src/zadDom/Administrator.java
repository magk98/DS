package zadDom;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import zadDom.utils.ExchangeListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Administrator {
    public static void main(String[] args) throws Exception{
        System.out.println("Enter Admin name: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String name = bf.readLine();

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String SERVICE_EXCHANGE_NAME = "service";
        String ADMIN_EXCHANGE_NAME = "admin";
        channel.exchangeDeclare(SERVICE_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(ADMIN_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // starting listeners
        ExchangeListener exchangeListener = new ExchangeListener("#", SERVICE_EXCHANGE_NAME);
        exchangeListener.run();

        while(true){
            System.out.println("Enter message: (A - agencies, T - transporters, L - all) \";\" and message content");
            String line = bf.readLine();
            String type = line.split(";")[0].trim();
            String message = "Admin - " + line.split(";")[1].trim();
            // break condition
            if ("exit".equals(line))
                break;
            if(!type.matches("[ATL]") || line.split(";").length < 2){
                System.out.println("Unknown service type (A - agencies, T - transporters, L - all) or empty message");
                continue;
            }
            if (type.equals("L"))
                type = "agencies.carriers";
            String key = "admin." + type;
            // publish
            channel.basicPublish(ADMIN_EXCHANGE_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent: " + message + " to " + key);

        }

    }
}
