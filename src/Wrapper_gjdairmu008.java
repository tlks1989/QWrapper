import java.io.UnsupportedEncodingException;
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

public class Wrapper_gjdairmu008 implements QunarCrawler {
	// 无票
	private static final String NO_TICKET = "We apologize that there are no sufficient seats on";

	// 系统使用的货币单位
	public static String CURRENCY = "USD";
	private static Map cityMap = null;
	private static Map cityMap2 = null;
	static {
		cityMap = new HashMap<String, String>() {
			{
				put("DUBAI", "DXB");
				put("CAIRNS", "CNS");
				put("MELBOURNE", "MEL");
				put("SYDNEY", "SYD");
				put("DHAKA", "DAC");
				put("VANCOUVER", "YVR");
				put("TorontoPearson", "YYZ");
				put("XingyiAirport", "ACX");
				put("HuangguoshuAirport", "AVA");
				put("Baotou", "BAV");
				put("Beihai", "BHY");
				put("Baoshan", "BSD");
				put("Guangzhou", "CAN");
				put("Zhengzhou", "CGO");
				put("Changchun", "CGQ");
				put("Chaoyang", "CHG");
				put("Chifeng", "CIF");
				put("Changzhi", "CIH");
				put("Chongqing", "CKG");
				put("Changsha", "CSX");
				put("Chengdu", "CTU");
				put("Changzhou", "CZX");
				put("Datong", "DAT");
				put("Daxian", "DAX");
				put("DaoCheng", "DCY");
				put("Dandong", "DDG");
				put("DiqingShangrilaAirport", "DIG");
				put("Dalian", "DLC");
				put("DaliCity", "DLU");
				put("Dunhuang", "DNH");
				put("DongYingAirport", "DOY");
				put("Daqing", "DQA");
				put("Dongsheng", "DSN");
				put("Dayong", "DYG");
				put("Enshi", "ENH");
				put("Yanan", "ENY");
				put("Fuzhou", "FOC");
				put("Fuyang", "FUG");
				put("Fuoshan", "FUO");
				put("Golmud", "GOQ");
				put("Haikou", "HAK");
				put("HANDAN", "HDG");
				put("Hohhot", "HET");
				put("Hefei", "HFE");
				put("Hangzhou", "HGH");
				put("HUAIAN", "HIA");
				put("Hailar", "HLD");
				put("Ulanhot", "HLH");
				put("Hami", "HMI");
				put("ShennongjiaAirport", "HPG");
				put("Harbin", "HRB");
				put("Zhoushan", "HSN");
				put("Hotan", "HTN");
				put("Huangyan", "HYN");
				put("Yinchuan", "INC");
				put("Qingyang", "IQN");
				put("Jiayuguan", "JGN");
				put("JINGGANGSHAN", "JGS");
				put("XishuangbannaGasaAirport", "JHG");
				put("QianjiangWulingshanAirport", "JIQ");
				put("Jiujiang", "JIU");
				put("Jiamusi", "JMU");
				put("Jining", "JNG");
				put("Jinzhou", "JNZ");
				put("QUZHOU", "JUZ");
				put("JixiXingkaihuAirport", "JXA");
				put("JIUZHAIGOU", "JZH");
				put("KangDing", "KGT");
				put("Kashi", "KHG");
				put("Nanchang", "KHN");
				put("KaiLiHuangPingJiChang", "KJH");
				put("KunmingChangShui", "KMG");
				put("Ganzhou", "KOW");
				put("Korla", "KRL");
				put("Guiyang", "KWE");
				put("Guilin", "KWL");
				put("YichunlinduAirport", "LDS");
				put("LanzhouAirport", "LHW");
				put("LijiangCity", "LJG");
				put("LvLiangDaWuAirport", "LLV");
				put("LINCANG", "LNJ");
				put("Luxi", "LUM");
				put("Lhasa", "LXA");
				put("Luoyang", "LYA");
				put("Lianyungang", "LYG");
				put("Linyi", "LYI");
				put("Liuzhou", "LZH");
				put("Luzhou", "LZO");
				put("Mudanjiang", "MDG");
				put("MianYang", "MIG");
				put("Nanchong", "NAO");
				put("BeijingNanyuanAirport", "NAY");
				put("Qiqihar", "NDG");
				put("Ningbo", "NGB");
				put("ElikunshaAirport", "NGQ");
				put("Nanjing", "NKG");
				put("Nanning", "NNG");
				put("Nanyang", "NNY");
				put("Nantong", "NTG");
				put("ManzhouliXijiaoAirport", "NZH");
				put("BeijingCapitalAirport", "PEK");
				put("ShanghaiPuDongAirport", "PVG");
				put("ShanghaiHongqiao", "SHA");
				put("Shenyang", "SHE");
				put("Qinhuangdao", "SHP");
				put("Shijiazhuang", "SJW");
				put("Shantou", "SWA");
				put("Simao", "SYM");
				put("Sanya", "SYX");
				put("Shenzhen", "SZX");
				put("Qingdao", "TAO");
				put("TENGCHONG", "TCZ");
				put("Tongliao", "TGO");
				put("Jinan", "TNA");
				put("Tianjin", "TSN");
				put("TangshanSannvheAirport", "TVS");
				put("Huangshan", "TXN");
				put("Taiyuan", "TYN");
				put("Urumqi", "URC");
				put("Yulin", "UYN");
				put("Weihai", "WEH");
				put("WenshanAirport", "WNH");
				put("Wenzhou", "WNZ");
				put("WuHai", "WUA");
				put("Wuhan", "WUH");
				put("Wuyishan", "WUS");
				put("Wuxi", "WUX");
				put("WANZHOU", "WXN");
				put("Xiangfan", "XFN");
				put("Xichang", "XIC");
				put("Xilinhot", "XIL");
				put("XianXianyangAirport", "XIY");
				put("Xiamen", "XMN");
				put("Xining", "XNN");
				put("Xuzhou", "XUZ");
				put("Yibin", "YBP");
				put("YUNCHENG", "YCU");
				put("Yichang", "YIH");
				put("Yanji", "YNJ");
				put("Yantai", "YNT");
				put("Yancheng", "YNZ");
				put("yushu", "YUS");
				put("Zhaotong", "ZAT");
				put("Zhanjiang", "ZHA");
				put("ZhongweiAirport", "ZHY");
				put("ZhangjiakouNingAirport", "ZQZ");
				put("ZHUHAIJINWANAIRPORT", "ZUH");
				put("Zunyi", "ZYI");
				put("FRANKFURT", "FRA");
				put("PARIS", "CDG");
				put("LONDON", "LHR");
				put("HONGKONG", "HKG");
				put("DENPASAR", "DPS");
				put("CALCUTTA", "CCU");
				put("DELHI", "DEL");
				put("RomeFiumicino", "FCO");
				put("ASAHIGAWA", "AKJ");
				put("CHITOSE", "CTS");
				put("SHIZUOKA", "FSZ");
				put("FUKUOKA", "FUK");
				put("HIROSHIMA", "HIJ");
				put("HANEDA", "HND");
				put("NIIGATA", "KIJ");
				put("OSAKA", "KIX");
				put("KAMATSU", "KMQ");
				put("KAGOSHIMA", "KOJ");
				put("MATSUYAMA", "MYJ");
				put("NAGOYA", "NGO");
				put("NAGASAKI", "NGS");
				put("NARITA", "NRT");
				put("OKINAWA", "OKA");
				put("OKAYAMA", "OKJ");
				put("TOYAMA", "TOY");
				put("PHNOMPENH", "PNH");
				put("SIEMREAP", "REP");
				put("CHEONGJU", "CJJ");
				put("CHEJU", "CJU");
				put("GIMPO", "GMP");
				put("SEOUL", "ICN");
				put("GWANGJU", "MWX");
				put("PUSAN", "PUS");
				put("TAEGU", "TAE");
				put("VIENTIANE", "VTE");
				put("COLOMBO", "CMB");
				put("MANDALAY", "MDL");
				put("YANGON", "RGN");
				put("MACAU", "MFM");
				put("MALE", "MLE");
				put("KUALALUMPUR", "KUL");
				put("KATHMANDU", "KTM");
				put("MANILA", "MNL");
				put("Moscow", "SVO");
				put("SINGAPORE", "SIN");
				put("BANGKOK", "BKK");
				put("ChiangRaiIntlAirport", "CEI");
				put("CHIANGMAI", "CNX");
				put("PHUKET", "HKT");
				put("KAOHSIUNG", "KHH");
				put("CHINGCHUANKANGAIRPORT", "RMQ");
				put("TaipeiTaoyuan", "TPE");
				put("TaipeiSongshan", "TSA");
				put("HONOLULU", "HNL");
				put("NEWYORKJOHNFKENNEDY", "JFK");
				put("LOSANGELES", "LAX");
				put("SanFrancisco", "SFO");
				put("SAIPAN", "SPN");
				put("DANANG", "DAD");
				put("HANOI", "HAN");
				put("HOCHIMINHCITY", "SGN");
			}
		};
	}

