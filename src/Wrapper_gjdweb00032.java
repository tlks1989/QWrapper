import java.io.UnsupportedEncodingException;
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
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;

public class Wrapper_gjdweb00032 implements QunarCrawler {
	// http://www.hop2.com/

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();
		// searchParam.setDep("PTP"); //INVALID_DATE
		// searchParam.setArr("STX");
		// searchParam.setDepDate("2014-08-20");

		// BGI SDQ 2014-08-16
		searchParam.setDep("BGI");
		searchParam.setArr("SLU");
		searchParam.setDepDate("2014-08-20");
		// searchParam.setDep("LUX"); // Dominica (DOM)
		// searchParam.setArr("BER"); // St. Vincent (SVD)
		// searchParam.setDepDate("2014-06-24");
		// searchParam.setDep("DOM"); // Dominica (DOM)
		// searchParam.setArr("SVD"); // St. Vincent (SVD)
		// searchParam.setDepDate("2014-08-12");
		searchParam.setWrapperid("gjdweb00032");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjdweb00032().getHtml(searchParam);
		// System.out.println(html);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjdweb00032().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			List<OneWayFlightInfo> flightList = (List<OneWayFlightInfo>) result.getData();
			System.out.println(flightList.size());
			for (OneWayFlightInfo in : flightList) {
				System.out.println(in.getInfo().toString());
				System.out.println(in.getDetail().toString());
				System.out.println("");
			}
		} else {
			System.out.println(result.getStatus());
		}
	}

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {
		String bookingUrlPre = "http://www.hop2.com/page/Flight/AirResultForm.aspx";
		String SearchFlightControl1 = null;
		String searchTemp = null;
		try {
			SearchFlightControl1 = java.net.URLEncoder.encode("../../", "utf-8");
			searchTemp = java.net.URLEncoder.encode("SearchFlightControl1$hdPath", "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 获取年月日
		String[] dateDep = arg0.getDepDate().split("-"); // [0]2014 [1]08 [2]01
		String date1 = dateDep[1] + "/" + dateDep[2] + "/" + dateDep[0];
		BookingResult bookingResult = new BookingResult();
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(searchTemp, SearchFlightControl1);
		map.put("type", "oneway");
		map.put("origin1", arg0.getDep());
		map.put("date1", date1);
		map.put("destination1", arg0.getArr());
		map.put("date2", date1);
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
			// 获取年月日
			String[] dateDep = arg0.getDepDate().split("-"); // [0]2014 [1]08 [2]01
			String SearchFlightControl1 = URLEncoder.encode("../../", "utf-8");
			String searchTemp = URLEncoder.encode("SearchFlightControl1$hdPath", "utf-8");
			String date1 = dateDep[1] + "/" + dateDep[2] + "/" + dateDep[0];
			String dateT = URLEncoder.encode(date1, "utf-8");

			// Expedia=on
			// SearchFlightControl1$hdPath=../../
			// adt=1
			// cabin=e
			// chd=0
			// date1=06/25/2014
			// date2=08/13/2014
			// destination1=(BER) Berlin, Germany - All Airports
			// origin1=(LUX) Luxembourg Airport - Luxembourg, Luxembourg
			// type=oneway

			String getUrl = String
					.format("http://www.hop2.com/page/Flight/AirResultForm.aspx?%s=%s&type=oneway&origin1=%s&date1=%s&destination1=%s&date2=%s&adt=1&chd=0&cabin=e",
							searchTemp, SearchFlightControl1, arg0.getDep(), dateT, arg0.getArr(), dateT);
			// http://www.hop2.com/page/Flight/AirResultForm.aspx?SearchFlightControl1%24hdPath=..%2F..%2F&type=oneway&origin1=%28LUX%29+Luxembourg+Airport+-+Luxembourg%2C+Luxembourg&date1=06%2F25%2F2014&destination1=%28BER%29+Berlin%2C+Germany+-+All+Airports&date2=08%2F13%2F2014&adt=1&chd=0&cabin=e&Expedia=on
			System.out.println(getUrl);
			get = new QFGetMethod(getUrl);
			get.getParams().setContentCharset("utf-8");
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			// get.setRequestHeader("Referer",
			// "http://www.hop2.com/page/Flight/AirResultForm.aspx?type=oneway&origin1=BGI&date1=08012014&destination1=SLU&date2=08022014&adt=1&chd=0&cabin=e");
			int getStatus = httpClient.executeMethod(get);

			if (getStatus != HttpStatus.SC_OK) {
				return "Exception";
			}

			while (true) {
				String ranNum = getRandomNum();
				String ajaxUrl = String
						.format("http://www.hop2.com/flight/results?type=oneway&cabin=E&origin1=%s&destination1=%s&date1=%s&origin2=%s&destination2=%s&date2=00000&adt=1&chd=0&near=0000&airline=&nextkey=&_=%s",
								arg0.getDep(), arg0.getArr(), dateT, arg0.getDep(), arg0.getArr(), ranNum);
				// http://www.hop2.com/flight/results?type=oneway&cabin=E&origin1=LUX&destination1=BER&date1=06252014&origin2=BER&destination2=LUX&date2=00000&adt=1&chd=0&near=0000&airline=&nextkey=&_=1403510031985
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
		// System.out.println(arg0);
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

		try {
			String jsonStr = StringUtils.substringBetween(html, "\"itins\":", "]}");
			jsonStr = jsonStr + "]";
			// System.out.println(jsonStr);
			if (StringUtils.isEmpty(jsonStr)) {
				result.setRet(false);
				result.setStatus(Constants.NO_RESULT);
				return result;
			}
			JSONArray ajson = JSON.parseArray(jsonStr);
			// System.out.println(ajson.size());
			if (ajson == null || ajson.size() == 0) {
				result.setRet(false);
				result.setStatus(Constants.NO_RESULT);
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
				OneWayFlightInfo oneWayFlight = new OneWayFlightInfo();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightDetail flightDetail = new FlightDetail();
				List<String> flightNoList = new ArrayList<String>();
				JSONObject ojson = ajson.getJSONObject(i);
				// System.out.println("ojson   " + ojson);
				String key = ojson.getString("key");
				if (existFlag.contains(key)) {
					continue;
				} else {
					existFlag.add(key);
				}

				JSONArray tps = ojson.getJSONArray("tps");
				JSONArray segmentArray = ((JSONObject) tps.get(0)).getJSONArray("segments");
				// System.out.println("segments   " + segmentArray);
				for (int j = 0; j < segmentArray.size(); j++) {
					FlightSegement seg = new FlightSegement();
					JSONObject object = (JSONObject) segmentArray.get(j);
					String flightNo = object.getString("ac") + object.getString("fn"); // flightNo
					flightNoList.add(flightNo);
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
					segs.add(seg);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				flightDetail.setDepdate(sdf.parse(searchParam.getDepDate()));
				flightDetail.setFlightno(flightNoList);
				flightDetail.setMonetaryunit(ojson.getString("currency"));
				flightDetail.setPrice(priceMap.get(key));
				flightDetail.setDepcity(searchParam.getDep());
				flightDetail.setArrcity(searchParam.getArr());
				flightDetail.setWrapperid(searchParam.getWrapperid());
				oneWayFlight.setDetail(flightDetail);
				oneWayFlight.setInfo(segs);
				flightList.add(oneWayFlight);
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
