/*******************************************************************************
 * Copyright (c) quickfixengine.org  All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixengine.org
 * license as defined by quickfixengine.org and appearing in the file
 * LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING
 * THE WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing
 * are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix43.NewOrderSingle;
import quickfix.fix50sp1.MarketDataRequest;
import quickfix.fix50sp1.QuoteCancel;
import quickfix.fix50sp1.QuoteRequest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Entry point for the Downstream application.
 */
public class Downstream {
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private static final Logger log = LoggerFactory.getLogger(Downstream.class);
    private static Downstream downstream;
    private boolean initiatorStarted = false;
    private static Initiator initiator = null;

    public Downstream(String[] args) throws Exception {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = Downstream.class.getResourceAsStream("banzai.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("usage: " + Downstream.class.getName() + " [configFile].");
            return;
        }
        SessionSettings settings = new SessionSettings(inputStream);
        inputStream.close();

        boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true"));

        OrderTableModel orderTableModel = new OrderTableModel();
        ExecutionTableModel executionTableModel = new ExecutionTableModel();
        BanzaiApplication application = new BanzaiApplication(orderTableModel, executionTableModel);
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);

        JmxExporter exporter = new JmxExporter();
        exporter.register(initiator);
    }

    public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
            } catch (Exception e) {
                log.error("Logon failed", e);
            }
        } else {
            for (SessionID sessionId : initiator.getSessions()) {
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    public void logout() {
        for (SessionID sessionId : initiator.getSessions()) {
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    public void stop() {
        shutdownLatch.countDown();
    }

    public static void main(String[] args) throws Exception {
        try {
            downstream = new Downstream(args);
            downstream.logon();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }finally{
//            testMarketDataRequest();
//            testNewOrderSingle();
                testQuoteRequest();
//            for(int i=0;i<10;i++){
//                testQuoteRequest(i);
//                Thread.sleep(1500);
//            }
//            testQuoteCancel();
        }
        shutdownLatch.await();
    }

    private static void testNewOrderSingle() throws SessionNotFound {
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setField(new PartyID("PDP_TRADE"));
        newOrderSingle.setField(new QuoteReqID("QuoteRequestID_39473ac0-1a82-4a02-85df-4b0c31da8e81"));
        newOrderSingle.setField(new QuoteID("QuoteID_bd4d108f-d353-464e-add2-633e755bfe71"));
        newOrderSingle.setField(new ClOrdID("ClOrdID_"+ UUID.randomUUID().toString()));
        newOrderSingle.setField(new Account("1001"));
        newOrderSingle.setField(new QuoteRespID("22337"));
        newOrderSingle.setField(new QuoteMsgID("GenIdeal"));
        newOrderSingle.setField(new QuoteType(2));//1.rfq,2.rfs,3.oneClick
        newOrderSingle.setField(new Side('2'));//1-b,2-s
        newOrderSingle.setField(new TradeDate(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        Session.sendToTarget(newOrderSingle,initiator.getSessions().get(0));
    }

    private static void testQuoteRequest() throws SessionNotFound{
        QuoteRequest qr=new QuoteRequest();
        qr.setField(new QuoteReqID("QuoteRequestID_"+ UUID.randomUUID().toString()));
        qr.setField(new PartyID("PDP_TRADE"));
        qr.setField(new Symbol("EURUSD"));
        qr.setField(new Side('2'));//1-b,2-s,7-not tell
        qr.setField(new QuoteType(3));//1.rfq,2.rfs,3.oneClick
        qr.setField(new OrdType('2'));
        qr.setField(new OptPayAmount(Double.valueOf("1000")));
        qr.setField(new TransactTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        Session.sendToTarget(qr,initiator.getSessions().get(0));
    }
    private static void testQuoteRequest(int i) throws SessionNotFound{
        int a=i%2;
        char side='0';
        if(a==1){
            side='1';
        }
        if(a==0){
            side='2';
        }
        QuoteRequest qr=new QuoteRequest();
        qr.setField(new QuoteReqID("QuoteRequestID_"+ UUID.randomUUID().toString()));
        qr.setField(new PartyID("PDP_TRADE"));
        qr.setField(new Symbol("EURUSD"));
        qr.setField(new Side(side));//1-b,2-s,7-not tell
        qr.setField(new QuoteType(1));//1.rfq,2.rfs,3.oneClick
        qr.setField(new OrdType('2'));
        qr.setField(new OptPayAmount(Double.valueOf("1000")));
        qr.setField(new TransactTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        Session.sendToTarget(qr,initiator.getSessions().get(0));
    }

    private static void testMarketDataRequest() throws SessionNotFound {
        MarketDataRequest marketDataRequest=new MarketDataRequest();
        marketDataRequest.setField(new SubscriptionRequestType('1'));
        marketDataRequest.setField(new MDReqID("TEST_marketDataRequest"));
        marketDataRequest.setField(new Symbol("USD/CNY"));
        marketDataRequest.setField(new MarketDepth(1));
        Session.sendToTarget(marketDataRequest,initiator.getSessions().get(0));
    }

    private static void testQuoteCancel() throws SessionNotFound {
        QuoteCancel quoteCancel=new QuoteCancel();
        quoteCancel.setField(new QuoteID("TEST_QuoteID"));
        quoteCancel.setField(new QuoteCancelType(5));
        quoteCancel.setField(new QuoteReqID("test_QuoteReqID"));
        Session.sendToTarget(quoteCancel,initiator.getSessions().get(0));
    }

}
