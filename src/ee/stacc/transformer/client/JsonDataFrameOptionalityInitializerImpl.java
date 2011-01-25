package ee.stacc.transformer.client;

import com.google.gwt.json.client.JSONObject;
import ee.stacc.transformer.client.data.json.JsonDataFrame;
import ee.stacc.transformer.client.mapping.Mapping;
import ee.stacc.transformer.client.util.JsonSchemaUtil;

public class JsonDataFrameOptionalityInitializerImpl implements JsonDataFrameOptionalityInitializer {
  @Override
  public void initDataFrameOptionality(JsonDataFrame jsonDataFrame) {
    for (Mapping mapping : jsonDataFrame.getMappingsSet()) {
      initMappingOptionality(mapping, jsonDataFrame.getJsonSchema());
    }
  }

  private void initMappingOptionality(Mapping mapping, JSONObject jsonSchema) {
    if (jsonSchema != null) {
      JSONObject mappingSchemaElement = JsonSchemaUtil.findElementByPath(jsonSchema, mapping.getPath());
      mapping.setOptional(!JsonSchemaUtil.isSchemaElementRequired(mappingSchemaElement));
    }
  }
}
