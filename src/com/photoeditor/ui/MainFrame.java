package com.photoeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import com.photoeditor.core.*;
import org.opencv.core.Mat;

public class MainFrame extends JFrame {
    private ImagePanel imagePanel;
    private JFileChooser fileChooser;

    public MainFrame() {
        super("33팀 이미지 편집 툴");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        imagePanel = new ImagePanel();
        add(imagePanel, BorderLayout.CENTER);

        fileChooser = new JFileChooser();

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("파일");
        JMenuItem openItem = new JMenuItem("열기");
        openItem.addActionListener(e -> openImage());
        JMenuItem saveItem = new JMenuItem("저장");
        saveItem.addActionListener(e -> saveImage());
        JMenuItem encryptItem = new JMenuItem("암호화");
        encryptItem.addActionListener(e -> encryptImage());
        JMenuItem decryptItem = new JMenuItem("복호화");
        decryptItem.addActionListener(e -> decryptImage());
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(encryptItem);
        fileMenu.add(decryptItem);

        JMenu editMenu = new JMenu("편집");
        JMenuItem histEqItem = new JMenuItem("Histogram Equalization");
        histEqItem.addActionListener(e -> applyHistEq());

        JMenuItem negItem = new JMenuItem("Negative");
        negItem.addActionListener(e -> applyNegative());

        JMenuItem smoothItem = new JMenuItem("Smooth");
        smoothItem.addActionListener(e -> applySmooth());

        JMenuItem sharpItem = new JMenuItem("Sharpen");
        sharpItem.addActionListener(e -> applySharpen());

        JMenuItem edgeItem = new JMenuItem("Edge Detection");
        edgeItem.addActionListener(e -> applyEdge());

        JMenuItem brightnessContrastItem = new JMenuItem("Brightness/Contrast 조정");
        brightnessContrastItem.addActionListener(e -> adjustBrightnessContrast());

        JMenuItem hueSatItem = new JMenuItem("Hue/Saturation 조정");
        hueSatItem.addActionListener(e -> adjustHueSaturation());

        JMenuItem cropItem = new JMenuItem("Crop");
        cropItem.addActionListener(e -> cropImage());

        JMenuItem resizeItem = new JMenuItem("Resize");
        resizeItem.addActionListener(e -> resizeImage());

        JMenuItem rotateItem = new JMenuItem("Rotate");
        rotateItem.addActionListener(e -> rotateImage());

        JMenuItem flipItem = new JMenuItem("Flip");
        flipItem.addActionListener(e -> flipImage());

        JMenuItem alphaBlendItem = new JMenuItem("Alpha Blending");
        alphaBlendItem.addActionListener(e -> alphaBlend());

        JMenu morphMenu = new JMenu("Morphological");
        JMenuItem erodeItem = new JMenuItem("Erode");
        erodeItem.addActionListener(e -> applyErode());
        JMenuItem dilateItem = new JMenuItem("Dilate");
        dilateItem.addActionListener(e -> applyDilate());
        morphMenu.add(erodeItem);
        morphMenu.add(dilateItem);

        editMenu.add(histEqItem);
        editMenu.add(negItem);
        editMenu.add(smoothItem);
        editMenu.add(sharpItem);
        editMenu.add(edgeItem);
        editMenu.add(brightnessContrastItem);
        editMenu.add(hueSatItem);
        editMenu.add(cropItem);
        editMenu.add(resizeItem);
        editMenu.add(rotateItem);
        editMenu.add(flipItem);
        editMenu.add(alphaBlendItem);
        editMenu.add(morphMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void openImage() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            imagePanel.loadImage(f.getAbsolutePath());
        }
    }

