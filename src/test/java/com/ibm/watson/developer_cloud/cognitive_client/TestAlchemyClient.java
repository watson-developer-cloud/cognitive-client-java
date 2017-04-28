/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.util.Scanner;

import com.ibm.watson.developer_cloud.cognitive_client.AlchemyClient;

/**
 * @author ArunIyengar
 * This class tests the Alchemy client. In order to run it, invoke the main method.
 */
public class TestAlchemyClient {

    public static void testAlchemyClient() {
        System.out.println("Running tests for AlchemyLanguage");
        try (Scanner input = new Scanner(System.in)) {
            String apikey = Util.readInputString("Enter API key for AlchemyLanguage: ", input);
            NaturalLanguageClient client = new AlchemyClient(apikey);
            TestClients.runAllTests(client, input);
        }
    }
 
    public static void main(String[] args) {
        testAlchemyClient();
    }

}
