import static io.restassured.RestAssured.given;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import io.restassured.RestAssured;

public class covax {
	public static void main(String[] args) {
		String res;
		ReadContext ctx;
		List<String> no_of_centers = new ArrayList<String>();
		List<String> name = new ArrayList<String>();
		List<Integer> age_limit = new ArrayList<Integer>();
		List<Integer> dose_1 = new ArrayList<Integer>();
		List<Integer> dose_2 = new ArrayList<Integer>();
		List<String> vaccine = new ArrayList<String>();
		RestAssured.baseURI = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin";
		DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime now = LocalDateTime.now();
		res = given().queryParam("pincode", args[0]).queryParam("date", dateformatter.format(now))
				.header("Content-Type", "application/json")
				.header("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
				.when().get().then().extract().response().asPrettyString();
		ctx = JsonPath.parse(res);
		no_of_centers = ctx.read("$.sessions");
		if (no_of_centers.size() > 0) {
			name = ctx.read("$.sessions[*].name");
			age_limit = ctx.read("$.sessions[*].min_age_limit");
			dose_1 = ctx.read("$.sessions[*].available_capacity_dose1");
			dose_2 = ctx.read("$.sessions[*].available_capacity_dose2");
			vaccine = ctx.read("$.sessions[*].vaccine");
			int counter = 0;
			for (int i = 0; i < dose_1.size(); i++) {
				if (dose_1.get(i) > 0) {
					counter++;
					System.out.println(
							"1st Dose : " + dose_1.get(i) + " " + vaccine.get(i) + " vaccine(s) is/are available at \'"
									+ name.get(i) + "\' for " + age_limit.get(i) + "+ age group.");
				}
			}
			if (counter == 0) {
				System.out.println("All slots booked for 1st dose.");
				counter = 0;
			}
			for (int i = 0; i < dose_2.size(); i++) {

				if (dose_2.get(i) > 0) {
					counter++;
					System.out.println(
							"2nd Dose : " + dose_2.get(i) + " " + vaccine.get(i) + " vaccine(s) is/are available at \'"
									+ name.get(i) + "\' for " + age_limit.get(i) + "+ age group.");
				}
			}
			if (counter == 0) {
				System.out.println("All slots booked for 2nd dose.");
			}
		} else {
			System.out.println("No Center available.");
		}
	}
}
