package com.photoeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import org.opencv.core.Mat;
import com.photoeditor.core.OpenCVUtils;

public class ImagePanel extends JPanel {
    private Mat currentMat;
    private BufferedImage currentImage;

    public void loadImage(String path) {
        Mat mat = OpenCVUtils.loadImage(path);
        if (mat != null) {
            setImageMat(mat);
            repaint();
        }
    }

    public Mat getCurrentMat() {
        return currentMat;
    }

    public void setImageMat(Mat mat) {
        this.currentMat = mat;
        this.currentImage = OpenCVUtils.matToBufferedImage(mat);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentImage != null) {
            int x = (getWidth() - currentImage.getWidth()) / 2;
            int y = (getHeight() - currentImage.getHeight()) / 2;
            g.drawImage(currentImage, x, y, this);
        }
    }
}