	static {
		cityMap2 = new HashMap<String, String>() {
			{
				put("MNL", "MANILA");
				put("PVG", "Shanghai Pu Dong Airport");
				put("DAT", "Datong");
				put("DIG", "Diqing Shangrila Airport");
				put("HSN", "Zhoushan");
				put("KMG", "Kunming ChangShui");
				put("LHW", "Lanzhou Airport");
				put("LXA", "Lhasa");
				put("SYM", "Simao");
				put("SZX", "Shenzhen");
				put("WUX", "Wuxi");
				put("XFN", "Xiangfan");
				put("XNN", "Xining");
				put("CGQ", "Changchun");
				put("DLC", "Dalian");
				put("HIA", "HUAIAN");
				put("HRB", "Harbin");
				put("NNG", "Nanning");
				put("URC", "Urumqi");
				put("WUA", "WuHai");
				put("WUS", "Wuyishan");
				put("HAK", "Haikou");
				put("JGN", "Jiayuguan");
				put("LYG", "Lianyungang");
				put("SJW", "Shijiazhuang");
				put("SWA", "Shantou");
				put("SYX", "Sanya");
				put("TSN", "Tianjin");
				put("WUH", "Wuhan");
				put("XIL", "Xilinhot");
				put("ACX", "Xingyi Airport");
				put("CGO", "Zhengzhou");
				put("CIF", "Chifeng");
				put("DYG", "Dayong");
				put("HDG", "HANDAN");
				put("JZH", "JIUZHAIGOU");
				put("KWE", "Guiyang");
				put("MDG", "Mudanjiang");
				put("NKG", "Nanjing");
				put("PEK", "Beijing Capital Airport");
				put("TAO", "Qingdao");
				put("TNA", "Jinan");
				put("TYN", "Taiyuan");
				put("UYN", "Yulin");
				put("XIY", "Xi an Xianyang Airport");
				put("YNT", "Yantai");
				put("ZUH", "ZHUHAI JINWAN AIRPORT");
				put("CTU", "Chengdu");
				put("LDS", "Yichun lindu Airport");
				put("LJG", "Lijiang City");
				put("LZO", "Luzhou");
				put("SZV", "Suzhou");
				put("WEH", "Weihai");
				put("CSX", "Changsha");
				put("FOC", "Fuzhou");
				put("HFE", "Hefei");
				put("INC", "Yinchuan");
				put("KHG", "Kashi");
				put("KHN", "Nanchang");
				put("LYI", "Linyi");
				put("LZH", "Liuzhou");
				put("NAO", "Nanchong");
				put("SHP", "Qinhuangdao");
				put("BHY", "Beihai");
				put("CAN", "Guangzhou");
				put("DNH", "Dunhuang");
				put("HET", "Hohhot");
				put("KWL", "Guilin");
				put("WNZ", "Wenzhou");
				put("CKG", "Chongqing");
				put("ENY", "Yanan");
				put("HYN", "Huangyan");
				put("KOW", "Ganzhou");
				put("LYA", "Luoyang");
				put("SHE", "Shenyang");
				put("XMN", "Xiamen");
				put("YNJ", "Yanji");
				put("JFK", "NEW YORK JOHN F KENNEDY");
				put("MEL", "MELBOURNE");
				put("SFO", "San Francisco");
				put("SYD", "SYDNEY");
				put("CDG", "PARIS");
				put("NRT", "NARITA");
				put("SIN", "SINGAPORE");
				put("CJU", "CHEJU");
				put("HNL", "HONOLULU");
				put("NGO", "NAGOYA");
				put("SVO", "Moscow");
				put("FRA", "FRANKFURT");
				put("KIX", "OSAKA");
				put("LAX", "LOS ANGELES");
				put("LHR", "LONDON");
				put("OKJ", "OKAYAMA");
				put("REP", "SIEM REAP");
				put("TPE", "Taipei Taoyuan");
				put("OKA", "OKINAWA");
				put("SGN", "HO CHI MINH CITY");
				put("DEL", "DELHI");
				put("DXB", "DUBAI");
				put("FCO", "Rome Fiumicino");
				put("ICN", "SEOUL");
				put("MFM", "MACAU");
				put("BKK", "BANGKOK");
				put("DPS", "DENPASAR");
				put("FSZ", "SHIZUOKA");
				put("FUK", "FUKUOKA");
				put("PNH", "PHNOM PENH");
				put("TSA", "Taipei Songshan");
				put("YVR", "VANCOUVER");
				put("HIJ", "HIROSHIMA");
				put("HKG", "HONG KONG");
				put("KUL", "KUALA LUMPUR");
				put("CNS", "CAIRNS");
				put("CTS", "CHITOSE");
				put("MYJ", "MATSUYAMA");

			}
		};
	}

