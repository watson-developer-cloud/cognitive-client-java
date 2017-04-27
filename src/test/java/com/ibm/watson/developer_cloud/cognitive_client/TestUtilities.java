/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import org.junit.Test;

import com.ibm.watson.developer_cloud.cognitive_client.Util;

import static org.junit.Assert.assertTrue;


/**
 * @author ArunIyengar
 *
 */
public class TestUtilities {
    
    @Test
    public void test2() {
        String url = "https://www.ibm.com";
        String webPage = (Util.urlToString(url));
        System.out.println(webPage);
        assertTrue(webPage.length() > 0);
    }

}
