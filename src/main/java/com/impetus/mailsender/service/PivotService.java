package com.impetus.mailsender.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.util.DataHelper;

@Service
public class PivotService implements DataService {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Employee> getEmployees(Filter filter, Date mailDate) {
        try {
            String webEndPoint = "http://pivot.impetus.co.in:8088/wishes/getUsers";
            getEmployee(webEndPoint, filter);

            RestTemplate restTemplate = new RestTemplate();
            ObjectNode request = prepareRequestData(filter);
            ResponseEntity<ArrayNode> responseEntity = restTemplate.postForEntity(webEndPoint, request, ArrayNode.class);
            ArrayNode jsonArray = responseEntity.getBody();
            List<Employee> employees = new ArrayList<Employee>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonNode jsonNode = jsonArray.get(i);
                Employee employee = new Employee();
                employee.setEMAIL(jsonNode.get("EMAIL").asText());
                employee.setEMAIL("sunil.gupta@impetus.co.in");
                employee.setNAME(jsonNode.get("NAME").asText());
                employee.setIMGURL(jsonNode.get("IMGURL").asText());
                employee.setSUBJECT(jsonNode.get("SUBJECT").asText());
                employees.add(employee);
            }
            return employees;

        } catch (Exception e) {
            throw new BWisherException("Pivot Service Exception occured.", e);
        }
    }

    private void getEmployee(String webEndPoint, Filter filter) throws Exception {
        ClientRequest clientRequest = new ClientRequest(webEndPoint);
        JSONObject requestBody = createRequestBody();
        clientRequest.body(MediaType.APPLICATION_JSON, requestBody);
        clientRequest.accept(MediaType.APPLICATION_JSON);
        clientRequest.header("Content-type", MediaType.APPLICATION_JSON);
        ClientResponse<JSONObject> response = clientRequest.post(JSONObject.class);
        ArrayList<Object> result = null;
        if (response != null && response.getStatus() == 200) {
            JSONObject leadObject = response.getEntity();
            System.out.println(leadObject);
        } else {
            throw new RuntimeException("Request Failed: " + response.getStatus());
        }
    }

    private JSONObject createRequestBody() {
        JSONObject requestBody = new JSONObject();
        JSONArray loc = new JSONArray();
        loc.add("NOIDA");
        JSONArray occ = new JSONArray();
        loc.add("Birthday");
        JSONArray clients = new JSONArray();
        loc.add("Amex");
        requestBody.put("location", loc);
        requestBody.put("occations", occ);
        requestBody.put("clients", clients);
        requestBody.put("date", "2017/06/10");
        return requestBody;
    }

    public static void main(String args[]) {
        new PivotService().getEmployees(null, new Date());
    }

    /** @param filter
     * @return */
    private ObjectNode prepareRequestData(Filter filter) {
        ObjectNode request = mapper.createObjectNode();

        if (filter != null && filter.getLocations() != null && filter.getLocations().length > 0) {
            ArrayNode locations = mapper.createArrayNode();
            for (String location : filter.getLocations()) {
                locations.add(location);
            }
            request.set("locations", locations);
        }

        if (filter != null && filter.getClients() != null && filter.getClients().length > 0) {
            ArrayNode clients = mapper.createArrayNode();
            for (String client : filter.getClients()) {
                clients.add(client);
            }
            request.set("clients", clients);
        }

        if (filter != null && filter.getOccations() != null && filter.getOccations().length > 0) {
            ArrayNode occations = mapper.createArrayNode();
            for (String occation : filter.getOccations()) {
                occations.add(occation);
            }
            request.set("occations", occations);
        }

        request.put("date", DataHelper.formateDateYYYYMMDD(new Date()));
        return request;
    }

}
