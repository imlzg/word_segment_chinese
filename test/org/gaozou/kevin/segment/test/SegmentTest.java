package org.gaozou.kevin.segment.test;

import junit.framework.TestCase;
import org.gaozou.kevin.segment.Dictionary;
import org.gaozou.kevin.segment.Segment;
import org.gaozou.kevin.segment.Segmenter;
import org.gaozou.kevin.segment.Word;
import org.gaozou.kevin.utility.SimpleException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class SegmentTest extends TestCase {

    public void testDictionary() throws IOException {
        Dictionary dct = Dictionary.getInstance();

        System.out.println(Dictionary.getSize());
        
//        dct.print(new FileWriter(new File("/home/george/develop/dct/test.out")));

        dct.print(new BufferedWriter(new OutputStreamWriter(System.out, "UTF-8")));
        //new BufferedWriter(new OutputStreamWriter(outStream, encoding));
    }

    
    public void testSegment() throws IOException {
//        String str = "This is for test.中文";
        
        String str = "";


//        str += "有 变 态 男 ， 嗜 裸 卧 于 草 丛 ， 某 日 ， 偶 遇 采 蘑 菇 的 小 女 孩 ， 女 孩 且 采 且 数 ， 不 亦 乐 乎： “ 一 个 、 两 个 、 三 个 、 四 个 、 五 个 … … 五 个 … … 五 个 … … 五 个 … … ” 小 女 孩 郁 闷而 去 ， 裸 男 甚 爽 。 翌 日 ， 复 躺 于 是 。 来 一 采 蘑 菇 的 小 熊 ， 且 采 且 数 ， 不 亦 乐 乎 ： “ 一 个 、 两 个 、 三 个 、 四 个 、 五 个 … … 五 个 … … 五 个 … … 六 个 、 七 个 、 八 个 … … ”";

//        str = "新浪体育讯　英超赛季结束后，曼城队一直处在各种动荡之中";

//        str = "长春市长春节致词 ";

//        str = "中华人民共和国";

        
//        str = "欧美超刺激犯罪动作惊险大片";


//        str += "80年代后的应该都有印象没，就是那个鼻子不断长长，环游世界那个，呵呵。\n好不容易才找到，给大家怀怀旧。希望大家喜欢。\n注：只有英文字幕，抱歉。 ";

//        str += "2004年9月 abc语言 PHP4 and asp-JSP ---9坚持脚本八亿人民币语言为主";

//        str = "欧美超刺激犯罪动作惊险大片";
        str += "单身母亲苔丝·科尔曼和自己15岁的女儿安娜对各种事情的意见都不一致，两人总在斗嘴。母亲不理解女儿的高中生活，女儿也不明白做医生的母亲的责任和她的未婚夫。这天两人又闹起来，并认为对方在生活中的表现让自己很不满意，觉得如果相同的情况让自己来应付，肯定可以轻松搞定，活得漂漂亮亮，而不象现在这样。";


        str += "公元前一世纪，初步走向繁荣的汉帝国，面临内外的双重威胁人民共和test中国青年志愿者服务日.";
//        str = "人民共和";

        str += "Reach people when they are actively looking for information about your products and services online, and send targeted visitors directly to what you are offering. With AdWords cost-per-click pricing, it's easy to control costs—and you only pay when people click on your ad.";

//        str = "Help with Google Search, Services and Products";


        str = "中华人民共和国";
        str = "北京精神文明建设";
        str = "赤裸羔羊";
//        str = "在生活中的表现";
//        str = "漂漂亮亮";

//        str = "搜索引擎的发展历史证明，没有做不到只有想不到，让人们更方便准确的获取信息是搜索引擎的使命。";
//        str = "Reach people when they are actively looking for information about your products and services online";
//        str = "是是是是是是是是是是是是是是是是是是是是是是是";

        Segment seg = new Segmenter(new StringReader(str));

//        char[] chars = str.toCharArray();
//        List<Word> l = seg.segment(chars, 0, chars.length);


//        List<Word> l = seg.segment();
//        int size = l.size();
//        System.out.println("size: " + size);
//
//        for (int i = 0; i < size; i++) {
//            System.out.println(l.get(i).toString());
//        }


        int s = 0;
        while (true) {
            Word w = seg.next();
            if (null == w) break;
            s++;
            System.out.println(w);
        }
        System.out.println(s);


//        List<Word> l1 = seg.segment();
//        for (Word w : l1) {
//            System.out.print(w.getText());
//        }

    }


    public void testMove() {
        String p1 = "";
        String p2 = "";


        BufferedReader reader = null;
        List<String> temp = new ArrayList<String>();
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(p1)), "UTF-8"));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    temp.add(line);
                }
            }
        } catch (IOException e) {
            throw new SimpleException("problem reading word list.", e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        Collections.sort(temp);

        OutputStream out = null;
        try {
            out = new FileOutputStream(p2);
            PrintStream p = new PrintStream(out);
//            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
            for (String i : temp) {
                p.println(new String(i.getBytes("UTF-8"), "UTF-8"));

//                out.write(i.getBytes("UTF-8"));
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void testEx() {
        String regEx = "((\\d{2,4}[-|.|\\s|年])?(\\d{1,2}[-|.|\\s|月])?(\\d{1,2}[日]?)?)";
//        String regEx = "\\d{2,4}年\\d{1,2}月\\d{1,2}日";

        Pattern p = Pattern.compile(regEx);

        
//        String[] s = p.split("2004年11月20日");
//        System.out.println("size: "+ s.length);
//        for (int i = 0; i < s.length; i++) {
//            System.out.println(i + " "+ s[i]);
//        }


        Matcher m = p.matcher("网友评论2004年11月20日网友评论");
        System.out.println(m.find());


//        Matcher m = p.matcher("网友评论2004-11-20网友评论");
//        Matcher m = p.matcher("网友评论2004.11网友评论");
//        Matcher m = p.matcher("网友评论网友评论");

        System.out.println(m.find());
        System.out.println(m.group());
        System.out.println(m.group(0));

        System.out.println(m.groupCount());
        for (int i = 1; i <= m.groupCount(); i++) {
            System.out.println(m.group(i));
        }
    }
}
