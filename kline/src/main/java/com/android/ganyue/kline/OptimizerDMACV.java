package com.android.ganyue.kline;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


/**
 * VAR1:=DMA(CLOSE,VOL/SUM(VOL,34));
 * VAR2:=DMA(CLOSE,VOL/SUM(VOL,13));
 * VAR3:=(CLOSE-VAR1)/VAR1*100;
 * VAR4:=(CLOSE-VAR2)/VAR2*100;
 * 买入: VAR3<=-19 AND VAR4<=-12 ,COLORRED,LINETHICK2;
 *
 * 公式共4个参数 VAR1中的34 (P1)， VAR2中的13 (P2)，VAR3的-19 (P3)，VAR4的-12 (P4)
 * 再加上止损 5%-10% (P5)， 5-20 (P6)天后卖出这两个参数
 * 共6个变量，不停变动几个变量，找出最优解
 */
public class OptimizerDMACV extends Thread {

    String mRootDir = "/home/ph/桌面/stock/";
    File mStocksDir = new File(mRootDir, "export"); // 历史记录位置
    File mPropFile = new File(mRootDir, "prop.txt"); // 记录计算位置，下次可以接着上次位置计算
    File mResultFile = new File(mRootDir, "result.txt"); // 计算结果输出文件
    File mMaxResultFile = new File(mRootDir, "resultMax.txt"); // 计算结果筛选最大值输出文件
    File mStockResultDir = new File(mRootDir, "stockResult"); // 各个股票各种计算结果

    public OptimizerDMACV () {
    }

    private Properties mProperties = new Properties();
    private boolean mIsPropertiesLoaded = false;
    private int getPref (String key, int defVal) throws Exception {
        if (!mIsPropertiesLoaded && mPropFile.exists()) {
            mIsPropertiesLoaded = true;
            InputStream in = new FileInputStream(mPropFile);
            mProperties.load(in);
            in.close();
        }
        return Integer.parseInt(mProperties.getProperty(key, "" + defVal));
    }

    private void setPref (String key, int val) throws Exception {
        mProperties.setProperty(key, "" + val);
        OutputStream out = new FileOutputStream(mPropFile);
        mProperties.store(out, "");
        out.close();
    }

