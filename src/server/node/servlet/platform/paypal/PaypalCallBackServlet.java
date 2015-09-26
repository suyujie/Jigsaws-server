package server.node.servlet.platform.paypal;

import gamecore.servlet.AbstractHttpServlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.pay.platform.PayPalPaid;

import com.paypal.ipn.IPNMessage;

@WebServlet("/paypal/ipn_listener")
public class PaypalCallBackServlet extends AbstractHttpServlet {

	private static final Logger logger = LogManager.getLogger(PaypalCallBackServlet.class);

	private static final long serialVersionUID = 8694349367959659999L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Map<String, String> configurationMap = Configuration.getConfig();
		IPNMessage ipnlistener = new IPNMessage(request, configurationMap);

		boolean isIpnVerified = ipnlistener.validate();

		String transactionType = ipnlistener.getTransactionType();

		//INFO: ******* IPN (name:value) pair : {
		//charset=gb2312, num_cart_items=1, payer_email=wudi801213@hotmail.com, receiver_email=453577387@qq.com, 
		//mc_gross_1=0.01, address_country_code=FR, payer_status=unverified, receiver_id=5MK83QGMEETG4, 
		//address_state=, transaction_subject=Margharita, address_name=di wu, address_status=unconfirmed, 
		//item_name1=Margharita, residence_country=FR, tax1=0.00, txn_id=6E303581UL377422E, mc_shipping=0.00, 
		//pending_reason=multi_currency, protection_eligibility=Eligible, payment_gross=, 
		//verify_sign=AlGSuQ-uce8iC-eS3XtHuOuOM8LqAdz.-7wvesf7dY7Q63Aqfwwz2dGa, first_name=di, payment_date=20:42:03 Mar 31, 2015 PDT, 
		//quantity1=1, mc_handling=0.00, address_country=France, payment_status=Pending, custom=, mc_handling1=0.00, last_name=wu, 
		//tax=0.00, notify_version=3.8, mc_shipping1=0.00, mc_currency=CAD, address_city=pamiers, item_number1=ITEM001, payment_type=instant, 
		//txn_type=cart, address_street=71bis av du capitaine tournissa, payer_id=8J45ZQ6CYN3WC, address_zip=09100, mc_gross=0.01, 
		//ipn_track_id=299e54ad22b1b}  ######### TransactionType : cart  ======== IPN verified : false

		//订单号
		if (isIpnVerified) {
			String txnId = ipnlistener.getIpnValue("txn_id");
			PayPalPaid payPalPaid = new PayPalPaid();
			payPalPaid.saveNoUsedOrder(txnId, ipnlistener.getIpnMap());
		}

		logger.info("******* IPN (name:value) pair : " + ipnlistener.getIpnMap() + "  " + "######### TransactionType : " + transactionType + "  ======== IPN verified : "
				+ isIpnVerified + "===txn_id : " + ipnlistener.getIpnValue("txn_id"));
	}
}
