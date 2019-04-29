package Yazlab2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class main extends JFrame{
    private JPanel panel;
    private JButton random,manuel;
    private JScrollPane jsp;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private JLabel title,rowText,columnText;
    private JTextField rowValue,columnValue;
    JTextField[][] values;
    private static int sumCounter =0,prodCounter=0;
    public main(){
        onStart();
        initComps();
    }

    private void onStart(){
        setSize(300,250); //Form boyutunu belirler
        setTitle("Main"); //Form başlığı
        setLocation(screenSize.width/2-this.getSize().width/2,screenSize.height/2-this.getSize().height/2); //Ekran ortasına ortaya çıkar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Tıklanınca kapanması için

        panel = new JPanel();
        jsp = new JScrollPane(panel , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED , JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //Scroll panelin içine atılır
        jsp.getVerticalScrollBar().setUnitIncrement(20); // Scroll hızını arttırmak
        panel.setSize(new Dimension(300,250));
        panel.setLayout(null);
        getContentPane().add(jsp);
    }

    private void initComps(){
        random = new JButton("Random");
        manuel = new JButton("Manuel");
        random.addActionListener(randomListener);
        manuel.addActionListener(manuelListener);
        title = new JLabel("Please select button.");

        title.setBounds(75,20,150,30);
        random.setBounds(75,60,150,30);
        manuel.setBounds(75,110,150,30);

        panel.add(random);
        panel.add(manuel);
        panel.add(title);
    }

    private void randomMain(){
        setSize(1000,700);
        panel.setSize(1000,700);
        setLocation(screenSize.width/2-this.getSize().width/2,screenSize.height/2-this.getSize().height/2);
        hideComps();
        initRandomBackButton();
        while(true){
            sumCounter=0;
            prodCounter=0;
            Random rand = new Random();
            int row,column;
            do{
                row = rand.nextInt(5)+1;
                column = rand.nextInt(5)+1;
            }while(row==column);
            //   row=5;
            //  column=5;
            System.out.println("row:"+row+" column:"+column);
            // ---------- MAIN MATRIX -------------
            double[][] matrix = getRandomMatrix(row, column); //Random Matrix oluşturulur
            showMainMatrixLabel(matrix, row, column); // Formada görmek icin
            System.out.println("Main Matrix");
            showMatrix(matrix, row, column); // Terminalde görmek icin

            // --------- Transpoze of Main Matrix ----------
            double[][] matrixT = getTranspoze(matrix,row,column);
            System.out.println("Transpoz Matrix");
            showMatrix(matrixT,column,row);
            showTranspozeMatrixLabel(matrixT, column, row);

            // -------- Prod of Main and Transpoze Matrix ---------
            double[][] matrixC = matrixProdwithTranspoze(matrix, row, column);
            System.out.println("Prod Matrix");
            showMatrix(matrixC, row);
            showProdMatrixLabel(matrixC,row);

            // ------- Determinant of Prod Matrix -------------
            double determinant = determinant(matrixC,row);
            JLabel detText = new JLabel();
            detText.setFont(new Font("Serif" , Font.BOLD,16));
            detText.setBounds(5,150,200,20);
            detText.setText("det(AX(A^T)) = "+ String.valueOf(determinant));
            panel.add(detText);

            // ----------- Ek Matrix -------------
            double[][] adjointMatrix = new double[row][row];
            adjoint(matrixC, adjointMatrix, row);

            double[][] cofactorMatrix = getTranspoze(adjointMatrix, row, row);
            showCofactorMatrix(cofactorMatrix,row);
            System.out.println("Kofaktör Matrix");
            showMatrix(cofactorMatrix, row);

            // -------- Konjuge Matrix ----------
            showAdjointMatrix(adjointMatrix,row);
            System.out.println("Konjuge Matrix");
            showMatrix(adjointMatrix, row);

            // --------- (AXA^T)^-1 -------
            double[][] inverseMatrix = new double[row][row];
            for(int i=0;i<row;i++){
                for(int j=0;j<row;j++){
                    inverseMatrix[i][j] = adjointMatrix[i][j]/determinant;
                }
            }
            showInverseMatrix(inverseMatrix,row);
            System.out.println("Matrisin Tersi");
            showMatrix(inverseMatrix, row);


            //------------- Pseudo Inverse Matrix ---------
            double[][] pseudoInverseMatrix = new double[column][row];
            // Deger NaN ve ya Sonsuz geliyorsa bastan Matrix olustur
            if(Double.isNaN(inverseMatrix[0][0]) || Double.isInfinite(inverseMatrix[0][0])){
                panel.removeAll();
                System.out.println("Sonuç NaN veya Sonsuz geliyor\n");
            }
            else{
                double toplam=0;
                // ------- (A^T)X(AX(A^T))^-1 -----------
                for(int i=0;i<column;i++){ //Soldaki satır
                    for(int j=0;j<row;j++){ // Sagdaki sutun
                        for(int k=0;k<row;k++){ //Soldaki sutun
                            toplam+=matrixT[i][k]*inverseMatrix[k][j];
                        }
                        pseudoInverseMatrix[i][j] = toplam;
                        toplam=0;
                    }
                }
                showPseudoInverseMatrix(pseudoInverseMatrix,column,row);
                System.out.println("Psuedo inverse");
                showMatrix(pseudoInverseMatrix, column,row);

                JLabel formul = new JLabel("Formul = (A^T)X(AX(A^T))^-1");
                formul.setBounds(710, 20, 300, 20);
                formul.setFont(new Font("Serif",Font.BOLD,18));
                formul.setForeground(Color.red);
                panel.add(formul);

                JLabel counterSum = new JLabel();
                counterSum.setBounds(710, 60, 150, 20);
                counterSum.setFont(new Font("Serif",Font.BOLD,16));
                counterSum.setText("Sum Counter="+sumCounter);
                panel.add(counterSum);

                JLabel counterProd = new JLabel();
                counterProd.setBounds(710, 90, 150, 20);
                counterProd.setFont(new Font("Serif",Font.BOLD,16));
                counterProd.setText("Prod Counter="+prodCounter);
                panel.add(counterProd);

                System.out.println("Sum Counter="+sumCounter+"Prod Counter="+prodCounter);
                break;
            }
        }
    }

    private double[][] getRandomMatrix(int row,int column){
        Random rand = new Random();
        double matrix[][] = new double[row][column];

        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                matrix[i][j] = rand.nextInt(9)+1;
            }
        }
        return matrix;
    }

    private static double[][] getTranspoze(double matrix[][],int row,int column){
        double[][] matrisT = new double[column][row];
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                matrisT[j][i] = matrix[i][j];
            }
        }
        return matrisT;
    }

    private static double[][] matrixProdwithTranspoze(double matrix[][],int row,int column){
        double[][] matrixC = new double[row][row];
        double[][] matrixT = getTranspoze(matrix, row, column);
        int sum=0;
        for(int i=0;i<row;i++){
            for(int j=0;j<row;j++){
                for(int k=0;k<column;k++){
                    sum+=matrix[i][k]*matrixT[k][j];
                    prodCounter++;
                    sumCounter++;
                }
                matrixC[i][j] = sum;
                sum=0;
            }
        }
        return matrixC;
    }

    private static void adjoint(double matrix[][], double adjM[][] , int length){
        if(length ==1){
            adjM[0][0]=1;
            return;
        }

        int prodValue= 1;
        double[][] tempM = new double[length][length];

        for(int i=0;i<length;i++){
            for(int j=0;j<length;j++){
                getCofactor(matrix , tempM ,i , j , length);

                if((i+j)%2==0){
                    prodValue=1;
                }
                else{
                    prodValue=-1;
                }

                adjM[j][i] = (prodValue)*(determinant(tempM,length-1));
                prodCounter++;
            }
        }
    }

    private static double determinant(double matrix[][] , int length){
        double result =0;

        if(length==1){
            return matrix[0][0];
        }

        double[][] tempM = new double[length][length];
        int prodValue = 1;

        for(int column=0;column<length;column++){
            getCofactor(matrix,tempM,0,column,length);
            result+= prodValue * matrix[0][column] * determinant(tempM,length-1);
            sumCounter++;
            prodCounter++;
            prodValue = -1*prodValue;
        }
        return result;
    }

    private static void getCofactor(double matrix[][] , double tempM[][] , int p,int q,int length){
        int i=0,j=0;

        for(int row=0;row<length;row++){
            for(int column=0;column<length;column++){
                if(row!=p && column!=q){
                    tempM[i][j++] = matrix[row][column];

                    if(j==length-1){
                        j=0;
                        i++;
                    }
                }
            }
        }
    }

    private void showMainMatrixLabel(double[][] matrix,int row,int column){
        JLabel[][] labelArr = new JLabel[row][column];
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                labelArr[i][j] = new JLabel(String.valueOf(matrix[i][j]));
                labelArr[i][j].setBounds(j*30+30,i*20+15,30,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("A=");
            label.setBounds(5,20,30,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private void showTranspozeMatrixLabel(double[][] matrix,int row,int column){
        JLabel[][] labelArr = new JLabel[row][column];
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                labelArr[i][j] = new JLabel(String.valueOf(matrix[i][j]));
                labelArr[i][j].setBounds(200+(j*30+30),(i*20+15),30,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("A^T=");
            label.setBounds(185,20,60,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private void showProdMatrixLabel(double[][] matrix,int n){
        JLabel[][] labelArr = new JLabel[n][n];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                labelArr[i][j] = new JLabel(String.valueOf(matrix[i][j]));
                labelArr[i][j].setBounds(430+(j*45+30),(i*20+15),60,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("AX(A^T)=");
            label.setBounds(380,20,100,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private void showCofactorMatrix(double[][] matrix,int n){
        JLabel[][] labelArr = new JLabel[n][n];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                labelArr[i][j] = new JLabel(String.valueOf(matrix[i][j]));
                labelArr[i][j].setBounds(310+(j*100+30),130+(i*20+15),100,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("adj(AX(A^T))=");
            label.setBounds(220,150,120,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private void showAdjointMatrix(double[][] matrix,int n){
        JLabel[][] labelArr = new JLabel[n][n];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                labelArr[i][j] = new JLabel(String.valueOf(matrix[i][j]));
                labelArr[i][j].setBounds(130+(j*100+30),250+(i*20+15),100,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("(adj(AX(A^T)))^T=");
            label.setBounds(20,270,150,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private void showInverseMatrix(double[][] matrix,int n){
        JLabel[][] labelArr = new JLabel[n][n];
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                labelArr[i][j] = new JLabel(String.format("%.5f",matrix[i][j]));
                labelArr[i][j].setBounds(300+(j*100+30),380+(i*20+15),100,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("(AXA^T)^-1=(adj(AxA^T))/det(AxA^T) -->");
            label.setBounds(20,400,320,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private void showPseudoInverseMatrix(double[][] matrix,int row,int column){
        JLabel[][] labelArr = new JLabel[row][row];
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                labelArr[i][j] = new JLabel(String.format("%.5f",matrix[i][j]));
                labelArr[i][j].setBounds(300+(j*100+30),500+(i*20+15),100,30);
                labelArr[i][j].setFont(new Font("Serif" , Font.PLAIN,16));
                panel.add(labelArr[i][j]);
            }
            JLabel label = new JLabel("(A^T)X(AX(A^T))^-1 (Pseudo Inverse) -->");
            label.setBounds(20,520,400,20);
            label.setFont(new Font("Serif" , Font.BOLD,16));
            panel.add(label);
            revalidate();
            repaint();
        }
    }

    private ActionListener randomListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Random Button");
            randomMain();
        }
    };

    private void manuelMain(int row , int column){
        setSize(1000,700);
        panel.setSize(1000,700);
        setLocation(screenSize.width/2-this.getSize().width/2,screenSize.height/2-this.getSize().height/2);

        panel.removeAll();

        JLabel formul = new JLabel("Formul = (A^T)X(AX(A^T))^-1");
        formul.setBounds(710, 20, 300, 20);
        formul.setFont(new Font("Serif",Font.BOLD,18));
        formul.setForeground(Color.red);
        panel.add(formul);

        initRandomBackButton();

        revalidate();
        repaint();
        row = getRow();
        column=getColumn();
        System.out.println("row:"+row+" column:"+column);

        // ---------- MAIN MATRIX -------------
        double[][] matrix = returnManuelMatrix(); //Random Matrix oluşturulur
        showMainMatrixLabel(matrix, row, column); // Formada görmek icin
        System.out.println("Main Matrix");
        showMatrix(matrix, row, column); // Terminalde görmek icin

        // --------- Transpoze of Main Matrix ----------
        double[][] matrixT = getTranspoze(matrix,row,column);
        System.out.println("Transpoz Matrix");
        showMatrix(matrixT,column,row);
        showTranspozeMatrixLabel(matrixT, column, row);

        // -------- Prod of Main and Transpoze Matrix ---------
        double[][] matrixC = matrixProdwithTranspoze(matrix, row, column);
        System.out.println("Prod Matrix");
        showMatrix(matrixC, row);
        showProdMatrixLabel(matrixC,row);

        // ------- Determinant of Prod Matrix -------------
        double determinant = determinant(matrixC,row);
        JLabel detText = new JLabel();
        detText.setFont(new Font("Serif" , Font.BOLD,16));
        detText.setBounds(5,150,200,20);
        detText.setText("det(AX(A^T)) = "+ String.valueOf(determinant));
        panel.add(detText);

        // ----------- Ek Matrix -------------
        double[][] adjointMatrix = new double[row][row];
        adjoint(matrixC, adjointMatrix, row);
        double[][] cofactorMatrix = getTranspoze(adjointMatrix, row, row);
        showCofactorMatrix(cofactorMatrix,row);
        System.out.println("Kofaktör Matrix");
        showMatrix(cofactorMatrix, row);

        // -------- Konjuge Matrix ----------
        showAdjointMatrix(adjointMatrix,row);
        System.out.println("Konjuge Matrix");
        showMatrix(adjointMatrix, row);

        // --------- (AXA^T)^-1 -------
        double[][] inverseMatrix = new double[row][row];
        for(int i=0;i<row;i++){
            for(int j=0;j<row;j++){
                inverseMatrix[i][j] = adjointMatrix[i][j]/determinant;
            }
        }
        showInverseMatrix(inverseMatrix,row);
        System.out.println("Matrisin Tersi");
        showMatrix(inverseMatrix, row);

        //------------- Pseudo Inverse Matrix ---------
        double[][] pseudoInverseMatrix = new double[column][row];

        // Deger NaN ve ya Sonsuz geliyorsa bastan Matrix olustur
        if(Double.isNaN(inverseMatrix[0][0]) || Double.isInfinite(inverseMatrix[0][0])){
            System.out.println("Sonuç NaN veya Sonsuz geliyor\n");
        }
        else{
            double toplam=0;
            // ------- (A^T)X(AX(A^T))^-1 -----------
            for(int i=0;i<column;i++){ //Soldaki satır
                for(int j=0;j<row;j++){ // Sagdaki sutun
                    for(int k=0;k<row;k++){ //Soldaki sutun
                        toplam+=matrixT[i][k]*inverseMatrix[k][j];
                    }
                    pseudoInverseMatrix[i][j] = toplam;
                    toplam=0;
                }
            }
            showPseudoInverseMatrix(pseudoInverseMatrix,column,row);
            System.out.println("Psuedo inverse");
            showMatrix(pseudoInverseMatrix, column,row);

            JLabel counterSum = new JLabel();
            counterSum.setBounds(710, 60, 150, 20);
            counterSum.setFont(new Font("Serif",Font.BOLD,16));
            counterSum.setText("Sum Counter="+sumCounter);
            panel.add(counterSum);

            JLabel counterProd = new JLabel();
            counterProd.setBounds(710, 90, 150, 20);
            counterProd.setFont(new Font("Serif",Font.BOLD,16));
            counterProd.setText("Prod Counter="+prodCounter);
            panel.add(counterProd);
            System.out.println("Sum Counter="+sumCounter+"Prod Counter="+prodCounter);
        }
    }

    private int getRow(){
        if(rowValue.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Deger girin.");
            rowValue.requestFocus();
            return -1;
        }
        else if(!Character.isDigit(rowValue.getText().charAt(0))){
            JOptionPane.showMessageDialog(this, "Pozitif tam sayı girin");
            rowValue.requestFocus();
            return -1;
        }
        else if(Integer.parseInt(rowValue.getText())>5||Integer.parseInt(rowValue.getText())<0){
            JOptionPane.showMessageDialog(this, "Uygun aralıkta bir deger girin [1,5]");
            rowValue.requestFocus();
            return -1;
            //JOptionPane.showMessageDialog(this, "Nickname cant empty.");
        }
        else{
            return Integer.parseInt(rowValue.getText());
        }
    }

    private int getColumn(){
        if(columnValue.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Deger girin.");
            columnValue.requestFocus();
            return -1;
        }
        else if(!Character.isDigit(columnValue.getText().charAt(0))){
            JOptionPane.showMessageDialog(this, "Tam sayı girin");
            columnValue.requestFocus();
            return -1;
        }
        else if(Integer.parseInt(columnValue.getText())>5||Integer.parseInt(columnValue.getText())<0){
            JOptionPane.showMessageDialog(this, "Uygun aralıkta bir deger girin [1,5]");
            columnValue.requestFocus();
            return -1;
        }
        else{
            return Integer.parseInt(columnValue.getText());
        }
    }

    private void initManuel(){
        hideComps();

        rowText = new JLabel("Please Enter Row");
        rowText.setBounds(20,20,200,20);
        panel.add(rowText);

        rowValue = new JTextField();
        rowValue.setBounds(150, 20, 100, 20);
        panel.add(rowValue);

        columnText = new JLabel("Please Enter Column");
        columnText.setBounds(20,70,200,20);
        panel.add(columnText);

        columnValue = new JTextField();
        columnValue.setBounds(150, 70, 100, 20);
        panel.add(columnValue);

        JButton go = new JButton("Go");
        go.setBounds(150, 120, 100, 30);
        go.addActionListener(goListener);
        panel.add(go);

        initManuelBackButton();

        revalidate();
        repaint();
    }

    private void initGo(int row,int column){
        panel.removeAll();
        setSize(300,400);
        panel.setSize(300,400);
        revalidate();
        repaint();

        JLabel title = new JLabel("Please enter values");
        title.setBounds(10,20,300,30);
        title.setFont(new Font("Serif" , Font.BOLD,16));
        title.setForeground(Color.red);
        panel.add(title);

        JButton start = new JButton("START");
        start.setBounds(150,20,100,30);
        start.addActionListener(startListener);
        panel.add(start);
        values = new JTextField[row][column];
        for(int i =0;i<row;i++){
            for(int j=0;j<column;j++){
                values[i][j] = new JTextField();
                values[i][j].setBounds(20+j*50, 70+i*50, 30, 20);
                panel.add(values[i][j]);
            }
        }

        JButton backButton = new JButton("Back");
        backButton.setBounds(150,320,100,30);
        backButton.addActionListener(backButtonListener);
        panel.add(backButton);

        revalidate();
        repaint();
    }

    private ActionListener manuelListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("manuel");
            initManuel();
        }
    };

    private ActionListener goListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("go");
            int rowInt = getRow();
            int columnInt = getColumn();
            if(rowInt!=-1 && columnInt!=-1 && rowInt!=columnInt){
                initGo(rowInt, columnInt);
            }
        }
    };

    private ActionListener backListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("back");
            panel.removeAll();
            setSize(300,250); //Form boyutunu belirler
            setTitle("Main"); //Form başlığı
            panel.setSize(new Dimension(300,250));
            setLocation(550,300);
            initComps();
            revalidate();
            repaint();
        }
    };

    private void initRandomBackButton(){
        JButton back = new JButton("Back");
        back.setBounds(850, 600, 100, 30);
        back.addActionListener(backListener);
        panel.add(back);
    }

    private void initManuelBackButton(){
        JButton back = new JButton("Back");
        back.setBounds(20, 120, 100, 30);
        back.addActionListener(backListener);
        panel.add(back);
    }

    private ActionListener backButtonListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("back button");
            setSize(300,250);
            panel.removeAll();
            initManuel();
            revalidate();
            repaint();
        }
    };

    private double[][] returnManuelMatrix(){
        double[][] matrix = new double[getRow()][getColumn()];
        for(int i=0;i<getRow();i++){
            for(int j=0;j<getColumn();j++){
                double value = Double.parseDouble(values[i][j].getText());
                matrix[i][j] = value;
            }
        }
        System.out.println("Manuel Matrix");
        showMatrix(matrix, getRow(), getColumn());
        return matrix;
    }

    private ActionListener startListener = new ActionListener() {//add actionlistner to listen for change
        @Override
        public void actionPerformed(ActionEvent e) {
            manuelMain(getRow(), getColumn());
        }
    };

    private void hideComps(){
        random.setVisible(false);
        manuel.setVisible(false);
        title.setVisible(false);
    }

    private static void showMatrix(double matrix[][],int row){
        for(int i=0;i<row;i++){
            for(int j=0;j<row;j++){
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void showMatrix(double matrix[][], int row, int column){
        for(int i=0;i<row;i++){
            for(int j=0;j<column;j++){
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println("");
    }
}
