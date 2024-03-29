package com.z.pyinlistview;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


/**
 *  把开头字母相通的app收集起来
 * 
 * @author berry
 * 
 */
public class AppCollector {

	private static final String[] letters = { "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z", "#" };

	/**
	   * 获取字符串对应的拼音
	   * 
	   * @return
	   */

	  public static String getPinYin(String src) {
	    char[] t1 = null;
	    t1 = src.toCharArray();
	    String[] t2 = new String[t1.length];
	    // 设置汉字拼音输出的格式
	    HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
	    t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	    t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	    t3.setVCharType(HanyuPinyinVCharType.WITH_V);
	    String t4 = "";
	    int t0 = t1.length;
	    try {
	      for (int i = 0; i < t0; i++) {
	        // 判断能否为汉字字符
	        if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
	          // 将汉字的几种全拼都存到t2数组中
	          t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
	          // 取出该汉字全拼的第一种读音并连接到字符串t4后
	          t4 += t2[0] + " ";
	        } else {
	          // 如果不是汉字字符，间接取出字符并连接到字符串t4后
	          t4 += Character.toString(t1[i]);
	        }
	      }
	    } catch (BadHanyuPinyinOutputFormatCombination e) {
	      e.printStackTrace();
	    }
	    return t4;
	  }
}
