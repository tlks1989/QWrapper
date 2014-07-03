import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

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
import com.qunar.qfwrapper.util.QFHttpClient;
import com.qunar.qfwrapper.util.QFPostMethod;

public class Wrapper_gjsairli001 implements QunarCrawler {

	// 无票
	private static final String NO_TICKET = "Sorry, there are no fares available on this date.";

	// 系统使用的货币单位
	public static String CURRENCY = "USD";

	// http://www.liat.com/
	public static void main(String[] args) {

		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setDep("SLU");
		searchParam.setArr("BGI");
		searchParam.setDepDate("2014-07-20");
		searchParam.setRetDate("2014-08-12");
		searchParam.setWrapperid("gjsairli001");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjsairli001().getHtml(searchParam);
		// System.out.println(html);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjsairli001().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
			System.out.println(jsonObject.toJSONString());
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
		String bookingUrlPre = "http://book.liat.com/Select.aspx";
		BookingResult bookingResult = new BookingResult();

		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		// 获取年月日
		String[] dates = arg0.getDepDate().split("-");
		String[] redates = arg0.getRetDate().split("-");
		Map<String, String> map = new LinkedHashMap<String, String>();

		map.put("AvailabilitySearchInputSelectView$ButtonSubmit", "");
		map.put("AvailabilitySearchInputSelectView$TextBoxMarketDestination2", arg0.getDep());
		map.put("AvailabilitySearchInputSelectView$TextBoxMarketOrigin2", arg0.getArr());
		map.put("AvailabilitySearchInputSelectView$promocode", "");
		map.put("ControlGroupPriceAndConverterSelectView$CurrConvertSelectViewAjax$DropDownListConvCurr", "");
		map.put("ControlGroupSelectView$AvailabilityInputSelectView$market1", "");
		map.put("ControlGroupSelectView$AvailabilityInputSelectView$market2", "");
		// new NameValuePair("ControlGroupSelectView$AvailabilityInputSelectView$market1",
		// "0~V~~V12~0950~~1~X|LI~ 370~ ~~BGI~08/19/2014 08:20~SLU~08/19/2014 09:05~"),
		map.put("__EVENTARGUMENT", "");
		map.put("__EVENTTARGET", "");
		map.put("destinationStation2", arg0.getDep());
		map.put("originStation2", arg0.getArr());
		map.put("pageToken", "");
		map.put("AvailabilitySearchInputSelectView$DropDownListMarketDateRange1", "0|0");
		map.put("AvailabilitySearchInputSelectView$DropDownListMarketDateRange2", "0|0");
		map.put("AvailabilitySearchInputSelectView$DropDownListMarketDay1", dates[2]);
		map.put("AvailabilitySearchInputSelectView$DropDownListMarketDay2", redates[2]);
		map.put("AvailabilitySearchInputSelectView$DropDownListMarketMonth1", dates[0] + "-" + dates[1]);
		map.put("AvailabilitySearchInputSelectView$DropDownListMarketMonth2", redates[0] + "-" + redates[1]);
		map.put("AvailabilitySearchInputSelectView$DropDownListPassengerType_ADS", "0");
		map.put("AvailabilitySearchInputSelectView$DropDownListPassengerType_ADT", "1");
		map.put("AvailabilitySearchInputSelectView$DropDownListPassengerType_CHD", "0");
		map.put("AvailabilitySearchInputSelectView$RadioButtonMarketStructure", "RoundTrip");
		map.put("AvailabilitySearchInputSelectView$TextBoxMarketDestination1", arg0.getArr());
		map.put("AvailabilitySearchInputSelectView$TextBoxMarketOrigin1", arg0.getDep());
		map.put("__VIEWSTATE", "/wEPDwUBMGRkbUFOpZl3yd/NgMo6CxihRbceiZ4=");
		map.put("date_picker", arg0.getDepDate());
		map.put("date_picker", arg0.getRetDate());
		map.put("destinationStation1", arg0.getArr());
		map.put("originStation1", arg0.getDep());
		map.put("query", "Search");

		bookingInfo.setInputs(map);
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;

	}

