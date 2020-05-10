# ProjectNseOne
ProjectNseOne will download Stock details from National Stock Exchange (NSE) through its API using Apache HTTPClient and write the output in JSon format.  

Usage :   
  For Windows OS : 
  Clone the project
  Run the following command : 
    # java -cp bin;lib/httpclient5-5.0.jar;lib/httpcore5-5.0.jar;lib/slf4j-api-1.7.25.jar com.touseef.nseone.Nse <OPTION>  
  Where OPTION :
    SYMBOL : You can mention any NSE Scrip Symbol like WIPRO, TCS, VEDL, etc.
    NIFTY50 : You can mention NIFTY50 to get top 50 Nifty stocks.
