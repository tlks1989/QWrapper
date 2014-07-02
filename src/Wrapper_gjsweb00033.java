import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.httpclient.HttpStatus;
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
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.bean.search.RoundTripFlightInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;

public class Wrapper_gjsweb00033 implements QunarCrawler {

	// http://www.hop2.com/
	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();
		// searchParam.setDep("PTP"); //INVALID_DATE
		// searchParam.setArr("STX");
		// searchParam.setDepDate("2014-08-20");

		// BGI SDQ 2014-08-16
//		searchParam.setDep("BGI");
//		searchParam.setArr("SLU");
//		searchParam.setDepDate("2014-08-01");
//		searchParam.setRetDate("2014-08-20");
		searchParam.setDep("HKG");
		searchParam.setArr("SYD");
		searchParam.setDepDate("2014-08-10");
		searchParam.setRetDate("2014-08-13");
		// searchParam.setDep("DOM"); // Dominica (DOM)
		// searchParam.setArr("SVD"); // St. Vincent (SVD)
		// searchParam.setDepDate("2014-08-12");
		// searchParam.setRetDate("2014-08-20");
		searchParam.setWrapperid("gjsweb00032");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjsweb00033().getHtml(searchParam);
		// System.out.println(html);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjsweb00033().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			List<RoundTripFlightInfo> flightList = (List<RoundTripFlightInfo>) result.getData();
			for (RoundTripFlightInfo in : flightList) {
				System.out.println(in.getInfo().toString());
				System.out.println(in.getDetail().toString());
				System.out.println("");
			}