	@Override
	public String getHtml(FlightSearchParam arg0) {
		// ControlGroupSelectView$AvailabilityInputSelectView$market1=0~V~~V12~0950~~1~X|LI~ 771~ ~~SLU~07/20/2014
		// 07:00~BGI~07/20/2014 07:45~
		// ControlGroupSelectView$AvailabilityInputSelectView$market2=0~V~~V12~0950~~1~X|LI~ 361~ ~~BGI~08/20/2014
		// 08:30~SVD~08/20/2014 11:05~POS^LI~ 756~ ~~SVD~08/20/2014 13:50~SLU~08/20/2014 14:20~

		QFHttpClient httpClient = null;
		QFPostMethod post = null;
		try {
			String urlPost = "http://book.liat.com/Select.aspx";
			// 生成http对象
			httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			// 获取年月日
			String[] dates = arg0.getDepDate().split("-");
			String[] redates = arg0.getRetDate().split("-");

			post = new QFPostMethod(urlPost);
			// 设置post提交表单数据
			NameValuePair[] parametersBody = new NameValuePair[] {
					new NameValuePair("AvailabilitySearchInputSelectView$ButtonSubmit", ""),
					new NameValuePair("AvailabilitySearchInputSelectView$TextBoxMarketDestination2", arg0.getDep()),
					new NameValuePair("AvailabilitySearchInputSelectView$TextBoxMarketOrigin2", arg0.getArr()),
					new NameValuePair("AvailabilitySearchInputSelectView$promocode", ""),
					new NameValuePair(
							"ControlGroupPriceAndConverterSelectView$CurrConvertSelectViewAjax$DropDownListConvCurr",
							""),
					new NameValuePair("ControlGroupSelectView$AvailabilityInputSelectView$market1", ""),
					new NameValuePair("ControlGroupSelectView$AvailabilityInputSelectView$market2", ""),
					// new NameValuePair("ControlGroupSelectView$AvailabilityInputSelectView$market1",
					// "0~V~~V12~0950~~1~X|LI~ 370~ ~~BGI~08/19/2014 08:20~SLU~08/19/2014 09:05~"),
					new NameValuePair("__EVENTARGUMENT", ""),
					new NameValuePair("__EVENTTARGET", ""),
					new NameValuePair("destinationStation2", arg0.getDep()),
					new NameValuePair("originStation2", arg0.getArr()),
					new NameValuePair("pageToken", ""),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListMarketDateRange1", "0|0"),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListMarketDateRange2", "0|0"),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListMarketDay1", dates[2]),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListMarketDay2", redates[2]),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListMarketMonth1", dates[0] + "-"
							+ dates[1]),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListMarketMonth2", redates[0] + "-"
							+ redates[1]),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListPassengerType_ADS", "0"),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListPassengerType_ADT", "1"),
					new NameValuePair("AvailabilitySearchInputSelectView$DropDownListPassengerType_CHD", "0"),
					new NameValuePair("AvailabilitySearchInputSelectView$RadioButtonMarketStructure", "RoundTrip"),
					new NameValuePair("AvailabilitySearchInputSelectView$TextBoxMarketDestination1", arg0.getArr()),
					new NameValuePair("AvailabilitySearchInputSelectView$TextBoxMarketOrigin1", arg0.getDep()),
					new NameValuePair("__VIEWSTATE", "/wEPDwUBMGRkbUFOpZl3yd/NgMo6CxihRbceiZ4="),
					new NameValuePair("date_picker", arg0.getDepDate()),
					new NameValuePair("date_picker", arg0.getRetDate()),
					new NameValuePair("destinationStation1", arg0.getArr()),
					new NameValuePair("originStation1", arg0.getDep()), new NameValuePair("query", "Search") };
			post.setRequestBody(parametersBody);
			post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setRequestHeader("Referer", "	http://book.liat.com/Select.aspx");
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

			// true代表去程航班； false代表返程航班
			// 去程航班列表
			List<OneWayFlightInfo> flightList = getFlightListFromHtml(searchParam, cleanHtml, true);
			// 返程航班列表
			List<OneWayFlightInfo> reflightList = getFlightListFromHtml(searchParam, cleanHtml, false);
			// System.out.println(flightList.size());
			// System.out.println(reflightList.size());

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
					detail.setPrice(detail.getPrice() + in.getDetail().getPrice());// 价格为来往的总价格

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

			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(roundList);
		} catch (Exception e) {
			e.printStackTrace();
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
		}
		return result;

	}

	/**
	 * 解析html，获取航班列表
	 * returnFlag true代表去程航班； false代表返程航班
	 */
	public List<OneWayFlightInfo> getFlightListFromHtml(FlightSearchParam searchParam, String cleanHtml,
			boolean returnFlag) throws Exception {

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
			List<FlightSegement> segs = getFlightSegementList(searchParam, flightHtml, returnFlag);

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
	 * 
	 * @param isOutBound - true代表去程航班； false代表返程航班
	 */
	private FlightDetail getFlightDetail(FlightSearchParam searchParam, String flightHtml, OneWayFlightInfo info,
			boolean isOutBound) throws Exception {
		// 1.2航班信息
		FlightDetail detail = new FlightDetail();

		// 价格列表
		String[] priceList = StringUtils.substringsBetween(flightHtml, "<tdclass=fareCol1>", "</td>");

		double price = 0;// 最低价的价格
		// ---------------start---获取该航班价格最低的信息-----
		for (String priceHtml : priceList) {// 循环航班的所有价格
			if ("-".equals(priceHtml)) {
				continue;
			}
			// ￥32,800 $199.05
			String priceStr = StringUtils.substringBetween(priceHtml, "<divclass=paxPriceDisplay>", "</div>");
			// 32800
			String temPrice = priceStr.replaceAll(",", "").replace("$", "");
			double tmpPrice = 0;
			if (NumberUtils.isNumber(temPrice)) {
				tmpPrice = NumberUtils.createDouble(temPrice);
			}
			if (0 == price || price > tmpPrice) {
				price = tmpPrice;
			}
		}
		// ---------------end---获取该航班价格最低的信息--------------------------

		if (price == 0d) {// 没查找到此航班，标识此航班就无效
			return null;
		}
		detail.setPrice(price);
		detail.setTax(0d);
		detail.setDepdate(getDate(isOutBound ? searchParam.getDepDate() : searchParam.getRetDate()));
		detail.setDepcity(isOutBound ? searchParam.getDep() : searchParam.getArr());
		detail.setArrcity(isOutBound ? searchParam.getArr() : searchParam.getDep());
		detail.setWrapperid(searchParam.getWrapperid());
		detail.setMonetaryunit(CURRENCY);

		return detail;
	}

	/**
	 * 航班的中转段列表
	 * return List<FlightSegement>
	 */
	protected List<FlightSegement> getFlightSegementList(FlightSearchParam searchParam, String flightHtml,
			boolean returnFlag) throws Exception {
		// System.out.println(flightHtml);
		List<FlightSegement> segs = new ArrayList<FlightSegement>();

		// 航班列表
		String[] fSegs = StringUtils.substringsBetween(flightHtml, "<divclass=innerInfo>", "<divid=propertyContainer");
		// 出发时间 <DEPTTIME>8:10AM</DEPTTIME>
		String[] departTime = StringUtils.substringsBetween(flightHtml, "<DEPTTIME>", "</DEPTTIME>");
		// 到达时间 <ARRVTIME>9:50AM</ARRVTIME>
		String[] arrTime = StringUtils.substringsBetween(flightHtml, "<ARRVTIME>", "</ARRVTIME>");
		String[] depPort = StringUtils.substringsBetween(flightHtml, "<DEPTSTATIONCODE>", "</DEPTSTATIONCODE>");
		String[] arrPort = StringUtils.substringsBetween(flightHtml, "<ARRVSTATIONCODE>", "</ARRVSTATIONCODE>");
		for (String segHtml : fSegs) {
			segHtml = segHtml.replace("<divclass=spacerTiny></div>", "");
			// System.out.println(segHtml);
			// 航段列表
			String[] segList = StringUtils.substringsBetween(segHtml, "<h6>", "</h6>"); // company LIAT
			String[] flightNo = StringUtils.substringsBetween(segHtml, "<p>Flight:", "</p>"); // 370 371
			String[] arriveDate = StringUtils.substringsBetween(segHtml, "<p>Date:", "</p>"); // 到达日期

			if (segList != null && segList.length > 0) {
				List<String> listTemp = new ArrayList<String>(); // 把航班号放入 处理经停
				int j = 1;
				for (int i = 0; i < segList.length; i++) {
					if (listTemp.contains(flightNo[i])) { // 经停 修改上一个的到达时间 到达机场
						FlightSegement fs = cloneFlightSegement(segs.get(i - j));
						// FlightSegement delete = cloneFlightSegement(segs.get(i - 1));
						fs.setArrairport(arrPort[i]);
						fs.setArrtime(get24Time(arrTime[i])); // arrTime

						arriveDate[i] = arriveDate[i].substring(3);
						String arrDate = arriveDate[i].substring(arriveDate[i].length() - 4) + "-"
								+ month2No(arriveDate[i].substring(2, 5)) + "-" + arriveDate[i].substring(0, 2);
						fs.setArrDate(arrDate); // arriveDate
						segs.remove(segs.size() - 1);
						j++;
						segs.add(fs);
						continue;
					} else {
						listTemp.add(flightNo[i]);
						FlightSegement seg = new FlightSegement();
						seg.setCompany(segList[i].substring(0, 2)); // company
						seg.setFlightno(segList[i].substring(0, 2) + flightNo[i]);// flightNo
						// System.out.println(segList[i].substring(0, 2) + flightNo[i]);
						seg.setDepDate(returnFlag ? searchParam.getDepDate() : searchParam.getRetDate());

						arriveDate[i] = arriveDate[i].substring(3);
						String arrDate = arriveDate[i].substring(arriveDate[i].length() - 4) + "-"
								+ month2No(arriveDate[i].substring(2, 5)) + "-" + arriveDate[i].substring(0, 2);
						seg.setArrDate(arrDate); // arriveDate

						seg.setDeptime(get24Time(departTime[i])); // depTime
						seg.setArrtime(get24Time(arrTime[i])); // arrTime

						seg.setDepairport(depPort[i]);
						seg.setArrairport(arrPort[i]);

						// System.out.print(depPort[i] + " ");
						// System.out.println(departTime[i]);
						//
						// System.out.print(arrPort[i] + " ");
						// System.out.println(arrTime[i]);
						segs.add(seg);
					}
				}
			} else {
				return null;
			}
			// System.out.println("");
		}

		return segs;
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
	public Date getDateByFormat(String date, String formatStr) throws ParseException {
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
	 * returnFlag false：去程----- true:返程
	 */
	public String[] getFlightHtmlList(String cleanHtml, boolean returnFlag) {
		// System.out.println(cleanHtml);
		// 航班列表的开始标记
		String listFlagBegin = returnFlag ? "<tableid=availabilityTable0class=availabilityTable>"
				: "tableid=availabilityTable1class=availabilityTable";
		// 航班列表的结束标记
		String listFlagEnd = "</table>";
		// 整个航班列表的html
		String list = StringUtils.substringBetween(cleanHtml, listFlagBegin, listFlagEnd);
		// System.out.println(list);

		// 用来分隔每个航班信息的分隔符
		String flightSplitStr = "<tr.*?><tdclass=availabilityFlightCol>";
		// 航班列表
		String[] flightList = list.split(flightSplitStr);
		// for (String f : flightList) {
		// System.out.println(f);
		// System.out.println("");
		// }
		// 删除第一个无效的内容
		return (String[]) ArrayUtils.remove(flightList, 0);

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

	public double sum(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.add(bd2).doubleValue();
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
}
