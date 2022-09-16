/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implementacoes;
import java.awt.Color;
import java.text.DecimalFormat; 

/**
 *
 * @author Flavia Mattos
 */
public class ColorSpaceConverter {
 
public int convertRGBGray(Color cor) {
        double calc;
        calc = 0.2989 * cor.getRed() + 0.5870 * cor.getGreen() + 0.1140 * cor.getBlue();
        
        
        return (int)calc;
    }
    
    
    public int[] convertRGBtoYIQ(Color cor) {
        int[] result = new int[3];
        
        double Y = (0.299 * cor.getRed()) + (0.587 * cor.getGreen()) + (0.114 * cor.getBlue());  // Cálculo do Y
        double I = (0.596 * cor.getRed()) - (0.275 * cor.getGreen()) - (0.321 * cor.getBlue());  // Cálculo do I
        double Q = (0.212 * cor.getRed()) - (0.523 * cor.getGreen()) + (0.311 * cor.getBlue());  // Cálculo do Q
        
        result[0] = (int)Y;
        result[1] = (int)I;
        result[2] = (int)Q;
        
        return result;
    }
    
    public Color convertYIQtoRGB(int Y, int I, int Q) {
        Color corRGB;
        int R, G, B;
        
        R = (int) (Y + (0.956 * I) + (0.621 * Q));
        if(R < 0) R = 0;
        else if (R > 255) R = 255;
        G = (int) (Y - (0.272 * I) - (0.647 * Q));
        if(G < 0) G = 0;
        else if (G > 255) G = 255;
        B = (int) (Y - (1.106 * I) + (1.703 * Q));
        if(B < 0) B = 0;
        else if (B > 255) B = 255;
        
        corRGB = new Color(R, G, B);
        
        return corRGB;
    }

}

