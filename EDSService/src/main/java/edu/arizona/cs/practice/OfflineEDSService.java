package edu.arizona.cs.practice;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfflineEDSService implements IEDSService {
	private static final Logger logger = LoggerFactory.getLogger(OfflineEDSService.class);
	@Override
	public EDSData getData(String netid, EDSConfig edsConfig) throws Exception {
		logger.info("*** USING OfflineEDSService***");
		return new EDSData(netid, "Last", "First", new ArrayList<EDSCourse>());
	}

}