	// http://ph.ceair.com/ 中国东方航空 东航菲律宾

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();
		// searchParam.setDep("PTP"); //INVALID_DATE
		// searchParam.setArr("STX");
		// searchParam.setDepDate("2014-08-20");

		searchParam.setDep("MNL");
		searchParam.setArr("CSX");
		// searchParam.setArr("LAX");
		searchParam.setDepDate("2014-09-22");
		searchParam.setWrapperid("gjdairmu008");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");

		String html = new Wrapper_gjdairmu008().getHtml(searchParam);
		ProcessResultInfo result = new ProcessResultInfo();
		result = new Wrapper_gjdairmu008().process(html, searchParam);
		if (result.isRet() && result.getStatus().equals(Constants.SUCCESS)) {
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
			System.out.println(jsonObject.toJSONString());
			List<OneWayFlightInfo> flightList = (List<OneWayFlightInfo>) result.getData();
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
		String bookingUrlPre = "http://ph.ceair.com/muovc/front/reservation/flight-search!doFlightSearch.shtml";
		BookingResult bookingResult = new BookingResult();
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("cond.tripType", "OW");
		map.put("cond.depCode", arg0.getDep());
		try {
			map.put("cond.arrCode_reveal", URLEncoder.encode((String) cityMap2.get(arg0.getArr()), "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		map.put("cond.arrCode", arg0.getArr());
		map.put("cond.routeType", "3");
		map.put("depDate", arg0.getDepDate());
		map.put("depRtDate", "");
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
			String getUrl = String
					.format("http://ph.ceair.com/muovc/front/reservation/flight-search!doFlightSearch.shtml?cond.tripType=OW&cond.depCode=%s&cond.arrCode=%s&cond.routeType=4&depDate=%s&submit=%s",
							arg0.getDep(), arg0.getArr(), arg0.getDepDate(),
							URLEncoder.encode("Search & Book", "utf-8"));
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
		// System.out.println("cleanHtml  " + cleanHtml);
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
			List<FlightSegement> segs = getFlightSegementList(searchParam, flightHtml);

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
	protected List<FlightSegement> getFlightSegementList(FlightSearchParam searchParam, String flightHtml)
			throws Exception {
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
			String tDate = departTime.substring(5); // sep.22
			String depDate = searchParam.getDepDate().split("-")[0] + "-" + month2No(tDate.substring(0, 3)) + "-"
					+ tDate.substring(4);// 出发日期
			seg.setDepDate(depDate);
			seg.setDeptime(departTime.substring(0, 5));// 出发时间

			// 到达
			String arrTime = StringUtils.substringBetween(segHtml,
					"<td><imgsrc=/muovc/resource/images/icon_time_red.gif/>", "</td>"); // 04:55Sep.22
			String aDate = arrTime.substring(5); // sep.22
			String arrDate = searchParam.getDepDate().split("-")[0] + "-" + month2No(aDate.substring(0, 3)) + "-"
					+ aDate.substring(4);// 到达日期
			seg.setArrDate(arrDate);// 到达日期
			seg.setArrtime(arrTime.substring(0, 5));// 到达时间

			String[] tdArray = StringUtils.substringsBetween(segHtml, "<td>", "</td>");
			// for (String t : tdArray) {
			// System.out.println(t);
			// }

			System.out.println(cityMap.get(tdArray[3]));
			System.out.println(cityMap.get(tdArray[4]));
			// System.out.println("************");
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
	 */
	public String[] getFlightHtmlList(String cleanHtml) {
		// System.out.println(cleanHtml);
		// 航班列表的开始标记
		// <tablecellpadding=0cellspacing=1class=booking_table>
		String listFlagBegin = "class=booking_table>";
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
}
