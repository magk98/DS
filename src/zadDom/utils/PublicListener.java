package zadDom.utils;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PublicListener implements Runnable{
    private String EXCHANGE_NAME;
    private char key;
    private Consumer consumer;
    private String listenerName;

    public PublicListener(char key, String EXCHANGE_NAME, String listenerName){
        this.key = key;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
        this.listenerName = listenerName;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(String.valueOf(this.key), true, false, false, null);
            channel.queueBind(String.valueOf(this.key), EXCHANGE_NAME, EXCHANGE_NAME + "." + key);
            channel.basicQos(1);

            this.consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received message: " + message);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String name = message.split(";")[0].trim();
                    String response = message + " done by: " + listenerName;
                    channel.basicPublish(EXCHANGE_NAME, "service.agency." + name, null, response.getBytes(StandardCharsets.UTF_8));
                }
            };

            channel.basicConsume(String.valueOf(this.key), true, consumer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
