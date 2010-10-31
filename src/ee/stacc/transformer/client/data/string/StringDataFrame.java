package ee.stacc.transformer.client.data.string;

import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.DataPackage;
import ee.stacc.transformer.client.data.InstanceFactory;

/**
 * Data frame representing a string message.
 * @author Rainer Villido
 *
 */
public class StringDataFrame extends DataFrame {

	@Override
	public void updateSchema(String schemaTxt) {
		//Since String messages don't have schemas, then this method is not implemented
	}

	@Override
	public DataPackage generateDataPackageInstance() {
		return new StringDataPackage(this);
	}

	@Override
	public String getDataType() {
		return InstanceFactory.STRING_DATA_TYPE;
	}
}
