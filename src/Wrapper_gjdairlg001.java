import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;
import com.qunar.qfwrapper.util.QFPostMethod;

public class Wrapper_gjdairlg001 implements QunarCrawler {
	private static final String postUrl = "https://wftc2.e-travel.com/plnext/5ANT/Override.action";
	private static final String postRefererUrl = "	https://www.luxair.lu/profile/encrypt.jsp?LANGUAGE=GB&SITE=5ANT5ANT&EMBEDDED_TRANSACTION=FlexPricerAvailability&ENCT=1";

	// https://www.luxair.lu/cms/Luxair-Luxembourg-Airlines?p=EN,17652,461,,1,,

	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		// searchParam.setDep("BCN");
		// searchParam.setArr("LUX");
		// searchParam.setDep("BIO"); // Bilbao
		// searchParam.setArr("LUX");
		// searchParam.setDep("TXL"); // Luxair
		// searchParam.setArr("LUX");
//		searchParam.setDep("SCN");
//		searchParam.setArr("TXL");
//		searchParam.setDepDate("2014-07-03");
//		 searchParam.setDep("ALC");
//		 searchParam.setArr("LUX");
//		 searchParam.setDepDate("2014-08-01");
		 searchParam.setDep("SCN");
		 searchParam.setArr("BCN");
		 searchParam.setDepDate("2014-07-01");
		// searchParam.setDep("LUX");
		// searchParam.setArr("FLR");
		// searchParam.setDepDate("2014-06-10");
		searchParam.setWrapperid("gjdairlg001");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjdairlg001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjdairlg001().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			List<OneWayFlightInfo> flightList = (List<OneWayFlightInfo>) result.getData();
System.out.println(flightList.size());
			for (OneWayFlightInfo in : flightList) {
				System.out.println("************" + in.getInfo().toString());
				System.out.println("++++++++++++" + in.getDetail().toString());
			}
		} else {
			System.out.println(result.getStatus());
		}
	}

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {
		String bookingUrlPre = "https://www.luxair.lu/cms/luxair.php";
		BookingResult bookingResult = new BookingResult();

		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("p", "EN,17958,,,,,");
		String lap = String
				.format("PRD,en,1,ECONOMY_BUSINESS_LASTMINUTE,%s,%s,%s,%s,false,O,1,0,0,0,WWW_LUXAIR_LU,true,true,true,false,false,true,,,",
						arg0.getDep(), arg0.getArr(), arg0.getDepDate().replaceAll("-", ""), arg0.getDepDate()
								.replaceAll("-", ""));
		map.put("lap", lap);
		// map.put("lgBookButton", "Book Now");
		// map.put("Referer", "https://www.luxair.lu/cms/page?p=en,17652,,,,,");
		bookingInfo.setInputs(map);
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;

	}

	@Override
	public String getHtml(FlightSearchParam arg0) {
		QFGetMethod get = null;
		QFGetMethod getRequest = null;
		QFPostMethod post = null;
		String responseBodyAsString = null;
		QFHttpClient httpClient = new QFHttpClient(arg0, false);
		String getUrl = String
				.format("https://www.luxair.lu/profile/booking/ERetailServiceCITYSPEC.action?mode=PRD&lang=en&tripType=1&cabin=ECONOMY_BUSINESS_LASTMINUTE&origin=%s&destination=%s&departureDate=%s&returnDate=%s&flexible=false&pricing=O&adultCount=1&youthCount=0&childCount=0&infantCount=0&rmSite=WWW_LUXAIR_LU&allowInsurance=true&allowPromo=true&allowCar=true&allowHotel=false&allowProfileLess=false&allowNego=true",
						arg0.getDep(), arg0.getArr(), arg0.getDepDate().replaceAll("-", ""), arg0.getDepDate()
								.replaceAll("-", ""));

		get = new QFGetMethod(getUrl);
		String urlGet = ""; // get请求的url
		try {
			get.setFollowRedirects(false);
			get.getParams().setContentCharset("utf-8");
			httpClient.executeMethod(get);

			if (get.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY
					|| get.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY) {

				Header location = get.getResponseHeader("Location");
				if (location != null) {
					urlGet = location.getValue();
					if (!urlGet.startsWith("http")) {
						urlGet = get.getURI().getScheme() + "://" + get.getURI().getHost()
								+ (get.getURI().getPort() == -1 ? "" : ":" + get.getURI().getPort()) + urlGet;
					}
				} else {
					return "";
				}

				getRequest = new QFGetMethod(urlGet);
				getRequest.getParams().setContentCharset("utf-8");
				httpClient.executeMethod(getRequest);
				responseBodyAsString = getRequest.getResponseBodyAsString();
			}

			Pattern pattern = Pattern.compile("ENC<textarea .*?>(.*)</textarea>");
			Matcher matcher = pattern.matcher(responseBodyAsString);
			StringBuffer encVal = null;
			if (matcher.find()) {
				encVal = new StringBuffer(matcher.group(1));
			}

			post = new QFPostMethod(postUrl);

			NameValuePair[] names = { new NameValuePair("EMBEDDED_TRANSACTION", "FlexPricerAvailability"),
					new NameValuePair("ENCT", "1"), new NameValuePair("LANGUAGE", "GB"),
					new NameValuePair("SITE", "5ANT5ANT"), new NameValuePair("ENC", encVal.toString()) };
			post.setRequestBody(names);
			post.setRequestHeader("Referer", postRefererUrl);
			post.getParams().setContentCharset("UTF-8");
			httpClient.executeMethod(post);
			return post.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != get) {
				get.releaseConnection();
			}
			if (null != getRequest) {
				getRequest.releaseConnection();
			}
			if (post != null) {
				post.releaseConnection();
			}
		}
		return "Exception";
	}

	@Override
	public ProcessResultInfo process(String arg0, FlightSearchParam arg1) {
		String html = arg0;
		/*
		 * ProcessResultInfo中，
		 * ret为true时，status可以为：SUCCESS(抓取到机票价格)|NO_RESULT(无结果，没有可卖的机票)
		 * ret为false时，status可以为:CONNECTION_FAIL|INVALID_DATE|INVALID_AIRLINE|PARSING_FAIL|PARAM_ERROR
		 * 
		 * FlightDetail中的Fligthnolist，把这两个航班号都add上，然后针对每个航班号，有一个FlightSegment
		 */
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;
		}

		List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>(); // 获得具体航班信息
		String jsonStr = StringUtils.substringBetween(html, "list_flight\":", "}],\"list_recommendation\"");
		String jsonPrice = StringUtils.substringBetween(html, "list_recommendation\":", ",\"list_date\"");
		if (StringUtils.isEmpty(jsonStr) || StringUtils.isEmpty(jsonPrice)) {
			result.setRet(false);
			result.setStatus(Constants.NO_RESULT);
			return result;
		}
		JSONArray ajson = JSON.parseArray(jsonStr);
		JSONArray ajsonPrice = JSON.parseArray(jsonPrice); // 价格json
		Map<String, Double> priceMap = new HashMap<String, Double>();// 取各个flight_id的最小price
		double price = 0;
		for (int t = 0; t < ajsonPrice.size(); t++) {
			JSONObject jsonObject = ajsonPrice.getJSONObject(t);
			JSONArray boundArray = jsonObject.getJSONArray("list_bound");
			price = jsonObject.getDoubleValue("price");
			for (int h = 0; h < boundArray.size(); h++) {
				JSONObject object = (JSONObject) boundArray.get(h);
				JSONArray flightArray = object.getJSONArray("list_flight");
				for (int k = 0; k < flightArray.size(); k++) {
					JSONObject flightObject = (JSONObject) flightArray.get(k);
					String flight_id = flightObject.getString("flight_id");
					if (priceMap.containsKey(flight_id)) {
						double tmpPrice = priceMap.get(flight_id);
						if (tmpPrice > price) {
							priceMap.put(flight_id, price);
						}
					} else {
						priceMap.put(flight_id, price);
					}
				}
			}
		}

		for (int i = 0; i < ajson.size(); i++) {
			OneWayFlightInfo baseFlight = new OneWayFlightInfo();
			List<FlightSegement> segs = new ArrayList<FlightSegement>();
			FlightDetail flightDetail = new FlightDetail();
			List<String> flightNoList = new ArrayList<String>();
			JSONObject ojson = ajson.getJSONObject(i);
			String flightid = ojson.getString("flight_id");
			JSONArray segmentArray = ojson.getJSONArray("list_segment");
			for (int j = 0; j < segmentArray.size(); j++) {
				FlightSegement seg = new FlightSegement();
				JSONObject object = (JSONObject) segmentArray.get(j);
				String flightNo = object.getJSONObject("airline").getString("code") + object.getString("flight_number");
				flightNoList.add(flightNo);
				seg.setFlightno(flightNo);
				try {
					seg.setDepDate(setDate(object.getString("b_date_date")));
					seg.setArrDate(setDate(object.getString("e_date_date")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				seg.setDepairport(object.getJSONObject("b_location").getString("location_code"));
				seg.setArrairport(object.getJSONObject("e_location").getString("location_code"));
				seg.setDeptime(object.getString("b_date_formatted_time"));
				seg.setArrtime(object.getString("e_date_formatted_time"));
				seg.setCompany(object.getJSONObject("airline").getString("code"));
				segs.add(seg);
			}
			JSONObject ob = (JSONObject) segmentArray.get(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				flightDetail.setDepdate(sdf.parse(setDate(ob.getString("b_date_date"))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			flightDetail.setFlightno(flightNoList);
			flightDetail.setMonetaryunit("EUR");
			flightDetail.setPrice(priceMap.get(flightid));
			flightDetail.setDepcity(arg1.getDep());
			flightDetail.setArrcity(arg1.getArr());
			flightDetail.setWrapperid(arg1.getWrapperid());
			baseFlight.setDetail(flightDetail);
			baseFlight.setInfo(segs);
			flightList.add(baseFlight);
		}
		result.setRet(true);
		result.setStatus(Constants.SUCCESS);
		result.setData(flightList);
		return result;

	}

	private String setDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		return sdf2.format(sdf.parse(date));
	}
}
