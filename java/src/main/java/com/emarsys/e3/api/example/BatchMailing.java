package com.emarsys.e3.api.example;

import org.restlet.data.Response;
import org.restlet.data.Status;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.io.StringReader;

import java.lang.Object;
import java.lang.String;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.out;

/**
 * BatchMailing forms the primary entry point to the emarsys API.
 * <p/>
 * The BatchMailing is a wrapper around the HTTP and SFTP requests
 * needed in order to communicate with the API.
 * <p/>
 * Currently the following functions are supported:
 * <ol>
 *     <li>{@link #create() create the batch mailing}</li>
 *     <li>{@link #transferRecipientData()}  transfer the recipients import file}</li>
 *     <li>{@link #triggerImport() trigger import}</li>
 * </ol>
 * <p/>
 * @author Michael Kulovits <kulovits@emarsys.com>
 */
public class BatchMailing {

    //constructor params
    private final String name;
    //state
    private boolean created = false;

    //client subsystems
    private final APIClient apiClient;
    private final RecipientDataTransferer dataTransferer;    

    /**
     * Constructor.
     *
     * @param name   - the unique id of the batch mailing
     * @param config - the config
     */
    public BatchMailing(String name, ClientConfiguration config) {

        this.name = name;
        this.apiClient = new APIClient(config);
        this.dataTransferer = new RecipientDataTransferer(config, this.name);
    }


    /**
     * Creates the batch mailing via an API call.
     *
     * @throws APIException
     */
    public void create() throws APIException {

        apiClient.createBatchMailing(name);
        created = true;
    }

    /**
     * Transfers the batch's corresponding recipient data via SFTP.
     *
     * @throws IOException
     */
    public void transferRecipientData( String filePath ) throws IOException {

        this.dataTransferer.transferRecipientData(filePath);
        out.println("transferred recipient data for " + this);
    }

    /**
     * Triggers the import process via an HTTP call.
     *
     * @throws APIException
     */
    public void triggerImport() throws APIException {
        if (!this.created) {
            throw new IllegalArgumentException(
                    "must not trigger import for non-existent " + this + "!"
            );
        }

        apiClient.triggerImport( name, this.dataTransferer.getRemoteFileName() );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.name;
    }
}//class BatchMailing
