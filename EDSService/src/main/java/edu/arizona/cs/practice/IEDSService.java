package edu.arizona.cs.practice;

public interface IEDSService {

	/**
	 * @param args
	 * @throws Exception
	 */
	public abstract EDSData getData(String netid, EDSConfig edsConfig)
			throws Exception;

}