/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author serafeim
 */
public class ErrorWindow {
    public static void showErrorWindow(String msg){
        JOptionPane.showMessageDialog(new JFrame(), msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
