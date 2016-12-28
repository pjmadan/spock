package entities;

import java.util.ArrayList;

public class System {
	public int Id;
	public String Name;
	public String ContentIdParamName;
	public String ApiKey;
	public String SecretKey;
	public String HttpMethod;
	public String SignitureType;
	public String Data;
	public boolean IsHttps;
	public boolean SignRequest;
	public String RequestPath;
	public String Host;
	public ArrayList<Parameter> Parameters;
	public ArrayList<Attribute> Attributes;
	public String DateFormat;
	public String DateFieldPath;
	public boolean hasSyndicationOfSuccess;
	public boolean hasAttributeAcuraccy;
	public boolean hasTransmissionDelay;
	public boolean hasAdsServer;
	public String responseType;
	public String VideoIdPath;
	
}