			// for (RoundTripFlightInfo round : flightList) {
			// System.out.println("************" + round.toString());
			// }
		} else {
			System.out.println(result.getStatus());
		}
	}

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {
		String bookingUrlPre = "http://www.hop2.com/page/Flight/AirResultForm.aspx";

		// 获取年月日
		String[] dateDep = arg0.getDepDate().split("-"); // [0]2014 [1]08 [2]01
		String date1 = dateDep[1] + "/" + dateDep[2] + "/" + dateDep[0];
		String[] dateRet = arg0.getRetDate().split("-"); // [0]2014 [1]08 [2]01
		String date2 = dateRet[1] + "/" + dateRet[2] + "/" + dateRet[0];

		BookingResult bookingResult = new BookingResult();
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("type", "roundtrip");
		map.put("origin1", arg0.getDep());
		map.put("date1", date1);
		map.put("destination1", arg0.getArr());
		map.put("date2", date2);
		map.put("adt", "1");
		map.put("chd", "0");
		map.put("cabin", "e");

		bookingInfo.setInputs(map);
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;
	}

	@Override
	public String getHtml(FlightSearchParam arg0) {
		QFGetMethod get = null;
		QFGetMethod getAjax = null;
		try {
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			// 获取年月日
			String[] dateDep = arg0.getDepDate().split("-"); // [0]2014 [1]08 [2]01
			String[] dateRet = arg0.getRetDate().split("-"); // [0]2014 [1]08 [2]20
			String date1 = dateDep[1] + "/" + dateDep[2] + "/" + dateDep[0];
			String date2 = dateRet[1] + "/" + dateRet[2] + "/" + dateRet[0];
			String date1Ajax = date1.replaceAll("/", "");
			// System.out.println(date1Ajax);
			String date2Ajax = date2.replaceAll("/", "");

			String getUrl = String
					.format("http://www.hop2.com/page/Flight/AirResultForm.aspx?type=roundtrip&origin1=%s&date1=%s&destination1=%s&date2=%s&adt=1&chd=0&cabin=e",
							arg0.getDep(), URLEncoder.encode(date1, "utf-8"), arg0.getArr(),
							URLEncoder.encode(date2, "utf-8"));

			get = new QFGetMethod(getUrl);
			String cookie = StringUtils.join(httpClient.getState().getCookies(),"; ");
			httpClient.getState().clearCookies();
			get.addRequestHeader("Cookie",cookie);
			get.getParams().setContentCharset("utf-8");
			int getStatus = httpClient.executeMethod(get);

			if (getStatus != HttpStatus.SC_OK) {
				return "Exception";
			}

			while (true) {
				String ranNum = getRandomNum();			
				String ajaxUrl = String
						.format("http://www.hop2.com/flight/results?type=roundtrip&cabin=E&origin1=%s&destination1=%s&date1=%s&origin2=%s&destination2=%s&date2=%s&adt=1&chd=0&near=0000&airline=&nextkey=&_=%s",
								arg0.getDep(), arg0.getArr(), date1Ajax, arg0.getArr(), arg0.getDep(), date2Ajax,
								ranNum);
				getAjax = new QFGetMethod(ajaxUrl);
				getAjax.getParams().setContentCharset("utf-8");
				// getAjax.addRequestHeader("connection","keep-alive");

				int ajaxStatus = httpClient.executeMethod(getAjax);
				if (ajaxStatus != HttpStatus.SC_OK) {
					return "Exception";
				}

				String returnHtml = getAjax.getResponseBodyAsString();
				if (StringUtils.isEmpty(returnHtml) || "error".equals(returnHtml)) {
					return "Exception";
				}

				JSONObject ajaxJson = JSONObject.parseObject(returnHtml);
				// System.out.println(ajaxJson.get("status"));
				if ("done".equals(ajaxJson.get("status"))) {
					return returnHtml;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != get) {
				get.releaseConnection();
			}
			if (null != getAjax) {
				getAjax.releaseConnection();
			}
		}
		return "Exception";
	}

	@Override
	public ProcessResultInfo process(String arg0, FlightSearchParam searchParam) {
		String html = arg0;
		// System.out.println(html);
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;
		}

		List<RoundTripFlightInfo> flightList = new ArrayList<RoundTripFlightInfo>(); // 获得具体航班信息

		try {
			String jsonStr = StringUtils.substringBetween(html, "\"itins\":", "]}");
			jsonStr = jsonStr + "]";
			// System.out.println(jsonStr);
			if (StringUtils.isEmpty(jsonStr)) {
				result.setRet(false);
				result.setStatus(Constants.PARSING_FAIL);
				return result;
			}
			JSONArray ajson = JSON.parseArray(jsonStr);
			// System.out.println(ajson.size());
			if (ajson == null || ajson.size() == 0) {
				result.setRet(false);
				result.setStatus(Constants.PARSING_FAIL);
				return result;
			}

			Map<String, Double> priceMap = new HashMap<String, Double>();// 取 最小price
			for (int i = 0; i < ajson.size(); i++) {
				JSONObject ojson = ajson.getJSONObject(i);
				String key = ojson.getString("key");
				double price = ojson.getDoubleValue("tp");
				if (priceMap.containsKey(key)) {
					double tmpPrice = priceMap.get(key);
					if (tmpPrice > price) {
						priceMap.put(key, price);
					}
				} else {
					priceMap.put(key, price);
				}
			}
			// System.out.println("priceMap " + priceMap.size());

			List<String> existFlag = new ArrayList<String>(); // 是否存在标识 key判断
			for (int i = 0; i < ajson.size(); i++) {
				RoundTripFlightInfo rtFlight = new RoundTripFlightInfo();
				FlightDetail flightDetail = new FlightDetail();
				List<String> flightNoList = new ArrayList<String>();
				List<String> flightRetNoList = new ArrayList<String>();
				JSONObject ojson = ajson.getJSONObject(i);
				// System.out.println("ojson   " + ojson);
				String key = ojson.getString("key");
				if (existFlag.contains(key)) {
					continue;
				} else {
					existFlag.add(key);
				}

				JSONArray tps = ojson.getJSONArray("tps");
				List<FlightSegement> gosegs = new ArrayList<FlightSegement>(); // 去程
				List<FlightSegement> resegs = new ArrayList<FlightSegement>(); // 返程
				for (int t = 0; t < tps.size(); t++) { // 0:去程 1:返程
					JSONArray segmentArray = ((JSONObject) tps.get(t)).getJSONArray("segments");
					// System.out.println("segments   " + segmentArray);
					for (int j = 0; j < segmentArray.size(); j++) {
						FlightSegement seg = new FlightSegement();
						JSONObject object = (JSONObject) segmentArray.get(j);
						String flightNo = object.getString("ac") + object.getString("fn"); // flightNo
						seg.setFlightno(flightNo);
						String[] timeDep = object.getString("dt").split(" ");
						String[] timeArr = object.getString("at").split(" ");
						seg.setDepDate(setDate(timeDep[0])); // depDate
						seg.setDeptime(timeDep[1]); // depTime
						seg.setArrDate(setDate(timeArr[0])); // arrDate
						seg.setArrtime(timeArr[1]); // arrTime
						seg.setDepairport(object.getString("dp")); // depairport
						seg.setArrairport(object.getString("ds")); // arrairport
						seg.setCompany(object.getString("ac")); // company

						if (t == 0) { // 去程
							gosegs.add(seg);
							flightNoList.add(flightNo);
						}
						if (t == 1) { // 返程
							resegs.add(seg);
							flightRetNoList.add(flightNo); // 返程航班号
						}
					}
					flightDetail.setFlightno(flightNoList);
				}
				flightDetail.setPrice(priceMap.get(key));
				flightDetail.setTax(0d);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				flightDetail.setDepdate(sdf.parse(searchParam.getDepDate()));
				flightDetail.setMonetaryunit(ojson.getString("currency"));
				flightDetail.setDepcity(searchParam.getDep());
				flightDetail.setArrcity(searchParam.getArr());
				flightDetail.setWrapperid(searchParam.getWrapperid());

				rtFlight.setDetail(flightDetail); // detail
				rtFlight.setInfo(gosegs); // 去程航班段
				rtFlight.setRetdepdate(sdf.parse(searchParam.getRetDate())); // 返程日期
				rtFlight.setRetflightno(flightRetNoList); // 返程航班号
				rtFlight.setRetinfo(resegs); // 返程航班段
				flightList.add(rtFlight);
			}
		} catch (Exception e) {// 解析失败
			e.printStackTrace();
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
		}
		result.setRet(true);
		result.setStatus(Constants.SUCCESS);
		result.setData(flightList);
		return result;

	}

	private String setDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		return sdf2.format(sdf.parse(date));
	}

	/**
	 * @return 13位的随机数
	 */
	private String getRandomNum() {
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(13);
		for (int i = 0; i < 13; i++) {
			sb.append((char) ('0' + rnd.nextInt(10)));
		}
		return sb.toString();
	}
}
