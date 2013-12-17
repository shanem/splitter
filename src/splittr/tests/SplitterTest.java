/*
package splittr.tests;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import splittr.startup.venmo.Venmo;

public class SplitterTest {

	@Test
	public void testGetListOfFriends() {
		int friendsCount = Venmo.getFriends("790795").size();

		assertTrue(friendsCount >= 10);
	}

	@Test
	public void testRequestPaymentWithUserID() throws ParseException {
		String apiResponse = Venmo.requestPayment("790795", 131, "testerex1808", null);

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(apiResponse);
		JSONObject jsonObject = (JSONObject) obj;

		String status = (String) jsonObject.get("status");

		assertTrue(status.equals("PENDING_CHARGE_CONFIRMATION"));

	}

	@Test
	public void testRequestPaymentWithUserEmail() throws ParseException {
		String apiResponse = Venmo.requestPayment(null, 143, "testerex1812", "testerex56%40gmail.com");

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(apiResponse);
		JSONObject jsonObject = (JSONObject) obj;

		String status = (String) jsonObject.get("status");

		assertTrue(status.equals("PENDING_SIGNUP_TO_COMPLETE_CHARGE"));

	}

}
*/
