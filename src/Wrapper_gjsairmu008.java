import java.net.URLEncoder;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

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

public class Wrapper_gjsairmu008 implements QunarCrawler {
	// 无票
	private static final String NO_TICKET = "We apologize that there are no sufficient seats on";

	// 系统使用的货币单位
	public static String CURRENCY = "USD";
	private static Map cityMap = null;
	static {
		cityMap = new HashMap<String, String>() {
			{
				put("MANILA", "MNL");
				put("ShanghaiPuDongAirport", "PVG");
				put("Datong", "DAT");
				put("DiqingShangrilaAirport", "DIG");
				put("Zhoushan", "HSN");
				put("KunmingChangShui", "KMG");
				put("LanzhouAirport", "LHW");
				put("Lhasa", "LXA");
				put("Simao", "SYM");
				put("Shenzhen", "SZX");
				put("Wuxi", "WUX");
				put("Xiangfan", "XFN");
				put("Xining", "XNN");
				put("Changchun", "CGQ");
				put("Dalian", "DLC");
				put("HUAIAN", "HIA");
				put("Harbin", "HRB");
				put("Nanning", "NNG");
				put("Urumqi", "URC");
				put("WuHai", "WUA");
				put("Wuyishan", "WUS");
				put("Haikou", "HAK");
				put("Jiayuguan", "JGN");
				put("Lianyungang", "LYG");
				put("Shijiazhuang", "SJW");
				put("Shantou", "SWA");
				put("Sanya", "SYX");
				put("Tianjin", "TSN");
				put("Wuhan", "WUH");
				put("Xilinhot", "XIL");
				put("XingyiAirport", "ACX");
				put("Zhengzhou", "CGO");
				put("Chifeng", "CIF");
				put("Dayong", "DYG");
				put("HANDAN", "HDG");
				put("JIUZHAIGOU", "JZH");
				put("Guiyang", "KWE");
				put("Mudanjiang", "MDG");
				put("Nanjing", "NKG");
				put("BeijingCapitalAirport", "PEK");
				put("Qingdao", "TAO");
				put("Jinan", "TNA");
				put("Taiyuan", "TYN");
				put("Yulin", "UYN");
				put("XianXianyangAirport", "XIY");
				put("Yantai", "YNT");
				put("ZHUHAIJINWANAIRPORT", "ZUH");
				put("Chengdu", "CTU");
				put("YichunlinduAirport", "LDS");
				put("LijiangCity", "LJG");
				put("Luzhou", "LZO");
				put("Suzhou", "SZV");
				put("Weihai", "WEH");
				put("Changsha", "CSX");
				put("Fuzhou", "FOC");
				put("Hefei", "HFE");
				put("Yinchuan", "INC");
				put("Kashi", "KHG");
				put("Nanchang", "KHN");
				put("Linyi", "LYI");
				put("Liuzhou", "LZH");
				put("Nanchong", "NAO");
				put("Qinhuangdao", "SHP");
				put("Beihai", "BHY");
				put("Guangzhou", "CAN");
				put("Dunhuang", "DNH");
				put("Hohhot", "HET");
				put("Guilin", "KWL");
				put("Wenzhou", "WNZ");
				put("Chongqing", "CKG");
				put("Yanan", "ENY");
				put("Huangyan", "HYN");
				put("Ganzhou", "KOW");
				put("Luoyang", "LYA");
				put("Shenyang", "SHE");
				put("Xiamen", "XMN");
				put("Yanji", "YNJ");
				put("NEWYORKJOHNFKENNEDY", "JFK");
				put("MELBOURNE", "MEL");
				put("SanFrancisco", "SFO");
				put("SYDNEY", "SYD");
				put("PARIS", "CDG");
				put("NARITA", "NRT");
				put("SINGAPORE", "SIN");
				put("CHEJU", "CJU");
				put("HONOLULU", "HNL");
				put("NAGOYA", "NGO");
				put("Moscow", "SVO");
				put("FRANKFURT", "FRA");
				put("OSAKA", "KIX");
				put("LOSANGELES", "LAX");
				put("LONDON", "LHR");
				put("OKAYAMA", "OKJ");
				put("SIEMREAP", "REP");
				put("TaipeiTaoyuan", "TPE");
				put("OKINAWA", "OKA");
				put("HOCHIMINHCITY", "SGN");
				put("DELHI", "DEL");
				put("DUBAI", "DXB");
				put("RomeFiumicino", "FCO");
				put("SEOUL", "ICN");
				put("MACAU", "MFM");
				put("BANGKOK", "BKK");
				put("DENPASAR", "DPS");
				put("SHIZUOKA", "FSZ");
				put("FUKUOKA", "FUK");
				put("PHNOMPENH", "PNH");
				put("TaipeiSongshan", "TSA");
				put("VANCOUVER", "YVR");
				put("HIROSHIMA", "HIJ");
				put("HONGKONG", "HKG");
				put("KUALALUMPUR", "KUL");
				put("CAIRNS", "CNS");
				put("CHITOSE", "CTS");
				put("MATSUYAMA", "MYJ");
			}
		};
	}