    @Override
    public void run() {
//        try {
//
//            List<Stock> stocks = new ArrayList<>();
//            StockParser parser = new StockParser();
//            File[] stockFiles = mStocksDir.listFiles();
//            for (File f: stockFiles) {
//                Stock stock = parser.parseSync(f.getPath());
//                if (stock == null) {
//                    System.out.println("parse failed -> " + f.getPath());
//                    f.delete();
//                } else {
//                    stocks.add(stock);
//                }
//            }
//
//            int p1MIN = getPref("p1min", 30), p1MAX = getPref("p1max", 50);
//            int p2MIN = getPref("p2min", 10), p2MAX = getPref("p2max", 25);
//            int p3MIN = getPref("p3min", -40), p3MAX = getPref("p3max", -15);
//            int p4MIN = getPref("p4min", -20), p4MAX = getPref("p4max", -10);
//            int p5MIN = getPref("p5min", 5), p5MAX = getPref("p5max", 20);
//            // 运算量太大，只算 p5 = 10; p6 = 20;
//            int p5 = p5MIN; // 止损位置
//            int p6 = 20; // 最大持有天数
//            int p1, p2, p3, p4;
//
//            FileOutputStream resultOutStream = null;// = new FileOutputStream(mResultFile, true);
//            FileOutputStream maxResultOutStream = new FileOutputStream(mMaxResultFile, true);
//            mStockResultDir.mkdirs();
//
//            Workbook workbook = new HSSFWorkbook();
//            Sheet resultSheet = workbook.createSheet("result");
//            Sheet countSheet = workbook.createSheet("count");
//            Row resultHeadRow = resultSheet.createRow(0);
//            resultHeadRow.createCell(0).setCellValue("result");
//            resultHeadRow.createCell(0).setCellValue("p1");
//            resultHeadRow.createCell(0).setCellValue("p2");
//            resultHeadRow.createCell(0).setCellValue("p3");
//            resultHeadRow.createCell(0).setCellValue("p4");
//            resultHeadRow.createCell(0).setCellValue("p5");
//            resultHeadRow.createCell(0).setCellValue("p6");
//            resultHeadRow.createCell(0).setCellValue("count");
//            resultHeadRow.createCell(0).setCellValue("avc");
//            Row countHeadRow = countSheet.createRow(0);
//            countHeadRow.createCell(0).setCellValue("result");
//            countHeadRow.createCell(0).setCellValue("p1");
//            countHeadRow.createCell(0).setCellValue("p2");
//            countHeadRow.createCell(0).setCellValue("p3");
//            countHeadRow.createCell(0).setCellValue("p4");
//            countHeadRow.createCell(0).setCellValue("p5");
//            countHeadRow.createCell(0).setCellValue("p6");
//            countHeadRow.createCell(0).setCellValue("count");
//            countHeadRow.createCell(0).setCellValue("avc");
//            workbook.write(maxResultOutStream);
//
//        	System.out.println("starting !!!");
//        	if (resultOutStream != null) resultOutStream.write("↓↓↓↓↓ starting !!! ↓↓↓↓↓\r\n".getBytes());
//
//            float prevResult = 0;
//            float prevCount = 0;
//            int resultRowNum = 1;
//            int countRowNum = 1;
//            p1 = p1MIN;
//            while(p1 <= p1MAX) {
//                p2 = p2MIN;
//                while(p2 <= p2MAX) {
//                    p3 = p3MIN;
//                    while(p3 <= p3MAX) {
//                        p4 = p4MIN;
//                        while(p4 <= p4MAX) {
//                            int count = 0; // 总买入次数
//                            int success = 0; // 总成功次数
//                            int averageP6 = 0;
//
//                            for (Stock stock: stocks) {
//                                boolean writeStockResult = false;
////                                                stock.code.equals("002206") ||
////                                                stock.code.equals("601118") ||
////                                                stock.code.equals("002083");
//                                FileOutputStream of = null;
//                                List<DayInfo> infos = getDMACVResult(stock, p1, p2, p3, p4); // 公式计算结果
//                                int maxSize = stock.dayInfos.size();
//                                int soldDate = 0;
//
//                                if (writeStockResult) {
//                                    of = new FileOutputStream(new File(mStockResultDir, stock.code), true);
//                                    of.write("↓↓↓↓↓ KLines ↓↓↓↓↓\r\n".getBytes());
//                                    for (DayInfo i: infos) {
//                                        String str = i.toString() + "\r\n";
//                                        of.write(str.getBytes());
//                                    }
//                                }
//
//                                for (int i = 0; i < infos.size(); i++) {
//                                    DayInfo info = infos.get(i);
//
//                                    // 信号出来时候距离最后一条日线数据不足止损天数不考虑
//                                    if (info.index + p6 >= maxSize) continue;
//                                    if (info.date < soldDate) continue;//还在持有不考虑买入
//
//                                    String infoLogStr = "";
//                                    if (writeStockResult) infoLogStr = stock.code + " " + stock.name + " start->" + info.date;
//                                    count++;
//
//                                    float successClose = info.close * 1.05f;
//                                    float failedClose = info.close * (1 - p5 / 100f);
//                                    int j = 1;
//                                    for (; j <= p6; j++) {
//                                        DayInfo tmp = stock.dayInfos.get(info.index + j);
//                                        if (tmp.close > successClose) {//止盈
//                                            if (writeStockResult) infoLogStr += " sold->" + tmp.date + " success\r\n";
//                                            soldDate = tmp.date;
//                                            success++;
//                                            averageP6 += j;
//                                            break;
//                                        } else if (tmp.close < failedClose) {//止损
//                                            if (writeStockResult) infoLogStr += " sold->" + tmp.date + " failed\r\n";
//                                            soldDate = tmp.date;
//                                            break;
//                                        }
//                                    }
//                                    if (j > p6) {
//                                        //被迫卖出
//                                        DayInfo tmp = stock.dayInfos.get(info.index + p6);
//                                        if (writeStockResult) infoLogStr += " sold->" + tmp.date + " failed\r\n";
//                                        soldDate = tmp.date;
//                                    }
//
//                                    if (writeStockResult && of != null) {
//                                        infoLogStr += String.format(Locale.getDefault(),
//                                                "p1=%02d, p2=%02d, p3=%02d, p4=%02d, p5=%02d, p6=%02d",
//                                                p1, p2, p3, p4, p5, p6);;
//                                        of.write(infoLogStr.getBytes());
//                                    }
//
//                                }
//                                if (of != null) {
//                                    of.close();
//                                }
//                            }
//
//                            float result = count == 0? 0: 1f*success/count;
//                            int avc = success == 0? 0: averageP6/success;
//                            String resultStr = String.format(Locale.getDefault(),
//                                    "%s result=%.6f, p1=%02d, p2=%02d, p3=%02d, p4=%02d, p5=%02d, p6=%02d count=%d, avc=%d\r\n",
//                                    getDateStr(), result, p1, p2, p3, p4, p5, p6, count, avc);
//                            System.out.println(resultStr);
//                            if (resultOutStream != null) resultOutStream.write(resultStr.getBytes());
//                            if (result > prevResult) {
//                                prevResult = result;
////                                maxResultOutStream.write(resultStr.getBytes());
//
//                                resultHeadRow = resultSheet.createRow(resultRowNum);
//                                resultHeadRow.createCell(0).setCellValue("result");
//                                resultHeadRow.createCell(0).setCellValue("p1");
//                                resultHeadRow.createCell(0).setCellValue("p2");
//                                resultHeadRow.createCell(0).setCellValue("p3");
//                                resultHeadRow.createCell(0).setCellValue("p4");
//                                resultHeadRow.createCell(0).setCellValue("p5");
//                                resultHeadRow.createCell(0).setCellValue("p6");
//                                resultHeadRow.createCell(0).setCellValue("count");
//                                resultHeadRow.createCell(0).setCellValue("avc");
//                                workbook.write(maxResultOutStream);
//                            }
//                            if (count > prevCount) {
//                                prevCount = count;
//                            }
//
//                            p4++;
//                        }
//                        p3++;
//                        setPref("p3min", p3);
//                    }
//                    p2++;
//                    setPref("p2min", p2);
//                }
//                p1++;
//                setPref("p1min", p1);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[ HH:mm:ss ] ", Locale.getDefault());
    private String getDateStr () {
        return simpleDateFormat.format(new Date());
    }

    public List<DayInfo> getDMACVResult (Stock stock, int p1, int p2, int p3, int p4) {
        float[] var1 = stock.DMA(DayInfo.Type.CLOSE, stock.SUMP(DayInfo.Type.VOL, p1));
        float[] var2 = stock.DMA(DayInfo.Type.CLOSE, stock.SUMP(DayInfo.Type.VOL, p2));
        float[] var3 = new float[stock.dayInfos.size()];
        float[] var4 = new float[stock.dayInfos.size()];

        for (int i = 0; i < stock.dayInfos.size(); i++) {
            float close = stock.dayInfos.get(i).close;
            var3[i] = (close - var1[i])/var1[i] * 100;
            var4[i] = (close - var2[i])/var2[i] * 100;
        }

        List<DayInfo> infos = new ArrayList<>();
        for (int i = 0; i < stock.dayInfos.size(); i++) {
            if (var3[i] <= p3 && var4[i] <= p4) infos.add(stock.dayInfos.get(i));
        }
        return infos;
    }
}
