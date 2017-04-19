/**
 * 
 */
package com.ibm.watson.cognitive_client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.ibm.watson.cognitive_client.Search.SearchType;
import com.ibm.watson.cognitive_client.Util.DataType;

/**
 * @author ArunIyengar
 *
 */
public class DataManager {
    
    private static final String FILESUFFIX = ".ana";

    /**
     * Analyze all files in a directory of character files and return a data structure with the analyzed
     * data aggregated. Optionally store analysis results of individual files on disk.
     * 
     * @param directory
     *      directory containing files to analyze
     * @param description
     *      user-provided description to be stored with the analyzed data
     * @param keepRawData
     *      indicates whether each file and analysis results of individual files should be kept in
     *      returned data structure
     * @param persistResults
     *      indicates whether analysis results for individual files should be stored on disk
     * @param outputDirectory
     *      if "persistResults == true", directory storing analysis results for individual files
     * @param type
     *      indicates if each input file contains text data, html data, or a url
     * @param client
     *      client to access natural language services
     * @return data structure containing the analysis results aggregated
     * 
     */
    public static AggregateData analyzeDirectory(String directory, String description, boolean keepRawData,
            boolean persistResults, String outputDirectory, DataType type, NaturalLanguageClient client) {
        File inputDir = new File(directory);
        AggregateData data = new AggregateData(description);
        if (persistResults) {
            Util.createDirectory(outputDirectory);
        }
        for (File file : inputDir.listFiles()) {
            String text = Util.fileToString(file.toString());
            AggregateData newdata = client.analyzeData(text, type, description);
            if (newdata == null) {
                continue;
            }
            if (keepRawData) {
                newdata.addDocument(text);
                newdata.addRawData();
            }
            data.combineData(newdata, keepRawData);
            if (persistResults) {
                newdata.writeToFile(createFullPath(analysisFileName(file.getName()), outputDirectory));
            }
        }
        return data;
    }
    
    
    /**
     * Perform a search on a query and return a data structure containing the combined analysis
     * of all documents found. Optionally store analysis results for individual documents found on disk. 
     * 
     * @param query
     *            query to pass to search engine
     * @param numResults
     *            number of documents to search for
     * @param searchType
     *            Type of search
     * @param description
     *      user-provided description to be stored with the analyzed data
     * @param keepRawData
     *      indicates whether analysis results of individual Web pages should be kept in returned data
     *      structure
     * @param persistResults
     *      indicates whether analysis results for individual Web pages should be stored on disk
     * @param dataDirectory
     *      if "persistResults == true", directory storing analysis results for individual Web pages
     * @param client
     *      client to access natural language services
     * @return data structure containing the analysis results aggregated
     * 
     * */
    public static AggregateData analyzeWebSearchResults(String query, int numResults, SearchType searchType,
            String description, boolean keepRawData, boolean persistResults, String dataDirectory,
            NaturalLanguageClient client) {
        AggregateData data = new AggregateData(description);
        
        ArrayList<String> urls = null;
        urls = Search.search(query, numResults, searchType, false);
        if (persistResults) {
            Util.createDirectory(dataDirectory);
        }
        int numAnalyzed = 0;
        for (String url : urls) {
            AggregateData newdata = client.analyzeData(url, DataType.URL, description);
            if (newdata == null) {
                continue;
            }
            numAnalyzed++;
            if (keepRawData) {
                newdata.addRawData();
            }
            data.combineData(newdata, keepRawData);
            if (persistResults) {
                String name;
                try {
                    name = URLEncoder.encode(url, Util.DEFAULT_ENCODING);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    continue;
                }
                newdata.writeToFile(createFullPath(analysisFileName(name), dataDirectory));
            }
        }
        System.out.println("CallService.analyzeWebSearchResults: " + urls.size() + " urls analyzed, "
                + numAnalyzed + " results obtained");
        return data;
    }
    
    /**
     * Read all files in a directory of data files and return a data structure with the data aggregated. 
     * 
     * @param directory
     *      directory containing data files to aggregate
     * @param description
     *      user-provided description to be stored with the aggregated data
     * @param keepRawData
     *      indicates whether analysis results read in from each file should be stored in the returned
     *      data structure
     * @return data structure containing the analysis results aggregated
     * 
     */
    public static AggregateData aggregateDirectoryStats(String directory, String description, boolean keepRawData) {
        File inputDir = new File(directory);
        AggregateData data = new AggregateData(description);
        for (File file : inputDir.listFiles()) {
            AggregateData storedData = AggregateData.readFromFile(file.toString());
            // System.out.println(storedData.toString());            
            data.combineData(storedData, keepRawData);
        }
        return data;
    }
    
    private static String analysisFileName(String baseName) {
        return baseName + FILESUFFIX;
    }
    
    private static String createFullPath(String filename, String directory) {
        if (directory == null) {
            return filename;
        }
        else {
            return directory + File.separator + filename;
        }
    }

}
