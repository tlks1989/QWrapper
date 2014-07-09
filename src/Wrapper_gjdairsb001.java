import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

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

/**
 * Aircalin
 * http://www.aircalin.com/
 * http://us.aircalin.com/billet-noumea.php?cc=USA
 * 
 * @author zhangyanchao
 */
public class Wrapper_gjdairsb001 implements QunarCrawler {
	// 无票
	private static final String NO_TICKET = "We are unable to find recommendations for the date(s) / time(s) specified.";
	// 系统使用的货币单位
	public static String CURRENCY = "USD";

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();

		// Brisbane - Noumea July 22, 2014
		// searchParam.setDep("BNE"); // INVALID_DATE
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-07-22");
		// searchParam.setDep("BNE");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-07-18");

		// searchParam.setDep("HNL");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-09-19");
		// searchParam.setDep("SFO");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-07-31");

		// searchParam.setDep("AKL");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-09-28");

		searchParam.setDep("LAX");
		searchParam.setArr("NOU");
		searchParam.setDepDate("2014-09-28");
		// searchParam.setDepDate("2014-09-30");
		searchParam.setWrapperid("gjdairsb001");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjdairsb001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjdairsb001().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
			System.out.println(jsonObject.toJSONString());

			List<OneWayFlightInfo> flightList = (List<OneWayFlightInfo>) result.getData();
			System.out.println(flightList.size());
			// for (OneWayFlightInfo in : flightList) {
			// System.out.println(in.getInfo().toString());
			// System.out.println(in.getDetail().toString());
			// System.out.println("");
			// }
		} else {
			System.out.println(result.getStatus());
		}
	}

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {
		String bookingUrlPre = "";
		try {
			bookingUrlPre = String
					.format("http://wftc3.e-travel.com/plnext/FPCaircalin/Override.action?__utma=%s&__utmb=%s&__utmc=1&__utmx=-&__utmz=%s&__utmv=-&__utmk=%s",
							URLEncoder.encode("1.1986134345.1404701675.1404707750.1404711913.3", "utf-8"), URLEncoder
									.encode("1.1.10.1404711913", "utf-8"), URLEncoder.encode(
									"1.1404711913.3.3.utmcsr=wl.aircalin.com|utmccn=referral|utmcmd=referral|utmcct=/",
									"utf-8"), getRandomNum());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		BookingResult bookingResult = new BookingResult();

		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		// 获取年月日
		String[] dates = arg0.getDepDate().split("-");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("ARRANGE_BY", "E");
		map.put("B_ANY_TIME_1", "TRUE");
		map.put("B_ANY_TIME_2", "TRUE");
		map.put("B_DATE_1", dates[0] + dates[1] + dates[2] + "0000");
		map.put("B_LOCATION_1", arg0.getDep());
		map.put("COMMERCIAL_FARE_FAMILY_1", "ECO");
		map.put("DATE_RANGE_QUALIFIER_1", "C");
		map.put("DATE_RANGE_QUALIFIER_2", "C");
		map.put("DATE_RANGE_VALUE", "2");
		map.put("DATE_RANGE_VALUE_1", "3");
		map.put("DATE_RANGE_VALUE_2", "");
		map.put("DIRECT_LOGIN", "No");
		map.put("DIRECT_NON_STOP", "FALSE");
		map.put("DISPLAY_TYPE", "2");
		map.put("EMBEDDED_TRANSACTION", "FlexPricerAvailability");
		map.put("EXTERNAL_ID", "BOOKING");
		map.put("E_LOCATION_1", arg0.getArr());
		map.put("LANGUAGE", "GB");
		map.put("PRICING_TYPE", "I");
		map.put("REFERRER", "http://us.aircalin.com/billet-noumea.php");
		map.put("REFRESH", "0");
		map.put("SITE", "CAQFCAQF");
		map.put("SO_QUEUE_CATEGORY", "6");
		map.put("SO_QUEUE_NUMBER", "0");
		map.put("SO_QUEUE_OFFICE_ID", "NOUSB0980");
		map.put("SO_SITE_BOOL_ISSUE_ETKT", "TRUE");
		map.put("SO_SITE_CMP_DATE_IN_GMT", "TRUE");
		map.put("SO_SITE_EXT_PSPURL", "https://secure.ogone.com/ncol/prod/orderstandard_gen.asp");
		map.put("SO_SITE_FD_DISPLAY_MODE", "0");
		map.put("SO_SITE_MOP_CREDIT_CARD", "FALSE");
		map.put("SO_SITE_MOP_EXT", "TRUE");
		map.put("SO_SITE_OFFICE_ID", "LAXSB08BB");
		map.put("SO_SITE_POINT_OF_SALE", "LAX");
		map.put("SO_SITE_POINT_OF_TICKETING", "LAX");
		map.put("SO_SITE_SPEC_SERV_CHARGEABLE", "TRUE");
		map.put("TRAVELLER_TYPE_1", "ADT");
		map.put("TRIP_FLOW", "Yes");
		map.put("TRIP_TYPE", "O");
		map.put("bookingSearchSubmit", "G0");
		map.put("contentpane__adults", "1");
		map.put("contentpane__cff1", "ECO");
		map.put("contentpane__childs", "0");
		map.put("contentpane__departDate", dates[2] + "/" + dates[1] + "/" + dates[0]);
		map.put("contentpane__infants", "0");

		bookingInfo.setInputs(map);
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;

	}

	@Override
	public String getHtml(FlightSearchParam arg0) {
		QFHttpClient httpClient = null;
		QFPostMethod post = null;
		try {
			String urlPost = String
					.format("http://wftc3.e-travel.com/plnext/FPCaircalin/Override.action?__utma=%s&__utmb=%s&__utmc=1&__utmx=-&__utmz=%s&__utmv=-&__utmk=%s",
							URLEncoder.encode("1.1986134345.1404701675.1404707750.1404711913.3", "utf-8"), URLEncoder
									.encode("1.1.10.1404711913", "utf-8"), URLEncoder.encode(
									"1.1404711913.3.3.utmcsr=wl.aircalin.com|utmccn=referral|utmcmd=referral|utmcct=/",
									"utf-8"), getRandomNum());

			// 生成http对象
			httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			// 获取年月日
			String[] dates = arg0.getDepDate().split("-");
			post = new QFPostMethod(urlPost);
			// 设置post提交表单数据
			NameValuePair[] parametersBody = new NameValuePair[] {
					new NameValuePair("ARRANGE_BY", "E"),
					new NameValuePair("B_ANY_TIME_1", "TRUE"),
					new NameValuePair("B_ANY_TIME_2", "TRUE"),
					new NameValuePair("B_DATE_1", dates[0] + dates[1] + dates[2] + "0000"),
					new NameValuePair("B_LOCATION_1", arg0.getDep()),
					new NameValuePair("COMMERCIAL_FARE_FAMILY_1", "ECO"),
					new NameValuePair("DATE_RANGE_QUALIFIER_1", "C"),
					new NameValuePair("DATE_RANGE_QUALIFIER_2", "C"),
					new NameValuePair("DATE_RANGE_VALUE", "2"),
					new NameValuePair("DATE_RANGE_VALUE_1", "3"),
					new NameValuePair("DATE_RANGE_VALUE_2", ""),
					new NameValuePair("DIRECT_LOGIN", "No"),
					new NameValuePair("DIRECT_NON_STOP", "FALSE"),
					new NameValuePair("DISPLAY_TYPE", "2"),
					new NameValuePair("EMBEDDED_TRANSACTION", "FlexPricerAvailability"),
					new NameValuePair("EXTERNAL_ID", "BOOKING"),
					new NameValuePair("E_LOCATION_1", arg0.getArr()),
					new NameValuePair("LANGUAGE", "GB"),
					new NameValuePair("PRICING_TYPE", "I"),
					new NameValuePair("REFERRER", "http://us.aircalin.com/billet-noumea.php"),
					new NameValuePair("REFRESH", "0"),
					new NameValuePair("SITE", "CAQFCAQF"),
					new NameValuePair("SO_QUEUE_CATEGORY", "6"),
					new NameValuePair("SO_QUEUE_NUMBER", "0"),
					new NameValuePair("SO_QUEUE_OFFICE_ID", "NOUSB0980"),
					new NameValuePair("SO_SITE_BOOL_ISSUE_ETKT", "TRUE"),
					new NameValuePair("SO_SITE_CMP_DATE_IN_GMT", "TRUE"),
					new NameValuePair("SO_SITE_EXT_PSPURL", "https://secure.ogone.com/ncol/prod/orderstandard_gen.asp"),
					new NameValuePair("SO_SITE_FD_DISPLAY_MODE", "0"),
					new NameValuePair("SO_SITE_MOP_CREDIT_CARD", "FALSE"),
					new NameValuePair("SO_SITE_MOP_EXT", "TRUE"), new NameValuePair("SO_SITE_OFFICE_ID", "LAXSB08BB"),
					new NameValuePair("SO_SITE_POINT_OF_SALE", "LAX"),
					new NameValuePair("SO_SITE_POINT_OF_TICKETING", "LAX"),
					new NameValuePair("SO_SITE_SPEC_SERV_CHARGEABLE", "TRUE"),
					new NameValuePair("TRAVELLER_TYPE_1", "ADT"), new NameValuePair("TRIP_FLOW", "Yes"),
					new NameValuePair("TRIP_TYPE", "O"), new NameValuePair("bookingSearchSubmit", "G0"),
					new NameValuePair("contentpane__adults", "1"), new NameValuePair("contentpane__cff1", "ECO"),
					new NameValuePair("contentpane__childs", "0"),
					new NameValuePair("contentpane__departDate", dates[2] + "/" + dates[1] + "/" + dates[0]),
					new NameValuePair("contentpane__infants", "0") };
			post.setRequestBody(parametersBody);
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setRequestHeader("Host", "wftc3.e-travel.com");
			post.setRequestHeader("Referer", "http://us.aircalin.com/billet-noumea.php?cc=USA");
			int postStatus = httpClient.executeMethod(post);
			if (postStatus != HttpStatus.SC_OK) {
				return "Exception";
			}
			return post.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (post != null) {
				post.releaseConnection();
			}
		}
		return "Exception";
	}

	@Override
	public ProcessResultInfo process(String arg0, FlightSearchParam searchParam) {
		String html = arg0;
		// System.out.println(html);
		ProcessResultInfo result = new ProcessResultInfo();
		try {
			if ("Exception".equals(html)) {
				result.setRet(false);
				result.setStatus(Constants.CONNECTION_FAIL);
				return result;
			}
			if (html.contains(NO_TICKET)) {
				result.setRet(false);
				result.setStatus(Constants.INVALID_DATE);
				return result;
			}

			// 去除所有空格换行等空白字符和引号
			String cleanHtml = cleanHtml(html);

			// 航班列表
			List<OneWayFlightInfo> flightList = getFlightListFromHtml(searchParam, cleanHtml);

			// 没有结果
			if (flightList == null || flightList.size() == 0) {
				result.setRet(false);
				result.setStatus(Constants.NO_RESULT);
				return result;
			}

			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(flightList);
		} catch (Exception e) {
			e.printStackTrace();
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
		}
		return result;

	}

	/**
	 * 解析html，获取航班列表
	 */
	public List<OneWayFlightInfo> getFlightListFromHtml(FlightSearchParam searchParam, String cleanHtml)
			throws Exception {

		// 二次请求用
		String sessionId = StringUtils.substringBetween(cleanHtml, "FlexPricerFlightDetailsPopUp.action;jsessionid=",
				"method=getclass=transparentForm");
		// 航班列表
		List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
		// 从html字符串中提取各个航班的信息
		String[] flightHtmlList = getFlightHtmlList(cleanHtml);
		// for (String f : flightHtmlList) {
		// System.out.println(f);
		// System.out.println("");
		// }
		// 循环所有航班信息
		for (String flightHtml : flightHtmlList) {
			// 航班信息
			OneWayFlightInfo info = new OneWayFlightInfo();

			// 1.获取航班信息
			FlightDetail detail = getFlightDetail(searchParam, flightHtml, info);
			if (detail == null) {// 未查找到价格 不处理此航班
				continue;
			}

			// 2.航班飞行中转段列表
			List<FlightSegement> segs = getFlightSegementList(searchParam, flightHtml, sessionId);

			// 3.航班号，有可能一趟飞行要中转，每个中转都有可能是不同的航班号，所以用list
			List<String> flightNoList = new ArrayList<String>();
			for (FlightSegement se : segs) {
				flightNoList.add(se.getFlightno());
			}
			detail.setFlightno(flightNoList);// 航班号列表

			// ---组装航班信息----
			info.setDetail(detail);
			info.setInfo(segs);

			flightList.add(info);

		}
		return flightList;
	}

	/**
	 * 获取航班的detail详情,【缺少航班号列表】
	 */
	private FlightDetail getFlightDetail(FlightSearchParam searchParam, String flightHtml, OneWayFlightInfo info)
			throws Exception {
		// 1.2航班信息
		FlightDetail detail = new FlightDetail();

		// 价格列表
		String[] priceList = StringUtils.substringsBetween(flightHtml, "Lowestpriceis$", "')");

		double price = 0d;// 最低价的价格
		if (priceList != null && priceList.length > 0) {
			price = Double.parseDouble(priceList[0].replaceAll(",", ""));
		} else {
			return null;
		}
		// ---------------start---获取该航班价格最低的信息-----
		// for (String priceHtml : priceList) {// 循环航班的所有价格
		// if ("-".equals(priceHtml)) {
		// continue;
		// }
		// // ￥32,800 $199.05
		// String priceStr = StringUtils.substringBetween(priceHtml, "<divclass=paxPriceDisplay>", "</div>");
		// // 32800
		// String temPrice = priceStr.replaceAll(",", "").replace("$", "");
		// double tmpPrice = 0;
		// if (NumberUtils.isNumber(temPrice)) {
		// tmpPrice = NumberUtils.createDouble(temPrice);
		// }
		// if (0 == price || price > tmpPrice) {
		// price = tmpPrice;
		// }
		// }
		// ---------------end---获取该航班价格最低的信息--------------------------
		detail.setPrice(price);
		detail.setTax(0d);
		detail.setDepdate(getDate(searchParam.getDepDate()));
		detail.setDepcity(searchParam.getDep());
		detail.setArrcity(searchParam.getArr());
		detail.setWrapperid(searchParam.getWrapperid());
		detail.setMonetaryunit(CURRENCY);

		return detail;
	}

	/**
	 * 航班的中转段列表
	 * return List<FlightSegement>
	 */
	protected List<FlightSegement> getFlightSegementList(FlightSearchParam searchParam, String flightHtml,
			String sessionId) throws Exception {

		String html = getSegmentHtml(searchParam, sessionId, flightHtml); // 二次请求
		if (StringUtils.isEmpty(html)) {
			return null;
		}
		// 去除所有空格换行等空白字符和引号
		String cleanHtml = cleanHtml(html);
		// System.out.println(cleanHtml);

		List<FlightSegement> segs = new ArrayList<FlightSegement>();

		// 航班列表
		String[] fSegs = StringUtils.substringsBetween(cleanHtml, "<tr><td><b>segment", "<br/></td></tr>");
		for (int t = 0; t < fSegs.length; t++) {
			// departureCode_0_0value=LAX/>
			String depPort = StringUtils.substringBetween(cleanHtml, "departureCode_0_" + t + "value=", "/>");
			// arrivalCode_0_0value=AKL/>
			String arrPort = StringUtils.substringBetween(cleanHtml, "arrivalCode_0_" + t + "value=", "/>");

			// company airlineCode_0_0value=NZ/>
			String company = StringUtils.substringBetween(cleanHtml, "airlineCode_0_" + t + "value=", "/>"); // company
			// flightNumber_0_0value=5/> // LIAT
			String flightNo = StringUtils.substringBetween(cleanHtml, "flightNumber_0_" + t + "value=", "/>"); // 370

			FlightSegement seg = new FlightSegement();
			seg.setCompany(company); // company
			seg.setFlightno(company + flightNo);// flightNo

			// 出发
			// TueSep3007:30:00GMT2014
			String departTime = StringUtils.substringBetween(cleanHtml, "departureDate_0_" + t + "value=", "/>");
			String tDate = departTime.substring(3, 8); // sep22
			String depDate = departTime.substring(19) + "-" + month2No(tDate.substring(0, 3)) + "-"
					+ tDate.substring(3);// 出发日期
			seg.setDepDate(depDate);
			seg.setDeptime(departTime.substring(8, 13));// 出发时间

			// System.out.println(depDate + " " + departTime.substring(8, 13));
			// 到达
			// 到达时间 arrivalDate_0_0value=TueSep3007:30:00GMT2014/>
			String arrTime = StringUtils.substringBetween(cleanHtml, "arrivalDate_0_" + t + "value=", "/>");
			String aDate = arrTime.substring(3, 8); // sep22
			String arrDate = arrTime.substring(19) + "-" + month2No(aDate.substring(0, 3)) + "-" + aDate.substring(3);// 到达日期
			seg.setArrDate(arrDate);// 到达日期
			seg.setArrtime(arrTime.substring(8, 13));// 到达时间
			// System.out.println(arrDate + " " + arrTime.substring(8, 13));

			seg.setDepairport(depPort);// 出发机场
			seg.setArrairport(arrPort);// 到达机场
			// System.out.println(depPort);
			// System.out.println(arrPort);

			segs.add(seg);
		}
		// System.out.println("");
		return segs;
	}

	public String getSegmentHtml(FlightSearchParam arg0, String sessionId, String flightHtml) {
		QFGetMethod get = null;
		try {
			String flightId = StringUtils.substringBetween(flightHtml, "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_0_",
					"cellspacing");
			if (StringUtils.isEmpty(flightId)) {
				flightId = "0";
			}
			// System.out.println(flightId);
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			String getUrl = "http://wftc3.e-travel.com/plnext/FPCaircalin/FlexPricerFlightDetailsPopUp.action;jsessionid="
					+ sessionId
					+ "?SITE=CAQFCAQF&LANGUAGE=GB&PAGE_TICKET=0&TRIP_TYPE=O&PRICING_TYPE=I&DISPLAY_TYPE=2&ARRANGE_BY=E&FLIGHT_ID_1="
					+ flightId + "&FLIGHT_ID_2=";

			get = new QFGetMethod(getUrl);
			get.getParams().setContentCharset("utf-8");
			String cookie = StringUtils.join(httpClient.getState().getCookies(), "; ");
			// System.out.println("cookie  " + cookie);
			httpClient.getState().clearCookies();
			get.addRequestHeader("Cookie", cookie);
			httpClient.executeMethod(get);
			return get.getResponseBodyAsString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != get) {
				get.releaseConnection();
			}
		}
		return "Exception";
	}

	/**
	 * 返回解析后的date对象
	 * 
	 * @param date--日期字符串 yyyy-MM-dd
	 */
	public Date getDate(String date) throws ParseException {
		return getDateByFormat(date, "yyyy-MM-dd");
	}

	/**
	 * 返回解析后的date对象
	 * 
	 * @param format-日期字符串的格式
	 */
	public static Date getDateByFormat(String date, String formatStr) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return StringUtils.isBlank(date) ? null : format.parse(date);
	}

	/**
	 * 去除所有空格换行等空白字符和引号
	 */
	public String cleanHtml(String html) {
		return html.replaceAll("[\\s\"]", "");// 去除所有空格换行等空白字符和引号
	}

	/**
	 * 从html字符串中提取各个航班的信息
	 */
	public String[] getFlightHtmlList(String cleanHtml) {
		// System.out.println(cleanHtml);

		// 航班列表的开始标记
		String listFlagBegin = "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_0_";
		// 航班列表的结束标记
		String listFlagEnd = "<divclass=etpHolder>";
		// 整个航班列表的html
		String list = StringUtils.substringBetween(cleanHtml, listFlagBegin, listFlagEnd);
		list = "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_0_" + list;
		// System.out.println(list);

		// 用来分隔每个航班信息的分隔符
		// String flightSplitStr = "<tr.*?><tdclass=availabilityFlightCol>";
		String flightSplitStr = "<li><spanstyle=font-weight:bold;display:noneid=.*?>waitlist</span></li></ul></td></tr></tbody></table>";
		// 航班列表
		String[] flightList = list.split(flightSplitStr);
		// for (String f : flightList) {
		// System.out.println(f);
		// System.out.println("");
		// }
		// 删除最后一个无效的内容
		return (String[]) ArrayUtils.remove(flightList, flightList.length - 1);

	}

	/**
	 * 把12小时制的时间转化成24小时制的时间
	 */
	public static String get24Time(String time) {
		if (StringUtils.isEmpty(time)) {
			return null;
		}
		try {
			Format f12 = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
			Format f24 = new SimpleDateFormat("HH:mm");
			return f24.format(f12.parseObject(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 月份转换
	 */
	private String month2No(String month) {
		month = month.toLowerCase();
		if (month.equals("jan")) {
			return "01";
		} else if (month.equals("feb")) {
			return "02";
		} else if (month.equals("mar")) {
			return "03";
		} else if (month.equals("apr")) {
			return "04";
		} else if (month.equals("may")) {
			return "05";
		} else if (month.equals("jun")) {
			return "06";
		} else if (month.equals("jul")) {
			return "07";
		} else if (month.equals("aug")) {
			return "08";
		} else if (month.equals("sep")) {
			return "09";
		} else if (month.equals("oct")) {
			return "10";
		} else if (month.equals("nov")) {
			return "11";
		} else if (month.equals("dec")) {
			return "12";
		}
		return null;
	}

	/**
	 * 克隆 FlightSegement
	 * 
	 * @param seg
	 * @return
	 */
	public static FlightSegement cloneFlightSegement(FlightSegement seg) {
		FlightSegement s = new FlightSegement();
		s.setDepairport(seg.getDepairport());
		s.setDepDate(seg.getDepDate());
		s.setDeptime(seg.getDeptime());
		s.setArrairport(seg.getArrairport());
		s.setArrDate(seg.getArrDate());
		s.setArrtime(seg.getArrtime());
		s.setFlightno(seg.getFlightno());

		return s;
	}

	/**
	 * @return 8位的随机数
	 */
	private String getRandomNum() {
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(8);
		for (int i = 0; i < 8; i++) {
			sb.append((char) ('0' + rnd.nextInt(10)));
		}
		return sb.toString();
	}
}
