package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ProxyStreamLambda implements RequestStreamHandler {

    JSONParser parser = new JSONParser();
    
    @SuppressWarnings("unchecked")
	@Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

     
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        String responseCode = "200";
        String proxy = null;
        String param1 = null;
        String param2 = null;

		try {
	        JSONObject event = (JSONObject) parser.parse(reader);
	        
			if (event.get("pathParameters") != null) {
                JSONObject pps = (JSONObject) event.get("pathParameters");
                if ( pps.get("proxy") != null) {
                    proxy = (String)pps.get("proxy");
                }
            }
			
	        if (event.get("queryStringParameters") != null) {
	            JSONObject qps = (JSONObject) event.get("queryStringParameters");
	               if ( qps.get("param1") != null)
	               {
	                   param1 = (String)qps.get("param1");
	               }
	        }
		
	        if (event.get("queryStringParameters") != null) {
	            JSONObject qps = (JSONObject) event.get("queryStringParameters");
	               if ( qps.get("param1") != null)
	               {
	                   param2 = (String)qps.get("param2");
	               }
	        }
	        
			if (event.get("headers") != null) {
                //JSONObject hps = (JSONObject) event.get("headers");
            }

		    // Implement your logic here
		    int output = 0;
		    if (proxy.equals("sum"))
		    {
		    	try {
		    		output = sum(Integer.parseInt(param1), Integer.parseInt(param2));
		    	} catch(Exception e) {
		    		logger.log(e.getMessage());
		    	}
		    }
		    else if (proxy.equals("subtract"))
		    {
		    	try {
		    		output = subtract(Integer.parseInt(param1), Integer.parseInt(param2));
		    	} catch (Exception e) {
		    		logger.log(e.getMessage());
		    	}
		    } else if (proxy.equals("multiply"))
		    {
		    	try {
		    		output = multiply(Integer.parseInt(param1), Integer.parseInt(param2));
		    	} catch (Exception e) {
		    		logger.log(e.getMessage());
		    	}
		    	
		    } else if (proxy.equals("divide"))
		    {
		    	try {
		    		output = divide(Integer.parseInt(param1), Integer.parseInt(param2));
		    	} catch (Exception e) {
		    		logger.log(e.getMessage());
		    	}

		    }
		    
		    
			responseJson.put("isBase64Encoded", false);
			responseJson.put("statusCode", responseCode);
		
			JSONObject headerJson = new JSONObject();
			responseJson.put("headers", headerJson);
			
			JSONObject responseBody = new JSONObject();
			responseBody.put("message", event.toString());
			responseBody.put("message", "Output is " + output);
				
			responseJson.put("body", responseBody.toString());
		
		} catch (ParseException pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
		}

		logger.log(responseJson.toString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();

	}       
    
    public int sum(int a, int b)
    {
    	return a+b;
    }
    
    public int subtract(int a, int b)
    {
    	return a-b;
    }
    
    public int multiply(int a, int b)
    {
    	return a*b;
    }
    
    public int divide(int a, int b)
    {
    	return a/b;
    }
}
