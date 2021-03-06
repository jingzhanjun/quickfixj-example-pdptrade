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

import com.pactera.fix.custom.*;
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
import java.time.LocalDateTime;
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
            downstream = new Downstream(new String[]{});
            downstream.logon();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }finally{
            String symbols="AUDCAD,AUDCHF,AUDHKD,AUDJPY,AUDNZD,AUDUSD,CADCHF,CADHKD,CADJPY,CHFHKD,CHFJPY,EURAUD,EURCAD,EURCHF,EURGBP,EURHKD,EURJPY,EURNZD,EURUSD,GBPAUD,GBPCAD,GBPCHF,GBPHKD,GBPJPY,GBPNZD,GBPUSD,HKDCNH,HKDJPY,NZDCAD,NZDCHF,NZDHKD,NZDJPY,NZDUSD,USDCAD,USDCHF,USDCNH,USDHKD,USDJPY,XAUUSD";
//            if(args!=null&&args.length>0){
//                int tradeType=Integer.valueOf(args[0]);
////                if(tradeType==3){
//                    String settlType=args[1];
//                    String amount=args[2];
//                    char way=args[3].charAt(0);
//                    String orderId=args[4];
//                    String userId=args[5];
//                    String clientId=args[6];
//                    String symbol=args[7];
//                    int DPS=Integer.valueOf(args[8]);
//                    Double streamQuote=Double.valueOf(args[9]);
//                    int oneClickAction=Integer.valueOf(args[10]);
//                    testNewOrderSingle(tradeType,settlType,amount,way,orderId,userId,clientId,symbol,DPS,streamQuote,oneClickAction);
//                }else{
//                    String settlType=args[1];
//                    String amount=args[2];
//                    char way=args[3].charAt(0);
//                    String symbol=args[4];
//                    testQuoteRequest(tradeType,settlType,amount,way,symbol);
//                }
//            }else{
//                int tradeType=Integer.valueOf(3);
//                String settlType="0";
//                String amount="20000";
//                char way='1';
//                String orderId="";
//                String userId="";
//                String clientId="";
//                String symbol="JPY.USD";
//                int DPS=3;
//                Double streamQuote=Double.valueOf("80.261");
//                int oneClickAction=2;
//                testNewOrderSingle(tradeType,settlType,amount,way,orderId,userId,clientId,symbol,DPS,streamQuote,oneClickAction);
//            }
            testNewOrderSingle();
//            testQuoteRequest();
//                    testQuoteRequest(o,symbol);
//                    Thread.sleep(1000);
//                    Thread.sleep(10000);
//                }
//            testQuoteCancel();
//            }
        }
        shutdownLatch.await();
    }

    private static void testNewOrderSingle(int tradeType,String settlType,String amount, char way,String orderId,String userId,String clientId,String symbol,int DPS,Double quote,int oneClickAction) throws SessionNotFound {
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setField(new PartyID("PDP_TRADE"));
        newOrderSingle.setField(new QuoteReqID("QuoteRequestID_be5548ba-b16d-4674-a718-366de4cbc342"));
        newOrderSingle.setField(new QuoteID("QuoteID_bd4d108f-d353-464e-add2-633e755bfe71"));
        newOrderSingle.setField(new ClOrdID("ClOrdID_"+UUID.randomUUID().toString()));
        newOrderSingle.setField(new Side(way));//1-b,2-s
        newOrderSingle.setField(new Account(clientId));//"client1@trapi"
        newOrderSingle.setField(new Issuer(userId));
        newOrderSingle.setField(new QuoteRespID(orderId));
        newOrderSingle.setField(new QuoteMsgID("GenIdeal"));
        newOrderSingle.setField(new Spread(Double.valueOf("10")));//markup
        newOrderSingle.setField(new TradeDate(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        //added=====================================
        newOrderSingle.setField(new SettlType(settlType));//0-SPOT,1-TODAY
        newOrderSingle.setField(new ExecutionStyle(tradeType));//1-rfq,2-rfs,3-one click
        newOrderSingle.setField(new Price(Double.valueOf("1.12397")));
        //one click fixed==================================
        newOrderSingle.setField(new Symbol(symbol));
        newOrderSingle.setField(new OrderQty(Double.valueOf(amount)));
        newOrderSingle.setField(new DPS(DPS));
        newOrderSingle.setField(new OneClickTolerance(0.005));
        newOrderSingle.setField(new OneClickAction(oneClickAction));//1-FILL_AT_MY_RATE_ONLY,2-FILL_AT_LATEST,3-SLIPPAGE
        newOrderSingle.setField(new StreamingQuote(quote));

        Session.sendToTarget(newOrderSingle,initiator.getSessions().get(0));
    }

    private static void testNewOrderSingle() throws SessionNotFound {
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setField(new PartyID("PDP_TRADE"));
        newOrderSingle.setField(new QuoteReqID("QuoteRequestID_be5548ba-b16d-4674-a718-366de4cbc342"));
        newOrderSingle.setField(new QuoteID("QuoteID_bd4d108f-d353-464e-add2-633e755bfe71"));
        newOrderSingle.setField(new ClOrdID("ClOrdID_"+UUID.randomUUID().toString()));
        newOrderSingle.setField(new Side('1'));//1-b,2-s
        newOrderSingle.setField(new Account("client1@trapi"));//"client1@trapi"
        newOrderSingle.setField(new Issuer("1000"));
        newOrderSingle.setField(new QuoteRespID("23545"));
        newOrderSingle.setField(new QuoteMsgID("GenIdeal"));
        newOrderSingle.setField(new Spread(Double.valueOf("10")));//markup
        newOrderSingle.setField(new TradeDate(new SimpleDateFormat("yyyyMMdd").format(new Date())));
        //added=====================================
        newOrderSingle.setField(new SettlType("0"));//0-SPOT,1-TODAY
        newOrderSingle.setField(new ExecutionStyle(3));//1-rfq,2-rfs,3-one click
        newOrderSingle.setField(new Price(Double.valueOf("1.12397")));
        //one click fixed==================================
        newOrderSingle.setField(new Symbol("USD.JPY"));
        newOrderSingle.setField(new OrderQty(Double.valueOf(25000)));
        newOrderSingle.setField(new DPS(3));
        newOrderSingle.setField(new OneClickTolerance(0.005));
        newOrderSingle.setField(new OneClickAction(2));//1-FILL_AT_MY_RATE_ONLY,2-FILL_AT_LATEST,3-SLIPPAGE
        newOrderSingle.setField(new StreamingQuote(Double.valueOf("1.12397")));

        Session.sendToTarget(newOrderSingle,initiator.getSessions().get(0));
    }

    private static void testQuoteRequest(int tradeType,String settlType,String amount, char way,String symbol) throws SessionNotFound{
        QuoteRequest qr=new QuoteRequest();
        qr.setField(new QuoteReqID("QuoteRequestID_"+ UUID.randomUUID().toString()));
        qr.setField(new PartyID("PDP_TRADE"));
        qr.setField(new Symbol(symbol));
        qr.setField(new Side(way));//1-b,2-s,7-not tell
        qr.setField(new ExecutionStyle(tradeType));//1.rfq,2.rfs
        qr.setField(new SettlType(settlType));//0-SPOT,1-TODAY
//        qr.setField(new Account("0"));//0-SPOT,1-2D
        qr.setField(new OrderQty(Double.valueOf(amount)));
        qr.setField(new TransactTime(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        Session.sendToTarget(qr,initiator.getSessions().get(0));
    }
    private static void testQuoteRequest() throws SessionNotFound{
        QuoteRequest qr=new QuoteRequest();
        qr.setField(new QuoteReqID("QuoteRequestID_"+ UUID.randomUUID().toString()));
        qr.setField(new PartyID("PDP_TRADE"));
        qr.setField(new Symbol("USD.JPY"));
        qr.setField(new Side('7'));//1-b,2-s,7-not tell
        qr.setField(new ExecutionStyle(1));//1.rfq,2.rfs
        qr.setField(new SettlType("0"));//0-SPOT,1-TODAY
//        qr.setField(new Account("0"));//0-SPOT,1-2D
        qr.setField(new OrderQty(Double.valueOf(5000)));
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
