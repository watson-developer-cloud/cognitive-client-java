/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;


import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import com.ibm.watson.developer_cloud.cognitive_client.Search;
import com.ibm.watson.developer_cloud.cognitive_client.Search.SearchType;


/**
 * @author ArunIyengar
 *
 */
public class TestSearch {
    
    private static int numResults = 6;
    private static String query = "IBM";
    
    public static void testAllSearchEngines(String query, int numResults, boolean verbose) {
        for (SearchType type : SearchType.values()) {
            System.out.println("Search engine: " + type);
            ArrayList<String> results = Search.search(query, numResults, type, verbose);
            assertTrue("Search engine " + type + " did not return any results", results.size() > 0);
            System.out.println();            
        }
    }

    @Test
    public void testSearchEngines() {
        testAllSearchEngines(query, numResults, true);
    }
    
}
