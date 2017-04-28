/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.util.Scanner;

import org.junit.Test;

import com.ibm.watson.developer_cloud.cognitive_client.AlchemyClient;


/**
 * @author ArunIyengar
 *
 */
public class TestAlchemyClient {

    @Test
    public void testAlchemyLanguageClient() {
        System.out.println("Running tests for AlchemyLanguage");

        try (Scanner input = new Scanner(System.in)) {
            String apikey = Util.readInputString("Enter API key for AlchemyLanguage: ", input);
            NaturalLanguageClient client = new AlchemyClient(apikey);
            TestClients.runAllTests(client, input);
        }
    }
   
}
