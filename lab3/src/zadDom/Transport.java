package zadDom;

import zadDom.utils.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Transport {
    public static void main(String[] args) throws Exception{
        System.out.println("Enter transporter name: ");
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String transporterName = bf.readLine();
        String infoMessage = "Enter transport type (unfollowed by space, e. g. PL): " +
                "P - people transport, L - load transport, S - satellite transport";
        System.out.println(infoMessage);
        String types = bf.readLine();
        while(!types.matches("[PSL]{2}") || (types.charAt(0) == types.charAt(1))){
            System.out.println(infoMessage);
            types = bf.readLine();
        }
        PublicListener listener1 = new PublicListener(types.charAt(0), "services", transporterName);
        PublicListener listener2 = new PublicListener(types.charAt(1), "services", transporterName);
        ExchangeListener adminListener = new ExchangeListener("#.transports", "admin");
        listener1.run();
        listener2.run();
        adminListener.run();
    }
}
