/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.util.Scanner;

import org.junit.Test;

import com.ibm.watson.developer_cloud.cognitive_client.NaturalLanguageClient;
import com.ibm.watson.developer_cloud.cognitive_client.NaturalLanguageUnderstandingClient;

/**
 * @author ArunIyengar
 *
 */
public class TestNaturalLanguageClient {
    
    private final static int MAX_RESPONSES = 5; // limit on quantity of items returned by NaturalLanguageUnderstanding

    @Test
    public void testNaturalLanguageClient() {
        System.out.println("Running tests for NaturalLanguageUnderstanding");
        try (Scanner input = new Scanner(System.in)) {
            String username = Util.readInputString("Enter user name: ", input);
            String password = Util.readInputString("Enter password: ", input);
            NaturalLanguageClient client = new NaturalLanguageUnderstandingClient(username, password, MAX_RESPONSES);
            TestClients.runAllTests(client, input);
        }
    }

}
