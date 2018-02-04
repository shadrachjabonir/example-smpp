package com.sjabonir;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;

import java.io.IOException;

public class SmsBean implements Processor {

    public void send() throws IOException {
        int smscPort = 2775;
        String smscHost = "127.0.0.1";
        String smscUsername = "user";
        String smscPassword = "pass";
        String recipientPhoneNumber = "08111111111";
        String messageText = "hello world";

        try {
            SubmitSM request = new SubmitSM();
            // request.setSourceAddr(createAddress(senderPhoneNumber)); // you can skip this
            request.setDestAddr(createAddress(recipientPhoneNumber));
            request.setShortMessage(messageText);
            // request.setScheduleDeliveryTime(deliveryTime);           // you can skip this
            request.setReplaceIfPresentFlag((byte) 0);
            request.setEsmClass((byte) 0);
            request.setProtocolId((byte) 0);
            request.setPriorityFlag((byte) 0);
            request.setRegisteredDelivery((byte) 1); // we want delivery reports
            request.setDataCoding((byte) 0);
            request.setSmDefaultMsgId((byte) 0);

            Session session = getSession(smscHost, smscPort, smscUsername, smscPassword);
            SubmitSMResp response = session.submit(request);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Session getSession(String smscHost, int smscPort, String smscUsername, String smscPassword) throws Exception{
//        if(sessionMap.containsKey(smscUsername)) {
//            return sessionMap.get(smscUsername);
//        }

        BindRequest request = new BindTransmitter();
        request.setSystemId(smscUsername);
        request.setPassword(smscPassword);
        // request.setSystemType(systemType);
        // request.setAddressRange(addressRange);
        request.setInterfaceVersion((byte) 0x34); // SMPP protocol version

        TCPIPConnection connection = new TCPIPConnection(smscHost, smscPort);
        // connection.setReceiveTimeout(BIND_TIMEOUT);
        Session session = new Session(connection);
//        sessionMap.put(smscUsername, session);

        BindResponse response = session.bind(request);
        return session;
    }

    private Address createAddress(String address) throws WrongLengthOfStringException {
        Address addressInst = new Address();
        addressInst.setTon((byte) 5); // national ton
        addressInst.setNpi((byte) 0); // numeric plan indicator
        addressInst.setAddress(address, Data.SM_ADDR_LEN);
        return addressInst;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            send();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        System.out.println("complete");
    }
}
