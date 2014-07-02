import java.math.BigDecimal;
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
import org.apache.commons.httpclient.cookie.CookiePolicy;
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
import com.qunar.qfwrapper.bean.search.RoundTripFlightInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;
import com.qunar.qfwrapper.util.QFPostMethod;

public class Wrapper_gjsairlg001 implements QunarCrawler {
	private static final String postUrl = "https://wftc2.e-travel.com/plnext/5ANT/Override.action";
	private static final String postRefererUrl = "https://www.luxair.lu/profile/encrypt.jsp?LANGUAGE=GB&SITE=5ANT5ANT&EMBEDDED_TRANSACTION=FlexPricerAvailability&ENCT=1";

	// https://www.luxair.lu/cms/Luxair-Luxembourg-Airlines?p=EN,17652,461,,1,,

	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		// SCN-BER 2014-07-16 2014-07-26
		// ADB-LUX 2014-07-26 2014-07-30
		// LUX-FOC 2014-08-20 2014-08-24
		// searchParam.setDep("MUC");
		// searchParam.setArr("LUX");
		// searchParam.setDepDate("2014-06-28");
		// searchParam.setRetDate("2014-07-05");
		searchParam.setDep("LUX");
		searchParam.setArr("FCO");
		searchParam.setDepDate("2014-08-20");
		searchParam.setRetDate("2014-08-24");
		searchParam.setWrapperid("gjsairlg001");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjsairlg001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjsairlg001().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			List<RoundTripFlightInfo> flightList = (List<RoundTripFlightInfo>) result.getData();
			for (RoundTripFlightInfo in : flightList) {
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
				.format("PRD,en,2,ECONOMY_BUSINESS_LASTMINUTE,%s,%s,%s,%s,false,O,1,0,0,0,WWW_LUXAIR_LU,true,true,true,false,false,true,,,",
						arg0.getDep(), arg0.getArr(), arg0.getDepDate().replaceAll("-", ""), arg0.getRetDate()
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
		String urlGet = ""; // get请求的url
		try {
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			String getUrl = String
					.format("https://www.luxair.lu/profile/booking/ERetailServiceCITYSPEC.action?mode=PRD&lang=en&tripType=2&cabin=ECONOMY_BUSINESS_LASTMINUTE&origin=%s&destination=%s&departureDate=%s&returnDate=%s&flexible=false&pricing=O&adultCount=1&youthCount=0&childCount=0&infantCount=0&rmSite=WWW_LUXAIR_LU&allowInsurance=true&allowPromo=true&allowCar=true&allowHotel=false&allowProfileLess=false&allowNego=true",
							arg0.getDep(), arg0.getArr(), arg0.getDepDate().replaceAll("-", ""), arg0.getRetDate()
									.replaceAll("-", ""));

			get = new QFGetMethod(getUrl);
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
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;
		}

		String jsonStr = StringUtils.substringBetween(html, "list_proposed_bound\":", ",\"list_recommendation\"");
		String jsonPrice = StringUtils.substringBetween(html, "list_recommendation\":", ",\"list_date\"");

		if (StringUtils.isEmpty(jsonStr) || StringUtils.isEmpty(jsonPrice)) {
			result.setRet(false);
			result.setStatus(Constants.NO_RESULT);
			return result;
		}
		JSONArray ajson = JSON.parseArray(jsonStr);
		JSONArray ajsonPrice = JSON.parseArray(jsonPrice); // 价格json

		Map<String, Double> goPriceMap = new HashMap<String, Double>();// 取 【去程】 各个flight_id的最小price
		Map<String, Double> rePriceMap = new HashMap<String, Double>();// 取 【返程】 各个flight_id的最小price
		for (int t = 0; t < ajsonPrice.size(); t++) {
			JSONObject jsonObject = ajsonPrice.getJSONObject(t);
			JSONArray boundArray = jsonObject.getJSONArray("list_bound");
			JSONArray priceArray = jsonObject.getJSONArray("list_price"); // 0：去程 1：返程
			for (int h = 0; h < boundArray.size(); h++) {
				JSONObject object = (JSONObject) boundArray.get(h);
				JSONArray flightArray = object.getJSONArray("list_flight");
				for (int k = 0; k < flightArray.size(); k++) {
					JSONObject flightObject = (JSONObject) flightArray.get(k);
					String flight_id = flightObject.getString("flight_id");
					if (h == 0) { // 去程
						JSONObject priceObj = (JSONObject) priceArray.get(0);
						double price = priceObj.getDoubleValue("price");
						if (goPriceMap.containsKey(flight_id)) {
							double tmpPrice = goPriceMap.get(flight_id);
							if (tmpPrice > price) {
								goPriceMap.put(flight_id, price);
							}
						} else {
							goPriceMap.put(flight_id, price);
						}
					}

					if (h == 1) { // 返程
						JSONObject priceObj = (JSONObject) priceArray.get(1);
						double price = priceObj.getDoubleValue("price");
						if (rePriceMap.containsKey(flight_id)) {
							double tmpPrice = rePriceMap.get(flight_id);
							if (tmpPrice > price) {
								rePriceMap.put(flight_id, price);
							}
						} else {
							rePriceMap.put(flight_id, price);
						}
					}
				}
			}
		}

		List<RoundTripFlightInfo> flightList = new ArrayList<RoundTripFlightInfo>(); // 获得具体航班信息
		List<OneWayFlightInfo> goflightList = new ArrayList<OneWayFlightInfo>(); // 去程具体航班信息
		List<OneWayFlightInfo> reflightList = new ArrayList<OneWayFlightInfo>(); // 返程具体航班信息

		for (int i = 0; i < ajson.size(); i++) {

			JSONObject ojson = ajson.getJSONObject(i); // i=0去程 i=1返程
			JSONArray list_flightArray = ojson.getJSONArray("list_flight");
			for (int fa = 0; fa < list_flightArray.size(); fa++) {
				OneWayFlightInfo baseFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();
				List<String> flightNoList = new ArrayList<String>();

				JSONObject ojsonTemp = list_flightArray.getJSONObject(fa);
				String flightid = ojsonTemp.getString("flight_id");
				JSONArray segmentArray = ojsonTemp.getJSONArray("list_segment");
				for (int j = 0; j < segmentArray.size(); j++) {
					FlightSegement seg = new FlightSegement();
					JSONObject object = (JSONObject) segmentArray.get(j);
					String flightNo = object.getJSONObject("airline").getString("code")
							+ object.getString("flight_number");
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
				if (i == 0) { // 去程
					flightDetail.setPrice(goPriceMap.get(flightid));
				}
				if (i == 1) { // 返程
					flightDetail.setPrice(rePriceMap.get(flightid));
				}
				flightDetail.setDepcity(arg1.getDep());
				flightDetail.setArrcity(arg1.getArr());
				flightDetail.setWrapperid(arg1.getWrapperid());
				baseFlight.setDetail(flightDetail);
				baseFlight.setInfo(segs);
				if (i == 0) {
					goflightList.add(baseFlight);
				}
				if (i == 1) {
					reflightList.add(baseFlight);
				}
			}
		}

		if (goflightList != null && goflightList.size() > 0 && reflightList != null && reflightList.size() > 0) {
			for (int i = 0; i < goflightList.size(); i++) {
				for (int j = 0; j < reflightList.size(); j++) {
					List<FlightSegement> goSegs = goflightList.get(i).getInfo();
					FlightDetail goDetail = goflightList.get(i).getDetail();
					List<FlightSegement> reSegs = reflightList.get(j).getInfo();
					FlightDetail reDetail = reflightList.get(j).getDetail();
					FlightDetail goreDetail = new FlightDetail();

					RoundTripFlightInfo rtf = new RoundTripFlightInfo();
					rtf.setRetdepdate(reDetail.getDepdate()); // 返程日期
					rtf.setRetflightno(reDetail.getFlightno()); // 返程航班号列表
					rtf.setRetinfo(reSegs); // 返程航班信息列表
					rtf.setOutboundPrice(goDetail.getPrice()); // 去程价格
					rtf.setReturnedPrice(reDetail.getPrice()); // 返程价格

					// protected String depcity;
					// protected String arrcity;
					// protected Date depdate;
					// protected List<String> flightno = Lists.newArrayList();
					// protected String monetaryunit;// 货币单位
					// protected double tax;
					// protected double price;
					// protected String wrapperid;

					goreDetail.setDepcity(goDetail.getDepcity());
					goreDetail.setArrcity(goDetail.getArrcity());
					goreDetail.setDepdate(goDetail.getDepdate());
					goreDetail.setFlightno(goDetail.getFlightno());
					goreDetail.setMonetaryunit("EUR");
					goreDetail.setTax(0.0);
					goreDetail.setPrice(sum(goDetail.getPrice(), reDetail.getPrice()));
					goreDetail.setWrapperid(goDetail.getWrapperid());
					rtf.setDetail(goreDetail);
					rtf.setInfo(goSegs);
					flightList.add(rtf);
				}
			}
		} else {
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
			return result;
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

	public double sum(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
	}
}
