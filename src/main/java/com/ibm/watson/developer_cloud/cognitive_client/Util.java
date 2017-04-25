/**
 * 
 */
package com.ibm.watson.developer_cloud.cognitive_client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.ibm.watson.developer_cloud.cognitive_client.Search.SearchType;

/**
 * @author ArunIyengar
 * 
 * This class provides general utility methods for clients
 */
public class Util {
    
    public enum DataType {
        HTML,
        TEXT,
        URL
    }
    
    public enum NaturalLanguageService {
        ALCHEMY,
        NATURAL_LANGUAGE_UNDERSTANDING
    }

    public static final String DEFAULT_ENCODING = "UTF-8";
    
    private static final int STRING_SIZE_INITIAL = 10000;
    
    /**
     * Return a string from a text file
     * 
     * @param filename
     *            string denoting full path to file
     * @return text from file
     * 
     * */
    public static String fileToString(String filename) {
        StringBuilder sb = new StringBuilder(STRING_SIZE_INITIAL);
        try {
            try (
                    FileInputStream fis = new FileInputStream(filename);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();           
        }
        return sb.toString();
    }
    
    /**
     * Return a string from a text file
     * 
     * @param path
     *            java Path object representing file
     * @return text from file
     * 
     * */
    public static String fileToString(Path path) {
        return fileToString(path.toString());
    }
    
    /**
     * Fetch a Web page, return it as a string
     * 
     * @param urlString
     *            URL for the Web page
     * @return string containing the Web page
     * 
     * */
    public static String urlToString(String urlString) {
        StringBuilder sb = new StringBuilder(STRING_SIZE_INITIAL);
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();           
        }

        return sb.toString();
        
    }
    
    /**
     * Store a string in a file
     * 
     * @param string
     *            string to store
     * @param filename
     *            string denoting full path to file
     * 
     * */
    public static void stringToFile(String string, String fileName) {
        try {
            try(PrintWriter out = new PrintWriter(fileName)  ){
                out.println(string);
            }
        }
        catch (Exception e) {
            e.printStackTrace();           
        }
    }
    
    /**
     * Store a string in a file
     * 
     * @param string
     *            string to store
     * @param path
     *            java Path object representing file
     * 
     * */
    public static void stringToFile(String string, Path path) {
        stringToFile(string, path.toString());
    }
    
    /**
     * Store a Web page in a file. The file name is the urlencoded version of the url appended with the
     * fileSuffix parameter
     * 
     * @param urlString
     *            url of the Web page to be stored
     * @param directory
     *            directory for storing the file. If the directory does not exist, an attempt is made to
     *            create it
     * @param fileSuffix
     *            suffix of file to be created, eg ".html"
     * 
     * */
    public static void urlToFile(String urlString, String directory, String fileSuffix) {
        createDirectory(directory);
        storeWebPage(urlString, directory, fileSuffix);
    }
    
    static void createDirectory(String directory) {
        if (directory != null) {
            try {
                Path directoryPath = Paths.get(directory);
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
    }
    
    private static void storeWebPage(String urlString, String directory, String fileSuffix) {
        String fileName = null;
        try {
            fileName = URLEncoder.encode(urlString, DEFAULT_ENCODING) + fileSuffix;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }       
        if (directory != null) {
            fileName = directory + File.separator + fileName;
        }
        stringToFile(urlToString(urlString), fileName);
    }
    
    /**
     * Store a Web page in a file. The file name is the urlencoded version of the url appended with the
     * fileSuffix parameter
     * 
     * @param urlString
     *            url of the Web page to be stored
     * @param directory
     *            java Path object representing directory for storing the file. If the directory does not
     *            exist, an attempt is made to create it
     * @param fileSuffix
     *            suffix of file to be created, eg ".html"
     * 
     * */
    public static void urlToFile(String urlString, Path directory, String fileSuffix) {
        urlToFile(urlString, directory.toString(), fileSuffix);
    }
    
    /**
     * Perform a Google search on a query, store each returned Web document in a separate file in a
     * directory. Each file name is the urlencoded version of the url appended with the fileSuffix
     * parameter
     * 
     * @param query
     *            query to pass to search engine
     * @param numResults
     *            number of documents to search for
     * @param searchType
     *            Type of search
     * @param directory
     *            directory for storing the file. If the directory does not exist, an attempt is made to
     *            create it
     * @param fileSuffix
     *            suffix for files storing Web pages, eg ".html"
     * 
     * */
    public static void searchWeb(String query, int numResults, SearchType searchType, String directory,
            String fileSuffix) {
        ArrayList<String> urls = null;
        urls = Search.search(query, numResults, searchType, false);
        createDirectory(directory);
        for (String url : urls) {
            storeWebPage(url, directory, fileSuffix);            
        }
    }
    
    /**
     * Perform an unchecked cast while suppressing warnings
     * 
     * @param obj
     *            object to be cast
     * @param <T>
     *            type of cast object
     * @return cast object
     * 
     * */
    @SuppressWarnings({"unchecked"})
    static <T> T uncheckedCast(Object obj) {
        return (T) obj;
    }

    
    /**
     * Convert an Integer to an int, returning 0 for a null Integer
     * 
     * @param i
     *            Integer
     * @return i if i != null, 0 if i == null
     * 
     * */
    public static int unboxInteger(Integer i) {
        if (i == null) {
            return 0;
        }
        else {
            return i;
        }
    }

    /**
     * Convert a Double to a double, returning 0.0 for a null Double
     * 
     * @param d
     *            Double
     * @return d if d != null, 0.0 if d == null
     * 
     * */
    public static double unboxDouble(Double d) {
        if (d == null) {
            return 0.0;
        }
        else {
            return d;
        }
    }

    // Store byte array in a file
    static void byteArrayToFile(byte[] data, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName, false)) {
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read byte array from a file
    static byte[] fileToByteArray(String fileName) {
        Path path = Paths.get(fileName);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
