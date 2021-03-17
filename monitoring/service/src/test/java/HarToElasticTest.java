

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import com.ic.monitoring.elastic.ElasticClient;
import processing.HarTransformMapper;
import processing.OnmsHarPollMetaData;


public class HarToElasticTest {
	
	String elasticUrl = "http://localhost:9200"; 
	String indexName = "onmshardata";
	String indexType = "onmshartype";
	ElasticClient elasticClient;
	
	@Before
	public void before() {
		elasticClient = new ElasticClient(elasticUrl,indexName, indexType);
	}
	
//        @Test
//        public void multipleTest() throws IOException {
//            for (int i = 0; i < 5; i++) {
//                test();
//}
//        }
        
	@Test
	public void test() throws JsonProcessingException, IOException {
		File inputFile = new File("./src/test/resources/newSolTest.har");
		System.out.println("reading inputFile from :" + inputFile.getAbsolutePath());
		
		ObjectMapper mapper = new ObjectMapper();
		
		HarTransformMapper harMapper = new HarTransformMapper();
		
		OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();

		JsonNode input = mapper.readTree(inputFile);
		
		ArrayNode jsonArrayData = harMapper.transform(input, metaData);
		
		System.out.println("transformed har into array of "+ jsonArrayData.size() + " objects :");
		
		elasticClient.sendBulkJsonArray(jsonArrayData);
		
	}
}
