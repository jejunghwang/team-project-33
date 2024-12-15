package com.photoeditor.core;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessor {
    public static Mat histogramEqualization(Mat src) {
        Mat ycrcb = new Mat();
        Imgproc.cvtColor(src, ycrcb, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> channels = new ArrayList<>();
        Core.split(ycrcb, channels);
        Imgproc.equalizeHist(channels.get(0), channels.get(0));
        Core.merge(channels, ycrcb);
        Mat result = new Mat();
        Imgproc.cvtColor(ycrcb, result, Imgproc.COLOR_YCrCb2BGR);
        return result;
    }

    public static Mat negative(Mat src) {
        Mat dst = new Mat();
        Core.bitwise_not(src, dst);
        return dst;
    }

    public static Mat smooth(Mat src, int ksize) {
        Mat dst = new Mat();
        Imgproc.blur(src, dst, new Size(ksize, ksize));
        return dst;
    }

    public static Mat sharpen(Mat src) {
        Mat dst = new Mat();
        Mat kernel = new Mat(3,3, CvType.CV_32F) {
            {
                put(0,0,0);  put(0,1,-1); put(0,2,0);
                put(1,0,-1); put(1,1,5);  put(1,2,-1);
                put(2,0,0);  put(2,1,-1); put(2,2,0);
            }
        };
        Imgproc.filter2D(src, dst, src.depth(), kernel);
        return dst;
    }

    public static Mat edgeDetect(Mat src) {
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 100, 200);
        Mat dst = new Mat();
        Imgproc.cvtColor(edges, dst, Imgproc.COLOR_GRAY2BGR);
        return dst;
    }

    public static Mat adjustBrightnessContrast(Mat src, double alpha, double beta) {
        // alpha > 1.0: 대비 증가
        // beta: 밝기 조절
        Mat dst = new Mat();
        src.convertTo(dst, -1, alpha, beta);
        return dst;
    }

    public static Mat adjustHueSaturation(Mat src, int hueAdjust, int satAdjust) {
        // HSV로 변환 후 H,S 채널 조정
        Mat hsv = new Mat();
        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
        List<Mat> channels = new ArrayList<>();
        Core.split(hsv, channels);
        // H 채널: 0~179, S 채널: 0~255
        // hueAdjust, satAdjust를 적용할 때 범위 체크 필요
        Mat h = channels.get(0);
        Mat s = channels.get(1);
        for (int r=0; r<h.rows(); r++) {
            for (int c=0; c<h.cols(); c++) {
                double[] hv = h.get(r,c);
                hv[0] = (hv[0] + hueAdjust) % 180;
                if (hv[0]<0) hv[0]+=180;
                h.put(r,c,hv);

                double[] sv = s.get(r,c);
                sv[0] = sv[0] + satAdjust;
                if (sv[0]<0) sv[0]=0;
                if (sv[0]>255) sv[0]=255;
                s.put(r,c,sv);
            }
        }

        channels.set(0, h);
        channels.set(1, s);
        Core.merge(channels, hsv);

        Mat dst = new Mat();
        Imgproc.cvtColor(hsv, dst, Imgproc.COLOR_HSV2BGR);
        return dst;
    }

    public static Mat crop(Mat src, int x, int y, int w, int h) {
        Rect roi = new Rect(x, y, w, h);
        return new Mat(src, roi);
    }

    public static Mat resize(Mat src, int width, int height) {
        Mat dst = new Mat();
        Imgproc.resize(src, dst, new Size(width, height));
        return dst;
    }

    public static Mat rotate(Mat src, double angle) {
        // 중심을 기준으로 회전
        Point center = new Point(src.cols()/2.0, src.rows()/2.0);
        Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        Mat dst = new Mat();
        Imgproc.warpAffine(src, dst, rotMat, new Size(src.cols(), src.rows()));
        return dst;
    }

    public static Mat flip(Mat src, int flipCode) {
        Mat dst = new Mat();
        Core.flip(src, dst, flipCode);
        return dst;
    }

    public static Mat alphaBlend(Mat src1, Mat src2, double alpha) {
        if (src1.size().equals(src2.size()) == false) {
            Mat resized = new Mat();
            Imgproc.resize(src2, resized, src1.size());
            src2 = resized;
        }
        Mat dst = new Mat();
        double beta = 1.0 - alpha;
        Core.addWeighted(src1, alpha, src2, beta, 0.0, dst);
        return dst;
    }

    public static Mat erode(Mat src, int size) {
        Mat dst = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));
        Imgproc.erode(src, dst, kernel);
        return dst;
    }

    public static Mat dilate(Mat src, int size) {
        Mat dst = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(size, size));
        Imgproc.dilate(src, dst, kernel);
        return dst;
    }
}