    private void saveImage() {
        if (imagePanel.getCurrentMat() == null) return;
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            OpenCVUtils.saveImage(imagePanel.getCurrentMat(), f.getAbsolutePath());
        }
    }

    private void encryptImage() {
        if (imagePanel.getCurrentMat() == null) return;
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String key = JOptionPane.showInputDialog(this, "암호화 키를 입력하세요 (16바이트 권장):");
            if (key != null && !key.isEmpty()) {
                File f = fileChooser.getSelectedFile();
                File tempFile = new File(f.getParent(), "temp.jpg");
                OpenCVUtils.saveImage(imagePanel.getCurrentMat(), tempFile.getAbsolutePath());
                FileEncryptor.encryptFile(tempFile, f, key);
                tempFile.delete();
                JOptionPane.showMessageDialog(this, "암호화 완료");
            }
        }
    }

    private void decryptImage() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            String key = JOptionPane.showInputDialog(this, "복호화 키를 입력하세요:");
            if (key != null && !key.isEmpty()) {
                File out = new File(f.getParent(), "decrypted.jpg");
                FileEncryptor.decryptFile(f, out, key);
                imagePanel.loadImage(out.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "복호화 완료");
            }
        }
    }

    private void applyHistEq() {
        if (imagePanel.getCurrentMat() == null) return;
        imagePanel.setImageMat(ImageProcessor.histogramEqualization(imagePanel.getCurrentMat()));
        imagePanel.repaint();
    }

    private void applyNegative() {
        if (imagePanel.getCurrentMat() == null) return;
        imagePanel.setImageMat(ImageProcessor.negative(imagePanel.getCurrentMat()));
        imagePanel.repaint();
    }

    private void applySmooth() {
        if (imagePanel.getCurrentMat() == null) return;
        String valStr = JOptionPane.showInputDialog(this, "블러 강도(정수):");
        if(valStr == null) return;
        try {
            int ksize = Integer.parseInt(valStr);
            imagePanel.setImageMat(ImageProcessor.smooth(imagePanel.getCurrentMat(), ksize));
            imagePanel.repaint();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

    private void applySharpen() {
        if (imagePanel.getCurrentMat() == null) return;
        imagePanel.setImageMat(ImageProcessor.sharpen(imagePanel.getCurrentMat()));
        imagePanel.repaint();
    }

    private void applyEdge() {
        if (imagePanel.getCurrentMat() == null) return;
        imagePanel.setImageMat(ImageProcessor.edgeDetect(imagePanel.getCurrentMat()));
        imagePanel.repaint();
    }

    private void adjustBrightnessContrast() {
        if (imagePanel.getCurrentMat() == null) return;
        String alphaStr = JOptionPane.showInputDialog(this, "대비(1.0 기본, 0.0~3.0):");
        String betaStr = JOptionPane.showInputDialog(this, "밝기(-100~100):");
        try {
            double alpha = Double.parseDouble(alphaStr);
            double beta = Double.parseDouble(betaStr);
            imagePanel.setImageMat(ImageProcessor.adjustBrightnessContrast(imagePanel.getCurrentMat(), alpha, beta));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "올바른 숫자를 입력하세요");
        }
    }

    private void adjustHueSaturation() {
        if (imagePanel.getCurrentMat() == null) return;
        String hueStr = JOptionPane.showInputDialog(this, "Hue 조정(-50~50):");
        String satStr = JOptionPane.showInputDialog(this, "Saturation 조정(-50~50):");
        try {
            int hue = Integer.parseInt(hueStr);
            int sat = Integer.parseInt(satStr);
            imagePanel.setImageMat(ImageProcessor.adjustHueSaturation(imagePanel.getCurrentMat(), hue, sat));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

    private void cropImage() {
        if (imagePanel.getCurrentMat() == null) return;
        String xStr = JOptionPane.showInputDialog(this, "Crop X 시작:");
        String yStr = JOptionPane.showInputDialog(this, "Crop Y 시작:");
        String wStr = JOptionPane.showInputDialog(this, "폭:");
        String hStr = JOptionPane.showInputDialog(this, "높이:");
        try {
            int x = Integer.parseInt(xStr);
            int y = Integer.parseInt(yStr);
            int w = Integer.parseInt(wStr);
            int h = Integer.parseInt(hStr);
            imagePanel.setImageMat(ImageProcessor.crop(imagePanel.getCurrentMat(), x, y, w, h));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

    private void resizeImage() {
        if (imagePanel.getCurrentMat() == null) return;
        String wStr = JOptionPane.showInputDialog(this, "새 폭:");
        String hStr = JOptionPane.showInputDialog(this, "새 높이:");
        try {
            int w = Integer.parseInt(wStr);
            int h = Integer.parseInt(hStr);
            imagePanel.setImageMat(ImageProcessor.resize(imagePanel.getCurrentMat(), w, h));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

    private void rotateImage() {
        if (imagePanel.getCurrentMat() == null) return;
        String angleStr = JOptionPane.showInputDialog(this, "회전 각도(도):");
        try {
            double angle = Double.parseDouble(angleStr);
            imagePanel.setImageMat(ImageProcessor.rotate(imagePanel.getCurrentMat(), angle));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "숫자를 입력하세요");
        }
    }

    private void flipImage() {
        if (imagePanel.getCurrentMat() == null) return;
        String flipStr = JOptionPane.showInputDialog(this, "0:상하, 1:좌우, -1:상하좌우:");
        try {
            int flipCode = Integer.parseInt(flipStr);
            imagePanel.setImageMat(ImageProcessor.flip(imagePanel.getCurrentMat(), flipCode));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

    private void alphaBlend() {
        if (imagePanel.getCurrentMat() == null) return;
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            Mat secondImg = OpenCVUtils.loadImage(f.getAbsolutePath());
            if (secondImg == null) {
                JOptionPane.showMessageDialog(this, "이미지 로드 실패");
                return;
            }
            String alphaStr = JOptionPane.showInputDialog(this, "알파(0.0~1.0):");
            try {
                double alpha = Double.parseDouble(alphaStr);
                imagePanel.setImageMat(ImageProcessor.alphaBlend(imagePanel.getCurrentMat(), secondImg, alpha));
                imagePanel.repaint();
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "0.0~1.0 범위의 숫자를 입력하세요");
            }
        }
    }

    private void applyErode() {
        if (imagePanel.getCurrentMat() == null) return;
        String sizeStr = JOptionPane.showInputDialog(this, "커널 사이즈(정수):");
        try {
            int size = Integer.parseInt(sizeStr);
            imagePanel.setImageMat(ImageProcessor.erode(imagePanel.getCurrentMat(), size));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

    private void applyDilate() {
        if (imagePanel.getCurrentMat() == null) return;
        String sizeStr = JOptionPane.showInputDialog(this, "커널 사이즈(정수):");
        try {
            int size = Integer.parseInt(sizeStr);
            imagePanel.setImageMat(ImageProcessor.dilate(imagePanel.getCurrentMat(), size));
            imagePanel.repaint();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "정수를 입력하세요");
        }
    }

}
