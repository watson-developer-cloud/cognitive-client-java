/**
 * 
 */
package com.ibm.watson.cognitive_client;

import com.ibm.watson.cognitive_client.Util.DataType;
import com.ibm.watson.cognitive_client.Util.NaturalLanguageService;


/**
 * @author ArunIyengar
 *
 */
public interface NaturalLanguageClient {
    
    /**
     * Analyze and aggregate data from the text analysis service
     * 
     * @param text
     *      data itself, or URL containing data to be analyzed
     * @param type
     *      indicates whether the first parameter should be treated as raw text, html, or a url
     * @param description
     *      user-provided description to be stored with the analyzed data
     * @return analyzed and aggregated results from text analysis service, null if the text analysis
     *      service failed to produce valid results
     *      
     */
    AggregateData analyzeData(String text, DataType type, String description);
    
    /**
     * Return identity of natural language service
     * 
     * @return identity of natural language service
     * 
     * */
    NaturalLanguageService serviceType();
}
