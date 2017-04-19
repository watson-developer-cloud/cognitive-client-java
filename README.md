# cognitive_client
This project contains several methods to make it easy to use Watson Developer Cloud services, particularly NaturalLanguageUnderstanding and AlchemyLanguage, to analyze multiple documents. Some of the key features include:
* Performing Web searches and feeding the results directly to the Watson Developer Cloud. The user can specify the number of documents to search for, as well as whether to search the entire Web or only news stories.
* Aggregating results across multiple documents. These documents might have been obtained via Web searches, although this does not have to be the case.
* Storing documents from Web searches locally so they can later be accessed quickly. These locally stored documents can easily be analyzed by the Watson Developer Cloud.
* Allowing all files in a directory to be easily analyzed using the Watson Developer Cloud with the results aggregated.
* Allowing analyzed data to be easily stored and retrieved from disk, as well as combined.
* Allowing directories of data analysis files to easily be read, written, and aggregated.
* Convenient methods to obtain data analysis values and statistics.
* Methods for aggregating quantities such as sentiment analysis values across several documents.

## Getting Started

The following classes should be imported:
~~~ java
import com.ibm.watson.cognitive_client.AggregateData;
import com.ibm.watson.cognitive_client.AlchemyClient;
import com.ibm.watson.cognitive_client.DataManager;
import com.ibm.watson.cognitive_client.NaturalLanguageClient;
import com.ibm.watson.cognitive_client.NaturalLanguageUnderstandingClient;
import com.ibm.watson.cognitive_client.Search.SearchType;
import com.ibm.watson.cognitive_client.Util.DataType;
~~~
There are two natural language services that can used with our cogitive client: Alchemy and Natural Language Understanding which are both available from IBM's  Watson Developer Cloud. The following creates a client for Alchemy:
~~~ java
        NaturalLanguageClient client = new AlchemyClient(apikey);
~~~
 The following creates a client for NaturalLanguageUnderstanding:
~~~ java
        NaturalLanguageUnderstandingClient client = new NaturalLanguageUnderstandingClient(userid, password);
~~~
In some cases, it is desirable to limit the number of entities, keywords, and concepts returned by a single call to NaturalLanguageUnderstanding. The following creates a client which limits the number of entities, keywords, and concepts returned by a single call to NaturalLanguageUnderstanding to 5:
~~~ java
        NaturalLanguageUnderstandingClient client = new NaturalLanguageUnderstandingClient(userid, password, 5);
~~~
The variable "client" defined using the constructors above can be used to analyze data using the Watson Developer Cloud as illustrated below.

The following calls the Watson Developer Cloud to get combined analysis including concepts, entities, keywords, and categories/taxonomies:
~~~ java
        AggregateData ad = client.analyzeData("https://en.wikipedia.org/wiki/IBM", DataType.URL, "IBM Wikipedia entry");
~~~
The 2nd parameter indicates whether the 1st parameter is a url, text data, or html data. The 3rd parameter is a string provided by the user which gives an explanation of the data set.

The following:
~~~ java
	AggregateData ad = DataManager.analyzeWebSearchResults ("IBM", 50, SearchType.GOOGLE_REGULAR, "IBM Google search", false, false, null, client);
~~~
performs a search on "IBM". The top 50 search results are analyzed by the Watson Developer Cloud with the results being stored in "ad". The 3rd parameter indicates the type of search. SearchType.GOOGLE_REGULAR indicates a regular Google search.  SearchType.GOOGLE_NEWS would indicate a Google search of just news stories. The 4th parameter is a string provided by the user which gives an explanation of the data set. The 5th parameter indicates whether "ad" should contain results from the analysis of all analyzed documents, or only the summary results. The fact that it is "false" indicates that "ad" will only contain a summary of the results, and not the analysis results from each individual document. The 6th parameter indicates whether or not the analysis results for each document should be stored on disk. If the 6th parameter is true, the 7th indicates the directory for storing the analysis results.

"ad" is serializable, with an implemented toString method. In order to see the contents of ad, use
~~~ java
        System.out.println(ad);
~~~

The following returns an ArrayList of the most frequently occurring disambiguated entities sorted by decreasing frequency of occurrence:
~~~ java
        ArrayList<Entry<String,Data>> sortedCounts = ad.getSortedValues(AggregateData.Type.DISAMBIGUATEDENTITY,AggregateData.DataType.COUNT);
~~~

The following returns an ArrayList of the most relevant keywords occurring sorted by decreasing sum of relevancy scores:
~~~ java
        ArrayList<Entry<String,Data>> sortedRelevancy = ad.getSortedValues(AggregateData.Type.KEYWORD,AggregateData.DataType.RELEVANCE);
~~~

The following writes "ad" to "filename" as binary data:
~~~ java
        ad.writeToFile(filename);
~~~

The following reads in "ad2" from the binary file "filename":
~~~ java
        AggregateData ad2 = AggregateData.readFromFile(filename);
~~~

It is also possible to analyze an entire directory of files. The following analyzes all files in "dir3" (but does not recursively search subdirectories):
~~~ java
        AggregateData ad = DataManager.analyzeDirectory("dir3", "IBM search results", true, true, "dir3-analysis", DataType.HTML, client);
~~~
The 2nd parameter is a string provided by the user which gives an explanation of the data set. The 3rd parameter indicates whether "ad" should contain results from the analysis of all analyzed documents, or only the summary results. The fact that it is "true" indicates that "ad" will contain the text data analyzed as well as the analysis results from each individual document, in addition to the summary results. The 4th parameter indicates whether or not the analysis results for each document should be stored on disk. Since the 4th parameter is true, the 5th parameter indicates the directory for storing the analysis results. The 6th parameter indicates whether the files being analyzed are text, html, or each contain a url representing a Web document to be analyzed.

Supposing the directory "dir3-analysis" contains files from analyzing text documents. The following method call aggregates all of the result files contained in this directory:
~~~ java
	AggregateData ad = DataManager.aggregateDirectoryStats("dir3-analysis", "IBM search results", false);
~~~
The 2nd parameter is a string provided by the user which gives an explanation of the data set. The 3rd parameter indicates whether analysis results read in from each file should be stored in the returned data structure. The fact that it is false means that "ad" will only contain a summary of the results, and not the actual results stored in each file.

In some cases, it is desirable to add the results from one AggregateData structure to another:
~~~ java
            data.combineData(newdata, true); // "data" and "newdata" are both of type "AggregateData"
~~~
This method call combines the data stored in "newdata" with the data stored in "data". The 2nd parameter indicates whether or not raw data stored in "newdata" should be added to raw data stored in "data". Since it is true, raw data stored in "newdata" is added to raw data stored in "data".

In some cases, it is desirable to perform a search and store all of the documents returned from the search on disk. That way, it is not necessary to re-fetch the documents from the Web when they need to be viewed more than once. In addition, the documents stored on disk can subsequently be passed to the Watson Developer Cloud to analyze their contents. This can be achieved via the following:
~~~ java
        Util.searchWeb("IBM", 15, SearchType.GOOGLE_REGULAR, "dir2", ".html");
~~~
In this example, a search for the first 15 responses to the query "IBM" is performed. The 3rd parameter indicates the type of search. SearchType.GOOGLE_REGULAR indicates a regular Google search. SearchType.GOOGLE_NEWS would indicate a Google search of just news stories. The files are stored in the directory "dir2". The last parameter is a suffix assigned to the file names. Each file name is the urlencoded version of the url appended with the last parameter.
