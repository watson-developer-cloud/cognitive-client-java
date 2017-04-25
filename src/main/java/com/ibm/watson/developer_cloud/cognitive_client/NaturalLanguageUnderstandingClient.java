/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import com.ibm.watson.developer_cloud.cognitive_client.Util.DataType;
import com.ibm.watson.developer_cloud.cognitive_client.Util.NaturalLanguageService;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions.Builder;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.ConceptsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.DisambiguationResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.FeatureSentimentResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;



/**
 * @author ArunIyengar
 *
 *  This class makes API calls to the NaturalLanguageUnderstanding service.
 *  
 */

public class NaturalLanguageUnderstandingClient implements NaturalLanguageClient {
    
    
    private NaturalLanguageUnderstanding service = 
            new NaturalLanguageUnderstanding(NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27);    
    private int maxResponses = Integer.MAX_VALUE;  // maximum # of responses to return, default value
    
    /**
     * Constructor.
     * 
     * @param username User name for NaturalLanguageUnderstanding service
     * @param password Password for NaturalLanguageUnderstanding service
     */
    public NaturalLanguageUnderstandingClient(String username, String password) {
        service.setUsernameAndPassword(username, password);
    }

    /**
     * Constructor.
     * 
     * @param username User name for NaturalLanguageUnderstanding service
     * @param password Password for NaturalLanguageUnderstanding service
     * @param maxItems Maximum number of concepts, entities, and keywords returned by each service call
     */
    public NaturalLanguageUnderstandingClient(String username, String password, int maxItems) {
        service.setUsernameAndPassword(username, password);
        maxResponses = maxItems;
    }

    
    /**
     * Return identity of natural language service
     * 
     * @return identity of natural language service
     * 
     * */
    @Override
    public NaturalLanguageService serviceType() {
        return NaturalLanguageService.NATURAL_LANGUAGE_UNDERSTANDING;
    }
    
    /**
     * Sets the maximum number of concepts, entities, and keywords returned by each service call
     * @param newValue new value for the parameter
     */
    public void setMaxResponses(int newValue) {
        maxResponses = newValue;
    }

    /**
     * Returns the maximum number of concepts, entities, and keywords returned by each service call
     * @return parameter described above
     */
    public int getMaxResponses() {
        return maxResponses;
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
        ConceptsOptions concepts = new ConceptsOptions.Builder()
            .limit(maxResponses)
            .build();    
        EntitiesOptions entities = new EntitiesOptions.Builder()
            .emotion(false)
            .limit(maxResponses)
            .sentiment(true)
            .build();
        KeywordsOptions keywords = new KeywordsOptions.Builder()
            .emotion(false)
            .limit(maxResponses)
            .sentiment(true)
            .build();
        Features features = new Features.Builder()
            .categories(new CategoriesOptions())
            .concepts(concepts)
            .entities(entities)
            .keywords(keywords)
            .build();
        Builder builder = new AnalyzeOptions.Builder()
            .features(features)
            .returnAnalyzedText(true);
        switch(type) {
        case HTML:
            builder.html(text);
            break;
        case URL:
            builder.url(text);
            break;
        default:
            builder.text(text);
            break;
        }
        AnalyzeOptions parameters = builder.build();
        AnalysisResults results = service.analyze(parameters).execute();
        AggregateData data = new AggregateData(description);
        for (CategoriesResult result : results.getCategories()) {
            String label = result.getLabel();
            double score = Util.unboxDouble(result.getScore());
            data.addData(label, 1, 0.0, score, AggregateData.Type.TAXONOMY);
        }
        for (ConceptsResult concept : results.getConcepts()) {
            double relevance = Util.unboxDouble(concept.getRelevance());
            String dbpedia = concept.getDbpediaResource();
            data.addData(dbpedia, 1, relevance, 0.0, AggregateData.Type.CONCEPT);
        }
        for (EntitiesResult result : results.getEntities()) {
            int count = Util.unboxInteger(result.getCount());
            double relevance = Util.unboxDouble(result.getRelevance());
            String text2 = result.getText();
            FeatureSentimentResults sentiment = result.getSentiment();
            double sentimentScore;
            if (sentiment == null) {
                sentimentScore = 0.0;
            }
            else {
                sentimentScore = Util.unboxDouble(sentiment.getScore());                
            }        
            DisambiguationResult disamResult = result.getDisambiguation();
            if (disamResult != null) {
                String disamName = disamResult.getName();
                data.addData(disamName, count, relevance, sentimentScore, AggregateData.Type.DISAMBIGUATEDENTITY);   
            }
            else {
                data.addData(text2, count, relevance, sentimentScore, AggregateData.Type.ENTITYAMBIGUOUS);
            }
        }
        for (KeywordsResult result : results.getKeywords()) {
            double relevance = Util.unboxDouble(result.getRelevance());
            String text2 = result.getText();
            FeatureSentimentResults sentiment = result.getSentiment();
            double sentimentScore;
            if (sentiment == null) {
                sentimentScore = 0.0;
            }
            else {
                sentimentScore = Util.unboxDouble(sentiment.getScore());                
            }
            data.addData(text2, 1, relevance, sentimentScore, AggregateData.Type.KEYWORD);
        }
        return data;
    }
    
}
