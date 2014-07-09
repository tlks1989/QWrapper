import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import com.qunar.qfwrapper.bean.search.RoundTripFlightInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;
import com.qunar.qfwrapper.util.QFPostMethod;

/**
 * Aircalin
 * http://www.aircalin.com/
 * 
 * @author zhangyanchao
 */
public class Wrapper_gjsairsb001 implements QunarCrawler {
	// 无票
	private static final String NO_TICKET = "We are unable to find recommendations for the date(s) / time(s) specified.";
	private static String urlPost = "";
	// 系统使用的货币单位
	public static String CURRENCY = "USD";
	private static Map<String, Double> mapPrice;

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();

		// July 12, 2014 July 24, 2014 INVALID_DATE
		// searchParam.setDep("VLI");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-07-12");
		// searchParam.setRetDate("2014-07-24");

		// searchParam.setDep("LAX");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-09-18");
		// searchParam.setRetDate("2014-09-30");

		// searchParam.setDep("BNE");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-07-18");
		// searchParam.setRetDate("2014-07-30");

		searchParam.setDep("LAX");
		searchParam.setArr("NOU");
		searchParam.setDepDate("2014-08-01");
		searchParam.setRetDate("2014-08-10");

		// searchParam.setDep("SYD");
		// searchParam.setArr("NOU");
		// searchParam.setDepDate("2014-09-12");
		// searchParam.setRetDate("2014-09-30");
		searchParam.setWrapperid("gjsairsb001");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjsairsb001().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjsairsb001().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
			System.out.println(jsonObject.toJSONString());
			List<RoundTripFlightInfo> flightList = (List<RoundTripFlightInfo>) result.getData();
			System.out.println(flightList.size());
			// for (RoundTripFlightInfo in : flightList) {
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
							URLEncoder.encode("1.1986134345.1404701675.1404717521.1404727661.5", "utf-8"), URLEncoder
									.encode("1.11.10.1404727661", "utf-8"), URLEncoder.encode(
									"1.1404727661.5.5.utmcsr=aircalin.com|utmccn=referral|utmcmd=referral|utmcct=/",
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
		String[] datesRe = arg0.getRetDate().split("-");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("ARRANGE_BY", "E");
		map.put("B_ANY_TIME_1", "TRUE");
		map.put("B_ANY_TIME_2", "TRUE");
		map.put("B_DATE_1", dates[0] + dates[1] + dates[2] + "0000");
		map.put("B_DATE_2", datesRe[0] + datesRe[1] + datesRe[2] + "0000");
		map.put("B_LOCATION_1", arg0.getDep());
		map.put("B_LOCATION_2", arg0.getArr());
		map.put("COMMERCIAL_FARE_FAMILY_1", "ECO");
		map.put("DATE_RANGE_QUALIFIER_1", "C");
		map.put("DATE_RANGE_QUALIFIER_2", "C");
		map.put("DATE_RANGE_VALUE", "2");
		map.put("DATE_RANGE_VALUE_1", "3");
		map.put("DATE_RANGE_VALUE_2", "3");
		map.put("DIRECT_LOGIN", "No");
		map.put("DIRECT_NON_STOP", "FALSE");
		map.put("DISPLAY_TYPE", "2");
		map.put("EMBEDDED_TRANSACTION", "FlexPricerAvailability");
		map.put("EXTERNAL_ID", "BOOKING");
		map.put("E_LOCATION_1", arg0.getArr());
		map.put("E_LOCATION_2", arg0.getDep());
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
		map.put("TRIP_TYPE", "R");
		map.put("bookingSearchSubmit", "G0");
		map.put("contentpane__adults", "1");
		map.put("contentpane__cff1", "ECO");
		map.put("contentpane__childs", "0");
		map.put("contentpane__departDate", dates[2] + "/" + dates[1] + "/" + dates[0]);
		map.put("contentpane__returnDate", datesRe[2] + "/" + datesRe[1] + "/" + datesRe[0]);
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
			urlPost = String
					.format("http://wftc3.e-travel.com/plnext/FPCaircalin/Override.action?__utma=%s&__utmb=%s&__utmc=1&__utmx=-&__utmz=%s&__utmv=-&__utmk=%s",
							URLEncoder.encode("1.1986134345.1404701675.1404717521.1404727661.5", "utf-8"), URLEncoder
									.encode("1.11.10.1404727661", "utf-8"), URLEncoder.encode(
									"1.1404727661.5.5.utmcsr=aircalin.com|utmccn=referral|utmcmd=referral|utmcct=/",
									"utf-8"), getRandomNum());
			// 生成http对象
			httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			// 获取年月日
			String[] dates = arg0.getDepDate().split("-");
			String[] datesRe = arg0.getRetDate().split("-");
			post = new QFPostMethod(urlPost);
			// 设置post提交表单数据
			NameValuePair[] parametersBody = new NameValuePair[] {
					new NameValuePair("ARRANGE_BY", "E"),
					new NameValuePair("B_ANY_TIME_1", "TRUE"),
					new NameValuePair("B_ANY_TIME_2", "TRUE"),
					new NameValuePair("B_DATE_1", dates[0] + dates[1] + dates[2] + "0000"),
					new NameValuePair("B_DATE_2", datesRe[0] + datesRe[1] + datesRe[2] + "0000"),
					new NameValuePair("B_LOCATION_1", arg0.getDep()),
					new NameValuePair("B_LOCATION_2", arg0.getArr()),
					new NameValuePair("COMMERCIAL_FARE_FAMILY_1", "ECO"),
					new NameValuePair("DATE_RANGE_QUALIFIER_1", "C"),
					new NameValuePair("DATE_RANGE_QUALIFIER_2", "C"),
					new NameValuePair("DATE_RANGE_VALUE", "2"),
					new NameValuePair("DATE_RANGE_VALUE_1", "3"),
					new NameValuePair("DATE_RANGE_VALUE_2", "3"),
					new NameValuePair("DIRECT_LOGIN", "No"),
					new NameValuePair("DIRECT_NON_STOP", "FALSE"),
					new NameValuePair("DISPLAY_TYPE", "2"),
					new NameValuePair("EMBEDDED_TRANSACTION", "FlexPricerAvailability"),
					new NameValuePair("EXTERNAL_ID", "BOOKING"),
					new NameValuePair("E_LOCATION_1", arg0.getArr()),
					new NameValuePair("E_LOCATION_2", arg0.getDep()),
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
					new NameValuePair("TRIP_TYPE", "R"), new NameValuePair("bookingSearchSubmit", "G0"),
					new NameValuePair("contentpane__adults", "1"), new NameValuePair("contentpane__cff1", "ECO"),
					new NameValuePair("contentpane__childs", "0"),
					new NameValuePair("contentpane__departDate", dates[2] + "/" + dates[1] + "/" + dates[0]),
					new NameValuePair("contentpane__returnDate", datesRe[2] + "/" + datesRe[1] + "/" + datesRe[0]),
					new NameValuePair("contentpane__infants", "0") };
			post.setRequestBody(parametersBody);
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setRequestHeader("Host", "wftc3.e-travel.com");
			post.setRequestHeader("Referer", "http://us.aircalin.com/billet-noumea.php?cc=USA");

			String cookie = StringUtils.join(httpClient.getState().getCookies(), "; ");
			httpClient.getState().clearCookies();
			post.addRequestHeader("Cookie", cookie);

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
		// System.out.println("**********  process start *********");
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
			// System.out.println(cleanHtml);

			// 价格map
			String priceJson = StringUtils.substringBetween(cleanHtml, "vargeneratedJSon=newString('", "');");
			// System.out.println("priceJson**** " + priceJson);
			if (StringUtils.isEmpty(priceJson)) {
				return null;
			}
			// 价格列表
			String[] priceList = StringUtils.substringsBetween(priceJson, "colorId:", "onlineFare");
			if (priceList == null || priceList.length == 0) {
				return null;
			}
			// System.out.println("priceList***  " + priceList.length);
			mapPrice = new HashMap<String, Double>();
			String priceKey = null;
			for (String p : priceList) {
				// maxIDInbound:2,id:43,inboundFlightIDs:|2|,price:{price:$2,566},outboundFlightWaitListStatus:|,maxIDOutbound:2,familyID:2|0,outboundFlights:[null,null,|2|],inboundFlights:[null,null,|2|],colorValue:#F0F3FC,textColorValue:#0033CC,nbOfLastSeatGlobalList:[],outboundFlightIDs:|2|,inboundFlightWaitListStatus:
				// inboundFlightIDs:|0|1|2|,
				// outboundFlightIDs:|0|6|7|1|5|2|3|4|, outboundFlights:[null,|0|],inboundFlights:[|1|],

				// outboundFlights:[null,null,|5|]
				// inboundFlights:[null,null,null,null,null,|2|]

				String outId = StringUtils.substringBetween(p, "outboundFlights:[", "]");
				String inId = StringUtils.substringBetween(p, "inboundFlights:[", "]");
				String price = StringUtils.substringBetween(p, "price:$", "}").replaceAll(",", ""); // 2566

				// System.out.println("++++++++++++++++++++");
				// System.out.println(outId + " *** " + inId + "  " + price);

				String[] id1sT = outId.split(",");
				String[] id2sT = inId.split(",");

				List<String> id1s = getNotNullString(id1sT);
				List<String> id2s = getNotNullString(id2sT);
				// String[] t1 = (String[]) ArrayUtils.remove(id1s, 0);
				// String[] t2 = (String[]) ArrayUtils.remove(id2s, 0);
				double priceTemp = Double.parseDouble(price);
				for (String s1 : id1s) {
					for (String s2 : id2s) {
						priceKey = s1 + "_" + s2;
						// System.out.println(priceKey + "  " + priceTemp);
						if (mapPrice.containsKey(priceKey)) {
							double tmpPrice = mapPrice.get(priceKey);
							if (tmpPrice > priceTemp) {
								// System.out.println("++++ " + tmpPrice + " " + priceTemp);
								mapPrice.put(priceKey, priceTemp);
							}
						} else {
							mapPrice.put(priceKey, priceTemp); // "0_2" "2566" 去程_返程
						}
					}
				}
			}

			// System.out.println("mapPrice***  " + mapPrice.keySet().size());
			// for (String k : mapPrice.keySet()) {
			// System.out.println(k + " ---  " + mapPrice.get(k));
			// }

			// true代表去程航班； false代表返程航班
			// 去程航班列表
			List<OneWayFlightInfo> flightList = getFlightListFromHtml(searchParam, cleanHtml, true);
			// 返程航班列表
			List<OneWayFlightInfo> reflightList = getFlightListFromHtml(searchParam, cleanHtml, false);
			System.out.println("flightList.size() ***  " + flightList.size());
			System.out.println("reflightList.size() *** " + reflightList.size());

			// 没有结果
			if (flightList == null || flightList.size() == 0 || reflightList == null || reflightList.size() == 0) {
				result.setRet(false);
				result.setStatus(Constants.NO_RESULT);
				return result;
			}

			List<RoundTripFlightInfo> roundList = new ArrayList<RoundTripFlightInfo>(); // 往返列表
			// 去返航班组合:笛卡尔积排列组合
			for (OneWayFlightInfo out : flightList) {
				for (OneWayFlightInfo in : reflightList) {
					RoundTripFlightInfo roundInfo = new RoundTripFlightInfo();

					FlightDetail detail = cloneDetail(out.getDetail());
					// 1_0 0_0 1_2 0_0 左列去程 右列返程组合
					// System.out.println(in.getDetail().getSource() + " " + detail.getSource());
					String key = detail.getSource().split("_")[1] + "_" + in.getDetail().getSource().split("_")[1];
					Double priceT = mapPrice.get(key);

					// System.out.println(key + " --- " + priceTemp);
					if (priceT == null) {
						System.out.println("null--- " + key);
						continue;
					}

					detail.setPrice(priceT);// 价格为来往的总价格
					roundInfo.setDetail(detail);// detail
					roundInfo.setInfo(cloneFlightSegementList(out.getInfo()));// 去程航班段
					roundInfo.setOutboundPrice(out.getDetail().getPrice());// 去程价格
					roundInfo.setRetdepdate(in.getDetail().getDepdate());// 返程日期
					roundInfo.setRetflightno(in.getDetail().getFlightno()); // 返程航班号
					roundInfo.setRetinfo(cloneFlightSegementList(in.getInfo()));// 返程航班段
					roundInfo.setReturnedPrice(in.getDetail().getPrice());// 返程价格

					roundList.add(roundInfo);
				}
			}

			for (RoundTripFlightInfo r : roundList) { // source置空
				r.getDetail().setSource("");
			}
			// System.out.println("**********  process end *********");
			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(roundList);
		} catch (Exception e) {
			e.printStackTrace();
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL + getExMessage(e));
		}
		return result;

	}

	/**
	 * 解析html，获取航班列表
	 * returnFlag true代表去程航班； false代表返程航班
	 */
	public List<OneWayFlightInfo> getFlightListFromHtml(FlightSearchParam searchParam, String cleanHtml,
			boolean returnFlag) throws Exception {

		// System.out.println("cleanHtml *** ******* " + cleanHtml);

		// 二次请求用
		String sessionId = StringUtils.substringBetween(cleanHtml, "FlexPricerFlightDetailsPopUp.action;jsessionid=",
				"method=getclass=transparentForm");
		// 航班列表
		List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
		// 从html字符串中提取各个航班的信息
		String[] flightHtmlList = getFlightHtmlList(cleanHtml, returnFlag);

		// for (String f : flightHtmlList) {
		// System.out.println(f);
		// System.out.println("");
		// }

		// 循环所有航班信息
		for (String flightHtml : flightHtmlList) {
			// 航班信息
			OneWayFlightInfo info = new OneWayFlightInfo();

			// 1.获取航班信息
			FlightDetail detail = getFlightDetail(searchParam, flightHtml, info, returnFlag);
			if (detail == null) {// 未查找到价格 不处理此航班
				continue;
			}

			// 2.航班飞行中转段列表
			List<FlightSegement> segs = getFlightSegementList(searchParam, flightHtml, sessionId, returnFlag);

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
	private FlightDetail getFlightDetail(FlightSearchParam searchParam, String flightHtml, OneWayFlightInfo info,
			boolean isOutBound) throws Exception {
		// System.out.println("flightHtml ** " + flightHtml);
		FlightDetail detail = new FlightDetail();

		// ---------------end---获取该航班价格最低的信息--------------------------
		// 0_0 0_1 0_2 0:去程 012:列标 --- 1:返程 012:列标
		String infoPrice = StringUtils.substringBetween(flightHtml, "id=CELL1_", "onmouseover");
		// System.out.println("infoPrice***  " + infoPrice);
		// detail.setPrice(0d); // 组合的时候处理
		detail.setTax(0d);
		detail.setDepdate(getDate(isOutBound ? searchParam.getDepDate() : searchParam.getRetDate()));
		detail.setDepcity(isOutBound ? searchParam.getDep() : searchParam.getArr());
		detail.setArrcity(isOutBound ? searchParam.getArr() : searchParam.getDep());
		detail.setWrapperid(searchParam.getWrapperid());
		detail.setMonetaryunit(CURRENCY);
		detail.setSource(infoPrice);

		return detail;
	}

	/**
	 * 航班的中转段列表
	 * return List<FlightSegement>
	 */
	protected List<FlightSegement> getFlightSegementList(FlightSearchParam searchParam, String flightHtml,
			String sessionId, boolean returnFlag) throws Exception {
		List<FlightSegement> segs = new ArrayList<FlightSegement>();

		String html = getSegmentHtml(searchParam, sessionId, flightHtml, returnFlag); // 二次请求
		if (StringUtils.isEmpty(html) || "Exception".equals(html)) {
			System.out.println("**********  fail  ***********");
			return null;
		}
		// 去除所有空格换行等空白字符和引号
		String cleanHtmlSecond = cleanHtml(html);

		if (!returnFlag) {
			System.out.println("");
			System.out.println(cleanHtmlSecond);
		}

		// 航班列表
		String[] fSegs = StringUtils.substringsBetween(cleanHtmlSecond, "<tr><td><b>segment", "<br/></td></tr>");

		// for (String s : fSegs) {
		// System.out.println(s);
		// }
		// System.out.println("");

		// if (fSegs != null && fSegs.length > 0) {
		for (int t = 0; t < fSegs.length; t++) {
			// departureCode_0_0value=LAX/>
			String depPort = StringUtils.substringBetween(cleanHtmlSecond, "departureCode_0_" + t + "value=", "/>");
			// arrivalCode_0_0value=AKL/>
			String arrPort = StringUtils.substringBetween(cleanHtmlSecond, "arrivalCode_0_" + t + "value=", "/>");

			// company airlineCode_0_0value=NZ/>
			String company = StringUtils.substringBetween(cleanHtmlSecond, "airlineCode_0_" + t + "value=", "/>"); // company
			// flightNumber_0_0value=5/> // LIAT
			String flightNo = StringUtils.substringBetween(cleanHtmlSecond, "flightNumber_0_" + t + "value=", "/>"); // 370

			FlightSegement seg = new FlightSegement();
			seg.setCompany(company); // company
			seg.setFlightno(company + flightNo);// flightNo

			// 出发
			// TueSep3007:30:00GMT2014
			String departTime = StringUtils.substringBetween(cleanHtmlSecond, "departureDate_0_" + t + "value=", "/>");
			String tDate = departTime.substring(3, 8); // sep22
			String depDate = departTime.substring(19) + "-" + month2No(tDate.substring(0, 3)) + "-"
					+ tDate.substring(3);// 出发日期
			seg.setDepDate(depDate);
			seg.setDeptime(departTime.substring(8, 13));// 出发时间

			// System.out.println(depDate + " " + departTime.substring(8, 13));
			// 到达
			// 到达时间 arrivalDate_0_0value=TueSep3007:30:00GMT2014/>
			String arrTime = StringUtils.substringBetween(cleanHtmlSecond, "arrivalDate_0_" + t + "value=", "/>");
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
		// }
		// System.out.println("");
		return segs;
	}

	public String getSegmentHtml(FlightSearchParam arg0, String sessionId, String flightHtml, boolean returnFlag) {
		// if (returnFlag) {
		// System.out.println("flightHtml***  " + flightHtml);
		// }
		QFGetMethod get = null;
		try {
			String flightId = null;
			if (returnFlag) {
				flightId = StringUtils.substringBetween(flightHtml, "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_0_",
						"cellspacing");
			} else {
				flightId = StringUtils.substringBetween(flightHtml, "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_1_",
						"cellspacing");
			}
			// System.out.println(flightId);
			// if (StringUtils.isEmpty(flightId)) {
			// flightId = "0";
			// }
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			String getUrl = null;
			if (returnFlag) {
				getUrl = "http://wftc3.e-travel.com/plnext/FPCaircalin/FlexPricerFlightDetailsPopUp.action;jsessionid="
						+ sessionId
						+ "?SITE=CAQFCAQF&LANGUAGE=GB&PAGE_TICKET=0&TRIP_TYPE=R&PRICING_TYPE=I&DISPLAY_TYPE=2&ARRANGE_BY=E&FLIGHT_ID_1="
						+ flightId + "&FLIGHT_ID_2=";
			} else {
				getUrl = "http://wftc3.e-travel.com/plnext/FPCaircalin/FlexPricerFlightDetailsPopUp.action;jsessionid="
						+ sessionId
						+ "?SITE=CAQFCAQF&LANGUAGE=GB&PAGE_TICKET=0&TRIP_TYPE=R&PRICING_TYPE=I&DISPLAY_TYPE=2&ARRANGE_BY=E&FLIGHT_ID_1="
						+ "&FLIGHT_ID_2=" + flightId;
			}

			get = new QFGetMethod(getUrl);
			get.getParams().setContentCharset("utf-8");
			get.setRequestHeader("Referer", urlPost);
			get.setRequestHeader("Host", "wftc3.e-travel.com");
			String cookie = StringUtils.join(httpClient.getState().getCookies(), "; ");
			// System.out.println("cookie  " + cookie);
			httpClient.getState().clearCookies();
			get.addRequestHeader("Cookie", cookie);

			int getStatus = httpClient.executeMethod(get);
			if (getStatus != HttpStatus.SC_OK) {
				return "Exception";
			}

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
	public String[] getFlightHtmlList(String cleanHtml, boolean returnFlag) {
		// System.out.println(cleanHtml);

		// 航班列表的开始标记
		String listFlagBegin = returnFlag ? "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_0_"
				: "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_1_";
		// 航班列表的结束标记
		String listFlagEnd = returnFlag ? "<tableclass=tableFFResultsHeader2cellspacing" : "<divclass=etpHolder>";
		// 整个航班列表的html
		String list = StringUtils.substringBetween(cleanHtml, listFlagBegin, listFlagEnd);
		if (returnFlag) {
			list = "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_0_" + list;
		} else {
			list = "<tableclass=fdff_TFPfdff_TFPbgrdNSid=FT_1_" + list;
		}
		// System.out.println("list****  " + list);

		// 用来分隔每个航班信息的分隔符
		// String flightSplitStr = "<tr.*?><tdclass=availabilityFlightCol>";
		String flightSplitStr = "<li><spanstyle=font-weight:bold;display:noneid=.*?>waitlist</span></li>";
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

	/**
	 * 克隆FlightDetail
	 * 
	 * @param oldDetail
	 * @return
	 */
	public static FlightDetail cloneDetail(FlightDetail oldDetail) {
		FlightDetail detail = new FlightDetail();
		detail.setArrcity(oldDetail.getArrcity());
		detail.setDepcity(oldDetail.getDepcity());
		detail.setDepdate(oldDetail.getDepdate());
		detail.setFlightno(oldDetail.getFlightno());
		detail.setMonetaryunit(oldDetail.getMonetaryunit());
		detail.setTax(oldDetail.getTax());
		detail.setPrice(oldDetail.getPrice());
		detail.setWrapperid(oldDetail.getWrapperid());
		detail.setSource(oldDetail.getSource());

		return detail;
	}

	/**
	 * 克隆 List<FlightSegement>
	 * 
	 * @param segs
	 * @return
	 */
	public static List<FlightSegement> cloneFlightSegementList(List<FlightSegement> segs) {
		List<FlightSegement> segList = new ArrayList<FlightSegement>();
		for (FlightSegement seg : segs) {
			FlightSegement s = new FlightSegement();
			s.setDepairport(seg.getDepairport());
			s.setDepDate(seg.getDepDate());
			s.setDeptime(seg.getDeptime());
			s.setArrairport(seg.getArrairport());
			s.setArrDate(seg.getArrDate());
			s.setArrtime(seg.getArrtime());
			s.setFlightno(seg.getFlightno());
			segList.add(s);
		}
		return segList;
	}

	public List<String> getNotNullString(String[] temp) {
		List<String> r = new ArrayList<String>();
		for (String t : temp) {
			if (!"null".equals(t)) {
				String[] tt = t.split("\\|");
				for (String f : tt) {
					if (StringUtils.isNotEmpty(f)) {
						r.add(f);
					}
				}
			}
		}
		return r;
	}

	/**
	 * 获取异常信息字符串
	 * 
	 * @param ex 异常对象
	 * @return 异常信息
	 */
	protected String getExMessage(Exception ex) {
		// 获取字符串写对象
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		// 将异常写入字符中
		ex.printStackTrace(pw);
		return sw.toString();
	}

}
