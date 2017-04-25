/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.util.ArrayList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;





import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



/**
 * @author ArunIyengar
 * This class implements Web searching
 *
 */
public class Search {
    
    public enum SearchType {
        GOOGLE_REGULAR,
        GOOGLE_NEWS,
/*        BING_REGULAR,
        BING_NEWS,
        YAHOO
*/
    }

    private static final String GOOGLE_BASE_URL = "https://www.google.com/search?";
    private static final String QUERY_PREFIX = "q=";
    private static final String GOOGLE_SEARCH_URL = GOOGLE_BASE_URL + QUERY_PREFIX;
    private static final String NEWS = "hl=en&gl=us&tbm=nws&";
    private static final String GOOGLE_NEWS_URL = GOOGLE_BASE_URL + NEWS + QUERY_PREFIX;
    
/*    private static final String BING_BASE_URL = "https://www.bing.com/";
    private static final String BING_QUERY_PREFIX = "search?q=";
    private static final String BING_SEARCH_URL = BING_BASE_URL + BING_QUERY_PREFIX;
    private static final String BING_NEWS = "news/";
    private static final String BING_NEWS_URL = BING_BASE_URL + BING_NEWS + BING_QUERY_PREFIX;
    private static final String YAHOO_SEARCH_URL = "https://search.yahoo.com/search?p=";
*/
    
    
    /**
     * Perform a Google search on a query and return an ArrayList of URLs found. 
     * 
     * @param query
     *            query to pass to search engine
     * @param numResults
     *            number of documents to search for
     * @param searchType
     *            Type of search
     * @param verbose
     *            true to print out information for debugging purposes
     * 
     * @return ArrayList of urls found
     * 
     * */
    public static ArrayList<String> search(String query, int numResults, SearchType searchType, boolean verbose) {
        String encodedQuery;
        ArrayList<String> urls = new ArrayList<String>();
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8") + "&num=" + numResults;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return urls;
        }
        String searchURL = searchPrefixString(searchType) + encodedQuery;
        if (verbose) {
            System.out.println(searchURL);
        }
        Document doc;
        try {
            // Without proper User-Agent, we will get 403 error
            doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
        } catch (IOException e) {
            e.printStackTrace();
            return urls;
        }
                
        // If google search results HTML change the <h3 class="r" to <h3 class="r1"
        // we need to change below accordingly
        Elements results = doc.select("h3.r > a");
        for (Element result : results) {
            String linkHref = result.attr("href");
            String linkText = result.text();
            String url = result.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            try {
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                continue;
            }
            if (!url.startsWith("http")) {
                continue; // Ads/news/etc.
            }
            urls.add(url);
            if (verbose) {
                System.out.println("Text::" + linkText + ", URL (undecoded): " + 
                        linkHref.substring(6, linkHref.indexOf("&")));
                System.out.println("URL (decoded): " + url);
            }            
        }
        System.out.println("Search.search: " + urls.size() + " urls found");
        return urls;
    }

    /**
     * Return a string corresponding to the type of search. 
     * 
     * @param searchType
     *            Type of search
     * 
     * @return String corresponding to the type of search
     * 
     * */
    public static String searchPrefixString(SearchType searchType) {
        switch(searchType) {
        case GOOGLE_REGULAR:
            return GOOGLE_SEARCH_URL;
        default:
            return GOOGLE_NEWS_URL;
/*        case BING_REGULAR:
            return BING_SEARCH_URL;
        case BING_NEWS:
            return BING_NEWS_URL;
        default:
            return YAHOO_SEARCH_URL;
*/  
        }
        
    }

    
}
