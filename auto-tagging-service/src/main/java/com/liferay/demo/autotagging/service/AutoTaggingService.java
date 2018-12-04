package com.liferay.demo.autotagging.service;

//import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.demo.autotagging.api.api.AutoTaggingApi;
//import com.liferay.portal.kernel.model.BaseModelListener;

import com.liferay.demo.autotagging.service.config.AutoTaggingConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import java.io.IOException;
import java.util.*;

/**
 * @author jverweij
 */
@Component(
	immediate = true,
	property = {

	},
	configurationPid = "com.liferay.demo.autotagging.service.config.AutoTaggingConfiguration",
	service = AutoTaggingApi.class
)
public class AutoTaggingService implements AutoTaggingApi  {
	private static enum QUERYTYPE {ALERTER,TAGGER};

	@Override
	public String Ping() {
		String msg = null;
		try {
			RestHighLevelClient client = getClient();
			MainResponse response = client.info();
			client.close();
			msg = String.format("Hello, welcome to cluster %s", response.getClusterName());
			System.out.println("Ping: " + msg);
		}
		catch (IOException e)
		{
			msg = String.format("Error: %s",e.getMessage());
			e.printStackTrace();

		}
		finally {
			return msg;
		}
	}

	@Override
	public HashMap<String,String> List() {
		SearchRequest searchRequest = new SearchRequest(_autotaggingConfiguration.ElasticIndex());
		searchRequest.indicesOptions(searchRequest.indicesOptions().fromOptions(true, true, true, false));
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.source(sourceBuilder);

		RestHighLevelClient client = getClient();
		try {
			HashMap<String,String> results = new HashMap<>();
			System.out.println("get results...");
			SearchResponse response = client.search(searchRequest);
			System.out.println("Found " + response.getHits().totalHits);
			for (SearchHit hit : response.getHits().getHits()) {
				System.out.println("hit..");
				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
				String tag = (String) sourceAsMap.get("tag");
				results.put(hit.getId(),tag);
			}
			return results;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String Update(String id, String tagname, String businessrule) {
		System.out.println("Id: " + id);
		System.out.println("Tagname: " + tagname);
		System.out.println("Rule: " + businessrule);

		try {

			XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
			builder.startObject();
			{
				builder.field("query");
				{
					try (XContentParser p = XContentFactory.xContent(XContentType.JSON).createParser(NamedXContentRegistry.EMPTY, businessrule)) {
						builder.copyCurrentStructure(p);
					}
				}
				builder.field("querytype", QUERYTYPE.TAGGER);
				builder.field("tag", tagname);
			}
			builder.endObject();

			System.out.println(builder.string());

			Map<String, String> params = Collections.emptyMap();
			HttpEntity entity = new NStringEntity(builder.string(), ContentType.APPLICATION_JSON);
			RestHighLevelClient client = getClient();
			RestClient lowclient = client.getLowLevelClient();
			if (id.isEmpty()) {
				lowclient.performRequest("POST", "/" + _autotaggingConfiguration.ElasticIndex() + "/" + _autotaggingConfiguration.ElasticType(), params, entity);
			} else {
				lowclient.performRequest("POST", "/" + _autotaggingConfiguration.ElasticIndex() + "/" + _autotaggingConfiguration.ElasticType() + "/" + id, params, entity);
			}
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "update done";
	}

	@Override
	public String Delete(String id)  {
		String msg = "";

		try {
			RestHighLevelClient client = getClient();

			DeleteRequest request = new DeleteRequest(_autotaggingConfiguration.ElasticIndex(),_autotaggingConfiguration.ElasticType(),id);
			DeleteResponse response = client.delete(request);
			client.close();
			msg = String.format("Deleted reversequery with id %s", id);
		} catch (IOException e) {
			//e.printStackTrace();
			msg = String.format("Error: %s",e.getMessage());
		} finally {
			return msg;
		}
	}

	@Override
	public String[] Match(String doc) {
		/*GET /my-index/_search
		{
			"query" : {
			"percolate" : {
				"field" : "query",
						"document" : {
					"content" : "A new bonsai tree in the office"
				}
			}
		}
		}*/
		return null;
	}

	@Override
	public Map<String, Object> Get(String id) {

		//String[] includes = new String[]{"query", "tag"};
		//String[] excludes = Strings.EMPTY_ARRAY;
		FetchSourceContext fetchSourceContext = new FetchSourceContext(true);
		GetRequest request = new GetRequest(_autotaggingConfiguration.ElasticIndex(),_autotaggingConfiguration.ElasticType(),id);
		request.fetchSourceContext(fetchSourceContext);

		RestHighLevelClient client = getClient();
		try {
			GetResponse response = client.get(request);

	//TODO convert source string to json object (jackson)
			//JsonNode jsonNode = JsonLoader.fromString(response.getSourceAsString());
			System.out.println("source: " + response.getSource().get("query").toString());

			return response.getSourceAsMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_autotaggingConfiguration = ConfigurableUtil.createConfigurable(
				AutoTaggingConfiguration.class, properties);
		System.out.println("Loaded configuration settings..");

		this.Ping();
		this.Init();
	}

	private volatile AutoTaggingConfiguration _autotaggingConfiguration;

	@Override
	public void Init() {
		//will initialize reverse query
		//only needed once, or check at statup whether this is available or not
		System.out.println("Initializing Autotagger to set mappings");
		System.out.println("Now what??");

		RestHighLevelClient client = getClient();

		try {
			XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject();
			{
				builder.startObject("mappings");
				{
					builder.startObject(_autotaggingConfiguration.ElasticType());
					{
						builder.startObject("properties");
						{
							builder.startObject("query");
							{
								builder.field("type", "percolator");
							}
							builder.endObject();
							builder.startObject("message");
							{
								builder.field("type", "text");
							}
							builder.endObject();
							builder.startObject("querytype");
							{
								builder.field("type", "text");
							}
							builder.endObject();
							builder.startObject("tag");
							{
								builder.field("type", "text");
							}
							builder.endObject();
						}
						builder.endObject();
					}
					builder.endObject();
				}
				builder.endObject();
			}
			builder.endObject();

			String jsonString = builder.string();
			System.out.printf("Mapping Request %s\n",jsonString);

			RestClient lowclient = client.getLowLevelClient();
			Map<String, String> params = Collections.emptyMap();
			HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
			Response response = lowclient.performRequest("PUT","/" + _autotaggingConfiguration.ElasticIndex(),params,entity);
			client.close();

			this.Update("","bonsaisample","{\"match\": {\"message\": \"bonsai tree\"}}");


		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Initializing Autotagger finished succesfully");
	}

	private RestHighLevelClient getClient() {
		return this.getClient(_autotaggingConfiguration.ElasticHost(), _autotaggingConfiguration.ElasticPort(), _autotaggingConfiguration.ElasticProtocol());
	}

	private RestHighLevelClient getClient(String host, int port, String protocol) {
		System.out.printf("Using Elasticsearch %s://%s:%s\n",protocol,host,port);
		return new RestHighLevelClient(
				RestClient.builder(
						new HttpHost(host, port, protocol)));
	}
}