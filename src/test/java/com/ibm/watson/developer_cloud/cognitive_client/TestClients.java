/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.util.Scanner;

import com.ibm.watson.developer_cloud.cognitive_client.AggregateData;
import com.ibm.watson.developer_cloud.cognitive_client.DataManager;
import com.ibm.watson.developer_cloud.cognitive_client.NaturalLanguageClient;
import com.ibm.watson.developer_cloud.cognitive_client.Search.SearchType;
import com.ibm.watson.developer_cloud.cognitive_client.Util.DataType;

/**
 * @author ArunIyengar
 *
 */
class TestClients {
    
    private static void analyzeDirectory(NaturalLanguageClient client, Scanner input) {
        System.out.println("This test analyzes a directory containing html files and stores the analysis"
                + " results in another directory");
        String inputDirectory = Util.readInputString("Enter directory where html files are stored: ", input);
        String outputDirectory = Util.readInputString("Enter directory where output files are to be stored: ", input);
        AggregateData ad = DataManager.analyzeDirectory(inputDirectory, "sample analysis results", true, true,
                outputDirectory, DataType.HTML, client);
        System.out.println(ad);
    }
    
    private static void writeReadAnalyzedData(NaturalLanguageClient client, Scanner input) {
        String url = "https://www.britannica.com/technology/computer";
        String explanation = "the computer entry from Encyclopedia Brittanica";
        AggregateData ad = client.analyzeData(url, DataType.URL, explanation);
        if (ad == null) {
            return;
        }
        ad.addRawData();
        System.out.println("This test analyzes URL " + url + " representing " + explanation + ", stores"
                + " the results\nin a file, reads the results from the file, and outputs the results retrieved"
                + " from the file.");
        String filename = Util.readInputString("Enter name of file for storing results: ", input);
        ad.writeToFile(filename);
        AggregateData ad2 = AggregateData.readFromFile(filename);
        System.out.println(ad2);
    }

    private static void testAggregation(Scanner input) {
        System.out.println("This test aggregates data from a directory of data analysis files, such as the"
                + " directory where output files were stored in the earlier test.");
        String inputDirectory = Util.readInputString("Enter directory where data analysis files are stored: ",
                input);
        AggregateData ad = DataManager.aggregateDirectoryStats(inputDirectory, "Sample aggregated results",
                false); ;
        System.out.println(ad); 
        System.out.println(ad.getSortedValues(AggregateData.Type.KEYWORD,AggregateData.DataType.RELEVANCE));
    }
    
    private static void testSearch(NaturalLanguageClient client) {
        String query = "USA";
        System.out.println("This test returns disambiguated entities and keywords from a Gooogle search of"
                + " \"" + query + "\"");
        AggregateData ad = DataManager.analyzeWebSearchResults(query, 5, SearchType.GOOGLE_REGULAR, "Google search for " + query, false, false, null, client);
        System.out.println(ad);
        System.out.println(ad.getSortedValues(AggregateData.Type.DISAMBIGUATEDENTITY,AggregateData.DataType.COUNT));
        System.out.println(ad.getSortedValues(AggregateData.Type.KEYWORD,AggregateData.DataType.RELEVANCE));
    }

    static void runAllTests(NaturalLanguageClient client, Scanner input) {
        analyzeDirectory(client, input);
        writeReadAnalyzedData(client, input);
        testAggregation(input);
        testSearch(client);
    }

}
