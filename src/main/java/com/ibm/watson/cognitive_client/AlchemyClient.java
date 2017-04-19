/**
 * 
 */
package com.ibm.watson.cognitive_client;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.watson.cognitive_client.Util.DataType;
import com.ibm.watson.cognitive_client.Util.NaturalLanguageService;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.CombinedResults;

/**
 * @author ArunIyengar
 *
 */
public class AlchemyClient implements NaturalLanguageClient {
    
    private static final String CONCEPTS = "concepts";
    private static final String COUNT = "count";
    private static final String DISAM = "disambiguated";
    private static final String ENT = "entities";
    private static final String KEYWORDS = "keywords";
    private static final String LABEL = "label";
    private static final String NAME = "name";
    private static final String REL = "relevance";
    private static final String SCORE = "score";
    private static final String SENT = "sentiment";
    private static final String TAX = "taxonomy";
    private static final String TEXT = "text";
    
    private AlchemyLanguage service = new AlchemyLanguage();

    /**
     * Constructor
     * 
     * @param apiKey API key for AlchemyLanguage service
     */
    public AlchemyClient(String apiKey) {
        service.setApiKey(apiKey);
    }

    /**
     * Return identity of natural language service
     * 
     * @return identity of natural language service
     * 
     * */
    @Override
    public NaturalLanguageService serviceType() {
        return NaturalLanguageService.ALCHEMY;
    }
    
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
    @Override
    public AggregateData analyzeData(String text, DataType type, String description) {
        String results = getCombinedResultsString(text, type);
        if (results == null) {
            return null;
        }
        else {
            return analyzeData(results, description);
        }
    }
    
    /**
     * Calls the text analysis services to analyze textual data
     * 
     * @param text
     *      data itself, or URL containing data to be analyzed
     * @param type
     *      indicates whether the first parameter should be treated as raw text, html, or a url
     * @return string containing results from the analysis services
     *      
     */
    String getCombinedResultsString(String text, DataType type) {
        Map<String,Object> params = new HashMap<String, Object>();
        switch(type) {
            case HTML:
                params.put(AlchemyLanguage.HTML, text);
                break;
            case URL:
                params.put(AlchemyLanguage.URL, text);
                break;
            default:
                params.put(AlchemyLanguage.TEXT, text);
                break;
        }
        params.put(AlchemyLanguage.SENTIMENT, 1);
        CombinedResults combinedResults = null;
        try {
            combinedResults = service.getCombinedResults(params).execute();
        }
        catch (com.ibm.watson.developer_cloud.service.exception.BadRequestException e) {
            e.printStackTrace();
            return null;
        }
        String resultsString = combinedResults.toString();
        return resultsString;
    }
    
    /**
     * Analyze and aggregate data from the text analysis services
     * 
     * @param resultsString
     *      data from the text analysis services
     * @param description
     *      user-provided description to be stored with the analyzed data
     * @return analyzed and aggregated results from text analysis
     * 
     */
    static AggregateData analyzeData(String resultsString, String description) {
        AggregateData data = new AggregateData(description);
        try {
            JSONObject json = new JSONObject(resultsString);
            if (json.has(KEYWORDS)) {
                JSONArray jsonarray = json.getJSONArray(KEYWORDS);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject keyjson = jsonarray.getJSONObject(i);
                    String word = keyjson.getString(TEXT);
                    double rel = keyjson.getDouble(REL);
                    Double score = 0.0;
                    if (keyjson.has(SENT)) {
                        // String sent = keyjson.getString(SENT);
                        JSONObject sentJson = keyjson.getJSONObject(SENT);
                        if (sentJson.has(SCORE)) {
                            score = sentJson.getDouble(SCORE);
                        }
                    }
                    data.addData(word, 1, rel, score, AggregateData.Type.KEYWORD);
                }
            }
            
            if (json.has(CONCEPTS)) {
                JSONArray jsonarray = json.getJSONArray(CONCEPTS);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject keyjson = jsonarray.getJSONObject(i);
                    String word = keyjson.getString(TEXT);
                    double rel = keyjson.getDouble(REL);
                    data.addData(word, 1, rel, 0.0, AggregateData.Type.CONCEPT);
                }
            }

            if (json.has(TAX)) {
                JSONArray jsonarray = json.getJSONArray(TAX);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject keyjson = jsonarray.getJSONObject(i);
                    String word = keyjson.getString(LABEL);
                    double score = keyjson.getDouble(SCORE);
                    data.addData(word, 1, 0.0, score, AggregateData.Type.TAXONOMY);
                }
            }
            
            if (json.has(ENT)) {
                JSONArray jsonarray = json.getJSONArray(ENT);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject keyjson = jsonarray.getJSONObject(i);
                    String word;
                    boolean disambiguated;
                    if (keyjson.has(DISAM)) {
                        JSONObject disamJson = keyjson.getJSONObject(DISAM);
                        word = disamJson.getString(NAME);
                        disambiguated = true;
                    }
                    else {
                        word = keyjson.getString(TEXT); 
                        disambiguated = false;
                    }
                    double rel = keyjson.getDouble(REL);
                    Double score = 0.0;
                    if (keyjson.has(SENT)) {
                        JSONObject sentJson = keyjson.getJSONObject(SENT);
                        if (sentJson.has(SCORE)) {
                            score = sentJson.getDouble(SCORE);
                        }
                    }
                    int count = keyjson.getInt(COUNT);
                    if (disambiguated) {
                        data.addData(word, count, rel, score, AggregateData.Type.DISAMBIGUATEDENTITY);   
                    }
                    else {
                        data.addData(word, count, rel, score, AggregateData.Type.ENTITYAMBIGUOUS);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        data.setAnalysisResults(resultsString);
        return data;
    }


}
