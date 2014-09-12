package com.niraj.jsonparsing.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class JsonServicehandler {
	private static String response = null;
	
	public String userDetailRequest(String url){
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpRes = null;
			HttpEntity httpEntity = null;
			HttpGet httpGet = new HttpGet(url);
			
			httpRes = httpClient.execute(httpGet);
			httpEntity = httpRes.getEntity();
			response = EntityUtils.toString(httpEntity);
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			
			e.printStackTrace();
		}catch (IOException e) {
			
			e.printStackTrace();
		}
		return response;
	}

}
