package org.linuxlsx.java.tools;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 用来解析 RspServer 的堆栈日志
 *
 * @author linuxlsx
 * @date 2018/9/14
 */
public class RspServerTreadProcess {

    private static final Pattern pattern = Pattern.compile("\".*\"");
    private static final Pattern THREAD_NAME_PATTERN = Pattern.compile("^\".*-\\d+\"$");

    public static void main(String[] args) throws Exception {

        //String file = args[0];
        //String file = "/Users/linuxlsx/Downloads/rsp-server011015186157.center.na61-20180913212626055-jstack.log-rongruo.lsx-0819.log";
        //String file = "/Users/linuxlsx/Downloads/rsp-server011001090215.center.na61-20180914114639235-jstack.log-rongruo.lsx-0819.log";
        //String file = "/Users/linuxlsx/Downloads/rsp-server011008040238.na61-20180919102136202-jstack.log-rongruo.lsx-0819.log";
        //String file = "/Users/linuxlsx/Downloads/rsp-server011134192001.na62-20180919104433123-jstack.log-rongruo.lsx-0819.log";
        String file = "/Users/linuxlsx/Downloads/rsp-server011008040238.na61-20180919143845086-jstack.log-rongruo.lsx-0819.log";

        threadNum(file);
        //businessNum(file);
    }

    private static void businessNum(String file) throws IOException {

        Map<String, Line> lineMap = new HashMap<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
        String l = bufferedReader.readLine();
        while (l != null) {

            if(StringUtils.contains(l, "com.taobao.rsp") || StringUtils.contains(l, "com.taobao.ifd")){
                lineMap.computeIfAbsent(l, k -> new Line(k, 0)).addOne();
            }

            l = bufferedReader.readLine();
        }

        bufferedReader.close();

        List<Line> lines = new ArrayList<>(lineMap.values());
        Collections.sort(lines);

        for (Line line : lines) {

            System.out.println(String.format("%s %d", line.text, line.count));
        }
    }

    private static void threadNum(String file) throws IOException {
        Map<String, Line> lineMap = new HashMap<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(file)));
        String l = bufferedReader.readLine();
        while (l != null) {

            if (pattern.matcher(l).find()) {

                String threadName = l.split(" ")[0];

                if (THREAD_NAME_PATTERN.matcher(threadName).find()) {
                    String[] t = threadName.split("-");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < t.length - 1; i++) {
                        sb.append(t[i]).append('-');
                    }

                    sb.deleteCharAt(sb.length() - 1).append('"');

                    lineMap.computeIfAbsent(sb.toString(), k -> new Line(k, 0)).addOne();
                } else {
                    lineMap.computeIfAbsent(threadName, k -> new Line(k, 0)).addOne();
                }

            }


            l = bufferedReader.readLine();
        }


        bufferedReader.close();

        List<Line> lines = new ArrayList<>(lineMap.values());
        Collections.sort(lines);

        for (Line line : lines) {

            System.out.printf(" %55s %4d \n", line.text, line.count);
        }
    }

    private static class Line implements Comparable<Line> {

        public String text;

        public Integer count;

        public Line(String text, Integer count) {
            this.text = text;
            this.count = count;
        }

        public void addOne() {
            count++;
        }


        @Override
        public int compareTo(Line o) {
            return -count.compareTo(o.count);
        }
    }
}
