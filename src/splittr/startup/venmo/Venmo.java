package splittr.startup.venmo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;

public class Venmo {

	public static String requestPayment(String userId, String amount, String note, String email) {
		String apiFormParams = "";

		if (userId != null) { //if the user is in venmo then
			apiFormParams = "access_token=KgTEGQvNuFsgwpMXZPkDKCBgq2nmu2DS&user_id="
					+ userId + "&amount=-" + amount + "&note=" + URLEncoder.encode(note);
		} else if (email != null) { //if the user is not in venmo then
			apiFormParams = "access_token=KgTEGQvNuFsgwpMXZPkDKCBgq2nmu2DS&email="
					+ email + "&amount=-" + amount + "&note=" + URLEncoder.encode(note);
		} else { // error: missing req params
			return null;
		}

		String apiResponse = sendPostApiRequest("https://api.venmo.com/payments", apiFormParams);

		return apiResponse;

	}

	protected static String sendGetApiRequest(URL apiCall) {
		try {

			HttpURLConnection conn = (HttpURLConnection) apiCall
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			String result = "";

			while ((output = br.readLine()) != null) {
				result += output;
			}

			conn.disconnect();

			return result;

		} catch (MalformedURLException e) {

			e.printStackTrace();

			return null;

		} catch (IOException e) {

			e.printStackTrace();

			return null;

		}
	}

	protected static String sendPostApiRequest(String targetUrl, String formParams) {

		try {

			URL urlObj = new URL(targetUrl);

			HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept", "application/json");

			// send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(formParams);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out
					.println("\nSending 'POST' request to URL : " + targetUrl);
			System.out.println("Post parameters : " + formParams);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();

		} catch (MalformedURLException e) {

			e.printStackTrace();

			return null;

		} catch (IOException e) {

			e.printStackTrace();

			return null;

		}
	}

	public static List<Person> getFriends(String userId) {
		String apiResponse;
		try {
			URL url = new URL("https://sandbox-api.venmo.com/users/" + userId
					+ "/friends?"
					+ "access_token=dXtNPewHpADdjvBCGQbhFtkjXrnBsFZ3");

			apiResponse = sendGetApiRequest(url);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		List<Person> venmoFriends = new ArrayList<Person>();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(apiResponse);
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray data = (JSONArray) jsonObject.get("data");

			JSONObject jo2;

			for (Object o : data) {

				System.out.println("\n\n");

				jo2 = (JSONObject) o;

				String username = (String) jo2.get("username");
				String fname = (String) jo2.get("first_name");
				String lname = (String) jo2.get("last_name");
				String picUrl = (String) jo2.get("profile_picture_url");
				Long venmoId = (Long) jo2.get("id");

				venmoFriends.add(new Person(fname + " " + lname, picUrl, venmoId));

			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return venmoFriends;
	}

	
	public static String submitSplitBill(String userId, List<ReceiptItem> items, int tipPercent) {
		return requestPayment(items.get(0).people.get(0).venmoId.toString(), "-1.00", "Thanks for all the fish", null);
	}
}
