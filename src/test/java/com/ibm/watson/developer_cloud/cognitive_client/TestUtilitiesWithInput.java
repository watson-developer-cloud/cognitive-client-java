/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.util.Scanner;

import com.ibm.watson.developer_cloud.cognitive_client.Search.SearchType;
import com.ibm.watson.developer_cloud.cognitive_client.Util;

/**
 * @author ArunIyengar
 *
 * These tests require users to provide input paramaeters. The tests are run by running the main method.
 */
public class TestUtilitiesWithInput {
    
    public static void storeWebPages() {
        try (Scanner input = new Scanner(System.in)) {
            String url1 = "https://www.ibm.com";
            String url2 = "https://www.google.com";
            System.out.println("The first test stores Web pages corresponding to " + url1 + " and " + url2 +
                    " in files. The first file has no suffix.\n" + "The second file has suffix: .html");
            String directory = Util.readInputString("Enter directory where files are to be stored: ", input);
            Util.urlToFile(url1, directory, "");
            Util.urlToFile(url2, directory, ".html");
            String query = "IBM";
            int numResults = 5;
            System.out.println("The second test does a Google search on \"" + query + "\", finds the first " +
                    numResults + " query results, and stores each result in a separate file.");
            directory = Util.readInputString("Enter directory where files are to be stored: ", input);
            Util.searchWeb(query, numResults, SearchType.GOOGLE_REGULAR, directory, ".html");
        }
    }
    
    public static void main(String[] args) {
        storeWebPages();
    }
    
}