package com.ic.monitoring.elastic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.searchbox.action.BulkableAction;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.indices.DeleteIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElasticClient {
    
static final Logger LOG = LoggerFactory.getLogger(ElasticClient.class);
	
	private String elasticUrl = "http://es01:9200";
	private String indexType;
	private String indexName;
	private final JestClient client;
	
	public ElasticClient(String elasticUrl, String indexName, String indexType) {
		this.elasticUrl = elasticUrl;
		this.indexType = indexType;
		this.indexName = indexName;
		
		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig
		                       .Builder(elasticUrl)
		                       .multiThreaded(true)
		                       .build());
		client = factory.getObject();
		
	}

	public void sendBulkJsonArray(ArrayNode jsonArrayData ) {
		
		// Construct Bulk request from jsonArrayData
		List<BulkableAction> actions = new ArrayList<BulkableAction>();

		for( JsonNode data : jsonArrayData) {
			actions.add(new Index.Builder(data.toString()).build() );
		}

		//BulkableAction actions;
		Bulk bulk = new Bulk.Builder()
		                .defaultIndex(indexName)
		                .defaultType(indexType)
		                .addAction(actions)
		                .build();

		try {
			client.execute(bulk);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteIndex() {
		try {
                        LOG.debug("*** deleting index: " + indexName);			
			client.execute(new DeleteIndex.Builder(indexName).build());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void stop() {
		if (client!=null)
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
