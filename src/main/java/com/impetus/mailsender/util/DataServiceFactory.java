package com.impetus.mailsender.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.impetus.mailsender.service.DBService;
import com.impetus.mailsender.service.DataService;
import com.impetus.mailsender.service.PivotService;

/** Create employee service and return on the basis of availability. */
@Component
public class DataServiceFactory {

    @Autowired
    private PivotService pivotService;

    @Autowired
    private DBService dbService;

    /** Create employee service and return on the basis of availability.
     * 
     * @return */
    public DataService getInstance() {
        if (pingPivot()) {
            return pivotService;
        } else {
            return dbService;
        }
    }

    /** Check Pivot Service is available of not.
     * 
     * @return */
    public static boolean pingPivot() {
        String webEndPoint = "http://pivot.impetus.co.in:8088/wishes/getLocations";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ArrayNode> responseEntity = restTemplate.getForEntity(webEndPoint, ArrayNode.class);
        if (responseEntity.getStatusCodeValue() == 200) {
            return true;
        } else {
            return false;
        }
    }

}
