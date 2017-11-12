package edu.arizona.cs.practice;
/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An example of HttpClient can be customized to authenticate
 * preemptively using BASIC scheme.
 * <b>
 * Generally, preemptive authentication can be considered less
 * secure than a response to an authentication challenge
 * and therefore discouraged.
 */
public class EDSService implements IEDSService {
	private static final Logger logger = LoggerFactory.getLogger(EDSService.class);
    /* (non-Javadoc)
	 * @see edu.arizona.cs.practice.IEDSService#getData(java.lang.String, edu.arizona.cs.practice.EDSConfig)
	 */
 	@Override
	public EDSData getData(String netid, EDSConfig edsConfig) throws Exception {
    	String req;
    	req = "/people/" + netid;
    	String xml = queryEds(req, edsConfig);
    	String xml2 = "<?xml version='1.0' encoding='UTF-8'?>" // test data, for debugging
    			+ "<dsml:dsml xmlns:dsml='http://www.dsml.org/DSML'>"
    			+ "<directory-entries>"
	    			+ "<entry dn='uaid=11248051431,ou=People,dc=eds,dc=arizona,dc=edu'>"
		    			+ "<dsml:attr name='dateOfBirth'>"
		    			+ "<value>18570101</value>"
		    			+ "</dsml:attr>"
	    			+ "</entry>"
	    			+ "<entry dn='uaid=11248051431,ou=People,dc=eds,dc=arizona,dc=edu'>"
		    			+ "<dsml:attr name='weight'>"
		    			+ "<value>175</value>"
		    			+ "</dsml:attr>"
    			+ "</entry>"
    			+ "</directory-entries>"
    			+ "</dsml:dsml>";

    	logger.info("xml for {}: {}", req, xml);
    	
    	// parse the XML as a W3C Document
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
    	org.w3c.dom.Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
    	
    	XPath xpath = XPathFactory.newInstance().newXPath();
    	
    	xpath.setNamespaceContext(new NamespaceContext() {
    	    public String getNamespaceURI(String prefix) {
    	        if (prefix == null) throw new NullPointerException("Null prefix");
    	        else if ("dsml".equals(prefix)) return "http://www.dsml.org/DSML";
    	        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
    	        return XMLConstants.NULL_NS_URI;
    	    }

    	    // This method isn't necessary for XPath processing.
    	    public String getPrefix(String uri) {
    	        throw new UnsupportedOperationException();
    	    }

    	    // This method isn't necessary for XPath processing either.
    	    public Iterator getPrefixes(String uri) {
    	        throw new UnsupportedOperationException();
    	    }
    	});

    	String lastNamePath = "//dsml:attr[@name='sn']";
    	String lastName = ((String)xpath.evaluate(lastNamePath, document, XPathConstants.STRING)).trim();
    	logger.info("for {} lastName = '{}'", req, lastName);

    	String givenNamePath = "//dsml:attr[@name='givenName']";
    	String givenName = ((String)xpath.evaluate(givenNamePath, document, XPathConstants.STRING)).trim();
    	logger.info("for {} givenName = '{}'", req, givenName);

    	//String membersPath = "//dsml:attr[@name='isMemberOf']";
    	//String membersStr = (String)xpath.evaluate(membersPath, document, XPathConstants.STRING);
    	//System.out.println("classes = " + membersStr);
    	
    	NodeList memberships = (NodeList)xpath.evaluate("//dsml:attr[@name='isMemberOf']/dsml:value", document, XPathConstants.NODESET);
    	int membershipCount = memberships.getLength();
    	logger.info("for {} # memberships = '{}'", req, membershipCount);
    	
    	HashMap<String, Integer> membershipMap = edsConfig.getMembershipToCourseIdMap();
    	ArrayList<EDSCourse> courses = new ArrayList<EDSCourse>();
    	for (int i = 0; i < membershipCount; i++) {
    		Node membershipNode = memberships.item(i);

    		String membership = membershipNode.getTextContent();
    		
    		logger.debug("Considering membership {} for {}", membership, netid);
    		
    		if (membershipMap.containsKey(membership)) {
    			Integer courseId = membershipMap.get(membership);
    			logger.info(String.format("----------> %s is taking %s, id %d", netid, membership, courseId));

    			courses.add(new EDSCourse(membership, courseId, 
    					EDSUtil.encodeSection(EDSUtil.extractSection(membership))));
    		}
      }
    	
    	return new EDSData(netid, lastName, givenName, courses);
      }
    
    private String queryEds(String request, EDSConfig edsConfig) throws Exception {
    	// TODO cite source of code -- apache example...
        HttpHost target = new HttpHost("eds.arizona.edu", 443, "https");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(edsConfig.getLogin(), edsConfig.getPassword()));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        try {

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpGet httpget = new HttpGet(request);
            
            logger.info("Executing request " + httpget.getRequestLine() + " to target " + target);
            CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
            try {
                logger.info("response.getStatusLine() = {}", response.getStatusLine());
                //EntityUtils.consume(response.getEntity());
                String result = EntityUtils.toString(response.getEntity());
                return result;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

}