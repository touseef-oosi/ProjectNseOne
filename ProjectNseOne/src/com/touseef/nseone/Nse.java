/**
 * Main Class to download Stock details from National Stock Exchange (NSE) through its API
 * using Apache HTTPClient and write the output in JSon format. 
 * 
 * Author : Touseef Ahmed Oosi
 * 
 * Version : 1.0.0
 * 
 * Date : 10-05-2020
 * 
 * Usage : 
 * For Windows OS :
 * 		Clone the project
 * 		Run the following command :
 * 			# java -cp bin;lib/httpclient5-5.0.jar;lib/httpcore5-5.0.jar;lib/slf4j-api-1.7.25.jar com.touseef.nseone.Nse <OPTION>
 * 		Where OPTION : 
 * 			SYMBOL : You can mention any NSE Scrip Symbol like WIPRO, TCS, VEDL, etc.
 * 			NIFTY50 : You can mention NIFTY50 to get top 50 Nifty stocks.
 * 
 */
package com.touseef.nseone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class Nse {
	
	private static final String NSE_BASE_URL = "https://www1.nseindia.com/";

	private static final String NSE_GetQuote = "live_market/dynaContent/live_watch/get_quote/GetQuote.jsp?symbol=";
	
	private static final String NSE_Nifty50 = "live_market/dynaContent/live_watch/stock_watch/niftyStockWatch.json";

	private static final String TEMP_FILENAME = "tmpFile.txt";

	private static String SYMBOL = "";

	public static void main(String args[]) {

		long startTime = System.currentTimeMillis();

		if (args.length == 0) {
			System.out.println("Didn't have any SYMBOL to proceed. ");
			System.out.println("Use as follows, ");
			System.out.println("java -classpath <RequiredJARs> com.touseef.nseone.Nse [<SYMBOL>|<NIFTY50>]");

			System.exit(1);
		}

		Nse nse = new Nse();
		
		// TODO Validate Symbol
		SYMBOL = args[0].toUpperCase();
		
		if (SYMBOL.equalsIgnoreCase("NIFTY50")) {
			System.out.println("Looking for Top 50 from NIFTY\n"); 
			nse.downloadJson(NSE_BASE_URL + NSE_Nifty50, "NIFTY50");
		} else {
			System.out.println("Looking for stock details of : " + SYMBOL);
			nse.downloadJson(NSE_BASE_URL + NSE_GetQuote + SYMBOL, "SYMBOL");
		}

		System.out.println("\nTotal Time took : " + (System.currentTimeMillis() - startTime) + " milli seconds. ");
	}


	/**
	 * Method to download the NSE API URL from NSE. 
	 * We can't access URL directly due to WebBot restriction. Hence, downloading using Apache HTTPClient
	 * 
	 * @param urlString
	 * 				URL to download
	 * 
	 * @param type
	 * 				Type of URL sent
	 */
	public void downloadJson(String urlString, String type) {
		File file = new File(TEMP_FILENAME);
		String userAgent = "-";
		
		CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(userAgent).build();
		HttpGet httpget = new HttpGet(urlString);
		httpget.addHeader("Accept-Language", "en-US");
		httpget.addHeader("Cookie", "");

		// System.out.println("Executing request " + httpget.getRequestUri());
		try (CloseableHttpResponse response = httpclient.execute(httpget)) {
			// TODO Handle response code
			int responseCode = response.getCode();
			switch (responseCode) {
			case 200:
			case 201:
				String body = EntityUtils.toString(response.getEntity());

				if (type.equalsIgnoreCase("Nifty50")) {
					System.out.println("\nOutput : ");
					System.out.println(body);
					return;
				}
				
				// Delete the temp file, if already exists
				deleteFileIfExists();

				// Write the data into temp file.
				Files.write(file.toPath(), body.getBytes());

				// Get only Trade Data in JSon Format
				try (BufferedReader reader = new BufferedReader(new FileReader(TEMP_FILENAME))) {
					String eachLine;
					while ((eachLine = reader.readLine()) != null) {
						if (eachLine.startsWith("tradedDate", 2)) {
							System.out.println("\nOutput : ");
							System.out.println(eachLine);
						}
					}
				}

				break;

			case 400:
			case 401:
			case 404:
				System.out.println("Error occurred while requesting. Response Code : " + responseCode);
				break;
			default : 
				System.out.println("This response code is not handled. Contact Admin to add. Response Code " +responseCode);
			}
		} catch (ParseException e) {
			System.out.println("ParseException in Nse : ");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in Nse : ");
			e.printStackTrace();
		} finally {
			deleteFileIfExists();
		}
	}

	/**
	 * Method to delete the temp file, if already exists. 
	 * 
	 */
	public void deleteFileIfExists() {
		try {
			Files.deleteIfExists(Paths.get(TEMP_FILENAME));
		} catch (IOException e) {
			System.out.println("IOException while file deleting.");
			e.printStackTrace();
		}
	}
}
