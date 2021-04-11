
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.FileUtils;
import processing.HarTransformMapper;
import processing.OnmsHarPollMetaData;

public class HarToElasticTest {

    String elasticUrl = "http://localhost:9200";
    String indexName = "onmshardata";
    String indexType = "onmshartype";
    ElasticClient elasticClient;
    InputStream is;

    @Before
    public void before() {
        elasticClient = new ElasticClient(elasticUrl, indexName, indexType);
    }

    @Test
    public void test() throws JsonProcessingException, IOException {

        InputStream initialStream = FileUtils.openInputStream(new File("./src/test/resources/newSolTest.har"));
        File targetFile = new File("./src/test/resources/newSolTest.tmp");
        FileUtils.copyInputStreamToFile(initialStream, targetFile);

//      File inputFile = this.getClass().getClassLoader().getResource("newSolTest.har");
//	File inputFile = new File("./src/test/resources/newSolTest.har");
        System.out.println("reading inputFile from :" + targetFile.getAbsolutePath());

        ObjectMapper mapper = new ObjectMapper();

        HarTransformMapper harMapper = new HarTransformMapper();

        OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();

        JsonNode input = mapper.readTree(targetFile);

        ArrayNode jsonArrayData = harMapper.transform(input, metaData);

        System.out.println("transformed har into array of " + jsonArrayData.size() + " objects :");

        elasticClient.sendBulkJsonArray(jsonArrayData);

    }
}
