package org.gaozou.kevin.segment;

import org.gaozou.kevin.utility.PropertiesUtil;
import org.gaozou.kevin.utility.SimpleException;
import org.gaozou.kevin.utility.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public final class SegmentHelper {
    private static final Logger log = LoggerFactory.getLogger(SegmentHelper.class);
    private static final String configFile = "segment.properties";
    private static final String stops="a,an,and,are,as,at,be,but,by,for,if,in,into,is,it,no,not,of,on,or,such,that,the,their,then,there,these,they,this,to,was,will,with";
    private static final String letter = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String num = "0123456789〇零一壹二貳三叁四肆五伍六陸柒七捌八九玖十拾百佰千仟万萬亿億俩两";
    private static final String hyphen = "=+-_@#$%^&*()~<>《》{}[]\"\"''/\\\\\\|";

    public static final List<String> buildWords;
    public static final List<String> noiseWords;
    public static final List<String> stopWords;

    public static final String dctPath;
    public static final String strategy;
    public static final String oddments;
    public static final String pointMark;
    public static final int bufferSize;

    static {
        log.info("Reading config file...");
//        Properties config = new Properties();
//        InputStream stream = ResourceUtil.getResourceAsStream(configFile);
//        try {
//            config.load(stream);
//        } catch (IOException e) {
//            log.warn("Could not read " + configFile);
//            throw new SimpleException("Could not read " + configFile, e);
//        } finally{
//            try {
//                stream.close();
//            } catch (IOException e) {
//                log.error("Could not read " + configFile, e);
//            }
//        }

        Properties config = PropertiesUtil.load(configFile);
        dctPath = config.getProperty("dct.home");
        if (StringUtil.isEmpty(dctPath)) {
            log.warn("not specify dictionary path.");
            throw new SimpleException("not specify dictionary path.");
        }

        String tmp;

        tmp = config.getProperty("build.file");
        if (null != tmp) {
            buildWords = readFile(new File(tmp));
        } else {
            buildWords = new ArrayList<String>();
        }

        tmp = config.getProperty("noise.file");
        if (null != tmp) {
            noiseWords = readFile(new File(tmp));
        } else {
            noiseWords = new ArrayList<String>();
        }

        tmp = config.getProperty("stop.file");
        if (null != tmp) {
            stopWords = readFile(new File(tmp));
        } else {
            stopWords = Arrays.asList(stops.split(","));
        }

        tmp = config.getProperty("strategy");
        strategy = null == tmp ? "mostWords" : tmp;

        tmp = config.getProperty("oddments");
        oddments = null == tmp ? "no" : tmp;

        tmp = config.getProperty("pointMark");
        pointMark = null == tmp ? "no" : tmp;

        tmp = config.getProperty("bufferSize");
        int bsize = 128;
        try {
            bsize = Integer.valueOf(tmp);
        } catch (NumberFormatException e){
            log.info("can not convert bufferSize to integer.");
        }
        bufferSize = bsize;
    }

    public static List<String> readFile(File path) {
        List<String> tmp = new ArrayList<String>();
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0 && line.charAt(0) != '#') {
                    if (line.indexOf(',') != -1) {
                        tmp.addAll(Arrays.asList(line.split(",")));
                    } else {
                        tmp.add(line);
                    }
                }
            }
        } catch (IOException e) {
            log.warn("problem reading word list.");
            throw new SimpleException("problem reading word list.", e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.info("Could not close word list reader.");
                }
            }
        }
        return tmp;
    }


    public static boolean isOddments(char c) {
        if ("yes".equalsIgnoreCase(pointMark)) {
            return ! Character.isWhitespace(c);
        }
        return isLetter(c) || isNumber(c) || StringUtil.isHan(c);
    }

    public static boolean isForBuild(char c) {
        return isLetter(c) || isNumber(c) || buildWords.contains(Character.toString(c));
    }

    public static boolean isLetter(char c) {
        return letter.indexOf(c) != -1;
    }
    public static boolean isNumber(char c) {
        return num.indexOf(c) != -1;
    }
    public static boolean isHyphen(char c) {
        return hyphen.indexOf(c) != -1;
    }

    public static char simpleChar(char c) {
        return StringUtil.toArabicDigit(StringUtil.toLowerCase(StringUtil.toDBC(c)));
    }
}
