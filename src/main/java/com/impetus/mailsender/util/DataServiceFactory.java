package com.impetus.mailsender.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("http://pivot.impetus.co.in", 8088), 10000);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
