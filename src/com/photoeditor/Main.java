package com.photoeditor;

import javax.swing.SwingUtilities;
import org.opencv.core.Core;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        SwingUtilities.invokeLater(() -> {
            new com.photoeditor.ui.MainFrame().setVisible(true);
        });
    }
}
