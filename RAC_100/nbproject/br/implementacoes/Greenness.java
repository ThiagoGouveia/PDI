package implementacoes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import swing.janelas.PDI_Lote;
import java.util.ArrayList;

/*
 * @author 
 */
public class Greenness {

    public BufferedImage GreennKG(BufferedImage img, float C) {
        //Declaração do buffer que armazenará o resultado final
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        ColorSpaceConverter corConverte = new ColorSpaceConverter();
        int[] colorPixel;  
        
        int altura = img.getHeight();
        int largura = img.getWidth();
        float resFinal; 
        double[][] media = new double[altura][largura];
        double[][] variancia = new double[altura][largura];
        double[][][] original = new double[altura][largura][3];
        int canalY = 0,canalI = 1,canalQ = 2; //acessa os canais YIQ no buffer
    
        // Iteração dupla utilizado para preencher as matrizes(média,variancia,original) com valores numéricos da imagem.
        for(int i=0;i<img.getWidth();i++){ 
            // VARREDURA DAS LINHAS DA IMAGEM
            for(int j=0;j<img.getHeight();j++){ // VARREDURA DAS COLUNAS DA IMAGEM
                Color x = new Color(img.getRGB(i, j));
                
                colorPixel = corConverte.convertRGBtoYIQ(x); // conversão do pixel para escala de cinza
                
                
                media[j][i] = colorPixel[canalY];
                variancia[j][i] = colorPixel[canalY];
                original[j][i][canalY] = colorPixel[canalY];
                original[j][i][canalI] = colorPixel[canalI];
                original[j][i][canalQ] = colorPixel[canalQ];
            }
        }
        
        //Série de iterações com o intuito de fazer a somatoria dos quadros 3x3 e tirar a media dos próprios
        //Armazenando em uma matriz media
        float soma=0;
        for(int i=1  ; i<img.getWidth()-1; i++) { 
            for( int j=1 ; j<img.getHeight()-1 ; j++) {
                soma=0;
                for(int col=i-1; col<=i+1; col++) {
                    for(int lin=j-1; lin<=j+1; lin++) {
                        soma+=original[lin][col][canalY];
		    }
                }

                soma = soma/9;
                media[j][i] = soma;
                
            }
        }
        
        //Série de iterações com o intuito de fazer a somatoria dos quadros 3x3 e tirar a variancia dos próprios
        //A variancia é a diferença entre pixel original e o pixel da média ao quadrado
        //Após esse processo, é preciso dividir o somatorio do quadro 3x3 por 9, e tirar a sua raiz quadrada.
        //Armazenando em uma matriz variancia ao final do processo
        for(int i=1  ; i<img.getWidth()-1; i++) { 
            for( int j=1 ; j<img.getHeight()-1 ; j++) {
                soma=0;
                for(int col=i-1; col<=i+1; col++) {
                    for(int lin=j-1; lin<=j+1; lin++) {
                        double temp = (original[j][i][canalY] - media[j][i]);
                        soma += temp*temp;
                    }
                }
                soma = soma/9;
                
                variancia[j][i] = Math.sqrt((double)soma);                       
            }
        }
 
        //Iteração para calcular o pixel que estará no resultado final e armazena-lo no buffer res declarado anteriormente
        //Para isso tem-se que tratar um erro que pode acontecer. Se o valor do pixel contido em variancia for igual à zero,
        //Ele recebe o píxel da imagem original, caso contrario, Faz se a operação final
        //A operação final consiste na soma entre media e uma multiplicação entre uma constante, o valor da variancia e a diferença
        //entre o pixel original e a média, divididos pela variancia.
        //Se o valor final for menor que zero, o valor final vai receber 0. E se o valor for maior que 255, recebe 255.
        for(int i=0;i<img.getWidth();i++){ 
            // VARREDURA DAS LINHAS DA IMAGEM
            for(int j=0;j<img.getHeight();j++){ // VARREDURA DAS COLUNAS DA IMAGEM
                if(variancia[j][i] == 0){
                    resFinal = (float)original[j][i][canalY];
                }
                else{
                    resFinal = (float) (media[j][i] + C * variancia[j][i]*((original[j][i][canalY] - media[j][i])/variancia[j][i]));
                    }
                
                if(resFinal < 0)
                    resFinal =0;
                else if(resFinal > 255)
                    resFinal = 255;
                Color setRes = corConverte.convertYIQtoRGB((int) resFinal, (int)original[j][i][1], (int)original[j][i][2]);
                res.setRGB(i, j, setRes.getRGB());
            }
        }
        
               
        return res;
        
    }
    

    
 /**
 * Essa função é a implementação da método de GreennessMin = G − min(R + B)
 * onde o R,G e B são os valores obtido da imagem
 * 
 * @param img A imagem onde o filtro será aplicado
 * @return retorna a imagem depois de aplicado o filtro
 */
    public BufferedImage GreennMin(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇAO
        double min = 10000;
        double max = 0;
        double menor ;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));
                
                if(x.getRed() < x.getBlue()){
                    menor = x.getRed();
                }else{
                    menor = x.getBlue();
                }
                
                double cor = x.getGreen() - menor;

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                
                Color p = new Color(img.getRGB(i, j));
                
                if(p.getRed() < p.getBlue()){
                    menor = p.getRed();
                }else {
                    menor = p.getBlue();
                }                
                
                
                double atual = p.getGreen() - menor;
                double cor = 255 * ((atual - min) / (max - min));

                int corB31 = (int) cor;

                Color novo = new Color(corB31, corB31, corB31);
                res.setRGB(i, j, novo.getRGB());
                
            }
        }
        return res;
    }
    
    /**
     * Essa função é a implementação da método de GreennessG−R = (G + R)/(G - R)
     * onde o R,G e B são os valores obtido da imagem
     * @param img A imagem onde o filtro será aplicado
     * @return retorna a imagem depois de aplicado o filtro
     */
    public BufferedImage GreennGmenR(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        double max = 0;
        int zero;
        double cor = 0;
        
        try {
            
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color x = new Color(img.getRGB(i, j));

                    zero = x.getGreen() + x.getRed();

                    if(zero != 0){

                        cor = (x.getGreen() - x.getRed()) / ((x.getGreen() + x.getRed())); //Oq fazer se tiver um pixel preto?

                    }else{

                        cor = 0;

                    }

                    if (cor < min) {
                        min = cor;
                    }
                    if (cor > max) {
                        max = cor;
                    }
                }
            }

            
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color p = new Color(img.getRGB(i, j));

                    zero = p.getGreen() + p.getRed();
                    if(zero != 0){

                        double atual = (p.getGreen() - p.getRed()) / ((p.getGreen() + p.getRed()));
                        cor = 255 * ((atual - min) / (max - min));

                    }else{

                        cor = 0;

                    }

                    int corB31 = (int) cor;

                    Color novo = new Color(corB31, corB31, corB31);
                    res.setRGB(i, j, novo.getRGB());
                }
            }
        } catch (java.lang.ArithmeticException e) {
            
            JOptionPane.showMessageDialog(null, "Divisão por Zero", "Error", 0);
            
        }
        //FINAL NORMALIZAÇÃO

        
    return res;
    }
    
    /**
     * Essa função é a implementação da método de GreennessG−R = (G − R)/(G + R)
     * onde K é o valor passado pelo usuário e o R,G e B são os valores obtido da imagem
     * @param img A imagem onde o filtro será aplicado
     * @return retorna a imagem depois de aplicado o filtro
     */
    public BufferedImage GreennGmaisR(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        int zero;
        double max = 0;
        double cor = 0;

        try {
            

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color x = new Color(img.getRGB(i, j));

                    zero = x.getGreen() - x.getRed();

                    if(zero != 0){

                        cor = (x.getGreen() + x.getRed() / (x.getGreen() - x.getRed()));

                    }else{

                        cor = 0;

                    }

                        if (cor < min) {
                            min = cor;
                        }
                        if (cor > max) {
                            max = cor;
                        }
                    }
            }
       
        //FINAL NORMALIZAÇÃO

       
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color p = new Color(img.getRGB(i, j));

                    zero = p.getGreen() - p.getRed();

                    if(zero != 0){

                            double atual = (p.getGreen() + p.getRed() / (p.getGreen() - p.getRed()));
                            cor = 255 * ((atual - min) / (max - min));

                    }else{

                        cor = 0;

                    }

                        int corB31 = (int) cor;

                        Color novo = new Color(corB31, corB31, corB31);
                        res.setRGB(i, j, novo.getRGB());
                }
            }
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Divisão por Zero", "Error", 0);
            
        }
        
        
    return res;
    }

    /**
     * Essa função é a implementação da método de Greenness = (G)/(R + G + B)
     * onde o R,G e B são os valores obtido da imagem
     * @param img
     * @return 
     */
    public BufferedImage Greenn(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        double max = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));

                double cor = x.getGreen() - (x.getRed() + x.getGreen() + x.getBlue());

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));

                double atual = p.getGreen() - (p.getRed() + p.getGreen() + p.getBlue() );

                double cor = 255 * ((atual - min) / (max - min));

                int corB32 = (int) cor;
                Color novo = new Color(corB32, corB32, corB32);
                res.setRGB(i, j, novo.getRGB());
            }
        }
        return res;
    }

    
    /**
     * Essa função é a implementação da método de GreennessSmolka = (G − Max{R,B})^2/G
     * onde o R,G e B são os valores obtido da imagem
     * @param img A imagem onde o filtro será aplicado
     * @return retorna a imagem depois de aplicado o filtro
     */
    public BufferedImage GreennSmolka(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        int zero;
        double max = 0;
        double cor = 0;
        double maior;

        try {
            

            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color x = new Color(img.getRGB(i, j));

                    zero = x.getGreen();

                    if(zero != 0){

                        if(x.getRed() > x.getBlue()){
                            maior = x.getRed();
                        }else{
                            maior = x.getBlue();
                        }

                        cor = x.getGreen() - Math.pow((maior),9) / x.getGreen();

                    }else{

                        cor = 0;

                    }

                        if (cor < min) {
                            min = cor;
                        }
                        if (cor > max) {
                            max = cor;
                        }
                    }
            }
       
        //FINAL NORMALIZAÇÃO

       
            for (int i = 0; i < img.getWidth(); i++) {
                for (int j = 0; j < img.getHeight(); j++) {
                    Color p = new Color(img.getRGB(i, j));

                    zero = p.getGreen();

                    if(zero != 0){

                        if(p.getRed() > p.getBlue()){
                            maior = p.getRed();
                        }else{
                            maior = p.getBlue();
                        }

                        double atual = p.getGreen() - Math.pow(( maior),9) / p.getGreen();
                        cor = 255 * ((atual - min) / (max - min));
                        
                    }else{

                        cor = 0;

                    }

                            
                        int corB31 = (int) cor;

                        Color novo = new Color(corB31, corB31, corB31);
                        res.setRGB(i, j, novo.getRGB());
                }
            }
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "Divisão por Zero", "Error", 0);
            
        }
        
    return res;
    }

    /**
    * Essa função é a implementação da método de GreennessG2 = (G^2 )/(B^2 + R^2 + k)
    * onde K é o valor passado pelo usuário e o R,G e B são os valores obtido da imagem
    * 
    * @param img A imagem onde o filtro será aplicado
    * @param K O valor K da equação
    * @return retorna a imagem depois de aplicado o filtro
    */
    public BufferedImage GreennG2(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇAO
        double min = 10000;
        double max = 0;
        double var = 14;
        double cor = 0;
        double maior;
        double zero;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                
                Color x = new Color(img.getRGB(i, j));
                
                zero = Math.pow(x.getBlue(),2) + Math.pow(x.getRed(),2) + var;

                if(zero != 0){

                    cor = Math.pow(x.getGreen(),2) / zero;

                }else{

                    cor = 0;

                }

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {

                Color p = new Color(img.getRGB(i, j));
                
                zero = Math.pow(p.getBlue(),2) + Math.pow(p.getRed(),2) + var;
                
                if(zero != 0){
                    
                    double atual = Math.pow(p.getGreen(),2) / zero;
                    cor = 255 * ((atual - min) / (max - min));

                }else{

                    cor = 0;

                }

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
                
                int corBK = (int) cor;

                Color novo = new Color(corBK, corBK, corBK);
                res.setRGB(i, j, novo.getRGB());
            }
        }
        return res;
    }
    
    /**
     * Essa função é a implementação da método de GreennessIPCA = I P CA = 0.7582|R − B| − 0.1168|R − G| + 0.6414|G − B|
     * onde o R,G e B são os valores obtido da imagem
     * @param img
     * @return 
     */
    public BufferedImage GreennIPCA(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 10000;
        double max = 0;
        double RB, RG, GB;
        
        

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));
                
                RB = x.getRed() - x.getBlue();
                RG = x.getRed() - x.getGreen();
                GB = x.getGreen() - x.getBlue();

                //Colocar a função IPCA
                double cor = 0.7582*(Math.abs(RB)) - 0.1168*(Math.abs(RG)) + 0.6414*(Math.abs(GB));

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));

                RB = p.getRed() - p.getBlue();
                RG = p.getRed() - p.getGreen();
                GB = p.getGreen() - p.getBlue();
                
                double atual = 0.7582*(Math.abs(RB)) - 0.1168*(Math.abs(RG)) + 0.6414*(Math.abs(GB));

                double cor = 255 * ((atual - min) / (max - min));

                int corB32 = (int) cor;
                Color novo = new Color(corB32, corB32, corB32);
                res.setRGB(i, j, novo.getRGB());
            }
        }
        return res;
    }
    
    public BufferedImage BIEspacoX(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 100000;
        double max = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
               Color x = new Color(img.getRGB(i, j));
               
                //Conversão de RGB para Espaço de cor XYZ
                double Xx = x.getRed() * 0.4124 + x.getGreen() * 0.3575 + x.getBlue() * 0.1804;
                double Yx = x.getRed() * 0.2126 + x.getGreen() * 0.7156 + x.getBlue() * 0.0721;
                double Zx = x.getRed() * 0.0193 + x.getGreen() * 0.1191 + x.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Yx / 100) - 16);
                double a = 500 * ((Xx / 95.047) - (Yx / 100));
                double b = 200 * ((Yx / 100) - (Zx / 108.883));

                double P = (a + (1.75 * L)) / ((5.465 * L) + a - (3.012 * b));
                double cor = (100 * (P - 0.31)) / 0.17;

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));
                //Conversão de RGB para Espaço de cor XYZ
                double X = p.getRed() * 0.4124 + p.getGreen() * 0.3575 + p.getBlue() * 0.1804;
                double Y = p.getRed() * 0.2126 + p.getGreen() * 0.7156 + p.getBlue() * 0.0721;
                double Z = p.getRed() * 0.0193 + p.getGreen() * 0.1191 + p.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Y / 100) - 16);
                double a = 500 * ((X / 95.047) - (Y / 100));
                double b = 200 * ((Y / 100) - (Z / 108.883));

                double P = (a + 1.75 * L) / (5.465 * L + a - 3.012 * b);
                double atual = (100 * (P - 0.31)) / 0.17;

                double cor = 255 * ((atual - min) / (max - min));

                int corBX = (int) cor;

                Color novo = new Color(corBX, corBX, corBX);
                res.setRGB(i, j, novo.getRGB());

            }
        }
        return res;
    }

    public BufferedImage BIEspacoI(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        //COMEÇO NORMALIZAÇÃO
        double min = 100000;
        double max = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color x = new Color(img.getRGB(i, j));
                
                //Conversão de RGB para Espaço de cor XYZ
                double Xx = x.getRed() * 0.4124 + x.getGreen() * 0.3575 + x.getBlue() * 0.1804;
                double Yx = x.getRed() * 0.2126 + x.getGreen() * 0.7156 + x.getBlue() * 0.0721;
                double Zx = x.getRed() * 0.0193 + x.getGreen() * 0.1191 + x.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Yx / 1) - 16);

                double cor = 100 - L;

                if (cor < min) {
                    min = cor;
                }
                if (cor > max) {
                    max = cor;
                }
            }
        }
        //FINAL NORMALIZAÇÃO

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color p = new Color(img.getRGB(i, j));

                //Conversão de RGB para Espaço de cor XYZ
                double X = p.getRed() * 0.4124 + p.getGreen() * 0.3575 + p.getBlue() * 0.1804;
                double Y = p.getRed() * 0.2126 + p.getGreen() * 0.7156 + p.getBlue() * 0.0721;
                double Z = p.getRed() * 0.0193 + p.getGreen() * 0.1191 + p.getBlue() * 0.9502;

                //Conversão de XYZ para L* a* b*
                double L = (116 * (Y / 1) - 16);
                double atual = 100 - L;
                double cor = 255 * ((atual - min) / (max - min));

                int corBII = (int) cor;
                
                Color novo = new Color(corBII, corBII, corBII);
                res.setRGB(i, j, novo.getRGB());

            }
        }
        return res;
    }
}