	// http://ph.ceair.com/ 中国东方航空

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();

		// searchParam.setDep("MNL"); //INVALID_DATE
		// searchParam.setArr("BHY");
		// searchParam.setDepDate("2014-08-01");
		// searchParam.setRetDate("2014-10-23");
		searchParam.setDep("MNL");
		searchParam.setArr("CSX");
		searchParam.setDepDate("2014-08-01");
		searchParam.setRetDate("2014-10-23");
		// searchParam.setDep("MNL");
		// searchParam.setArr("LAX");
		// searchParam.setDepDate("2014-08-01");
		// searchParam.setRetDate("2014-08-27");
		searchParam.setWrapperid("gjdairmu008");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjsairmu008().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjsairmu008().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			List<RoundTripFlightInfo> flightList = (List<RoundTripFlightInfo>) result.getData();
			System.out.println("+++++   " + flightList.size());
			for (RoundTripFlightInfo in : flightList) {
				System.out.println(in.getRetinfo().get(0).getDepDate());
				System.out.println(in.getRetdepdate());

				System.out.println(in.getInfo().toString());
				System.out.println(in.getRetinfo().toString());
				System.out.println(in.getRetflightno().toString());
				System.out.println(in.getDetail().toString());
				System.out.println("");
			}
		} else {
			System.out.println(result.getStatus());
		}
	}

	@Override
	public BookingResult getBookingInfo(FlightSearchParam arg0) {
		String bookingUrlPre = "http://ph.ceair.com/muovc/front/reservation/flight-search!doFlightSearch.shtml";
		// http://ph.ceair.com/muovc/front/reservation/flight-search!doFlightSearch.shtml?cond.tripType=RT&cond.depCode=MNL&cond.arrCode_reveal=Changsha&cond.arrCode=CSX&cond.routeType=3&depDate=2014-08-13&depRtDate=2014-08-27&cond.cabinRank=ECONOMY&submit=Search+%26+Book

		// cond.arrCode=CSX
		// cond.arrCode_reveal=Changsha
		// cond.cabinRank=ECONOMY
		// cond.depCode=MNL
		// cond.routeType=3
		// cond.tripType=RT
		// depDate=2014-08-13
		// depRtDate=2014-08-27
		// submit=Search & Book
		BookingResult bookingResult = new BookingResult();
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("cond.tripType", "RT");
		map.put("cond.depCode", arg0.getDep());
		map.put("cond.arrCode", arg0.getArr());
		map.put("cond.routeType", "3");
		map.put("depDate", arg0.getDepDate());
		map.put("depRtDate", arg0.getRetDate());
		map.put("cond.cabinRank", "ECONOMY");
		bookingInfo.setInputs(map);
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;
	}

	@Override
	public String getHtml(FlightSearchParam arg0) {
		QFGetMethod get = null;
		try {
			QFHttpClient httpClient = new QFHttpClient(arg0, false);
			// 按照浏览器的模式来处理cookie
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			// cond.arrCode=LAX
			// cond.arrCode_reveal=LOS ANGELES
			// cond.depCode=MNL
			// cond.routeType=4
			// cond.tripType=RT
			// depDate=2014-08-01
			// depRtDate=2014-08-27
			// submit=Book Now

			String getUrl = String
					.format("http://ph.ceair.com/muovc/front/reservation/flight-search!doFlightSearch.shtml?cond.tripType=RT&cond.depCode=%s&cond.arrCode=%s&cond.routeType=4&depDate=%s&depRtDate=%s&submit=%s",
							arg0.getDep(), arg0.getArr(), arg0.getDepDate(), arg0.getRetDate(),
							URLEncoder.encode("Book Now", "utf-8"));
			// System.out.println(getUrl);
			get = new QFGetMethod(getUrl);
			String urlGet = ""; // get请求的url
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

	@Override
	public ProcessResultInfo process(String arg0, FlightSearchParam searchParam) {
		// System.out.println(arg0);
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
	 * true代表去程航班； false代表返程航班
	 */
	public List<OneWayFlightInfo> getFlightListFromHtml(FlightSearchParam searchParam, String cleanHtml,
			boolean returnFlag) throws Exception {
		// System.out.println("cleanHtml  " + cleanHtml);
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

		// <tdrowspan=2class=pricecenter><span>-</span></td>
		// 价格列表
		String[] priceList = StringUtils.substringsBetween(flightHtml, "<tdrowspan=2class=pricecenter>", "</td>");

		double price = 0d;// 最低价的价格
		double tax = 0d;// 最低价的tax

		// ---------------start---获取该航班价格最低的信息-----
		for (String priceHtml : priceList) {// 循环航班的所有价格
			if ("<span>-</span>".equals(priceHtml)) {
				continue;
			}
			// System.out.println(priceHtml);
			// <spanclass=tkt_amtstyle=display:none>826</span>
			// <spanclass=tax_amtstyle=display:none>274.5</span>

			// 票价 ￥32,800 $199.05
			String priceStr = StringUtils.substringBetween(priceHtml, "<spanclass=tkt_amtstyle=display:none>",
					"</span>");
			if (StringUtils.isEmpty(priceStr)) {
				continue;
			}
			// 税
			String taxStr = StringUtils.substringBetween(priceHtml, "<spanclass=tax_amtstyle=display:none>", "</span>");
			// 32800
			String temPrice = priceStr.replaceAll(",", "").replace("$", "");
			String temTax = taxStr.replaceAll(",", "").replace("$", "");
			double tmpPrice = 0;
			double tmpTax = 0;
			if (NumberUtils.isNumber(temPrice)) {
				tmpPrice = NumberUtils.createDouble(temPrice);
			}
			if (NumberUtils.isNumber(temTax)) {
				tmpTax = NumberUtils.createDouble(temTax);
			}
			if (0 == price || price > tmpPrice) {
				price = tmpPrice;
				tax = tmpTax;
			}
		}
		// ---------------end---获取该航班价格最低的信息--------------------------

		if (price == 0d) {// 没查找到此航班，标识此航班就无效
			return null;
		}
		detail.setPrice(price);
		detail.setTax(tax);

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
		List<FlightSegement> segs = new ArrayList<FlightSegement>();

		// 航班列表
		String[] fSegs = StringUtils.substringsBetween(flightHtml, "<trclass=booking>", "</tr>");

		// // 出发时间<td><imgsrc=/muovc/resource/images/icon_time_blue.gif/>04:55Sep.22</td>
		// String[] departTime = StringUtils.substringsBetween(flightHtml,
		// "<td><imgsrc=/muovc/resource/images/icon_time_blue.gif/>", "</td>");
		//
		// // 到达时间 <td><imgsrc=/muovc/resource/images/icon_time_red.gif/>08:15Sep.22</td>
		// String[] arrTime = StringUtils.substringsBetween(flightHtml,
		// "<td><imgsrc=/muovc/resource/images/icon_time_red.gif/>", "</td>");

		// <li>From:MANILA,PHILIPPINES-MANILAT1</li>
		// String[] depPort = StringUtils.substringsBetween(flightHtml, "<li>From:", "</DEPTSTATIONCODE>");
		// String[] arrPort = StringUtils.substringsBetween(flightHtml, "<ARRVSTATIONCODE>", "</ARRVSTATIONCODE>");
		for (String segHtml : fSegs) {
			FlightSegement seg = new FlightSegement();
			// 序号、航班号、出发时间、到达时间
			String flightNo = StringUtils.substringBetween(segHtml,
					"<td><imgsrc=/muovc/resource/images/icon_logo_cea.gif/>", "</td>").trim();
			seg.setFlightno(flightNo);// 航班号
			seg.setCompany(flightNo.substring(0, 2)); // company

			// 出发
			String departTime = StringUtils.substringBetween(segHtml,
					"<td><imgsrc=/muovc/resource/images/icon_time_blue.gif/>", "</td>"); // 04:55Sep.22
			seg.setDepDate(returnFlag ? searchParam.getDepDate() : searchParam.getRetDate());// 出发日期

			seg.setDeptime(departTime.substring(0, 5));// 出发时间

			// 到达
			String arrTime = StringUtils.substringBetween(segHtml,
					"<td><imgsrc=/muovc/resource/images/icon_time_red.gif/>", "</td>"); // 04:55Sep.22
			String aDate = departTime.substring(5); // sep.22
			String year = returnFlag ? searchParam.getDepDate().split("-")[0] : searchParam.getRetDate().split("-")[0];
			String arrDate = year + "-" + month2No(aDate.substring(0, 3)) + "-" + aDate.substring(4);// 到达日期
			seg.setArrDate(arrDate);// 到达日期
			seg.setArrtime(arrTime.substring(0, 5));// 到达时间

			String[] tdArray = StringUtils.substringsBetween(segHtml, "<td>", "</td>");
			// for (String t : tdArray) {
			// System.out.println(t);
			// }

			seg.setDepairport((String) cityMap.get(tdArray[3]));// 出发机场
			// System.out.println(tdArray[4]);
			seg.setArrairport((String) cityMap.get(tdArray[4]));// 到达机场
			segs.add(seg);
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
	public static Date getDateByFormat(String date, String formatStr) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return StringUtils.isBlank(date) ? null : format.parse(date);
	}

	/**
	 * 去除所有空格换行等空白字符和引号
	 */
	public String cleanHtml(String html) {
		return html.replaceAll("[\\s\"]", "").replaceAll("<!--区分是飞机还是高铁的图标-->", "").replaceAll("<!--票价和税等等隐藏信息-->", "");// 去除所有空格换行等空白字符和引号
	}

	/**
	 * 从html字符串中提取各个航班的信息
	 * returnFlag true代表去程航班； false代表返程航班
	 */
	public String[] getFlightHtmlList(String cleanHtml, boolean returnFlag) {
		// System.out.println(cleanHtml);
		// 航班列表的开始标记
		// <tablecellpadding=0cellspacing=1class=booking_table>
		// <tablecellpadding=0cellspacing=1class=bluerbooking_table>
		String listFlagBegin = returnFlag ? "class=booking_table>" : "class=bluerbooking_table>";
		// 航班列表的结束标记
		String listFlagEnd = "</table>";
		// 整个航班列表的html
		String list = StringUtils.substringBetween(cleanHtml, listFlagBegin, listFlagEnd);
		// System.out.println("整个航班列表的html " + list);

		// 用来分隔每个航班信息的分隔符
		String flightSplitStr = "<tbody>";
		// 航班列表
		String[] flightList = list.split(flightSplitStr);
		// for (String f : flightList) {
		// System.out.println("++++++");
		// System.out.println(f);
		// System.out.println("*****");
		// }
		// 删除第一个无效的内容
		return (String[]) ArrayUtils.remove(flightList, 0);

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
}
