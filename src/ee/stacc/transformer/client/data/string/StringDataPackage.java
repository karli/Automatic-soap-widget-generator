package ee.stacc.transformer.client.data.string;

import com.google.gwt.core.client.GWT;

import ee.stacc.transformer.client.data.AtomicDataValue;
import ee.stacc.transformer.client.data.DataFrame;
import ee.stacc.transformer.client.data.DataPackage;
import ee.stacc.transformer.client.data.GeneratedDataValue;

/**
 * Data package for generating messages in String format.
 * 
 * @author Rainer Villido
 *
 */
public class StringDataPackage extends DataPackage {

	public StringDataPackage(DataFrame dataFrame) {
		super(dataFrame);
	}

	/* (non-Javadoc)
	 * @see ee.stacc.translation.transformer.client.DataPackage#getObjectToPublish()
	 */
	@Override
	public Object getObjectToPublish() {
		//Get a string object that can be published.
		String result = "";
		
		for(GeneratedDataValue dataValue: getDataValues().values()) {
			result = result + ((AtomicDataValue)dataValue).getStringValue();
		}
		GWT.log("Publishing "+result, null);
		return result;
	}

	@Override
	public String toString() {
		return (String)getObjectToPublish();
	}

}
