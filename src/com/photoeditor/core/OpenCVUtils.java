package com.photoeditor.core;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class OpenCVUtils {
    public static Mat loadImage(String path) {
        Mat img = Imgcodecs.imread(path);
        if (img.empty()) {
            System.err.println("이미지 로드 실패: " + path);
            return null;
        }
        return img;
    }

    public static boolean saveImage(Mat mat, String path) {
        return Imgcodecs.imwrite(path, mat);
    }

    public static BufferedImage matToBufferedImage(Mat matrix) {
        int type = 0;
        if (matrix.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (matrix.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        } else {
            throw new UnsupportedOperationException("지원하지 않는 채널 수: " + matrix.channels());
        }

        byte[] b = new byte[matrix.channels() * matrix.cols() * matrix.rows()];
        matrix.get(0, 0, b);
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}
